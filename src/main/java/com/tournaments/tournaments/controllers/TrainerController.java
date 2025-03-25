package com.tournaments.tournaments.controllers;

import com.tournaments.tournaments.dto.TrainerDTO;
import com.tournaments.tournaments.services.TrainerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/trainer")
@CrossOrigin(origins = "*")
public class TrainerController {

    private final TrainerService trainerService;

    public TrainerController(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @GetMapping
    public ResponseEntity<List<TrainerDTO>> getAllTrainers() {
        return ResponseEntity.ok(trainerService.getAllTrainers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<TrainerDTO>> getTrainerById(@PathVariable Integer id) {
        return ResponseEntity.ok(trainerService.getTrainerById(id));
    }

    @PostMapping
    public ResponseEntity<TrainerDTO> createdTrainer(@RequestBody TrainerDTO trainerDTO) {
        return createTrainer(trainerDTO);
    }

    private ResponseEntity<TrainerDTO> createTrainer(TrainerDTO trainerDTO) {
        TrainerDTO newTrainer = trainerService.createTrainer(trainerDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newTrainer.id()).toUri();

        return ResponseEntity.created(location).body(newTrainer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrainerDTO> updatedTrainer(@PathVariable Integer id, @RequestBody TrainerDTO trainerDTO) {
        Optional<TrainerDTO> updatedTrainer = trainerService.updateTrainerById(id, trainerDTO);
        return updatedTrainer.map(
                t->ResponseEntity.ok().body(t)).orElseGet(
                ()->{return createTrainer(trainerDTO);}
        );
    }


    @DeleteMapping("/{id}")
    public void deletedTrainer(@PathVariable Integer id) {
        trainerService.deleteTrainerById(id);
    }

}
