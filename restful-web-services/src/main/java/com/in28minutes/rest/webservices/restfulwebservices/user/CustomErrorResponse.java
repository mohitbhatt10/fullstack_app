package com.in28minutes.rest.webservices.restfulwebservices.user;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CustomErrorResponse {

    private String errorMessage;
    private String errorCode;
    private LocalDateTime timestamp;

}