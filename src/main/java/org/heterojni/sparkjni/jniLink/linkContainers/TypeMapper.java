package org.heterojni.sparkjni.jniLink.linkContainers;

import org.heterojni.sparkjni.dataLink.CppBean;
import org.immutables.value.Value;

@Value.Immutable
public interface TypeMapper {
    Class javaType();
    CppBean cppType();
    String jniType();
}
