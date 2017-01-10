package generator;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import sparkjni.utils.DeployMode;
import sparkjni.utils.SparkJni;
import sparkjni.utils.SparkJniClassifier;
import sparkjni.utils.SparkJniSingletonBuilder;

import java.io.File;

@Mojo(requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, name = "sparkjni-generator")
@SuppressWarnings("unused")
public class Generator extends AbstractMojo {
    @Parameter(readonly = true, required = true, defaultValue = "${project}")
    private MavenProject project;

    private CurrentProjectClassRetriever currentProjectClassRetriever;

    private String baseModuleDir;
    private String testSrcDir;
    private String javaSourcesDir;
    private String nativeAppDir;
    private String projectName;

    private String targetDir;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        deploy();
    }

    private void deploy() {
        setVarsAndDirs();
        currentProjectClassRetriever = new CurrentProjectClassRetriever(project, javaSourcesDir);
        deploySparkJni();
    }

    private void deploySparkJni() {
        DeployMode deployMode = new DeployMode(DeployMode.DeployModes.FULL_GENERATE_AND_BUILD);
        SparkJni sparkJni =  new SparkJniSingletonBuilder()
                .appName(projectName)
                .nativePath(nativeAppDir)
                .build();
        SparkJniClassifier sparkJniClassifier = new SparkJniClassifier(currentProjectClassRetriever.getClassesInProject());
        sparkJni.registerClassifier(sparkJniClassifier);
        sparkJni.addToClasspath(targetDir);
        sparkJni.setClassloader(currentProjectClassRetriever.getClassLoader());
        sparkJni.deploy();
    }

    private void setVarsAndDirs() {
        baseModuleDir = project.getBasedir().getAbsolutePath();
        projectName = project.getName().replaceAll("[^a-zA-Z0-9]", "");
        javaSourcesDir = baseModuleDir + "/src/main/java";
        targetDir = baseModuleDir + "/target/classes";
        nativeAppDir = baseModuleDir + "/src/main/resources/" + projectName;
        File nativeAppDirFile = new File(nativeAppDir);
        if(!nativeAppDirFile.exists())
            nativeAppDirFile.mkdirs();
    }
}
