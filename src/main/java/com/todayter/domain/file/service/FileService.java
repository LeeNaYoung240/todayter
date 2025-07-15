package com.todayter.domain.file.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.todayter.domain.file.entity.File;
import com.todayter.domain.file.repository.FileRepository;
import com.todayter.global.exception.CustomException;
import com.todayter.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FileService {

    private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024;
    private static final long MAX_VIDEO_SIZE = 200 * 1024 * 1024;

    private static final String JPG = "jpg";
    private static final String JPEG = "jpeg";
    private static final String PNG = "png";
    private static final String HEIC = "heic";
    private static final String AVI = "avi";
    private static final String MP4 = "mp4";
    private static final String GIF = "gif";

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;
    private final FileRepository fileRepository;

    public List<File> uploadFile(List<MultipartFile> multipartFiles) {
        if (multipartFiles == null) {
            throw new CustomException(ErrorCode.NULL_MULTIPART_FILES_EXCEPTION);
        }

        List<File> fileEntities = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            String originalFilename = multipartFile.getOriginalFilename();
            String fileName = createFileName(originalFilename);
            String extension = getFileExtension(originalFilename);

            validateFileSize(multipartFile.getSize(), extension);

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(multipartFile.getSize());
            objectMetadata.setContentType(multipartFile.getContentType());

            try (InputStream inputStream = multipartFile.getInputStream()) {
                amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata));
            } catch (IOException e) {
                throw new CustomException(ErrorCode.PUT_OBJECT_EXCEPTION);
            }

            String fileUrl = amazonS3.getUrl(bucket, fileName).toString();

            File fileEntity = new File(fileName, fileUrl, multipartFile.getContentType(), multipartFile.getSize());
            fileRepository.save(fileEntity);
            fileEntities.add(fileEntity);
        }

        return fileEntities;
    }

    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    private String getFileExtension(String fileName) {
        try {
            validateFileExtension(fileName);
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new CustomException(ErrorCode.FILE_NAME_INVALID);
        }
    }

    private void validateFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            throw new CustomException(ErrorCode.EXTENSION_IS_EMPTY);
        }

        String extension = filename.substring(lastDotIndex + 1).toLowerCase();
        List<String> allowedExtensionList = Arrays.asList(JPG, JPEG, PNG, HEIC, AVI, MP4, GIF);

        if (!allowedExtensionList.contains(extension)) {
            throw new CustomException(ErrorCode.EXTENSION_INVALID);
        }
    }

    private void validateFileSize(long size, String extension) {
        if (isImageFile(extension) && size > MAX_IMAGE_SIZE) {
            throw new IllegalArgumentException("이미지 파일 크기는 10MB를 초과할 수 없습니다.");
        } else if (isVideoFile(extension) && size > MAX_VIDEO_SIZE) {
            throw new IllegalArgumentException("비디오 파일 크기는 200MB를 초과할 수 없습니다.");
        }
    }

    private boolean isImageFile(String extension) {
        return extension.equals(JPEG) || extension.equals(JPG) || extension.equals(PNG) || extension.equals(HEIC);
    }

    private boolean isVideoFile(String extension) {
        return extension.equals(MP4) || extension.equals(AVI) || extension.equals(GIF);
    }

    public void deleteFile(String fileUrl) {
        String key = getKeyFromFileAddress(fileUrl);

        try {
            amazonS3.deleteObject(new DeleteObjectRequest(bucket, key));
            fileRepository.deleteByFileUrl(fileUrl);
        } catch (Exception e) {
            log.error("Error occurred while deleting the file", e);
            throw new CustomException(ErrorCode.IO_EXCEPTION_ON_IMAGE_DELETE);
        }
    }

    private String getKeyFromFileAddress(String fileAddress) {
        try {
            URL url = new URL(fileAddress);
            String decodingKey = URLDecoder.decode(url.getPath(), StandardCharsets.UTF_8);
            return decodingKey.substring(1);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new CustomException(ErrorCode.IO_EXCEPTION_ON_IMAGE_DELETE);
        }
    }

    public File findFileByUrl(String fileUrl) {
        return fileRepository.findByFileUrl(fileUrl)
                .orElseThrow(() -> new CustomException(ErrorCode.EXTENSION_IS_EMPTY));
    }
}
