package pl.bartlomiej.keycloakspibundle.common.config;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

public class ConfigCache {
    private final ConcurrentHashMap<String, Object> configCache = new ConcurrentHashMap<>();

    public <T> T computeIfAbsent(final String configFileName, final Class<T> propertiesClass, final BiFunction<String, Class<T>, T> computeFunction) {
        Object existingConfigClass = configCache.get(Objects.requireNonNull(configFileName,
                "Config file name cannot be null."));
        if (existingConfigClass == null) {
            T computedConfigClass = computeFunction.apply(configFileName, propertiesClass);
            configCache.put(configFileName, computedConfigClass);
            return computedConfigClass;
        }
        if (!propertiesClass.isInstance(existingConfigClass)) {
            throw new IllegalArgumentException("Invalid config class type: expected "
                    + existingConfigClass.getClass().getName() + ", but was " + propertiesClass.getName());
        }
        return propertiesClass.cast(existingConfigClass);
    }
}