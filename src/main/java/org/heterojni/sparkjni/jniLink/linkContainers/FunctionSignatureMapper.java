package org.heterojni.sparkjni.jniLink.linkContainers;

import org.immutables.value.Value.Immutable;

import java.util.List;

@Immutable
public interface FunctionSignatureMapper {
    List<TypeMapper> parameterList();
    TypeMapper returnTypeMapper();
    EntityNameMapper functionNameMapper();
    boolean staticMethod();
}
