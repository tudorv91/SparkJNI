package sparkjni.utils;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.*;

public class NativeFunctionPrototypesCollector {
    private final JniLinkHandler jniLinkHandler;
    private final MetadataHandler metadataHandler;

    @Inject
    public NativeFunctionPrototypesCollector(JniLinkHandler jniLinkHandler, MetadataHandler metadataHandler) {
        this.jniLinkHandler = jniLinkHandler;
        this.metadataHandler = metadataHandler;
    }

    boolean collectNativeFunctionPrototypes() {
        File nativeLibDir = new File(metadataHandler.getNativePath());

        if (nativeLibDir.exists() && nativeLibDir.isDirectory()) {
            File[] headerFiles = nativeLibDir.listFiles(headerFilenameFilter());
            for (File headerFile : headerFiles) {
                jniLinkHandler.getJniHeaderFiles().add(headerFile.getName());
                String[] splittedFileName = headerFile.getName().split("_");
                if (splittedFileName.length == 1) {
                    continue;
                }
                parseFileForPrototypes(headerFile);
            }
            return true;
        } else
            return false;
    }

    @Nonnull
    private FilenameFilter headerFilenameFilter() {
        return new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return s.endsWith(".h");
            }
        };
    }

    private void parseFileForPrototypes(@Nonnull File headerFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(headerFile))) {
            for (String line; (line = br.readLine()) != null; ) {
                // TODO replace with regex
                if (line.startsWith("JNIEXPORT")) {
                    String[] splitted = line.split("_");
                    String methodName = splitted[splitted.length - 1];
                    jniLinkHandler.registerNativePrototype(line, methodName);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
