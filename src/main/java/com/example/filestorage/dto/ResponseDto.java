package com.example.filestorage.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ResponseDto {

    private final Boolean success;

    private final String error;
}
