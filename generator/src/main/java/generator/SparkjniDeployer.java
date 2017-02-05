package generator;

import com.google.common.base.Function;
import sparkjni.utils.DeployMode;
import sparkjni.utils.SparkJni;
import sparkjni.utils.SparkJniClassifier;
import sparkjni.utils.SparkJniSingletonBuilder;

import javax.annotation.Nullable;

public class SparkjniDeployer {
    private MetadataProvider metadataProvider;
    private CurrentProjectClassRetriever currentProjectClassRetriever;

    public SparkjniDeployer(MetadataProvider metadataProvider, CurrentProjectClassRetriever currentProjectClassRetriever) {
        this.metadataProvider = metadataProvider;
        this.currentProjectClassRetriever = currentProjectClassRetriever;
    }

    public void deploySparkJni() {
        PropertiesHandler propertiesHandler = metadataProvider.loadProperties();
        SparkJni sparkJni = new SparkJniSingletonBuilder()
                .appName(metadataProvider.getProjectName())
                .nativePath(metadataProvider.getNativeAppDir())
                .build();
        SparkJniClassifier sparkJniClassifier = new SparkJniClassifier(currentProjectClassRetriever.getClassesInProject());
        sparkJni.registerClassifier(sparkJniClassifier);
        sparkJni.addToClasspath(metadataProvider.getTargetDir());
        SparkJni.setClassloader(currentProjectClassRetriever.getClassLoader());

        setUserDefinedProperties(propertiesHandler, sparkJni);
        sparkJni.deploy();
    }

    private void setUserDefinedProperties(PropertiesHandler propertiesHandler, final SparkJni sparkJni) {
        // For each of these properties, we set them if a non-absent value is stored in the sparkjni.properties file
        // Otherwise, uses SparkJNI defaults.
        propertiesHandler.getBuildMode().transform(new Function<DeployMode, Object>() {
            @Nullable
            @Override
            public Object apply(@Nullable DeployMode deployMode) {
                sparkJni.setDeployMode(deployMode);
                return new Object();
            }
        });
        propertiesHandler.getJdkPath().transform(new Function<String, Object>() {
            @Nullable
            @Override
            public Object apply(@Nullable String s) {
                sparkJni.setJdkPath(s);
                return new Object();
            }
        });
    }
}
