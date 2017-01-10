package generator;

import sparkjni.utils.jniAnnotations.JNI_class;
import sparkjni.utils.jniAnnotations.JNI_functionClass;

import java.util.Set;

public class SparkJniClassifier {
    private Set<Class> jniFunctionClasses;
    private Set<Class> beanClasses;

    public SparkJniClassifier(Set<Class> classesInProject) {
        classify(classesInProject);
    }

    private void classify(Set<Class> classesInProject) {
        for(Class classInProject: classesInProject) {
            checkAndStoreJniFunction(classInProject);
            checkAndStoreBeanClass(classInProject);
        }
    }

    private void checkAndStoreBeanClass(Class classInProject) {
        if(classInProject.getAnnotation(JNI_functionClass.class) != null)
            beanClasses.add(classInProject);
    }

    private void checkAndStoreJniFunction(Class classInProject) {
        if(classInProject.getAnnotation(JNI_class.class) != null)
            jniFunctionClasses.add(classInProject);
    }
}
