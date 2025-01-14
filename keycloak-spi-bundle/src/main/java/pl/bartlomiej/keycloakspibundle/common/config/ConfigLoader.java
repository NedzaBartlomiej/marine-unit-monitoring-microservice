package pl.bartlomiej.keycloakspibundle.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;

public class ConfigLoader<T> {

    private static final Logger log = LoggerFactory.getLogger(ConfigLoader.class);
    private static final String ROOT_CONFIG_PATH = "config/";
    private final Yaml yaml;
    private final ConfigCache configCache;

    public ConfigLoader(Yaml yaml, ConfigCache configCache) {
        this.yaml = yaml;
        this.configCache = configCache;
    }

    /**
     * If config by configFileName exists, return it from cache.
     * If not, load config file, from src/main/resources/config final root path.
     */
    public T load(final String configFileName, final Class<T> propertiesClass) {
        return this.configCache.computeIfAbsent(configFileName, propertiesClass, this::loadFromFile);
    }

    private T loadFromFile(final String configFileName, final Class<T> propertiesClass) {
        log.info("Loading config file named {}", configFileName);
        try (InputStream stream = ConfigLoader.class.getClassLoader().getResourceAsStream(ROOT_CONFIG_PATH + configFileName)) {
            if (stream == null) {
                throw new IllegalArgumentException("No config file found in path: " + ROOT_CONFIG_PATH + configFileName + " when loading.");
            }
            T loadedConfig = this.yaml.loadAs(stream, propertiesClass);
            if (loadedConfig == null) {
                throw new IllegalStateException("Loaded config is null for file: " + configFileName);
            }
            return loadedConfig;
        } catch (IOException e) {
            throw new RuntimeException("Something go wrong loading a config file: " + configFileName, e);
        }
    }
}