# Clash of Clans - Java

A Clash of Clans game engine written in Java 21 and exposed over a REST API.

Players own villages, villages hold buildings, buildings and troops are upgraded
with three different currencies, and players raid each other for loot and trophies.

## Requirements

- **Docker Desktop**, running. It hosts PostgreSQL.
- Nothing else. Java and Maven come with the project through the Maven wrapper.

## Run it

### Development: application on your machine, database in Docker

```bash
./mvnw spring-boot:run
```

Spring Boot reads `docker-compose.yml`, starts the PostgreSQL container itself and
derives the connection settings from it. No credentials in the configuration.

### Demo: everything in Docker

```bash
docker compose --profile full up --build
```

The first build downloads a JDK image and the dependencies, so allow a few minutes.

> The `app` service sits behind the `full` Compose profile. Without it, a local run
> would start a second copy of the application and both would fight over port 8080.

### Optional: play a demo raid on startup

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=demo
```

## Once it is up

| URL | What it is |
| --- | --- |
| http://localhost:8080/ | the 3D village viewer |
| http://localhost:8080/swagger-ui/index.html | interactive API documentation, every endpoint described |
| http://localhost:8080/v3/api-docs | the OpenAPI specification |
| http://localhost:8080/api/troop-types | a quick check that it answers |

Import `postman/clash-of-clans.postman_collection.json` into Postman for the full
set of requests, grouped by topic.

The database is seeded on first start with **fifty players**, their villages,
buildings and troops. Roland sits on top with 5 110 trophies, a fully maxed base and
999 999 999 999 of each currency; Dutofa follows with 4 931, and the rest of the
table runs down to 20 trophies. A chief's village is equipped from a tier derived
from their trophies, so a base near the top of the table is visibly tougher than one
at the bottom. The seed is idempotent: it does nothing if players already exist.

## The 3D front end

`http://localhost:8080/` serves a three.js village built entirely from the API.
It is plain ES modules under `src/main/resources/static`, no build step, no
framework, no package manager: three.js comes from a CDN through an import map.

```
static/index.html      the shell
static/css/app.css     the Clash of Clans look
static/js/api.js       one function per endpoint
static/js/meshes.js    a low-poly mesh per building and troop type
static/js/village.js   the scene, the ring layout, the animation loop
static/js/app.js       the panels, the raid, the result screen
```

What it shows and what it lets you do, all of it against the running API:

| Panel | What it does |
| --- | --- |
| Top bar | gold, elixir, dark elixir and trophies, live after every action |
| Village | every building with its level and hit points, and an **upgrade button** showing the exact cost in the right currency — greyed out when you cannot afford it, `MAX` at the last level |
| Build | adds a building to your village, the list only offering types below their `maxCount` |
| Leaderboard | click any chief to **scout** their village; the panels turn read-only and a *Back to my village* button appears |
| Army | every unlocked troop with its level, hit points and damage, and the same upgrade button |
| Raid | pick a target, compose an army against your camp capacity, and watch it play out |

Upgrading is the API doing the work: the button calls `POST /api/buildings/{id}/upgrade`
or `POST /api/troops/{id}/upgrade`, the engine charges the right currency and refuses
with `409` when the purse is short — the front end only shows what came back.

The village is an island: grass inside a wall ring, defences on the outside ring,
resources in the middle, the core buildings at the centre, with trees, rocks, water
and drifting clouds around it.

> The crest and the wordmark are original artwork drawn for this project. The real
> Clash of Clans logo, fonts and art are Supercell's, and are deliberately not used
> or imitated pixel for pixel — this is a school project, not a distribution of
> someone else's assets.

> The raid endpoint returns the outcome of a battle, not a turn-by-turn log. The
> numbers on screen are exactly the ones the engine produced — including how many
> buildings fell and how many troops died — but which building falls in which order
> is the viewer's own staging. A battle-log endpoint would make the replay literal.

Editing a file under `static/` while the application is running has no effect: Spring
serves the copy that was put in `target/classes` at build time. Restart, or copy the
file across.

## Game content

### Currencies

A balance is a `long`, not an `int`. A chief can hold more than two billion of a
currency, and the loot taken from such a stock is computed without overflowing:

```java
private static long share(long stock, int percentage) {
    return stock / 100 * percentage + stock % 100 * percentage / 100;
}
```

Costs stay `int` — they live in the enum tables and none of them come close to the
limit.


| Currency | Pays for |
| --- | --- |
| `GOLD` | every defence except the Monolith, plus the Elixir Collector and Elixir Storage |
| `ELIXIR` | troops, the laboratory, the camps, the Gold Mine and Gold Storage |
| `DARK_ELIXIR` | the Monolith, the Hero Hall, the Pet House and the Hog Rider |

