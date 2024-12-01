package pl.bartlomiej.keycloakspibundle.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

class PropertyMapper {

    private final ObjectMapper objectMapper;
    private static final Logger log = LoggerFactory.getLogger(PropertyMapper.class);

    PropertyMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    <T> T map(final Properties properties, final String propPrefix, final Class<T> target) {
        try {
            ObjectNode targetJson = objectMapper.createObjectNode();
            for (String propertyName : properties.stringPropertyNames()) {
                if (propertyName.startsWith(propPrefix)) {
                    String fieldName = hyphenSeparatedToCamelCase(
                            propertyName.substring(propPrefix.length()));
                    targetJson.put(fieldName, properties.getProperty(propertyName));
                }
            }
            return objectMapper.readValue(targetJson.toString(), target);
        } catch (Exception e) {
            log.error("Failed to map properties object to provided class.", e);
            throw new RuntimeException(e);
        }
    }

    private static String hyphenSeparatedToCamelCase(final String hyphenSepString) {
        StringBuilder camelBuilder = new StringBuilder();
        boolean toUpperCase = false;

        for (char c : hyphenSepString.toCharArray()) {
            if (c == '-') {
                toUpperCase = true;
            } else {
                camelBuilder.append(toUpperCase ? Character.toUpperCase(c) : Character.toLowerCase(c));
                toUpperCase = false;
            }
        }
        return camelBuilder.toString();
    }
}