package eng.project.peoplehubdemo.service;

import eng.project.peoplehubdemo.model.ImportInfo;
import eng.project.peoplehubdemo.model.dto.FileInfoDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {
    @InjectMocks
    private FileService fileService;
    @Mock
    private FileLoaderService fileLoaderService;
    @Mock
    private ImportInfoService importInfoService;
    @Mock
    private MultipartFile multipartFile;

    @Captor
    private ArgumentCaptor<Path> pathCaptor;
    @Captor
    private ArgumentCaptor<ImportInfo> importInfoCaptor;

    @TempDir
    Path tempDir;

    @Test
    public void testHandleFileUpload_ShouldReturnFileInfoDto() throws IOException {
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream("test data".getBytes()));
        doNothing().when(fileLoaderService).load(pathCaptor.capture(), importInfoCaptor.capture());
        when(importInfoService.initialize()).thenReturn(new ImportInfo(1, "abcd", "INITIALIZED", LocalDateTime.now(), null, 0));

        FileInfoDto result = fileService.handleFileUpload(multipartFile);

        ImportInfo capturedImportInfo = importInfoCaptor.getValue();
        Path expectedFilePath = tempDir.resolve(capturedImportInfo.getFileId() + ".csv");
        verify(fileLoaderService).load(pathCaptor.capture(), importInfoCaptor.capture());
        assertEquals(capturedImportInfo.getFileId(), result.getFileId());
        assertEquals(expectedFilePath.getFileName().toString(), pathCaptor.getValue().getFileName().toString());

        Files.deleteIfExists(pathCaptor.getValue());
    }
}