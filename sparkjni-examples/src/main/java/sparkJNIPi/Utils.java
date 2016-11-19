package sparkJNIPi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Utils {
    public static void log(String logFilePath, Long duration, String executionMode, int noSlices, int sliceSize, String local){
        String logLine = String.format("SparkCLPi, %s, %s, %d, %d, %s\n", local, executionMode, noSlices, sliceSize, duration.toString());
        try{
            Files.write(Paths.get(logFilePath), logLine.getBytes(), StandardOpenOption.APPEND);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
