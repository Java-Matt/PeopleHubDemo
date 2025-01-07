package eng.project.peoplehubdemo.model.command;

import eng.project.peoplehubdemo.validation.SupportedParam;
import eng.project.peoplehubdemo.validation.SupportedType;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Accessors(chain = true)
public class CreatePersonCommand {
    @SupportedType
    private String type;
    @SupportedParam
    private Map<String, String> params;
}
