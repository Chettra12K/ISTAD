package com.chettra.devflow.controller;

import com.chettra.devflow.config.ApiConstrants;
import com.chettra.devflow.dto.response.ApiResponse;
import com.chettra.devflow.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConstrants.API_V1)
public class DevFlowController {
	
	@GetMapping("/hello")
	public ResponseEntity<ApiResponse<String>> hello() {
		ApiResponse<String> response = ApiResponse.<String>builder().status("success").message("DevFlow API is running").data("OK").build();
		
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/test-error")
	public ResponseEntity<ApiResponse<String>> testError() {
		throw new ResourceNotFoundException("Resource not found");
	}
}
