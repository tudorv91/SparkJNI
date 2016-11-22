package memoryCopy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tudor on 11/20/16.
 */
public class MemoryCopyUtil {
    public static List<DoubleArray> generateArray(int sliceSize, int slices) {
        List<DoubleArray> doubleArrays = new ArrayList<>();
        for(int sliceIdx = 0; sliceIdx < slices; sliceIdx++) {
            double[] arr = new double[sliceSize];
            for (int i = 0; i < sliceSize; i++)
                arr[i] = Math.random();
            doubleArrays.add(new DoubleArray(arr));
        }
        return doubleArrays;
    }
}
