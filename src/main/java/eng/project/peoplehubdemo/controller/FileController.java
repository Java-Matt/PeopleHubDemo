package eng.project.peoplehubdemo.controller;

import eng.project.peoplehubdemo.model.dto.FileInfoDto;
import eng.project.peoplehubdemo.model.dto.ImportInfoDto;
import eng.project.peoplehubdemo.service.FileService;
import eng.project.peoplehubdemo.service.ImportInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class FileController {

    private final ImportInfoService importInfoService;
    private final FileService fileService;

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public FileInfoDto loadFromFile(@RequestParam MultipartFile file) {
        return fileService.handleFileUpload(file);
    }

    @GetMapping("/{fileNumber}")
    public ImportInfoDto getStatus(@PathVariable String fileNumber) {
        return importInfoService.getImportInfo(fileNumber);
    }
}
