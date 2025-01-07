package eng.project.peoplehubdemo.service;

import eng.project.peoplehubdemo.exception.ClassImportException;
import eng.project.peoplehubdemo.model.FieldType;
import eng.project.peoplehubdemo.model.ImportInfo;
import eng.project.peoplehubdemo.properties.BatchSizeProperties;
import eng.project.peoplehubdemo.strategy.PersonStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class FileLoaderServiceTest {
    @InjectMocks
    private FileLoaderService fileLoaderService;
    @Mock
    private JdbcTemplate jdbcTemplate;
    @Mock
    private ImportInfoService importInfoService;
    @Mock
    private Map<String, PersonStrategy> strategy;
    @Mock
    private BatchSizeProperties batchSizeProperties;

    private Map<String, FieldType> studentMap;
    private Map<String, FieldType> employeeMap;
    private Map<String, FieldType> pensionerMap;

    private Path testFilePath;
    private Path originalTestFilePath;
    private ImportInfo importInfo;

    @BeforeEach
    void init() {
        originalTestFilePath = Paths.get("src/test/resources/test.csv");

        String uniqueFileName = "test-" + UUID.randomUUID() + ".csv";
        testFilePath = Paths.get("src/test/resources", uniqueFileName);

        String fileId = "test-file-id";
        importInfo = new ImportInfo()
                .setFileId(fileId)
                .setStatus("INITIALIZED")
                .setCreationTime(LocalDateTime.now())
                .setProcessedRows(0);

        studentMap = new LinkedHashMap<>();
        studentMap.put("university", FieldType.STRING);
        studentMap.put("yearOfStudy", FieldType.NUMBER);
        studentMap.put("fieldOfStudy", FieldType.STRING);
        studentMap.put("scholarship", FieldType.NUMBER);

        employeeMap = new LinkedHashMap<>();

        pensionerMap = new LinkedHashMap<>();
        pensionerMap.put("pension", FieldType.NUMBER);
        pensionerMap.put("yearsOfWork", FieldType.NUMBER);

        when(batchSizeProperties.getSize()).thenReturn(1000);
    }

    @Test
    void testLoad_ShouldInitializeAndCompleteSuccessfully() throws Exception {
        Files.copy(originalTestFilePath, testFilePath);

        PersonStrategy studentStrategy = mock(PersonStrategy.class);
        when(studentStrategy.getPersonExtensionFields()).thenReturn(studentMap);
        when(strategy.get("STUDENT")).thenReturn(studentStrategy);

        PersonStrategy employeeStrategy = mock(PersonStrategy.class);
        when(employeeStrategy.getPersonExtensionFields()).thenReturn(employeeMap);
        when(strategy.get("EMPLOYEE")).thenReturn(employeeStrategy);

        PersonStrategy pensionerStrategy = mock(PersonStrategy.class);
        when(pensionerStrategy.getPersonExtensionFields()).thenReturn(pensionerMap);
        when(strategy.get("PENSIONER")).thenReturn(pensionerStrategy);

        fileLoaderService.load(testFilePath, importInfo);

        verify(importInfoService, times(1)).start(importInfo);
        verify(importInfoService, atLeastOnce()).updateProcessedRows(anyInt(), eq(importInfo));
        verify(importInfoService, times(1)).completed(anyInt(), eq(importInfo));

        assertFalse(Files.exists(testFilePath));
    }

    @Test
    void testLoadPerformance_ShouldInitializeAndCompleteSuccessfully() throws Exception {
        Files.copy(originalTestFilePath, testFilePath);

        PersonStrategy studentStrategy = mock(PersonStrategy.class);
        when(studentStrategy.getPersonExtensionFields()).thenReturn(studentMap);
        when(strategy.get("STUDENT")).thenReturn(studentStrategy);

        PersonStrategy employeeStrategy = mock(PersonStrategy.class);
        when(employeeStrategy.getPersonExtensionFields()).thenReturn(employeeMap);
        when(strategy.get("EMPLOYEE")).thenReturn(employeeStrategy);

        PersonStrategy pensionerStrategy = mock(PersonStrategy.class);
        when(pensionerStrategy.getPersonExtensionFields()).thenReturn(pensionerMap);
        when(strategy.get("PENSIONER")).thenReturn(pensionerStrategy);

        long startTime = System.currentTimeMillis();
        fileLoaderService.load(testFilePath, importInfo);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        verify(importInfoService, times(1)).start(importInfo);
        verify(importInfoService, atLeastOnce()).updateProcessedRows(anyInt(), eq(importInfo));
        verify(importInfoService, times(1)).completed(100000, importInfo);

        assertTrue(duration <= 4000, "Processing took too long");

        assertFalse(Files.exists(testFilePath));
    }

    @Test
    void testLoad_ShouldHandleFileProcessingFailure() throws Exception {
        Files.copy(originalTestFilePath, testFilePath);

        assertThrows(ClassImportException.class, () -> fileLoaderService.load(testFilePath, importInfo));

        assertFalse(Files.exists(testFilePath));
    }

    @Test
    void testLoad_ShouldSkipEmptyFile() throws Exception {
        Path emptyFilePath = Files.createTempFile("empty-test-file", ".csv");
        String emptyFileContent = "dtype,first_name,last_name,personal_number,height,weight,email\n";
        Files.write(emptyFilePath, emptyFileContent.getBytes());

        fileLoaderService.load(emptyFilePath, importInfo);

        verify(importInfoService, times(1)).start(importInfo);
        verify(importInfoService, times(1)).completed(0, importInfo);
        Files.deleteIfExists(emptyFilePath);
    }
}