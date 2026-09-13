package com.chettra.devflow.common;

import java.time.LocalDateTime;

public class ApiResponse <T> {
    private T data;
    private String message;
    private Boolean success;
    private LocalDateTime timestamp;
}
