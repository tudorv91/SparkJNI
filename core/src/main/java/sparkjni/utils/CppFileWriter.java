package sparkjni.utils;

import sparkjni.dataLink.CppBean;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class CppFileWriter {
    public static void writeCppHeaderPairs(ArrayList<CppBean> registeredCppContainers) {
        for (CppBean cppBean : registeredCppContainers) {
            PrintWriter writer = null;
            try {
                writer = new PrintWriter(cppBean.getCppFilePath());
                writer.write(cppBean.getCppImplementation());

                writer.close();

                writer = new PrintWriter(cppBean.getHeaderFilePath());
                writer.write(cppBean.getHeaderImplementation());
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
                System.exit(5);
            } finally {
                if (writer != null)
                    writer.close();
            }
        }
    }
}
