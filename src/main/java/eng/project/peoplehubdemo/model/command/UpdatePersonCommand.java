package eng.project.peoplehubdemo.model.command;

import eng.project.peoplehubdemo.validation.SupportedParam;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Accessors(chain = true)
public class UpdatePersonCommand {

    private Integer version;

    @SupportedParam
    private Map<String, String> params;
}
