package memoryCopy;

/**
 * Created by tudor on 11/20/16.
 */
public class MemoryCopyUtil {
    public static double[] generateArray(int size) {
        double[] arr = new double[size];
        for (int i = 0; i < size; i++)
            arr[i] = Math.random();

        return arr;
    }
}
