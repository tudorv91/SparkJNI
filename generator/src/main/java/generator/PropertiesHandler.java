package generator;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesHandler {
    private Properties properties;
    private String propertiesFilePath;
    private DefaultPropertiesFileCreator defaultPropertiesFileCreator;

    @Inject
    public PropertiesHandler(Properties properties, String propertiesFilePath, DefaultPropertiesFileCreator defaultPropertiesFileCreator) {
        this.properties = properties;
        this.propertiesFilePath = propertiesFilePath;
        this.defaultPropertiesFileCreator = defaultPropertiesFileCreator;
    }

    public Properties loadPropertiesOrCreateDefault(){
        File propertiesFile = new File(propertiesFilePath);
        if(!propertiesFile.exists()) {
            defaultPropertiesFileCreator.createDefaultPropertiesFile(propertiesFile);
        } else {
            try {
                properties.loadFromXML(new FileInputStream(propertiesFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return properties;
    }
}
