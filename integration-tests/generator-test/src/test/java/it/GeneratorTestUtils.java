package it;

import org.junit.Assert;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class GeneratorTestUtils {
    static boolean fileExists(String relativeFilePath) {
        return new File(relativeFilePath).exists();
    }

    static void cleanTargetForProject(String projectName) throws IOException, InterruptedException {
        String targetPath = String.format("src/test/resources/%s/target", projectName);
        File targetDir = new File(targetPath);
        if (targetDir.exists() && targetDir.isDirectory())
            runProcess("rm -r " + targetPath);

        String generatedPath = String.format(String.format("src/test/resources/%s/src/main/resources/%s", projectName, projectName));
        File nativeDir = new File(generatedPath);
        if (nativeDir.exists() && nativeDir.isDirectory())
            runProcess("rm -r " + generatedPath);
    }

    static void runProcess(String command) throws IOException, InterruptedException {
        String line;
        Process process = Runtime.getRuntime().exec(command);
        if (process.waitFor() != 0) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = bufferedReader.readLine()) != null)
                System.out.println(line);

            BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = error.readLine()) != null)
                System.out.println(line);
            Assert.fail();
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while ((line = bufferedReader.readLine()) != null)
            System.out.println(line);
    }

    static void mvnCleanInstallProject(String mavenProjectName) {
        String command = String.format("mvn -f src/test/resources/%s/pom.xml clean install", mavenProjectName);
        try {
            runProcess(command);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
