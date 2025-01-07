package eng.project.peoplehubdemo.mapper;

import eng.project.peoplehubdemo.model.ImportInfo;
import eng.project.peoplehubdemo.model.dto.ImportInfoDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ImportStatusMapperTest {
    @InjectMocks
    private ImportInfoMapper importInfoMapper;

    private ImportInfo importInfo;

    private String fileNumber;
    private String status;
    private LocalDateTime creationTime;
    private LocalDateTime startTime;
    private int processedRows;

    @BeforeEach
    public void init() {
        fileNumber = UUID.randomUUID().toString();
        status = "COMPLETED";
        creationTime = LocalDateTime.of(2024, 07, 25, 22, 22, 20);
        startTime = LocalDateTime.of(2024, 07, 25, 22, 22, 40);
        processedRows = 100000;
        importInfo = new ImportInfo()
                .setId(1)
                .setStatus(status)
                .setFileId(fileNumber)
                .setStartTime(startTime)
                .setCreationTime(creationTime)
                .setProcessedRows(processedRows);
    }

    @Test
    void testMapFromCommand_ResultsInExperienceBeingMappedFromCommand() {
        ImportInfoDto importInfoDto = importInfoMapper.mapToDto(importInfo);

        assertEquals(1, importInfoDto.getId());
        assertEquals(fileNumber, importInfoDto.getFileId());
        assertEquals(status, importInfoDto.getStatus());
        assertEquals(creationTime, importInfoDto.getCreationTime());
        assertEquals(startTime, importInfoDto.getStartTime());
        assertEquals(processedRows, importInfoDto.getProcessedRows());
    }
}