package de.adesso.kicker.match;

import de.adesso.kicker.match.controller.MatchController;
import de.adesso.kicker.match.exception.FutureDateException;
import de.adesso.kicker.match.exception.InvalidCreatorException;
import de.adesso.kicker.match.exception.SamePlayerException;
import de.adesso.kicker.match.persistence.Match;
import de.adesso.kicker.match.service.MatchService;
import de.adesso.kicker.user.persistence.User;
import de.adesso.kicker.user.UserDummy;
import de.adesso.kicker.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = MatchController.class, secure = false)
class MatchControllerTest {

    @MockBean
    MatchService matchService;

    @MockBean
    UserService userService;

    @Autowired
    private MockMvc mockMvc;

    private static User createUser() {
        return UserDummy.defaultUser();
    }

    private static List<User> createUserList() {
        return Arrays.asList(UserDummy.defaultUser(), UserDummy.alternateUser());
    }

    @Test
    @DisplayName("Return 'match/add.html' and contain a new Match")
    @WithMockUser
    void getAddMatch() throws Exception {
        // given
        var user = createUser();
        var userList = createUserList();
        when(userService.getAllUsers()).thenReturn(userList);
        when(userService.getLoggedInUser()).thenReturn(user);

        // when
        var result = this.mockMvc.perform(get("/matches/add"));

        // then
        result.andExpect(status().isOk())
                .andExpect(view().name("sites/matchresult.html"))
                .andExpect(model().attribute("match", new Match()))
                .andExpect(model().attribute("currentUser", user))
                .andExpect(model().attribute("users", userList));
    }

