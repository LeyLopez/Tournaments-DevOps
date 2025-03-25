package com.tournaments.tournaments.controllers;

import com.tournaments.tournaments.dto.PhaseDTO;
import com.tournaments.tournaments.repositories.BattleRepository;
import com.tournaments.tournaments.services.PhaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/phase")
@CrossOrigin(origins = "*")
public class PhaseController {
    private final PhaseService phaseService;

    public PhaseController(PhaseService phaseService) {
        this.phaseService = phaseService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<PhaseDTO>> getPhaseById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(phaseService.getPhaseById(id));
    }

    @GetMapping("/tournament/{tournamentId}")
    public ResponseEntity<PhaseDTO> getPhaseByTournament(@PathVariable("tournamentId") Integer id) {
        return ResponseEntity.ok(phaseService.getPhaseDTOByTournamentId(id));
    }

    @GetMapping
    public ResponseEntity<List<PhaseDTO>> getAllPhases() {
        return ResponseEntity.ok(phaseService.getAllPhases());
    }

    @PostMapping
    public ResponseEntity<PhaseDTO> createdPhase(@RequestBody PhaseDTO phaseDTO) {
        return createPhase(phaseDTO);
    }

    private ResponseEntity<PhaseDTO> createPhase(PhaseDTO phaseDTO) {
        PhaseDTO newPhase = phaseService.createPhase(phaseDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newPhase.id()).toUri();

        return ResponseEntity.created(location).body(newPhase);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PhaseDTO> updatePhase(@PathVariable("id") Integer id, @RequestBody PhaseDTO phaseDTO) {
        Optional<PhaseDTO> phaseUpdated = phaseService.updatePhaseById(id, phaseDTO);
        return phaseUpdated.map(ResponseEntity::ok)
                .orElseGet(()->
                        createPhase(phaseDTO));

    }

    @DeleteMapping("/{id}")
    public void deletePhase(@PathVariable("id") Integer id) {
        phaseService.deletePhaseById(id);
    }

}
