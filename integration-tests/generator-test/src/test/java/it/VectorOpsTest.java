package it;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static it.GeneratorTestUtils.*;
import static org.junit.Assert.assertTrue;

public class VectorOpsTest {
    private static final String PROJECT_NAME = "vectorOps";

    @Before
    public void setup() throws Exception {
        cleanTargetForProject(PROJECT_NAME);
    }

    @Test
    public void testBuild() throws Exception {
        mvnCleanInstallProject(PROJECT_NAME);
        assertTrue(fileExists("src/test/resources/vectorOps/src/main/resources/vectorOps/CPPVectorBean.cpp"));
        assertTrue(fileExists("src/test/resources/vectorOps/src/main/resources/vectorOps/vectorOps.cpp"));
        assertTrue(fileExists("src/test/resources/vectorOps/src/main/resources/vectorOps/vectorOps.so"));
        assertTrue(fileExists("src/test/resources/vectorOps/src/main/resources/sparkjni.properties"));
    }

    /**
     * This integration test builds and runs an entire project (VectorOps in src/test/resources/vectorOps/src/main/resources/vectorOps).
     * It simulates an entire user development cycle: builds the Java project, overwrites the generated kernel template (vectorOps.cpp),
     * builds the entire app again and runs it.
     */
    @Test
    public void testRun() throws Exception {
        testBuild();
        // Replace the generated template file with a application-tailored one.
        Files.copy(new File("src/test/resources/vectorOps/src/main/cpp/vectorOps.cpp").toPath(),
                new File("src/test/resources/vectorOps/src/main/resources/vectorOps/vectorOps.cpp").toPath(), StandardCopyOption.REPLACE_EXISTING);
        mvnCleanInstallProject(PROJECT_NAME);
        runProcess("java -cp src/test/resources/vectorOps/target/vectorOps-0.2-jar-with-dependencies.jar vectorOps.GeneratorVectorOpsMain");
    }
}
