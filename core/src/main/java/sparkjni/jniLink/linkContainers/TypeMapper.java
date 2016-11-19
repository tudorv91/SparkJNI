package sparkjni.jniLink.linkContainers;

import sparkjni.dataLink.CppBean;
import org.immutables.value.Value;

@Value.Immutable
public abstract class TypeMapper {
    public abstract Class javaType();
    public abstract CppBean cppType();
    public abstract String jniType();

    @Override
    public boolean equals(Object o) {
        if(o == null)
            return false;
        if(!(o instanceof TypeMapper))
            return false;
        TypeMapper other = (TypeMapper) o;

        return this.javaType().equals(other.javaType())
                && this.cppType().equals(other.cppType())
                && this.jniType().equals(other.jniType());
    }
}
