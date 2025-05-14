package com.sb02.fms;

public record FileResponseDto(
        Long id,
        String fileName,
        String description,
        String contentType,
        String url,
        Long size
) {
}
