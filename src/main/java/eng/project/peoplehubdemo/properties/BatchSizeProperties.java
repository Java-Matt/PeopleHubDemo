package eng.project.peoplehubdemo.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "batch")
@Getter
@Setter
public class BatchSizeProperties {
    private int size;
}
