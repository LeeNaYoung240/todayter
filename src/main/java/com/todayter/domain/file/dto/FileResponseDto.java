package com.todayter.domain.file.dto;

import com.todayter.domain.file.entity.File;
import lombok.Getter;

@Getter
public class FileResponseDto {

    private String fileName;
    private String fileUrl;
    private String fileType;
    private long fileSize;

    public FileResponseDto(File file) {
        this.fileName = file.getFileName();
        this.fileUrl = file.getFileUrl();
        this.fileType = file.getFileType();
        this.fileSize = file.getFileSize();
    }
}
