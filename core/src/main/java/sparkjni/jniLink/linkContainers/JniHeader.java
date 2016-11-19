package sparkjni.jniLink.linkContainers;

import org.immutables.value.Value;

import java.io.File;
import java.util.List;

@Value.Immutable
public abstract class JniHeader {
    public abstract File jniHeaderFile();
    public abstract List<FunctionSignatureMapper> jniFunctions();
    public abstract String fileName();
    public abstract String fullyQualifiedJavaClassName();

    public boolean removeHeaderFile(){
        if(jniHeaderFile().exists())
            return jniHeaderFile().delete();
        else
            return false;
    }
}
