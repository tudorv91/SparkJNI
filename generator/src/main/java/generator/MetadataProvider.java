package generator;

import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;

public class MetadataProvider {
    private Logger logger = LoggerFactory.getLogger(MetadataProvider.class);
    private MavenProject project;

    private String baseModuleDir;

    private String testSrcDir;
    private String javaSourcesDir;
    private String nativeAppDir;
    private String projectName;
    private String targetDir;

    private String propertiesFilePath;
    private String relativeResourcesDir;
    private String absoluteResourcesDir;

    public MetadataProvider(MavenProject project) {
        this.project = project;
        setVarsAndDirs();
    }

    private void setVarsAndDirs() {
        baseModuleDir = project.getBasedir().getAbsolutePath();
        projectName = project.getName().replaceAll("[^a-zA-Z0-9]", "");
        javaSourcesDir = baseModuleDir + "/src/main/java";
        targetDir = baseModuleDir + "/target/classes";
        relativeResourcesDir = "/src/main/resources";
        absoluteResourcesDir = baseModuleDir + relativeResourcesDir;
        nativeAppDir = absoluteResourcesDir + "/" + projectName;
        propertiesFilePath = absoluteResourcesDir + "/" + "sparkjni.properties";
        File nativeAppDirFile = new File(nativeAppDir);
        if (!nativeAppDirFile.exists())
            nativeAppDirFile.mkdirs();
    }

    public PropertiesHandler loadProperties() {
        if (!new File(absoluteResourcesDir).exists()) {
            try {
                Files.createDirectories(new File(absoluteResourcesDir).toPath());
            } catch (IOException e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }
        }
        Properties properties = new Properties();
        PropertiesHandler propertiesHandler = new PropertiesHandler(properties, propertiesFilePath, new DefaultPropertiesFileCreator(properties));
        propertiesHandler.loadPropertiesOrCreateDefault();
        return propertiesHandler;
    }

    public String getJavaSourcesDir() {
        return javaSourcesDir;
    }

    public String getNativeAppDir() {
        return nativeAppDir;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getTargetDir() {
        return targetDir;
    }
}
