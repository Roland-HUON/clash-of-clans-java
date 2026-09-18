import base64, io, json, os, re, subprocess, urllib.error, urllib.parse, urllib.request
from http.cookiejar import CookieJar

B = 'http://localhost:8080'
ok = bad = 0
def check(label, cond, detail=''):
    global ok, bad
    if cond: ok += 1; print('  OK   ', label)
    else:    bad += 1; print('  WRONG', label, '|', detail)

class NoRedirect(urllib.request.HTTPRedirectHandler):
    def redirect_request(self, *a, **k): return None
NO_REDIRECT = urllib.request.build_opener(NoRedirect)

def http(path, method='GET', user=None, headers=None, body=None, follow=True):
    req = urllib.request.Request(B + path, method=method, data=body)
    for k, v in (headers or {}).items(): req.add_header(k, v)
    if user:
        req.add_header('Authorization', 'Basic ' + base64.b64encode(user.encode()).decode())
    opener = urllib.request.urlopen if follow else NO_REDIRECT.open
    try:
        with opener(req) as r: return r.status, r.read().decode('utf-8', 'replace')
    except urllib.error.HTTPError as e: return e.code, e.read().decode('utf-8', 'replace')


def signed_in():
    jar = CookieJar()
    opener = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(jar))
    opener.open(B + '/login.html').read()
    token = next((c.value for c in jar if c.name == 'XSRF-TOKEN'), '')
    form = urllib.parse.urlencode({'username': 'user', 'password': 'password', '_csrf': token}).encode()
    opener.open(B + '/login', form).read()
    return opener, next((c.value for c in jar if c.name == 'XSRF-TOKEN'), '')


def through(opener, req):
    try:
        with opener.open(req) as r: return r.status
    except urllib.error.HTTPError as e: return e.code


def battle(count, entries=1):
    unit = {'type': 'BARBARIAN', 'level': 1, 'count': count}
    body = json.dumps({'army': [unit] * entries, 'village': [{'type': 'CANNON', 'level': 1}]}).encode()
    return http('/api/battles', 'POST', 'user:password', {'Content-Type': 'application/json'}, body)[0]


readme = io.open('README.md', encoding='utf-8').read()
SRC = 'src/main/java/com/rolandhuon/clashofclans'

print('\n== README: the URL table ==')
status, _ = http('/', headers={'Accept': 'text/html'}, follow=False)
check('/ redirects a browser to the login page', status == 302, f'got {status}')
check('/login.html is public', http('/login.html')[0] == 200)
check('/swagger-ui/index.html reachable signed in', http('/swagger-ui/index.html', user='user:password')[0] == 200)
check('/v3/api-docs reachable signed in', http('/v3/api-docs', user='user:password')[0] == 200)
check('/api/troop-types needs credentials', http('/api/troop-types')[0] == 401)
check('/api/troop-types answers with credentials', http('/api/troop-types', user='user:password')[0] == 200)

print('\n== README: "Signing in" ==')
check('user/password signs in', http('/api/players', user='user:password')[0] == 200)
check('admin/admin signs in', http('/api/players', user='admin:admin')[0] == 200)
check('a wrong password is refused', http('/api/players', user='user:wrong')[0] == 401)
check('user may NOT delete (403, not 401)', http('/api/players/999999', 'DELETE', 'user:password')[0] == 403)
check('admin may delete (reaches the service)', http('/api/players/999999', 'DELETE', 'admin:admin')[0] == 404)
check('a non-delete write is allowed to user',
      http('/api/players', 'POST', 'user:password', {'Content-Type': 'application/json'},
           b'{"name":"","level":0,"gold":-1,"elixir":0,"darkElixir":0}')[0] == 400)
cfg = io.open(f'{SRC}/config/WebSecurityConfig.java', encoding='utf-8').read()
check('passwords are BCrypt hashed', 'BCryptPasswordEncoder' in cfg)
check('the users live in an InMemoryUserDetailsManager', 'InMemoryUserDetailsManager' in cfg)
check('the README names the file that holds them', 'config/WebSecurityConfig.java' in readme)
check('the CSRF exemption is conditioned on there being no live session',
      'isStatelessBasicCall' in cfg and 'getRequestedSessionId() == null' in cfg
      and 'presents no live session' in readme)
check('the CSRF cookie is the readable XSRF-TOKEN', 'CookieCsrfTokenRepository.withHttpOnlyFalse' in cfg)
front = io.open('src/main/resources/static/js/api.js', encoding='utf-8').read()
check('the front end echoes X-XSRF-TOKEN on writes', "X-XSRF-TOKEN" in front)
login = io.open('src/main/resources/static/login.html', encoding='utf-8').read()
check('the login page carries a hidden _csrf field', 'name="_csrf"' in login)
check('the top bar has a Sign out control',
      'Sign out' in io.open('src/main/resources/static/index.html', encoding='utf-8').read())
