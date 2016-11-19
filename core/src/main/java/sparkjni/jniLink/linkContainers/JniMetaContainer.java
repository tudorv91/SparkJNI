package sparkjni.jniLink.linkContainers;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface JniMetaContainer {
    List<JniHeader> jniHeaderContainers();
    String appName();
    String nativePathName();
}
