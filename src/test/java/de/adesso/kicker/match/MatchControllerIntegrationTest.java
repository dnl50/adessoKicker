package de.adesso.kicker.match;

import de.adesso.kicker.email.EmailService;
import de.adesso.kicker.events.match.MatchVerificationSentEvent;
import de.adesso.kicker.match.persistence.MatchRepository;
import de.adesso.kicker.notification.persistence.NotificationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@TestPropertySource(locations = "classpath:application-integration-test.properties")
@SpringBootTest
@AutoConfigureMockMvc
class MatchControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @MockBean
    private EmailService emailService;

    @Test
    @DisplayName("Assures that adding a match works through all layers")
    @WithMockUser(username = "user1")
    void addMatchWorksThroughAllLayers() throws Exception {
        // given
        doNothing().when(emailService).sendVerification(any(MatchVerificationSentEvent.class));

        // when
        mockMvc.perform(post("/matches/add").with(csrf())
                .param("date", LocalDate.now().toString())
                .param("teamAPlayer1.userId", "user1")
                .param("teamAPlayer1.firstName", "user1")
                .param("teamAPlayer1.lastName", "user1")
                .param("teamAPlayer1.email", "user1@mail")
                .param("teamAPlayer1.wins", "0")
                .param("teamAPlayer1.losses", "0")
                .param("teamAPlayer1.ranking.rankingId", "1")
                .param("teamAPlayer1.ranking.rating", "1500")
                .param("teamBPlayer1.userId", "user2")
                .param("teamBPlayer1.firstName", "user2")
                .param("teamBPlayer1.lastName", "user2")
                .param("teamBPlayer1.email", "user2@mail")
                .param("teamBPlayer1.wins", "0")
                .param("teamBPlayer1.losses", "0")
                .param("teamBPlayer1.ranking.rankingId", "2")
                .param("teamBPlayer1.ranking.rating", "1500")
                .param("winnerTeamA", "true"));

        // then
        var match = matchRepository.findById("1");
        var notification = notificationRepository.findById(1L);
        assertTrue(match.isPresent());
        assertTrue(notification.isPresent());
        assertEquals("user1", match.get().getTeamAPlayer1().getUserId());
        assertEquals("user2", match.get().getTeamBPlayer1().getUserId());
        assertEquals(match.get().getTeamAPlayer1(), notification.get().getSender());
        verify(emailService, times(1)).sendVerification(any(MatchVerificationSentEvent.class));
    }
}
