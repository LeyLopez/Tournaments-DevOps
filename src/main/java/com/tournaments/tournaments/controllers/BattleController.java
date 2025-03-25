package com.tournaments.tournaments.controllers;

import com.tournaments.tournaments.dto.BattleDTO;
import com.tournaments.tournaments.services.BattleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tournament/matches")
@CrossOrigin(origins = "*")
public class BattleController {
    private final BattleService battleService;

    public BattleController(BattleService battleService) {
        this.battleService = battleService;
    }

    @PostMapping("/{tournamentId}")
    public ResponseEntity<List<BattleDTO>> generateMatchUps(@PathVariable("tournamentId") Integer tournamentId) {
        List<BattleDTO> battles = battleService.createBattlesByTournamentId(tournamentId);
        return ResponseEntity.ok(battles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<BattleDTO>> getMatchUps(@PathVariable("id") Integer id) {
        List<BattleDTO> battle = battleService.getAllBattlesByTournamentId(id);
        return ResponseEntity.ok(battle);
    }

    @PutMapping(("/{id}"))
    public ResponseEntity<BattleDTO> updateMatchUps(@PathVariable("id") Integer id, @RequestBody BattleDTO battle) {
        return ResponseEntity.ok(battleService.updateBattle(id, battle).orElse(null));
    }
}