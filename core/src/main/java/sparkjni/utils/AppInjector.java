package sparkjni.utils;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class AppInjector extends AbstractModule{
    private static Injector injector;

    static SparkJni injectSparkJni() {
        return getInjector().getInstance(SparkJni.class);
    }

    public static Injector getInjector() {
        if(injector == null) {
            injector = Guice.createInjector(new AppInjector());
        }
        return injector;
    }

    public static void injectMembers(Object o) {
        getInjector().injectMembers(o);
    }

    @Override
    protected void configure() {
        bind(SparkJni.class).asEagerSingleton();
        bind(JniLinkHandler.class).asEagerSingleton();
        bind(MetadataHandler.class).asEagerSingleton();
    }
}
