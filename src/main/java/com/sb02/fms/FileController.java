package com.sb02.fms;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@Slf4j
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileResponseDto> uploadFile(
        @RequestParam("file") MultipartFile file,
        @RequestParam("description") String description
    ) {
        log.info("Uploading file {}", file.getOriginalFilename());

        FileResponseDto fileResponseDto = fileService.uploadFile(file, description);

        return ResponseEntity.ok(fileResponseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FileResponseDto> getFile(@PathVariable Long id) {
        log.info("Getting file with id {}", id);

        FileResponseDto fileResponseDto = fileService.getFileById(id);

        return ResponseEntity.ok(fileResponseDto);
    }
}
