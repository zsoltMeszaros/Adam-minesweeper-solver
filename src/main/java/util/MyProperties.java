package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MyProperties {
    private static Properties getProperties() {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream("src/main/resources/project.properties")) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static String getBaseUrl() {
        return getProperties().getProperty("base_url");
    }
    public static long getTimeout() {
        return Long.parseLong(getProperties().getProperty("timeout"));
    }
    public static String getDriver() {
        return getProperties().getProperty("driver");
    }
}
