package org.heterojni.sparkjni.jniLink.linkContainers;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface JniMetaContainer {
    List<JniHeaderContainer> jniHeaderContainers();
    String appName();
    String nativePathName();
}
