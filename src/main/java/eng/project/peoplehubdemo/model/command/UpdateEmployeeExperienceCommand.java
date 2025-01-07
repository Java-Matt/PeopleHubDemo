package eng.project.peoplehubdemo.model.command;

import eng.project.peoplehubdemo.validation.SupportedDateOrder;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;


@Data
@Accessors(chain = true)
@SupportedDateOrder
public class UpdateEmployeeExperienceCommand {
    @PastOrPresent(message = "DATE_MUST_BE_IN_PAST_OR_PRESENT")
    private LocalDate startDate;
    @FutureOrPresent(message = "DATE_MUST_BE_IN_FUTURE_OR_PRESENT")
    private LocalDate endDate;
    @NotNull(message = "POSITION_CANNOT_BE_NULL")
    private String position;
    @Positive(message = "SALARY_MUST_BE_POSITIVE")
    private double salary;
}
