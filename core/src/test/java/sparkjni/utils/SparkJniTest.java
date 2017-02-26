package sparkjni.utils;

import org.junit.Before;
import org.junit.Test;

public class SparkJniTest {
    SparkJni testSubject;

    @Before
    public void init() {
        testSubject = new SparkJniBuilder().nativePath("src/test/resources/vectorBean").build();
    }

    @Test
    public void sparkJni() throws Exception {

    }

    @Test
    public void deploy() throws Exception {

    }

    @Test
    public void deployWithCodeInjections() throws Exception {

    }

    @Test
    public void addToClasspath() throws Exception {

    }

    @Test
    public void registerClassifier() throws Exception {

    }

    @Test
    public void setUserDefines() throws Exception {

    }

    @Test
    public void setUserLibraryDirs() throws Exception {

    }

    @Test
    public void setSparkContext() throws Exception {

    }

    @Test
    public void setUserIncludeDirs() throws Exception {

    }

    @Test
    public void setUserLibraries() throws Exception {

    }

    @Test
    public void setJdkPath() throws Exception {

    }

    @Test
    public void registerJniFunction() throws Exception {

    }

    @Test
    public void registerContainer() throws Exception {

    }

    @Test
    public void getJniHandler() throws Exception {

    }

    @Test
    public void getJniRootContainer() throws Exception {

    }

    @Test
    public void setDeployMode() throws Exception {

    }

    @Test
    public void getDeployTimesLogger() throws Exception {

    }

    @Test
    public void getDeployMode() throws Exception {

    }

    @Test
    public void getClassloader() throws Exception {

    }

    @Test
    public void setClassloader() throws Exception {

    }

    @Test
    public void setOverwriteKernelFile() throws Exception {

    }
}