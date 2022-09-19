package com.example.study.simple.code.s3;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.modelmapper.internal.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class PresignedUrl {
    private static final Logger LOGGER = LoggerFactory.getLogger(PresignedUrl.class);

    @Value("${cloud.aws.bucket}")
    private String bucket;
    private AmazonS3 amazonS3;
    private final String[] NOT_ALLOWED_STR_LIST = new String[]{"/", ":", "*", "?", "\"", "<", ">", "|", "\\", "%00", "%zz"};
    private final String AVI_CONTENT_TYPE = "video/x-msvideo";
    private final String MP4_CONTENT_TYPE = "video/mp4";
    private final String[] AVAILABLE_CONTENT_TYPE = new String[]{MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE, MediaType.IMAGE_PNG_VALUE, AVI_CONTENT_TYPE, MP4_CONTENT_TYPE};
    private final String[] AVAILABLE_FILE_EXTENSION = new String[]{
            "jpg",
            "jpeg",
            "png",
            "gif"};
    private final long MAX_FILE_SIZE = 5242880L; // 5MB
    private final int PRESIGNED_URL_EXPIRATION_MILLISECONDS = 300000; // 5분
    private final int MAX_FILE_COUNT = 10;

    public PresignedUrl(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public List<String> getPresignedUrl(long boardArticleId, List<MultipartFile> fileList) {
        List<String> getPresignedUrlResponse = new ArrayList<>();

        validateFiles(boardArticleId, fileList);

        fileList.forEach(file -> {
            try {
                String md5Checksum = getMD5Checksum(file);
                String fileName = getFilePathAndName(file);

                LOGGER.info("boardArticleId= {}, file_origin_name={}, file_upload_name={},  file_size={}, file_content_type={}, file_md5_checksum={}, file_md5_checksum_hash_str={}",
                        boardArticleId, file.getOriginalFilename(), fileName, file.getSize() + "bytes", file.getContentType(), md5Checksum, getMD5ChecksumHashString(file));

                GeneratePresignedUrlRequest generatePresignedUrlRequest = makeGeneratePresignedUrlRequest(fileName, file.getContentType(), md5Checksum);

                URL presignedUrl = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);

                getPresignedUrlResponse.add(presignedUrl.toString());
            } catch (IOException | NoSuchAlgorithmException exception) {
                LOGGER.error("board_article_id={}, exception_message={}", boardArticleId, exception.getMessage());
                throw new IllegalStateException("업로드 주소 획득에 실패했습니다.");
            }
        });

        return getPresignedUrlResponse;
    }

    private String getFilePathAndName(MultipartFile file) {
        return file.getOriginalFilename().split("\\.")[1];
    }

    private void validateFiles(long boardArticleId, List<MultipartFile> fileList) {
        Assert.isTrue(!(fileList == null), "파일을 전달해주세요.");
        Assert.isTrue(fileList.size() <= MAX_FILE_COUNT, "최대 10개의 파일만 업로드 할 수 있습니다.");

        // 3. 파일 개별 체크
        fileList.forEach(file -> {
            validateFile(file);
        });
    }

    private void validateFile(MultipartFile file) {
        // 1. 빈 파일인지 확인
        Assert.isTrue(!file.isEmpty(), "파일을 전달해주세요.");

        // 2. 컨텐츠 타입 체크
        Assert.isTrue(Arrays.asList(AVAILABLE_CONTENT_TYPE).contains(file.getContentType()), "파일 형식을 확인해주세요.");

        // 3. 확장자 체크
        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename().trim());
        Assert.isTrue(Arrays.asList(AVAILABLE_FILE_EXTENSION).contains(fileExtension.toLowerCase()), "파일 확장자를 확인해주세요.");

        // 4. 파일명 체크
        String fileName = file.getOriginalFilename().trim();

        for (String s : NOT_ALLOWED_STR_LIST) {
            Assert.isTrue(!fileName.contains(s), "업로드 할 수 없는 파일명이 포함되어 있습니다.");
        }

        // 5. 파일 용량 체크
        Assert.isTrue(file.getSize() < MAX_FILE_SIZE, "업로드 할 수 없는 파일 용량입니다.");
    }

    private String getMD5Checksum(MultipartFile file) throws IOException {
        byte[] content_bytes = IOUtils.toByteArray(file.getInputStream());
        String md5 = new String(Base64.encodeBase64(DigestUtils.md5(content_bytes)));

        return md5;
    }

    private String getMD5ChecksumHashString(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] digest = md5.digest(file.getBytes());
        String hashString = new BigInteger(1, digest).toString(16);
        Base64.encodeBase64String(hashString.getBytes("utf-8"));
        return hashString;
    }

    private GeneratePresignedUrlRequest makeGeneratePresignedUrlRequest(String fileName, String contentType, String md5Checksum) {
        String key = fileName.split("\\.")[1];

        return new GeneratePresignedUrlRequest(this.bucket, key)
                .withMethod(HttpMethod.PUT)
                .withContentType(contentType)
                .withContentMd5(md5Checksum)
                .withKey(key)
                .withExpiration(this.getExpiration(PRESIGNED_URL_EXPIRATION_MILLISECONDS));
    }

    private Date getExpiration(long expirationAsMilliseconds) {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += expirationAsMilliseconds;
        expiration.setTime(expTimeMillis);
        return expiration;
    }
}
