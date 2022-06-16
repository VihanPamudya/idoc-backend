package com.iterminal.ndis.util;
import com.iterminal.ndis.dto.ErrorResponseDto;
import com.iterminal.exception.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


public class ErrorResponseUtil {
    
    public static ResponseEntity errorResponse(CustomException ex) {

        ErrorResponseDto response = new ErrorResponseDto();
        response.setErrorCode(ex.getErrorCode());
        response.setMessage(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

    }
}
