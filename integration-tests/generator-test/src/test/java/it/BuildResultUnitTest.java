package it;

import org.junit.Before;
import org.junit.Test;

import static it.GeneratorTestUtils.*;
import static org.junit.Assert.assertTrue;

public class BuildResultUnitTest {
    private static final String PROJECT_NAME = "generatorTester";

    @Before
    public void setup() throws Exception {
        cleanTargetForProject(PROJECT_NAME);
    }

    @Test
    public void testBuild() throws Exception {
        mvnCleanInstallProject(PROJECT_NAME);
        assertTrue(fileExists("src/test/resources/generatorTester/src/main/resources/generatortest/CPPDoubleArray.cpp"));
        assertTrue(fileExists("src/test/resources/generatorTester/src/main/resources/generatortest/generatortest.cpp"));
        assertTrue(fileExists("src/test/resources/generatorTester/src/main/resources/generatortest/generatortest.so"));
    }
}
