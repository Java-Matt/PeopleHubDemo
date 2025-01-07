package eng.project.peoplehubdemo.service;

import eng.project.peoplehubdemo.exception.FileImportInfoNotFoundException;
import eng.project.peoplehubdemo.mapper.ImportInfoMapper;
import eng.project.peoplehubdemo.model.ImportInfo;
import eng.project.peoplehubdemo.model.dto.ImportInfoDto;
import eng.project.peoplehubdemo.repository.ImportInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImportInfoServiceTest {
    @InjectMocks
    private ImportInfoService importInfoService;
    @Mock
    private ImportInfoRepository importInfoRepository;
    @Mock
    private ImportInfoMapper importInfoMapper;

    @Captor
    private ArgumentCaptor<ImportInfo> importInfoCaptor;

    private ImportInfo importInfo;
    private ImportInfoDto importInfoDto;
    private String fileId;

    @BeforeEach
    void init() {
        fileId = "541179a1-6e47-4c23-b838-85d8b46ebf72";
        importInfo = new ImportInfo()
                .setFileId(fileId)
                .setStatus("INITIALIZED")
                .setCreationTime(LocalDateTime.now())
                .setProcessedRows(0);

        importInfoDto = new ImportInfoDto()
                .setFileId(fileId)
                .setStatus("INITIALIZED")
                .setCreationTime(LocalDateTime.now())
                .setProcessedRows(0)
                .setStartTime(null);
    }

    @Test
    public void testGetImportInfo_ShouldReturnImportInfoDto() {
        when(importInfoRepository.findByFileId(fileId)).thenReturn(Optional.of(importInfo));
        when(importInfoMapper.mapToDto(importInfo)).thenReturn(importInfoDto);

        ImportInfoDto result = importInfoService.getImportInfo(fileId);

        assertEquals(importInfoDto, result);
    }

    @Test
    public void testGetImportInfo_ShouldThrowException_WhenNotFound() {
        when(importInfoRepository.findByFileId(fileId)).thenReturn(Optional.empty());

        assertThrows(FileImportInfoNotFoundException.class, () -> importInfoService.getImportInfo(fileId));
    }

    @Test
    public void testInitialize_ShouldReturnSavedImportInfo() {
        when(importInfoRepository.save(any(ImportInfo.class))).thenReturn(importInfo);

        ImportInfo result = importInfoService.initialize();

        assertEquals(importInfo, result);
    }

    @Test
    public void testInitialize_ShouldThrowException_WhenFileNumberAlreadyExists() {
        when(importInfoRepository.save(any(ImportInfo.class))).thenThrow(DataIntegrityViolationException.class);

        assertThrows(RuntimeException.class, () -> importInfoService.initialize());
    }

    @Test
    void testStart_ShouldUpdateStartTimeAndStatus() {
        importInfo.setStatus("INITIALIZED");
        when(importInfoRepository.save(any(ImportInfo.class))).thenReturn(importInfo);

        importInfoService.start(importInfo);

        verify(importInfoRepository).save(importInfoCaptor.capture());
        assertEquals("STARTED", importInfoCaptor.getValue().getStatus());
        assertNotNull(importInfoCaptor.getValue().getStartTime());
    }

    @Test
    void testUpdateProcessedRows_ShouldUpdateProcessedRows() {
        when(importInfoRepository.save(any(ImportInfo.class))).thenReturn(importInfo);

        importInfoService.updateProcessedRows(100, importInfo);

        verify(importInfoRepository).save(importInfoCaptor.capture());
        assertEquals(100, importInfoCaptor.getValue().getProcessedRows());
    }

    @Test
    void testCompleted_ShouldUpdateStatusAndProcessedRows() {
        when(importInfoRepository.save(any(ImportInfo.class))).thenReturn(importInfo);

        importInfoService.completed(100, importInfo);

        verify(importInfoRepository).save(importInfoCaptor.capture());
        assertEquals(100, importInfoCaptor.getValue().getProcessedRows());
        assertEquals("COMPLETED", importInfoCaptor.getValue().getStatus());
    }

    @Test
    void testFailed_ShouldUpdateStatusAndProcessedRows() {
        when(importInfoRepository.save(any(ImportInfo.class))).thenReturn(importInfo);

        importInfoService.failed(50, importInfo);

        verify(importInfoRepository).save(importInfoCaptor.capture());
        assertEquals(50, importInfoCaptor.getValue().getProcessedRows());
        assertEquals("FAILED", importInfoCaptor.getValue().getStatus());
    }
}