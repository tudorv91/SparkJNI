package sparkjni.jniLink.linkContainers;

import org.immutables.value.Value;

@Value.Immutable
public interface EntityNameMapper {
    String javaName();
    String jniName();
    String cppName();
}
