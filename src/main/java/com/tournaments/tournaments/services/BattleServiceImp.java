package com.tournaments.tournaments.services;

import com.tournaments.tournaments.dto.BattleDTO;
import com.tournaments.tournaments.dto.BattleMapper;
import com.tournaments.tournaments.entities.Battle;
import com.tournaments.tournaments.entities.Phase;
import com.tournaments.tournaments.entities.Trainer;
import com.tournaments.tournaments.repositories.BattleRepository;
import com.tournaments.tournaments.repositories.TournamentRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BattleServiceImp implements BattleService {
    private final BattleRepository battleRepository;
    private final TournamentRepository tournamentRepository;
    private final BattleMapper battleMapper;
    private final PhaseService  phaseService;
    private final TrainerService trainerService;
    private final TournamentRegistrationService tournamentRegistrationService;



    public BattleServiceImp(BattleRepository battleRepository, TournamentRepository tournamentRepository, BattleMapper battleMapper, PhaseService phaseService, TrainerService trainerService, TournamentRegistrationService tournamentRegistrationService) {
        this.battleRepository = battleRepository;
        this.tournamentRepository = tournamentRepository;
        this.tournamentRegistrationService = tournamentRegistrationService;
        this.battleMapper = battleMapper;
        this.phaseService = phaseService;
        this.trainerService = trainerService;
    }

    @Override
    public List<BattleDTO> getAllBattles() {
        return battleRepository.findAll().stream()
                .map(battleMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<BattleDTO> getBattleById(Integer id) {
        return battleRepository.findById(id).map(battleMapper::toDTO);
    }

    @Override
    public Optional<Battle> findBattleById(Integer id) {
        return battleRepository.findById(id);
    }

    @Override
    public BattleDTO createBattle(BattleDTO battleDTO) {
        Battle battle = battleRepository.save(battleMapper.toEntity(battleDTO, phaseService, trainerService));
        return battleMapper.toDTO(battle);
    }

    @Override
    public Optional<BattleDTO> updateBattle(Integer id, BattleDTO battleDTO) {
        Battle battle = battleMapper.toEntity(battleDTO, phaseService, trainerService);
        return battleRepository.findById(id).map(
                battleInDB-> {
                    battleInDB.setWinner(battle.getWinner());
                    battleInDB.setBattleDuration(battle.getBattleDuration());
                    return battleRepository.save(battleInDB);
                }
        ).map(battleMapper::toDTO);
    }

    @Override
    public void deleteBattleById(Integer id) {
        battleRepository.deleteById(id);
    }

    @Override
    public List<BattleDTO> createBattlesByTournamentId(Integer tournamentId) {
        List<Trainer> trainers = tournamentRegistrationService.getRegistrationsByTournamentId(tournamentId);
        Collections.shuffle(trainers);
        Phase phase = phaseService.getPhaseByTournamentId(tournamentId);

        if (battleRepository.existsByPhaseId(phase.getId())) {
            throw new RuntimeException("Battles for phase " + phase.getName() + " already exist.");
        }

        if (trainers.size() == tournamentRepository.getMinParticipantQuantityById(tournamentId)) {
            List<BattleDTO> battleDTOs;

            if (phase.getConsecutiveNumberWithinTournament() == 1) {
                battleDTOs = createFirstRoundBattles(trainers, phase);
            } else {
                battleDTOs = createNextRoundBattles(tournamentId, phase);
            }

            for (BattleDTO battleDTO : battleDTOs) {
                createBattle(battleDTO);
            }
            return battleDTOs;
        } else {
            throw new RuntimeException("No se ha alcanzado la cantidad mínima de participantes");
         }
    }


    @Override
    public List<BattleDTO> getBattlesByTournamentId(Integer tournamentId) {
        Phase phase = phaseService.getPhaseByTournamentId(tournamentId);
        return battleRepository.findByPhaseId(phase.getId()).stream()
                .map(battleMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BattleDTO> getAllBattlesByTournamentId(Integer tournamentId) {
        Phase phase = phaseService.getPhaseByTournamentId(tournamentId);

        List<Battle> existingBattles = battleRepository.findByTournamentId(tournamentId);
        List<Battle> existingBattlesByPhase = battleRepository.findByPhaseIdAndTournamentId(phase.getId(), tournamentId);

        if (existingBattlesByPhase.isEmpty()) {
            createBattlesByTournamentId(tournamentId);
            existingBattles = battleRepository.findByTournamentId(tournamentId);
        }

        return existingBattles.stream()
                .map(battleMapper::toDTO)
                .collect(Collectors.toList());
    }
    private List<BattleDTO> createFirstRoundBattles(List<Trainer> trainers, Phase phase) {
        List<BattleDTO> battles = new ArrayList<>();

        for (int i = 0; i < trainers.size(); i += 2) {
            Trainer trainer1 = trainers.get(i);
            Trainer trainer2 = trainers.get(i + 1);

            Battle battle = new Battle();
            battle.setFirstParticipant(trainer1);
            battle.setSecondParticipant(trainer2);
            battle.setPhase(phase);
            BattleDTO battleDTO = battleMapper.toDTO(battle);
            battles.add(battleDTO);
        }
        return battles;
    }

    private List<BattleDTO> createNextRoundBattles(Integer tournamentId, Phase currentPhase) {
        List<Phase> phases = phaseService.getAllPhasesByTournamentId(tournamentId);
        phases.sort(Comparator.comparingInt(Phase::getConsecutiveNumberWithinTournament));

        int currentIndex = -1;
        for (int i = 0; i < phases.size(); i++) {
            if (phases.get(i).getId().equals(currentPhase.getId())) {
                currentIndex = i;
                break;
            }
        }

        if (currentIndex == -1) {
            throw new IllegalStateException("Current phase not found in the list of phases.");
        }

        Phase previousPhase = phases.get(currentIndex - 1);

        List<Battle> previousBattles = battleRepository.findByPhaseIdAndTournamentId(previousPhase.getId(), tournamentId);

        List<Trainer> winners = previousBattles.stream()
                .sorted(Comparator.comparing(Battle::getId))
                .map(Battle::getWinner)
                .collect(Collectors.toList());
        return createFirstRoundBattles(winners, currentPhase);
    }
}