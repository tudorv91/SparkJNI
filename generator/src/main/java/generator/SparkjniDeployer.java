package generator;

import sparkjni.utils.SparkJni;
import sparkjni.utils.SparkJniClassifier;
import sparkjni.utils.SparkJniSingletonBuilder;

public class SparkjniDeployer {
    private MetadataProvider metadataProvider;
    private CurrentProjectClassRetriever currentProjectClassRetriever;

    public SparkjniDeployer(MetadataProvider metadataProvider, CurrentProjectClassRetriever currentProjectClassRetriever) {
        this.metadataProvider = metadataProvider;
        this.currentProjectClassRetriever = currentProjectClassRetriever;
    }

    public void deploySparkJni() {
        PropertiesHandler propertiesHandler = metadataProvider.loadProperties();
        SparkJni sparkJni =  new SparkJniSingletonBuilder()
                .appName(metadataProvider.getProjectName())
                .nativePath(metadataProvider.getNativeAppDir())
                .build();
        SparkJniClassifier sparkJniClassifier = new SparkJniClassifier(currentProjectClassRetriever.getClassesInProject());
        sparkJni.registerClassifier(sparkJniClassifier);
        sparkJni.addToClasspath(metadataProvider.getTargetDir());
        SparkJni.setClassloader(currentProjectClassRetriever.getClassLoader());
        sparkJni.setDeployMode(propertiesHandler.getBuildMode());
        sparkJni.setJdkPath(propertiesHandler.getJdkPath());
        sparkJni.deploy();
    }
}
