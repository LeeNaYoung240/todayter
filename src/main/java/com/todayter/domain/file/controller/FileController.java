package com.todayter.domain.file.controller;

import com.todayter.domain.file.dto.FileResponseDto;
import com.todayter.domain.file.entity.File;
import com.todayter.domain.file.service.FileService;
import com.todayter.global.dto.CommonResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FileController {

    private final FileService fileService;

    @PostMapping(value = "/upload-to-s3", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommonResponseDto<List<FileResponseDto>>> uploadImageToS3(@RequestPart("file") MultipartFile file) {

        List<File> files = fileService.uploadFile(List.of(file));
        List<FileResponseDto> response = files.stream()
                .map(FileResponseDto::new)
                .toList();

        return ResponseEntity.ok(new CommonResponseDto<>(200, "업로드 완료", response));
    }
}
