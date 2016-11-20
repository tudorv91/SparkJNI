package memoryCopy;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import sparkjni.utils.SparkJni;
import testutils.TestUtils;

import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by tudor on 11/19/16.
 */
public class MemoryCopy {
    private TestUtils testUtils;
    private SparkJni sparkJni;

    @Before
    public void init(){
        String sparkjniClasspath = FileSystems.getDefault().getPath("../core/target/classes").toAbsolutePath().normalize().toString();
        String classpath = FileSystems.getDefault().getPath("../sparkjni-examples/target/classes").toAbsolutePath().normalize().toString();
        String testClasspath = FileSystems.getDefault().getPath("target/test-classes").toAbsolutePath().normalize().toString();

        HashMap<String, String> codeInjection = new HashMap<>();
        codeInjection.put("arrayCopy", "return 0;");

        testUtils = new TestUtils(MemoryCopy.class);
        sparkJni = testUtils.getSparkJni(sparkjniClasspath + ":" + classpath + ":" + testClasspath);
        sparkJni.registerContainer(DoubleArray.class);
        sparkJni.registerJniFunction(MapCopyFunc.class);
        sparkJni.deployWithCodeInjections(codeInjection);
    }

    @Ignore
    @Test
    public void memoryCopyTest(){
        int copySize = 65536;
        List<DoubleArray> inputList = new ArrayList<>();
        DoubleArray input = new DoubleArray(MemoryCopyUtil.generateArray(copySize));
        inputList.add(input);

        @SuppressWarnings(value = "Unchecked")
        List<DoubleArray> result = testUtils.getSparkContext().parallelize(inputList)
                .map(new MapCopyFunc(testUtils.fullPath, "arrayCopy")).collect();
    }
}