Each type carries its own currency, so a single upgrade path handles all of them:
`EntityType.upgradeResource()` says what to charge, `upgradeCostFrom(level)` says how much.

A building also costs something to put up. That price is the level-one entry of its
own stats table — a slot that used to hold a hard-coded `0` that nothing ever read,
since `upgradeCostFrom(level)` always looks at `level + 1`. `BuildingType.buildCost()`
gives it a meaning instead of adding a field.

### Loot

Loot is proportional to the **destruction percentage**, not to the stars: a raid that
breaks 8 % of a village and earns no star still takes 8 % of what that village holds.
A village whose stock has already been emptied pays nothing, which is why the raid
panel shows what a target still has before you commit an army.

### Production

A Gold Mine and an Elixir Collector fill up on their own, at a rate that grows with
their level (200 an hour at level one, 1 800 at level six). Nothing runs in the
background: a building remembers when it was last emptied, and what it owes is worked
out from the time elapsed when you ask. That way the mines keep filling while the
application is down, and a test can hand the calculation any instant it likes instead
of waiting.

A mine stops once it holds **six hours** of production, so a village left alone for a
month is worth no more than one left alone for an evening.

`POST /api/villages/{id}/collect` empties every producer. What comes out lands in two
places: the owner's **purse**, which is what upgrades and buildings are paid from, and
the village's **stock**, which is what raiders can take. Those are two views of the
same storages in this model, with one simplification worth knowing: a raid empties the
stock without touching the purse, so being raided costs you loot but never costs you
an upgrade you had already saved for.

The clock is a `Clock` bean rather than a call to `Instant.now()` scattered around, so
`VillageBuildingProductionTest` can prove the six-hour cap without sleeping.

### Troops

| Troop | Housing | Moves | Targets | Role |
| --- | --- | --- | --- | --- |
| Barbarian | 1 | ground | `FIRST_ALIVE` | attacker |
| Archer | 1 | ground | `WEAKEST_FIRST` | attacker |
| Giant | 5 | ground | `DEFENSE_FIRST` | attacker |
| Goblin | 1 | ground | `RESOURCE_FIRST` | attacker |
| Balloon | 5 | **air** | `DEFENSE_FIRST` | attacker |
| Wizard | 4 | ground | `FIRST_ALIVE` | attacker |
| Hog Rider | 5 | ground | `DEFENSE_FIRST` | attacker |
| Healer | 14 | **air** | `WEAKEST_FIRST` | support |
| Dragon | 20 | **air** | `FIRST_ALIVE` | attacker |

A targeting mode is not a `switch` in the battle loop: it is a key into the
`TargetingStrategy` beans. `BattleService` refuses to start if a mode used by a
troop type has no strategy, so a missing one is a startup failure, never a
`NullPointerException` in the middle of a raid.

A `SUPPORT` troop heals its weakest wounded ally instead of attacking.

A `SPLASH` troop (Balloon, Wizard, Dragon) hits its target and the units beside it:
`AttackProfile.splashTargets()` derives how many from the blast radius, one for a
wizard or a dragon, two for a balloon. Without a grid there is no geometry, so
"beside" means the next units the battle is holding; splash never reaches a unit the
attacker could not target on its own.

Every troop type carries one class, `StandardTroop`. There is no `Barbarian` class
and no `Archer` class: what separates two troops is data, and that data lives in
`TroopType`. A subclass is written the day a troop needs to override a behaviour,
not to hold a constant.

### Air and ground

A troop moves on the ground or through the air, and a defence only fires at what it
can reach.

| Defence | Fires at |
| --- | --- |
| Cannon, Mortar, Ricochet Cannon, Multi-Gear Tower, Monolith | ground only |
| Air Defense | air only |
| Archer Tower, Wizard Tower, Hidden Tesla, Multi-Archer Tower | ground and air |

A village defended only by cannons is helpless against dragons; add air defences and
the same three dragons are wiped out, while a ground army walks past those air
defences untouched. A defence that cannot reach anybody simply skips its turn — it
never blocks the defences that can fire.

The rule lives in the domain, not in the battle loop: `Attacker.canTarget(Damageable)`
defaults to `true`, and `DefensiveBuilding` overrides it by asking its
`BuildingType` whether its `TargetScope` covers the unit's `Movement`.

### Buildings

