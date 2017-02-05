package generator;

import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sparkjni.utils.DeployMode;
import sparkjni.utils.exceptions.HardSparkJniException;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static sparkjni.utils.DeployMode.DEPLOY_MODES_MAP;

public class PropertiesHandler {
    private final Logger logger = LoggerFactory.getLogger(PropertiesHandler.class);
    private Properties properties;
    private String propertiesFilePath;
    private DefaultPropertiesFileCreator defaultPropertiesFileCreator;

    @Inject
    public PropertiesHandler(Properties properties, String propertiesFilePath, DefaultPropertiesFileCreator defaultPropertiesFileCreator) {
        this.properties = properties;
        this.propertiesFilePath = propertiesFilePath;
        this.defaultPropertiesFileCreator = defaultPropertiesFileCreator;
    }

    @Nonnull
    public Properties loadPropertiesOrCreateDefault(){
        File propertiesFile = new File(propertiesFilePath);
        if(!propertiesFile.exists()) {
            defaultPropertiesFileCreator.createDefaultPropertiesFile(propertiesFile);
        } else {
            try {
                properties.load(new FileInputStream(propertiesFile));
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
        return properties;
    }

    @Nonnull
    public Optional<String> getJdkPath() {
        if(properties != null) {
            return Optional.fromNullable(properties.getProperty(DefaultPropertiesFileCreator.JDK_PATH));
        } else
            throw new HardSparkJniException("Properties field is null");
    }

    @Nonnull
    public Optional<DeployMode> getBuildMode() {
        if(properties != null) {
            String buildMode = properties.getProperty(DefaultPropertiesFileCreator.BUILD_MODE);
            if(buildMode == null) {
                return Optional.absent();
            } else {
                return Optional.of(new DeployMode(DEPLOY_MODES_MAP.get(buildMode)));
            }
        } else
            throw new HardSparkJniException("Properties field is null");
    }

    @Nonnull
    public Optional<String> getNativePath() {
        if(properties != null) {
            return Optional.fromNullable(properties.getProperty(DefaultPropertiesFileCreator.NATIVE_SOURCE_PATH));
        } else
            throw new HardSparkJniException("Properties field is null");
    }
}
