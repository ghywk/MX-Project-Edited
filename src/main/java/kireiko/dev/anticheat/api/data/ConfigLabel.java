package kireiko.dev.anticheat.api.data;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@AllArgsConstructor
@Data
public class ConfigLabel {
    private final String name;
    private final Map<String, Object> parameters;
}
