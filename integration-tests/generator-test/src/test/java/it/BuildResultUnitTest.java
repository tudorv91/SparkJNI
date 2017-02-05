package it;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class BuildResultUnitTest {

    @Before
    public void setup() throws Exception {
        cleanTarget();
    }

    @Test
    public void testBuild() throws Exception {
        runProcess("mvn -f src/test/resources/generatorTester/pom.xml clean install");
    }

    private void cleanTarget() throws IOException, InterruptedException {
        String targetPath = "src/test/resources/generatorTester/target";
        File targetDir = new File(targetPath);
        if (targetDir.exists() && targetDir.isDirectory())
            runProcess("rm -r " + targetPath);
    }

    private void runProcess(String command) throws IOException, InterruptedException {
        String line;
        Process process = Runtime.getRuntime().exec(command);
        if (process.waitFor() != 0) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = bufferedReader.readLine()) != null)
                System.err.println(line);

            BufferedReader error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = error.readLine()) != null)
                System.err.println(line);
            Assert.fail();
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while ((line = bufferedReader.readLine()) != null)
            System.out.println(line);
    }
}
