package eng.project.peoplehubdemo.repository;

import eng.project.peoplehubdemo.model.Employee;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface EmployeeRepository extends PersonRepository<Employee> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Employee> findWithLockingById(int id);
}
