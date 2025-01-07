package eng.project.peoplehubdemo.service;

import eng.project.peoplehubdemo.exception.ClassImportException;
import eng.project.peoplehubdemo.exception.FileLoadingFailureException;
import eng.project.peoplehubdemo.model.ImportInfo;
import eng.project.peoplehubdemo.properties.BatchSizeProperties;
import eng.project.peoplehubdemo.strategy.PersonStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileLoaderService {
    private static final String BASE_INSERT_QUERY = "INSERT INTO person (dtype, first_name, last_name, personal_number, height, weight, email";

    private final JdbcTemplate jdbcTemplate;
    private final ImportInfoService importInfoService;
    private final BatchSizeProperties batchSizeProperties;
    private final Map<String, PersonStrategy> strategy;

    @Async("importTaskExecutor")
    @Transactional
    public void load(Path filePath, ImportInfo importInfo) {
        int counter = 0;
        int batchSize = batchSizeProperties.getSize();
        log.info(MessageFormat.format("New File Started Loading id={0}", importInfo.getFileId()));

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            long start = System.currentTimeMillis();
            importInfoService.start(importInfo);
            String line;
            reader.readLine();
            List<String[]> batchParams = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                String[] personParams = line.split(",");
                batchParams.add(personParams);

                if (++counter % batchSize == 0) {
                    processBatch(batchParams);
                    batchParams.clear();
                    importInfoService.updateProcessedRows(counter, importInfo);
                }
            }

            if (!batchParams.isEmpty()) {
                processBatch(batchParams);
            }
            importInfoService.completed(counter, importInfo);
            log.info("Completed loading file with fileId=" + importInfo.getFileId() + ", loaded " + counter + " rows, in " + (System.currentTimeMillis() - start) / 1000 + " seconds.");
        } catch (IOException | DataIntegrityViolationException e) {
            importInfoService.failed(counter, importInfo);
            throw new FileLoadingFailureException(MessageFormat
                    .format("File with id={0} failed to load due to {1}", importInfo.getFileId(), e.getCause()));
        } finally {
            deleteFile(filePath, importInfo.getFileId());
        }
    }

    private void processBatch(List<String[]> batchParams) {
        Map<String, List<String[]>> groupedByType = batchParams.stream()
                .collect(Collectors.groupingBy(params -> params[0]));

        for (Map.Entry<String, List<String[]>> entry : groupedByType.entrySet()) {
            String type = entry.getKey();
            List<String[]> personOfType = entry.getValue();
            List<String> uniqueFields = getPersonExtensionFields(type);

            String insertQuery = buildInsertQuery(uniqueFields);
            StringBuilder paramsBuilder = new StringBuilder();

            for (String[] params : personOfType) {
                paramsBuilder.append(buildPersonParams(params, uniqueFields.size()));
            }

            if (!paramsBuilder.isEmpty()) {
                paramsBuilder.setLength(paramsBuilder.length() - 1);
            }

            String finalQuery = String.format(insertQuery, paramsBuilder);
            jdbcTemplate.update(finalQuery);
        }
    }

    private List<String> getPersonExtensionFields(String type) {
        try {
            PersonStrategy fieldStrategy = strategy.get(type);
            return new ArrayList<>(fieldStrategy.getPersonExtensionFields().keySet());
        } catch (Exception e) {
            throw new ClassImportException(MessageFormat.format("Class {0} not found and is not supported in this app version!", type));
        }
    }

    private String buildInsertQuery(List<String> uniqueFields) {
        StringBuilder insertQuery = new StringBuilder(BASE_INSERT_QUERY);
        for (String field : uniqueFields) {
            insertQuery.append(", ")
                    .append(toSqlLanguage(field));
        }
        insertQuery.append(") VALUES %s");
        return insertQuery.toString();
    }

    private String buildPersonParams(String[] params, int uniqueFieldCount) {
        StringBuilder personParams = new StringBuilder("(");
        for (int i = 0; i < 7 + uniqueFieldCount; i++) {
            personParams.append("'")
                    .append(params[i].replace("'", "''"))
                    .append("'")
                    .append(i < 6 + uniqueFieldCount ? ", " : "");
        }
        personParams.append("),");
        return personParams.toString();
    }

    private String toSqlLanguage(String fieldName) {
        return fieldName.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }

    private void deleteFile(Path filePath, String fileId) {
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new FileLoadingFailureException(MessageFormat
                    .format("File {0} failed to delete due to {1}", fileId, e.getLocalizedMessage()));
        }
    }
}