_, page = http('/login.html')
check('the login page names both chiefs', '<code>user</code>' in page and '<code>admin</code>' in page)

print('\n== README: the architecture listing ==')
listed = set(re.findall(r'^([a-z]+)/\s{2,}', readme[readme.index('## Architecture'):], re.M))
actual = {d for d in os.listdir(SRC) if os.path.isdir(os.path.join(SRC, d))}
check('every package listed in the README exists', listed <= actual, f'extra: {listed - actual}')
check('every package on disk is listed in the README', actual <= listed, f'missing: {actual - listed}')

print('\n== README: Postman ==')
coll = json.load(io.open('postman/clash-of-clans.postman_collection.json', encoding='utf-8'))
reqs = []
def walk(items):
    for i in items:
        walk(i['item']) if 'item' in i else reqs.append(i)
walk(coll['item'])
claimed = int(re.search(r'into Postman: (\d+) requests', readme).group(1))
check(f'the README claims {claimed} requests and the file holds {len(reqs)}', claimed == len(reqs))
check('every request asserts its status', all(
    any('test' in e.get('listen','') for e in r.get('event', [])) for r in reqs),
    str([r['name'] for r in reqs if not any('test' in e.get('listen','') for e in r.get('event', []))]))
check('the collection authenticates with Basic', coll.get('auth', {}).get('type') == 'basic')
check('its credentials default to admin/admin',
      {v['key']: v['value'] for v in coll['variable']}.get('username') == 'admin')

print('\n== README: the API surface ==')
spec = json.loads(http('/v3/api-docs', user='user:password')[1])
ops = sum(len([m for m in v if m in ('get','post','put','patch','delete')]) for v in spec['paths'].values())
endpoints = int(re.search(r'covering all (\d+) endpoints', readme).group(1))
check(f'the README claims {endpoints} endpoints and Swagger publishes {ops}', endpoints == ops)
schemes = spec.get('components', {}).get('securitySchemes', {})
check('Swagger UI can authenticate: the document declares an HTTP Basic scheme',
      any(v.get('type') == 'http' and v.get('scheme') == 'basic' for v in schemes.values()),
      str(list(schemes)))
check('every operation is covered by that scheme', bool(spec.get('security')))

check('no operation carries a ghost 400/404/409',
      not any(set(op.get('responses', {})) == {'200','400','404','409'}
              for v in spec['paths'].values() for m, op in v.items() if m != 'parameters'))

print('\n== house style ==')
java = subprocess.run(['git', 'ls-files', SRC], capture_output=True, text=True).stdout.split()
def scan(pattern):
    hits = []
    for f in java:
        for n, line in enumerate(io.open(f, encoding='utf-8'), 1):
            if re.search(pattern, line): hits.append(f'{f}:{n}')
    return hits
check('no comments left in the main sources', not scan(r'^\s*(//|/\*|\*)'), str(scan(r'^\s*(//|/\*|\*)')[:3]))
check('no System.out anywhere', not scan(r'System\.out'), str(scan(r'System\.out')[:3]))
check('no Instant.now() outside the Clock', not scan(r'Instant\.now\(\)'), str(scan(r'Instant\.now\(\)')[:3]))

print('\n== configuration table ==')
yaml = io.open('src/main/resources/application.yaml', encoding='utf-8').read()
for key in sorted(set(re.findall(r'`(coc\.api\.rate-limit\.[a-z-]+)`', readme))):
    check(f'{key} exists in application.yaml', key.split('.')[-1] in yaml)

print('')
print('== the defects the review of 2026-09-18 found ==')
opener, token = signed_in()
write = urllib.request.Request(B + '/api/players', method='POST',
                               data=b'{"name":"ZZ-Audit","level":1,"gold":0,"elixir":0,"darkElixir":0}')
write.add_header('Content-Type', 'application/json')
write.add_header('Authorization', 'Basic ' + base64.b64encode(b'user:WRONGPASS').decode())
check('a Basic header cannot switch CSRF off for a session write', through(opener, write) == 403)

probe = urllib.request.Request(B + '/api/players')
probe.add_header('Authorization', 'Basic ' + base64.b64encode(b'user:password').decode())
with urllib.request.urlopen(probe) as r:
    handed_out = [v for k, v in r.getheaders() if k.lower() == 'set-cookie' and 'JSESSIONID' in v]
check('a Basic client is never handed a session cookie', not handed_out, str(handed_out))

