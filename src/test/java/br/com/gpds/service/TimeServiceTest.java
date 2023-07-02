package br.com.gpds.service;

import br.com.gpds.domain.TimeEntity;
import br.com.gpds.repository.TimeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimeServiceTest {

    @Mock
    private TimeRepository timeRepository;

    @InjectMocks
    private TimeService timeService;

    @Test
    @DisplayName("Should return an empty list when no team name matches the input")
    void findTeamsByNameContainingWhenNoNameMatches() {
        var name = "TeamA";
        var emptyList = new ArrayList<TimeEntity>();
        when(timeRepository.findAllByNomeContainingIgnoreCase(name)).thenReturn(emptyList);

        var result = timeService.findTeamsByNameContaining(name);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(timeRepository, times(1)).findAllByNomeContainingIgnoreCase(name);
    }

    @Test
    @DisplayName("Should return a list of teams when the name partially matches")
    void findTeamsByNameContainingWhenNamePartiallyMatches() {
        var name = "team";
        var expectedTeams = new ArrayList<TimeEntity>();
        expectedTeams.add(new TimeEntity("Team A"));
        expectedTeams.add(new TimeEntity("Team B"));
        expectedTeams.add(new TimeEntity("Team C"));

        when(timeRepository.findAllByNomeContainingIgnoreCase(name)).thenReturn(expectedTeams);

        var actualTeams = timeService.findTeamsByNameContaining(name);

        assertEquals(expectedTeams.size(), actualTeams.size());
        for (int i = 0; i < expectedTeams.size(); i++) {
            assertEquals(expectedTeams.get(i), actualTeams.get(i));
        }
        verify(timeRepository, times(1)).findAllByNomeContainingIgnoreCase(name);
    }

}
