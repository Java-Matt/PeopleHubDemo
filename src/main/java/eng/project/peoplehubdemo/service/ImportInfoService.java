package eng.project.peoplehubdemo.service;

import eng.project.peoplehubdemo.exception.FileImportInfoNotFoundException;
import eng.project.peoplehubdemo.exception.FileInfoWithFileNumberAlreadyExistsException;
import eng.project.peoplehubdemo.mapper.ImportInfoMapper;
import eng.project.peoplehubdemo.model.ImportInfo;
import eng.project.peoplehubdemo.model.dto.ImportInfoDto;
import eng.project.peoplehubdemo.repository.ImportInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImportInfoService {
    private final ImportInfoRepository importInfoRepository;
    private final ImportInfoMapper importInfoMapper;

    @PreAuthorize("hasRole('ROLE_ADMIN')or hasRole('ROLE_IMPORTER')")
    public ImportInfoDto getImportInfo(String fileId) {
        return importInfoMapper.mapToDto(importInfoRepository.findByFileId(fileId)
                .orElseThrow(() -> new FileImportInfoNotFoundException(MessageFormat
                        .format("File Import Status with id={0}, not found", fileId))));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ImportInfo initialize() {
        String fileId = UUID.randomUUID().toString();
        ImportInfo importInfo = new ImportInfo()
                .setFileId(fileId)
                .setCreationTime(LocalDateTime.now())
                .setStartTime(null)
                .setStatus("INITIALIZED")
                .setProcessedRows(0);
        try {
            return importInfoRepository.save(importInfo);
        } catch (DataIntegrityViolationException e) {
            throw new FileInfoWithFileNumberAlreadyExistsException(MessageFormat
                    .format("ImportInfo with FileNumber={0} Already Exists In DataBase", importInfo.getFileId()));
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void start(ImportInfo importInfo) {
        importInfo.setStartTime(LocalDateTime.now());
        importInfo.setStatus("STARTED");
        importInfoRepository.save(importInfo);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateProcessedRows(int processedRows, ImportInfo importInfo) {
        importInfo.setProcessedRows(processedRows);
        importInfoRepository.save(importInfo);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void completed(int processedRows, ImportInfo importInfo) {
        importInfo.setProcessedRows(processedRows);
        importInfo.setStatus("COMPLETED");
        importInfoRepository.save(importInfo);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void failed(int processedRows, ImportInfo importInfo) {
        importInfo.setProcessedRows(processedRows);
        importInfo.setStatus("FAILED");
        importInfoRepository.save(importInfo);
    }
}
