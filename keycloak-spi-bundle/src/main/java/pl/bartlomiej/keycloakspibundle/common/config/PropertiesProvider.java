package pl.bartlomiej.keycloakspibundle.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;

public class PropertiesProvider {

    private final PropertyMapper propertyMapper;

    public PropertiesProvider(ObjectMapper objectMapper) {
        this.propertyMapper = new PropertyMapper(objectMapper);
    }

    public <T> T get(final String configFileName,
                     final String propertyGroupPrefix,
                     final Class<T> propertyTargetClass) {
        return propertyMapper.map(ConfigLoader.load(configFileName),
                propertyGroupPrefix, propertyTargetClass);
    }
}