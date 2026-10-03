package com.chettra.devflow.common.response;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@Builder
public class ApiErrorResponse {
	private boolean success;
	private String message;
	private int status;
	private Instant timestamp;
	private String path;
	private String error;
	private Map<String, String> validationErrors;
}