Defences (`Cannon`, `Archer Tower`, `Mortar`, `Wizard Tower`, `Air Defense`,
`Hidden Tesla`, `Monolith`) and the merged defences (`Ricochet Cannon`,
`Multi-Archer Tower`, `Multi-Gear Tower`) carry a damage table and **shoot back**
at the attacking army on every turn, within the limits of the table above. Resource buildings (`Gold Mine`,
`Elixir Collector`, `Gold Storage`, `Elixir Storage`) are what a Goblin runs for.
Military camps decide how many troops an army may hold.

`BuildingType.isDefensive()` is derived from the damage table rather than declared,
so a building is a defence exactly when it can deal damage, and the constructor
refuses a building that has a damage table but nothing to shoot at.

## Tests

```bash
./mvnw test
```

Domain tests run in milliseconds and need nothing. The single `@SpringBootTest`
starts the PostgreSQL container, so Docker must be running. To skip it:

```bash
./mvnw test -Dtest='!ClashOfClansApplicationTests'
```

## Configuration

`src/main/resources/application.yaml`

| Property | Default | Meaning |
| --- | --- | --- |
| `coc.battle.max-turns` | `180` | hard limit on a battle, so a raid always terminates |
| `coc.api.rate-limit.enabled` | `true` | turns the API rate limiting on or off |
| `coc.api.rate-limit.capacity` | `60` | requests allowed per client and per window |
| `coc.api.rate-limit.refill-seconds` | `60` | length of the window, in seconds |
| `coc.api.rate-limit.max-tracked-clients` | `10000` | bound on the in-memory client table |
| `coc.api.rate-limit.trust-forwarded-for` | `false` | read the client from `X-Forwarded-For` |
| `spring.jpa.show-sql` | `true` | development only, prints every statement |
| `spring.jpa.hibernate.ddl-auto` | `update` | development only, see the limitation below |
| `spring.jpa.open-in-view` | `false` | no lazy loading from a controller |

Any property can be overridden at launch without rebuilding:

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments=--coc.battle.max-turns=20
```

## API protection

Every `/api/**` request goes through a per-client fixed window. The client is the
remote address, and **only** the `X-Forwarded-For` address when
`coc.api.rate-limit.trust-forwarded-for` is on — otherwise any client could leave
its own quota behind by setting one header. Each answer carries `X-RateLimit-Limit`
and `X-RateLimit-Remaining`; over the quota the request is refused with `429` and a
`Retry-After` header instead of reaching a controller.

The client table is bounded: past `max-tracked-clients`, expired windows are dropped
first, and any client that still does not fit shares one overflow window rather than
taking the application down.

Swagger UI and the OpenAPI document are not under `/api`, so they are never throttled.

Input is validated before the controller body runs: `@Valid` on the request bodies,
`@NotBlank` / `@Min` / `@PositiveOrZero` / `@NotEmpty` on their fields.
`GlobalExceptionHandler` turns the failures below into the same JSON shape
`{"status":.., "message":..}`. Anything it does not handle — an unknown route, a
wrong HTTP method, an unsupported media type — still answers with Spring's own
default body, which carries `error` instead of `message`:

| Exception | Status |
| --- | --- |
| `MethodArgumentNotValidException`, `IllegalArgumentException`, `IllegalStateException` | `400` |
| `MethodArgumentTypeMismatchException`, `HttpMessageNotReadableException` | `400` |
| `NotFoundException` | `404` |
| `InsufficientResourcesException` | `409` |

## Swagger

Every endpoint carries a summary, a paragraph of description and the meaning of each
status code it can return, written with `@Tag`, `@Operation` and `@ApiResponses` on
the controllers. The eight groups are Players, Villages, Upgrades, Raids, Battles,
Leaderboard, Troop types and Building types.

## Architecture

```
domain/      pure game model - no Spring, no JPA
model/       JPA entities - the persistence shape of the data
repository/  Spring Data interfaces
service/     game rules and orchestration
controller/  REST endpoints
dto/         what the API exposes, kept separate from the domain
handler/     exception to HTTP status translation
web/         servlet filters - rate limiting
config/      beans and typed configuration properties
app/         CLI runner, data seeder, event listeners
```

`grep -r springframework src/main/java/com/rolandhuon/clashofclans/domain/` returns
nothing: the game model does not know that Spring exists.

## Known limitation

`ddl-auto: update` adds columns and tables but never modifies existing constraints.
Adding a constant to `BuildingType` or `TroopType` leaves the generated `CHECK`
constraint out of date, and inserts of the new value fail. Until a migration tool
such as Flyway is introduced, drop the affected tables and let Hibernate recreate them:

```bash
docker compose exec postgres psql -U coc -d coc \
  -c "DROP TABLE IF EXISTS village_building, player_troop, village, player CASCADE;"
```
