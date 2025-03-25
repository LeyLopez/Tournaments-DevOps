package com.tournaments.tournaments.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalException {

    @ExceptionHandler(value = TrainerIsRequired.class)
    public ResponseEntity<ErrorMessage> trainerHandler(TrainerIsRequired ex, WebRequest wr){
        ErrorMessage error = new ErrorMessage();
        error.setStatus(500);
        error.setMessage(ex.getMessage());
        error.setTimeStamp(LocalDateTime.now());
        return ResponseEntity.status(500).body(error);

    }

    @ExceptionHandler(value = TrainerAlreadyRegisterException.class)
    public ResponseEntity<ErrorMessage> emailDuplicatedHandler(TrainerAlreadyRegisterException ex, WebRequest wr){
        ErrorMessage error = new ErrorMessage();
        error.setStatus(500);
        error.setMessage(ex.getMessage());
        error.setTimeStamp(LocalDateTime.now());
        return ResponseEntity.status(500).body(error);
    }


    @ExceptionHandler(value = TournamentFullException.class)
    public ResponseEntity<ErrorMessage> tournamentHandler(TournamentFullException ex, WebRequest wr){
        ErrorMessage error = new ErrorMessage();
        error.setStatus(500);
        error.setMessage(ex.getMessage());
        error.setTimeStamp(LocalDateTime.now());
        return ResponseEntity.status(500).body(error);

    }
}
