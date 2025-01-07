package eng.project.peoplehubdemo.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
@Data
@Accessors(chain = true)
public class ImportInfoDto {
    private int id;
    private String fileId;
    private String status;
    private LocalDateTime creationTime;
    private LocalDateTime startTime;
    private int processedRows;
}
