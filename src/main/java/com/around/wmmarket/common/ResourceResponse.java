package com.around.wmmarket.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@Builder
public class ResourceResponse {
    private final HttpHeaders httpHeaders;
    private final HttpStatus httpStatus;
    private final String message;
    private final Resource resource;
}
