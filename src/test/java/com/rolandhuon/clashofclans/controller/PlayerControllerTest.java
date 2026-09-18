package com.rolandhuon.clashofclans.controller;

import com.rolandhuon.clashofclans.model.Player;
import com.rolandhuon.clashofclans.service.PlayerNotFoundException;
import com.rolandhuon.clashofclans.service.PlayerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlayerController.class)
class PlayerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlayerService playerService;

    private Player player(long id, String name, int trophies) {
        Player player = mock(Player.class);
        when(player.getId()).thenReturn(id);
        when(player.getName()).thenReturn(name);
        when(player.getLevel()).thenReturn(10);
        when(player.getGold()).thenReturn(5_000L);
        when(player.getElixir()).thenReturn(4_000L);
        when(player.getDarkElixir()).thenReturn(300L);
        when(player.getTrophies()).thenReturn(trophies);
        return player;
    }

    @Test
    @DisplayName("GET /api/players returns every player.")
    void listsAllPlayers() throws Exception {
        Player roland = player(1, "Roland", 1450);
        Player ada = player(2, "Ada", 1120);
        when(playerService.findAll()).thenReturn(List.of(roland, ada));

        mockMvc.perform(get("/api/players"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Roland"))
                .andExpect(jsonPath("$[1].trophies").value(1120));

        verify(playerService).findAll();
    }

    @Test
    @DisplayName("GET /api/players?name= delegates to the name lookup.")
    void searchesByName() throws Exception {
        Player roland = player(1, "Roland", 1450);
        when(playerService.findByName("Roland")).thenReturn(List.of(roland));

        mockMvc.perform(get("/api/players").param("name", "Roland"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)));

        verify(playerService).findByName("Roland");
    }

    @Test
    @DisplayName("GET /api/players?name= returns an empty array, not a 404.")
    void unknownNameReturnsAnEmptyArray() throws Exception {
        when(playerService.findByName("Nobody")).thenReturn(List.of());

        mockMvc.perform(get("/api/players").param("name", "Nobody"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/players/{id} returns the player.")
    void readsOnePlayer() throws Exception {
        Player roland = player(1, "Roland", 1450);
        when(playerService.findById(1L)).thenReturn(roland);

        mockMvc.perform(get("/api/players/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Roland"));
    }

    @Test
    @DisplayName("An unknown id is translated into a 404 by the exception handler.")
    void unknownIdReturnsNotFound() throws Exception {
        when(playerService.findById(999L)).thenThrow(new PlayerNotFoundException(999L));

        mockMvc.perform(get("/api/players/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Player 999 not found."));
    }

    @Test
    @DisplayName("A non numeric id is rejected with a 400 before reaching the service.")
    void nonNumericIdReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/players/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/players answers 201 Created.")
    void createsAPlayer() throws Exception {
        Player newbie = player(3, "Newbie", 0);
        when(playerService.create(any())).thenReturn(newbie);

        mockMvc.perform(post("/api/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Newbie","level":1,"gold":100,"elixir":100,"darkElixir":0}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3));
    }

    @Test
    @DisplayName("DELETE /api/players/{id} answers 204 with no body.")
    void deletesAPlayer() throws Exception {
        mockMvc.perform(delete("/api/players/1"))
                .andExpect(status().isNoContent());

        verify(playerService).delete(eq(1L));
    }
}
