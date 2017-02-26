package generator;

import javax.inject.Inject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class DefaultPropertiesFileCreator {
    public static final String JDK_PATH = "JDK_PATH";
    public static final String NATIVE_SOURCE_PATH = "NATIVE_SOURCE_PATH";
    public static final String BUILD_MODE = "BUILD_MODE";
    public static final String OVERWRITE_KERNEL = "OVERWRITE_KERNEL";

    private final Properties properties;
    private static final String PROPERTY_FILE_COMMENTS = "SparkJNI default properties file: Change properties if needed.\n" +
            "NATIVE_SOURCE_PATH is the path to your desired source directory (i.e. where SparkJNI also generates wrappers and the template kernel.\n" +
            "BUILD_MODE has 4 possible options: FULL_GENERATE_AND_BUILD, JAVAH_MAKEFILE_AND_BUILD, JUST_BUILD, ASSUME_EVERYTHING_IS_THERE.\n" +
            "OVERWRITE_KERNEL set to false stops SparkJNI in overwriting the kernel, if it exists and conforms to the default name.\n" +
            "The application name (and the derivatives e.g. kernel file name) are set with APP_NAME. " +
            "DEFAULT ignores this field and uses the Maven project as a resource for the project name.";

    @Inject
    public DefaultPropertiesFileCreator(Properties properties) {
        this.properties = properties;
    }

    public void createDefaultPropertiesFile(File propertiesFile) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(propertiesFile);
            writeDefaultPropertiesFile(fileOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeDefaultPropertiesFile(FileOutputStream propertiesFileOutputStream) throws IOException {
        properties.setProperty(BUILD_MODE, "FULL_GENERATE_AND_BUILD");
        properties.setProperty(OVERWRITE_KERNEL, "false");
        properties.setProperty(NATIVE_SOURCE_PATH, "src/main/resources");
        properties.setProperty(JDK_PATH, "/usr/lib/jvm/default-java");
        properties.store(propertiesFileOutputStream, PROPERTY_FILE_COMMENTS);
    }
}
