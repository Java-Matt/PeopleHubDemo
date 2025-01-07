package eng.project.peoplehubdemo.mapper;

import eng.project.peoplehubdemo.model.ImportInfo;
import eng.project.peoplehubdemo.model.dto.ImportInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImportInfoMapper {
    public ImportInfoDto mapToDto(ImportInfo importInfo) {
        return new ImportInfoDto()
                .setId(importInfo.getId())
                .setFileId(importInfo.getFileId())
                .setStatus(importInfo.getStatus())
                .setCreationTime(importInfo.getCreationTime())
                .setStartTime(importInfo.getStartTime())
                .setProcessedRows(importInfo.getProcessedRows());
    }
}
