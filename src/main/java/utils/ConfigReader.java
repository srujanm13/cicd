package utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class ConfigReader {

    private static final String CONFIG_DIRECTORY = "src/main/resources/configs";
    private static final String DEFAULT_ENV = "qa";
    private static final String SELECTED_ENV = getSelectedEnvironment();
    private static final Properties properties = new Properties();

    static {

        String configFileName = "config-" + SELECTED_ENV + ".properties";

        try (InputStream inputStream = getConfigInputStream(configFileName)) {

            properties.load(inputStream);

        } catch (IOException e) {

            throw new RuntimeException("Unable to load config file: " + configFileName, e);

        }
    }

    // Generic Method
    public static String getProperty(String key) {

        // First Priority -> Environment Variable
        String envValue = getNonEmptyValue(System.getenv(key));

        if (envValue == null) {

            envValue = getNonEmptyValue(System.getenv(key.toUpperCase()));

        }

        if (envValue != null) {

            return envValue;

        }

        // Second Priority -> Maven Command Line
        String systemProperty = getNonEmptyValue(System.getProperty(key));

        if (systemProperty != null) {

            return systemProperty;

        }

        // Third Priority -> config.properties
        return properties.getProperty(key);
    }

    // Helper Methods

    public static String getBrowser() {

        return getProperty("browser");

    }

    public static String getUrl() {

        return getProperty("url");

    }

    public static String getUsername() {

        return getEnvironmentSpecificProperty("uname");

    }

    public static String getPassword() {

        return getEnvironmentSpecificProperty("pwd");

    }

    private static String getNonEmptyValue(String value) {

        if (value == null || value.trim().isEmpty()) {

            return null;

        }

        return value.trim();
    }

    private static String getSelectedEnvironment() {

        String env = getNonEmptyValue(System.getProperty("env"));

        if (env == null) {

            env = DEFAULT_ENV;

        }

        return env.trim().toLowerCase();
    }

    private static InputStream getConfigInputStream(String configFileName) throws IOException {

        InputStream inputStream = ConfigReader.class.getClassLoader()
                .getResourceAsStream("configs/" + configFileName);

        if (inputStream != null) {

            return inputStream;

        }

        Path configPath = Path.of(CONFIG_DIRECTORY, configFileName);
        return Files.newInputStream(configPath);
    }

    private static String getEnvironmentSpecificProperty(String key) {

        String underscoreKey = key + "_" + SELECTED_ENV;
        String hyphenKey = key + "-" + SELECTED_ENV;

        String value = getProperty(underscoreKey);

        if (value != null) {

            return value;

        }

        value = getProperty(hyphenKey);

        if (value != null) {

            return value;

        }

        return getProperty(key);
    }
}
