package generator;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Mojo(name = "sparkjni-generator")
public class Generator extends AbstractMojo {
    @Parameter(defaultValue = "${project.basedir}/src")
    String sourcesDir;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        String javaSourcesDir = sourcesDir + "/main/java";
        String resourcesDir = sourcesDir + "/main/resources";
        String testSrcDir = sourcesDir + "/test/java";

        testResourcesDir(resourcesDir);
    }

    private void testResourcesDir(String resourcesDir) {
        String testFilePath = resourcesDir + "/test.out";
        File dir = new File(resourcesDir);
        if(!dir.exists())
            throw new RuntimeException("Could not locate resources dir " + dir.getAbsolutePath());
        try {
            FileWriter fileWriter = new FileWriter(testFilePath);
            fileWriter.write("Successfully generated maven plugin at gen-src");
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to write file " + testFilePath, e);
        }
    }
}
