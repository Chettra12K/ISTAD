package com.chettra.devflow.common.exception;

import com.chettra.devflow.common.response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	/**
	 * Handles @Valid request body validation errors.
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(
			MethodArgumentNotValidException exception,
			HttpServletRequest request) {
		
		Map<String, String> validationErrors = new LinkedHashMap<>();
		
		exception.getBindingResult()
				.getFieldErrors()
				.forEach(error ->
						validationErrors.put(
								error.getField(),
								error.getDefaultMessage()
						)
				);
		
		ApiErrorResponse response = ApiErrorResponse.builder()
				.success(false)
				.status(HttpStatus.BAD_REQUEST.value())
				.error(HttpStatus.BAD_REQUEST.getReasonPhrase())
				.message("Validation failed")
				.path(request.getRequestURI())
				.timestamp(Instant.now())
				.validationErrors(validationErrors)
				.build();
		
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(response);
	}
	
	/**
	 * Handles validation errors from method parameters,
	 * for example @Min, @Max, @Positive, etc.
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiErrorResponse> handleConstraintViolation(
			ConstraintViolationException exception,
			HttpServletRequest request) {
		
		Map<String, String> validationErrors = new LinkedHashMap<>();
		
		exception.getConstraintViolations()
				.forEach(violation ->
						validationErrors.put(
								violation.getPropertyPath().toString(),
								violation.getMessage()
						)
				);
		
		ApiErrorResponse response = ApiErrorResponse.builder()
				.success(false)
				.status(HttpStatus.BAD_REQUEST.value())
				.error(HttpStatus.BAD_REQUEST.getReasonPhrase())
				.message("Validation failed")
				.path(request.getRequestURI())
				.timestamp(Instant.now())
				.validationErrors(validationErrors)
				.build();
		
		return ResponseEntity
				.status(HttpStatus.BAD_REQUEST)
				.body(response);
	}
	
	/**
	 * Handles unexpected exceptions.
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleException(
			Exception exception,
			HttpServletRequest request) {
		
		ApiErrorResponse response = ApiErrorResponse.builder()
				.success(false)
				.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
				.error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
				.message("Something went wrong, please try again later.")
				.path(request.getRequestURI())
				.timestamp(Instant.now())
				.build();
		
		return ResponseEntity
				.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(response);
	}
}