    @Test
    @DisplayName("When no date entered then 'noDate' should exist")
    @WithMockUser
    void whenMatchWithOutDateThenReturnNoDate() throws Exception {
        // given
        var user = createUser();
        var userList = createUserList();
        when(userService.getAllUsers()).thenReturn(userList);
        when(userService.getLoggedInUser()).thenReturn(user);

        // when
        var result = this.mockMvc.perform(post("/matches/add").param("teamAPlayer1.userId", "user")
                .param("teamBPlayer1.userId", "user2")
                .param("winnerTeamA", "true"));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("noDate"))
                .andExpect(model().attribute("currentUser", user))
                .andExpect(model().attribute("users", userList));
        verify(matchService, times(0)).addMatchEntry(any(Match.class));
    }

    @Test
    @DisplayName("When no winner has been selected 'noWinner' should exist")
    @WithMockUser
    void whenMatchWithNoWinnerThenReturnNoWinner() throws Exception {
        // given
        var user = createUser();
        var userList = createUserList();
        when(userService.getAllUsers()).thenReturn(userList);
        when(userService.getLoggedInUser()).thenReturn(user);

        // when
        var result = mockMvc.perform(post("/matches/add").param("date", LocalDate.now().toString())
                .param("teamAPlayer1.userId", "user")
                .param("teamBPlayer1.userId", "user2"));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("noWinner"))
                .andExpect(model().attribute("currentUser", user))
                .andExpect(model().attribute("users", userList));
        verify(matchService, times(0)).addMatchEntry(any(Match.class));
    }

    @Test
    @DisplayName("When no players have been selected 'nullPlayer' should exist")
    @WithMockUser
    void whenMatchWithNoPlayersThenReturnNullPlayers() throws Exception {
        // given
        var user = createUser();
        var userList = createUserList();
        when(userService.getAllUsers()).thenReturn(userList);
        when(userService.getLoggedInUser()).thenReturn(user);

        // when
        var result = mockMvc
                .perform(post("/matches/add").param("date", LocalDate.now().toString()).param("winnerTeamA", "true"));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("nullPlayer"))
                .andExpect(model().attribute("currentUser", user))
                .andExpect(model().attribute("users", userList));
        verify(matchService, times(0)).addMatchEntry(any(Match.class));
    }

    @Test
    @DisplayName("When only 'teamAPlayer1' has been selected 'nullPlayer' should exist")
    @WithMockUser
    void whenMatchWithNoPlayerB1ThenReturnNullPlayers() throws Exception {
        // given
        var user = createUser();
        var userList = createUserList();
        when(userService.getAllUsers()).thenReturn(userList);
        when(userService.getLoggedInUser()).thenReturn(user);

        // when
        var result = mockMvc.perform(post("/matches/add").param("date", LocalDate.now().toString())
                .param("teamAPlayer1.userId", "user")
                .param("winnerTeamA", "true"));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("nullPlayer"))
                .andExpect(model().attribute("currentUser", user))
                .andExpect(model().attribute("users", userList));
    }

    @Test
    @DisplayName("When only 'teamBPlayer1' has been selected 'nullPlayer' should exist")
    @WithMockUser
    void whenMatchWithNoPlayerA1ThenReturnNullPlayers() throws Exception {
        // given
        var user = createUser();
        var userList = createUserList();
        when(userService.getAllUsers()).thenReturn(userList);
        when(userService.getLoggedInUser()).thenReturn(user);

        // when
        var result = mockMvc.perform(post("/matches/add").param("date", LocalDate.now().toString())
                .param("teamBPlayer1.userId", "user2")
                .param("winnerTeamA", "true"));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("nullPlayer"))
                .andExpect(model().attribute("currentUser", user))
                .andExpect(model().attribute("users", userList));
        verify(matchService, times(0)).addMatchEntry(any(Match.class));
    }

    @Test
    @DisplayName("When a valid match is entered expect 'successMessage' and called service")
    @WithMockUser
    void whenMatchValidThenReturnSuccessMessage() throws Exception {
        // given
        var user = createUser();
        var userList = createUserList();
        when(userService.getAllUsers()).thenReturn(userList);
        when(userService.getLoggedInUser()).thenReturn(user);

        // when
        var result = mockMvc.perform(post("/matches/add").param("date", LocalDate.now().toString())
                .param("teamAPlayer1.userId", "user")
                .param("teamBPlayer1.userId", "user2")
                .param("winnerTeamA", "true"));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("successMessage"))
                .andExpect(model().attribute("currentUser", user))
                .andExpect(model().attribute("users", userList));
        verify(matchService, times(1)).addMatchEntry(any(Match.class));
    }

    @Test
    @DisplayName("When 'FutureDateException' is thrown expect 'futureDate'")
    @WithMockUser
    void expectFutureDate() throws Exception {
        // given
        var user = createUser();
        var userList = createUserList();
        when(userService.getAllUsers()).thenReturn(userList);
        when(userService.getLoggedInUser()).thenReturn(user);
        doThrow(FutureDateException.class).when(matchService).addMatchEntry(any(Match.class));

        // when
        var result = mockMvc.perform(post("/matches/add").param("date", LocalDate.now().plusDays(1).toString())
                .param("teamAPlayer1.userId", "user")
                .param("teamBPlayer1.userId", "user2")
                .param("winnerTeamA", "true"));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("futureDate"))
                .andExpect(model().attribute("currentUser", user))
                .andExpect(model().attribute("users", userList));
    }

    @Test
    @DisplayName("When 'InvalidCreatorException' is thrown expect 'invalidCreator'")
    @WithMockUser
    void expectInvalidCreator() throws Exception {
        // given
        var user = createUser();
        var userList = createUserList();
        when(userService.getAllUsers()).thenReturn(userList);
        when(userService.getLoggedInUser()).thenReturn(user);
        doThrow(InvalidCreatorException.class).when(matchService).addMatchEntry(any(Match.class));

        // when
        var result = mockMvc.perform(post("/matches/add").param("date", LocalDate.now().plusDays(1).toString())
                .param("teamAPlayer1.userId", "user")
                .param("teamBPlayer1.userId", "user2")
                .param("winnerTeamA", "true"));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("invalidCreator"))
                .andExpect(model().attribute("currentUser", user))
                .andExpect(model().attribute("users", userList));
    }

    @Test
    @DisplayName("When 'SamePlayerException' is thrown expect 'samePlayer'")
    @WithMockUser
    void expectSamePlayer() throws Exception {
        // given
        var user = createUser();
        var userList = createUserList();
        when(userService.getAllUsers()).thenReturn(userList);
        when(userService.getLoggedInUser()).thenReturn(user);
        doThrow(SamePlayerException.class).when(matchService).addMatchEntry(any(Match.class));

        // when
        var result = mockMvc.perform(post("/matches/add").param("date", LocalDate.now().plusDays(1).toString())
                .param("teamAPlayer1.userId", "user")
                .param("teamBPlayer1.userId", "user2")
                .param("winnerTeamA", "true"));

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("samePlayer"))
                .andExpect(model().attribute("currentUser", user))
                .andExpect(model().attribute("users", userList));
    }
}