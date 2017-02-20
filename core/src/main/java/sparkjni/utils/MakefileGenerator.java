package sparkjni.utils;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.PrintWriter;

@Singleton
public class MakefileGenerator {
    private final MetadataHandler metadataHandler;

    @Inject
    public MakefileGenerator(MetadataHandler metadataHandler) {
        this.metadataHandler = metadataHandler;
    }

    boolean generateMakefile(DeployMode deployMode) {
        String jdkPathStr = deployMode.doBuild ? metadataHandler.getJdkPath() : "";
        String newMakefileContent = String.format(CppSyntax.NEW_MAKEFILE_SECTION,
                metadataHandler.getAppName(), jdkPathStr, metadataHandler.getUserIncludeDirs(),
                metadataHandler.getUserLibraryDirs(), metadataHandler.getUserLibraries(),
                metadataHandler.getUserStaticLibraries(), metadataHandler.getUserDefines());

        return writeMakefile(newMakefileContent);
    }

    private boolean writeMakefile(String newMakefileContent) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(metadataHandler.getNativePath() + "/Makefile");
            writer.write(newMakefileContent);
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        } finally {
            if (writer != null)
                writer.close();
        }
        return true;
    }
}
