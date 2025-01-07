package eng.project.peoplehubdemo.controller;

import eng.project.peoplehubdemo.model.ImportInfo;
import eng.project.peoplehubdemo.repository.ImportInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@EnableAsync
class FileControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ImportInfoRepository importInfoRepository;

    private MockMultipartFile testFile;
    private MockMultipartFile badTestFile;

    @BeforeEach
    void init() throws IOException {
        Path originalPath = Paths.get("src/test/resources/test.csv");
        Path tempFilePath = Files.createTempFile("test_copy", ".csv");
        Files.copy(originalPath, tempFilePath, StandardCopyOption.REPLACE_EXISTING);
        testFile = new MockMultipartFile(
                "file",
                "test.csv",
                MediaType.TEXT_PLAIN_VALUE,
                Files.newInputStream(tempFilePath)
        );

        Path badFilePath = Paths.get("src/test/resources/badFile.csv");
        Path tempBadFilePath = Files.createTempFile("bad_data_copy", ".csv");
        Files.copy(badFilePath, tempBadFilePath, StandardCopyOption.REPLACE_EXISTING);
        badTestFile = new MockMultipartFile(
                "file",
                "bad_data.csv",
                MediaType.TEXT_PLAIN_VALUE,
                Files.newInputStream(tempBadFilePath)
        );
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "IMPORTER"})
    void shouldHandleFileUploadSuccessfully() throws Exception {
        int initialRowCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM person", Integer.class);

        mockMvc.perform(multipart("/api/files")
                        .file(testFile))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.fileId").exists())
                .andReturn();

        boolean dataLoaded = waitForDataToBeLoaded(100000, 4000);
        int finalRowCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM person", Integer.class);
        assertTrue(dataLoaded);
        assertTrue(initialRowCount < finalRowCount);
        assertEquals(0, initialRowCount);
        assertEquals(100000, finalRowCount);
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "IMPORTER"})
    void shouldRollbackWhenProcessingFails() throws Exception {
        int initialRowCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM person", Integer.class);

        mockMvc.perform(multipart("/api/files")
                        .file(badTestFile))
                .andExpect(status().isAccepted())
                .andReturn();

        Thread.sleep(2000);

        int finalRowCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM person", Integer.class);

        assertEquals(initialRowCount, finalRowCount);
        assertEquals(0, initialRowCount);
        assertEquals(0, finalRowCount);
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "IMPORTER"})
    void testGetStatus_ReturnsImportInfo() throws Exception {
        String fileId = "541179a1-6e47-4c23-b838-85d8b46ebf72";
        String status = "COMPLETED";
        int processedRows = 10000;
        ImportInfo importInfo = new ImportInfo()
                .setStatus(status)
                .setProcessedRows(processedRows)
                .setFileId(fileId);

        importInfoRepository.save(importInfo);

        mockMvc.perform(get("/api/files/{fileId}", fileId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileId").value(fileId))
                .andExpect(jsonPath("$.status").value(status))
                .andExpect(jsonPath("$.processedRows").value(processedRows));
    }

    private boolean waitForDataToBeLoaded(int expectedCount, int timeoutMillis) throws InterruptedException {
        int attempts = 0;
        int maxAttempts = timeoutMillis / 100;
        while (attempts < maxAttempts) {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM person", Integer.class);
            if (count != null && count == expectedCount) {
                return true;
            }
            Thread.sleep(100);
            attempts++;
        }
        return false;
    }
}