package pl.bartlomiej.keycloakspibundle.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

class ConfigLoader {

    private static final Logger log = LoggerFactory.getLogger(ConfigLoader.class);
    private static final String ROOT_CONFIG_PATH = "config/";
    private static final Map<String, Properties> configCache = new ConcurrentHashMap<>();

    /**
     * Loads config file, from src/main/resources/config final root path.
     */
    static Properties load(final String configFileName) {
        return configCache.computeIfAbsent(configFileName, ConfigLoader::loadFromFile);
    }

    private static Properties loadFromFile(final String configFileName) {
        log.info("Loading config file - {}", configFileName);

        try (InputStream stream = ConfigLoader.class.getClassLoader().getResourceAsStream(ROOT_CONFIG_PATH + configFileName)) {
            Properties properties = new Properties();
            properties.load(stream);
            return properties;
        } catch (IOException e) {
            log.error("Something go wrong loading a config file.", e);
            throw new RuntimeException(e);
        }
    }
}