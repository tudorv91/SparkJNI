package generator;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mojo(requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, name = "sparkjni-generator")
@SuppressWarnings("unused")
public class Generator extends AbstractMojo {
    Logger logger = LoggerFactory.getLogger(Generator.class);
    @Parameter(readonly = true, required = true, defaultValue = "${project}")
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            deploy();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void deploy() {
        MetadataProvider metadataProvider = new MetadataProvider(project);
        CurrentProjectClassRetriever currentProjectClassRetriever = new CurrentProjectClassRetriever(project, metadataProvider.getJavaSourcesDir());
        SparkjniDeployer sparkjniDeployer = new SparkjniDeployer(metadataProvider, currentProjectClassRetriever);
        sparkjniDeployer.deploySparkJni();
    }
}