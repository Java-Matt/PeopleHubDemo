package eng.project.peoplehubdemo.repository;

import eng.project.peoplehubdemo.model.ImportInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImportInfoRepository extends JpaRepository<ImportInfo, Integer> {
    Optional<ImportInfo> findByFileId(String fileId);
}
