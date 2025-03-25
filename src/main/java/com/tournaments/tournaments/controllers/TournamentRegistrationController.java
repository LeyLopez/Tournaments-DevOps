package com.tournaments.tournaments.controllers;

import com.tournaments.tournaments.dto.TrainerDTO;
import com.tournaments.tournaments.entities.Trainer;
import com.tournaments.tournaments.exceptions.TrainerIsRequired;
import com.tournaments.tournaments.services.TournamentRegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tournament/register")
@CrossOrigin(origins = "*")
public class TournamentRegistrationController {
    private final TournamentRegistrationService tournamentRegistrationService;

    public TournamentRegistrationController(TournamentRegistrationService tournamentRegistrationService) {
        this.tournamentRegistrationService = tournamentRegistrationService;
    }

    @PostMapping("/{tournamentId}")
    public ResponseEntity<String> registerTrainer( @PathVariable("tournamentId") Integer tournamentId, @RequestBody Map<String, Integer> request) {
        Integer trainerId = request.get("trainerId");
        if (trainerId == null) {
            throw new TrainerIsRequired("Trainer ID is required");
        }
        tournamentRegistrationService.registerTrainerForTournament(tournamentId, trainerId);
        return ResponseEntity.ok("Trainer registered successfully");
    }
    @GetMapping("/{tournamentId}")
    public ResponseEntity<List<Trainer>> getRegistrationsByTournamentId(@PathVariable("tournamentId") Integer tournamentId) {
        List<Trainer> registrations = tournamentRegistrationService.getRegistrationsByTournamentId(tournamentId);
        return ResponseEntity.ok(registrations);
    }
    @GetMapping("/participants/{tournamentId}")
    public ResponseEntity<List<TrainerDTO>> getParticipantsByTournamentId(@PathVariable("tournamentId") Integer tournamentId) {
        List<TrainerDTO> registrations = tournamentRegistrationService.getParticipantsByTournamentId(tournamentId);
        return ResponseEntity.ok(registrations);
    }
}
