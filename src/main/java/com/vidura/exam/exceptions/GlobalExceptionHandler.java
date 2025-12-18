package com.vidura.exam.exceptions;

import com.vidura.exam.dto.GlobalDTO;
import com.vidura.exam.dto.response.ServerResponse;
import com.vidura.exam.dto.response.ServerStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalDTO> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        GlobalDTO globalDTO = new GlobalDTO();
        globalDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
        globalDTO.setStatus(ServerStatus.ERROR);
        globalDTO.setMessage(errors.values().toArray()[0].toString());
        System.out.println(globalDTO);
        return ResponseEntity.ok(globalDTO);
    }
}