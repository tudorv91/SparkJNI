package sparkjni.utils;

import sparkjni.utils.jniAnnotations.JNI_class;
import sparkjni.utils.jniAnnotations.JNI_functionClass;

import java.util.HashSet;
import java.util.Set;

public class SparkJniClassifier {
    private Set<Class> jniFunctionClasses = new HashSet<>();
    private Set<Class> beanClasses = new HashSet<>();

    public SparkJniClassifier(Set<Class> classesInProject) {
        classify(classesInProject);
    }

    Set<Class> getJniFunctionClasses() {
        return jniFunctionClasses;
    }

    Set<Class> getBeanClasses() {
        return beanClasses;
    }

    private void classify(Set<Class> classesInProject) {
        for(Class classInProject: classesInProject) {
            checkAndStoreJniFunction(classInProject);
            checkAndStoreBeanClass(classInProject);
        }
    }

    private void checkAndStoreBeanClass(Class classInProject) {
        if(classInProject.getAnnotation(JNI_class.class) != null)
            beanClasses.add(classInProject);
    }

    private void checkAndStoreJniFunction(Class classInProject) {
        if(classInProject.getAnnotation(JNI_functionClass.class) != null)
            jniFunctionClasses.add(classInProject);
    }
}
