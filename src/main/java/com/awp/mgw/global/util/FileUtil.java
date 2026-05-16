package com.awp.mgw.global.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Component
public class FileUtil {

    private static final String UPLOAD_ROOT = System.getProperty("user.home") + "/mgw/uploads";

    /**
     * 파일을 지정된 하위 디렉토리에 저장합니다.
     * @param file 저장할 파일
     * @param subDir 하위 디렉토리명 (예: "activities", "members")
     * @return 저장된 파일의 상대 경로 (예: "activities/uuid.png")
     */
    public String saveFile(MultipartFile file, String subDir) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            Path uploadDir = Path.of(UPLOAD_ROOT, subDir);
            Files.createDirectories(uploadDir);

            String originalFilename = file.getOriginalFilename();
            String extension = resolveExtension(originalFilename);
            String savedFilename = UUID.randomUUID() + extension;

            Path targetPath = uploadDir.resolve(savedFilename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            
            // 데이터베이스 저장 및 접근을 위해 subDir을 포함한 경로 반환
            return subDir + "/" + savedFilename;
        } catch (IOException e) {
            throw new IllegalStateException("파일 저장에 실패했습니다.", e);
        }
    }

    private String resolveExtension(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            return "";
        }

        int extensionIndex = originalFilename.lastIndexOf('.');
        if (extensionIndex < 0 || extensionIndex == originalFilename.length() - 1) {
            return "";
        }

        return originalFilename.substring(extensionIndex);
    }
}
