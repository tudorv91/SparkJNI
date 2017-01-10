package generator;

import java.io.File;
import java.io.FileFilter;

public class Utils {
    private Utils(){}

    static FileFilter FILE_FILTER_IS_DIRECTORY = new FileFilter() {
        @Override
        public boolean accept(File file) {
            return file.isDirectory();
        }
    };
}
