package com.tournaments.tournaments.controllers;

import com.tournaments.tournaments.entities.Team;
import com.tournaments.tournaments.services.TeamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RequestMapping("/api/team")
@RestController
@CrossOrigin(origins = "*")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping
    public ResponseEntity<List<Team>> getAllTeams() {
        return ResponseEntity.ok(teamService.getAllTeams());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Team>> getTeamById(@PathVariable Integer id) {
        return ResponseEntity.ok(teamService.getTeamById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Team> updateTeam(@PathVariable Integer id, @RequestBody Team team) {
        Optional<Team> teamOptional = teamService.getTeamById(id);
        return teamOptional.map(
                a->ResponseEntity.ok().body(a)
        ).orElseGet(
                ()->{ return createTeam(team); }
        );
    }

    @PostMapping
    public ResponseEntity<Team> createdTeam(@RequestBody Team team) {
        return createTeam(team);
    }

    private ResponseEntity<Team> createTeam(Team team) {
        Team newTeam = teamService.createTeam(team);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(newTeam.getId()).toUri();

        return ResponseEntity.created(location).body(newTeam);
    }

    @DeleteMapping("/{id}")
    public void deleteTeam(@PathVariable Integer id) {
        teamService.deleteTeamById(id);
    }
}
