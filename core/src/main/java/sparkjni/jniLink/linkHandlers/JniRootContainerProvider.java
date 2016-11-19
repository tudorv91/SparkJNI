package sparkjni.jniLink.linkHandlers;

import sparkjni.jniLink.linkContainers.ImmutableJniRootContainer;
import sparkjni.jniLink.linkContainers.JniHeader;
import sparkjni.jniLink.linkContainers.JniRootContainer;
import sparkjni.utils.JniUtils;
import org.immutables.value.Value;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Value.Immutable
public abstract class JniRootContainerProvider {
    public JniRootContainer buildJniRootContainer(@Nonnull String path, @Nonnull String appName) {
        File nativeDir = new File(path);
        JniUtils.checkNativePath(nativeDir);
        List<JniHeader> jniHeaders = new CopyOnWriteArrayList<>();
        for (File file : new File(path).listFiles())
            processFile(file, jniHeaders);

        return ImmutableJniRootContainer.builder()
                .path(path)
                .appName(appName)
                .jniHeaders(jniHeaders)
                .build();
    }

    private void processFile(@Nonnull File file, @Nonnull List<JniHeader> jniHeaders) {
        try {
            if(JniUtils.isJniNativeFunction(file.toPath()))
                jniHeaders.add(
                        ImmutableJniHeaderProvider.builder()
                                .jniHeaderFile(file)
                                .build()
                                .buildJniHeader()
                );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
