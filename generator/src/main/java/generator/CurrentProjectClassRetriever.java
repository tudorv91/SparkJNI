package generator;

import com.google.common.reflect.ClassPath;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class CurrentProjectClassRetriever {
    private String sourceDir;
    private MavenProject mavenProject;

    private Set<Class> classesInProject;

    CurrentProjectClassRetriever(MavenProject mavenProject, String sourceDir) {
        this.sourceDir = sourceDir;
        this.mavenProject = mavenProject;
        detectClasses();
    }

    private Set<String> getTopLevelPackages() {
        Set<String> packages = new HashSet<>();
        File javaSrcDirFile = new File(sourceDir);
        if (javaSrcDirFile.exists() && javaSrcDirFile.isDirectory()) {
            File[] files = javaSrcDirFile.listFiles(Utils.FILE_FILTER_IS_DIRECTORY);
            if (files == null)
                return null;
            for (File topLevelPackageFile : files) {
                packages.add(topLevelPackageFile.getName());
            }
        }
        return packages;
    }

    private void detectClasses() {
        List runtimeClasspathElements = null;
        try {
            runtimeClasspathElements = mavenProject.getRuntimeClasspathElements();
        } catch (DependencyResolutionRequiredException e) {
            e.printStackTrace();
        }
        URL[] runtimeUrls = new URL[runtimeClasspathElements.size()];
        for (int i = 0; i < runtimeClasspathElements.size(); i++) {
            String element = (String) runtimeClasspathElements.get(i);
            try {
                runtimeUrls[i] = new File(element).toURI().toURL();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        setClassesInProject(new URLClassLoader(runtimeUrls,
                Thread.currentThread().getContextClassLoader()));
    }

    private void setClassesInProject(URLClassLoader urlClassLoader) {
        classesInProject = new HashSet<>();
        Set<String> topLevelPackages = getTopLevelPackages();
        if (topLevelPackages == null)
            return;
        for (String packageInProject : topLevelPackages) {
            try {
                Set<ClassPath.ClassInfo> classesInCurrentPackage = ClassPath.from(urlClassLoader).getTopLevelClassesRecursive(packageInProject);
                for (ClassPath.ClassInfo classInfo : classesInCurrentPackage) {
                    classesInProject.add(classInfo.load());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Set<Class> getClassesInProject() {
        return classesInProject;
    }
}