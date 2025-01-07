package eng.project.peoplehubdemo.service;

import eng.project.peoplehubdemo.exception.FileLoadingFailureException;
import eng.project.peoplehubdemo.model.ImportInfo;
import eng.project.peoplehubdemo.model.dto.FileInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileLoaderService fileLoaderService;
    private final ImportInfoService importInfoService;

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_IMPORTER')")
    public FileInfoDto handleFileUpload(MultipartFile file) {
        ImportInfo importInfo = importInfoService.initialize();
        Path tempFilePath = saveFileTemporarilyToResourcesDirectory(file, importInfo.getFileId(), Paths.get("src/main/resources"));
        FileInfoDto toReturn = new FileInfoDto(importInfo.getFileId());

        fileLoaderService.load(tempFilePath, importInfo);
        return toReturn;
    }

    private Path saveFileTemporarilyToResourcesDirectory(MultipartFile file, String fileId, Path directoryPath) {
        try {
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }
            Path tempFilePath = directoryPath.resolve(fileId + ".csv");
            Files.copy(file.getInputStream(), tempFilePath);
            return tempFilePath;
        } catch (IOException e) {
            throw new FileLoadingFailureException(MessageFormat
                    .format("File failed to save due to {0}", e.getCause()));
        }
    }
}
