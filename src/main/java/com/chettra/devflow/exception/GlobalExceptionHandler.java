package com.chettra.devflow.exception;

import com.chettra.devflow.config.ApiConstrants;
import com.chettra.devflow.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(ResourceNotFoundException ex){
		
		ApiResponse<Void> response = ApiResponse.<Void>builder().status(HttpStatus.NOT_FOUND.name()).message(ex.getMessage()).build();
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleGeneralExceptions(Exception ex){
		
		ApiResponse<Void> response = ApiResponse.<Void>builder().status(HttpStatus.INTERNAL_SERVER_ERROR.name()).message(ex.getMessage()).build();
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}
}