troop = io.open(SRC + '/domain/troop/TroopType.java', encoding='utf-8').read()
ceiling = int(re.search(r'MAX_ARMY_SIZE = (\d+)', troop).group(1))
entries = int(re.search(r'MAX_ARMY_ENTRIES = (\d+)', troop).group(1))
check('an army of ' + str(ceiling) + ' is still allowed', battle(ceiling) == 200)
check('an army of ' + str(ceiling + 1) + ' is refused', battle(ceiling + 1) == 400)
check('a billion units is refused, not attempted', battle(1_000_000_000) == 400)
check(str(entries + 1) + ' army entries are refused', battle(1, entries + 1) == 400)

upgrade = io.open(SRC + '/service/UpgradeService.java', encoding='utf-8').read()
check('a producer is emptied before its level is raised',
      upgrade.index('bankWhatTheOldLevelProduced(building)') < upgrade.index('building.setLevel(nextLevel)'))

raid = io.open(SRC + '/service/RaidService.java', encoding='utf-8').read()
check('a raid checks housing space before it creates any troop',
      raid.index('Army too large') < raid.index('troopFactory.create'))
print('')
print('== the stat tables the README quotes ==')
building_src = io.open(SRC + '/domain/building/BuildingType.java', encoding='utf-8').read()
troop_src = io.open(SRC + '/domain/troop/TroopType.java', encoding='utf-8').read()


def table(source, name, wrapper):
    block = source[source.index('\n    ' + name + '('):][:8000]
    body = re.search(wrapper + r'\(\s*List\.of\(([^)]*)\)', block, re.S).group(1)
    return [int(v) for v in body.replace('\n', '').split(',') if v.strip()]


mine = table(building_src, 'GOLD_MINE', 'Production')
collector = table(building_src, 'ELIXIR_COLLECTOR', 'Production')
drill = table(building_src, 'DARK_ELIXIR_DRILL', 'Production')
spoken = [int(n.replace(' ', '').replace(' ', ''))
          for n in re.findall(r'(\d[\d  ]*) an hour at level', readme)]
check('the README quotes the mine and the drill starting rates',
      spoken == [mine[0], drill[0]], str(spoken))
check('a mine and a collector share one rate table', mine == collector)
top = re.search(r'and ([\d  ]+) at level seventeen', readme)
check('the README quotes the mine top rate', top and int(top.group(1).replace(' ', '')) == mine[-1],
      f'README says {top.group(1) if top else None}, the table ends at {mine[-1]}')
top_drill = re.search(r'and ([\d  ]+) at level eleven', readme)
check('the README quotes the drill top rate',
      top_drill and int(top_drill.group(1).replace(' ', '')) == drill[-1],
      f'README says {top_drill.group(1) if top_drill else None}, the table ends at {drill[-1]}')
check('the mine has seventeen levels and the drill eleven, as the README says',
      len(mine) == 17 and len(drill) == 11, f'{len(mine)} and {len(drill)}')

minted = re.search(r'to 6 sitting on a full buffer was worth ([\d  ]+) gold', readme)
check('the 5-to-6 upgrade windfall the README quotes is what the table gives',
      minted and int(minted.group(1).replace(' ', '')) == (mine[5] - mine[4]) * 6,
      f'README says {minted.group(1) if minted else None}, the table gives {(mine[5] - mine[4]) * 6}')
minted_top = re.search(r'16 to 17\s*\nis worth ([\d  ]+)', readme)
check('so is the 16-to-17 one',
      minted_top and int(minted_top.group(1).replace(' ', '')) == (mine[16] - mine[15]) * 6,
      f'README says {minted_top.group(1) if minted_top else None}, the table gives {(mine[16] - mine[15]) * 6}')

quoted_rate = re.search(r'at ([\d  ]+) an hour, one unit is worth ([\d.]+) s', readme)
rate = int(quoted_rate.group(1).replace(' ', ''))
check('the rate the nanosecond example uses is a real rate', rate in mine, str(rate))
check('and its per-unit cost is arithmetically right',
      abs(3600 / rate - float(quoted_rate.group(2))) < 0.001)

rows = re.findall(r'^\| (\w[\w ]*?) \| (\d+) \| (?:\*\*)?(\w+)(?:\*\*)? \| `(\w+)` \| (\w+) \|$', readme, re.M)
declared = {}
for m in re.finditer(r'^    [A-Z_]+\("([^"]+)", (\d+), \w+, (\w+), (\w+),(.*?)$', troop_src, re.M):
    declared[m.group(1)] = (int(m.group(2)), m.group(4).lower(), m.group(3),
                            'support' if 'SUPPORT' in m.group(5) else 'attacker')
check(f'the README troop table has a row per troop ({len(declared)})', len(rows) == len(declared),
      f'{len(rows)} rows for {len(declared)} troops')
wrong = [r[0] for r in rows if declared.get(r[0]) != (int(r[1]), r[2], r[3], r[4])]
check('every troop row matches the enum', not wrong, str(wrong))
print(f'\n{ok} claims verified, {bad} wrong')
raise SystemExit(1 if bad else 0)
