package generator;

import javax.inject.Inject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class DefaultPropertiesFileCreator {
    private final Properties properties;

    @Inject
    public DefaultPropertiesFileCreator(Properties properties) {
        this.properties = properties;
    }

    public void createDefaultPropertiesFile(File propertiesFile) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(propertiesFile);
            writeDefaultPropertiesToXML(fileOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeDefaultPropertiesToXML(FileOutputStream propertiesFileOutputStream) throws IOException {
        properties.setProperty("DEFAULT_BUILDMODE", "FULL_BUILD");
        properties.setProperty("DEFAULT_NATIVE_SOURCE_PATH", "src/main/resources");
        properties.setProperty("DEFAULT_JVM_PATH", "/usr/lib/jvm/default-java");
        properties.storeToXML(propertiesFileOutputStream, "SparkJNI default properties file: Change properties if needed");
    }
}
