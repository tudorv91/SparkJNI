package org.heterojni.sparkjni.utils;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import javax.annotation.Generated;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * {@code SparkJniSingletonBuilder} collects parameters and invokes the static factory method:
 * {@code org.heterojni.sparkjni.utils.SparkJni.sparkJniSingleton(..)}.
 * Call the {@link #build()} method to get a result of type {@code org.heterojni.sparkjni.utils.SparkJni}.
 * <p><em>{@code SparkJniSingletonBuilder} is not thread-safe and generally should not be stored in a field or collection,
 * but instead used immediately to create instances.</em>
 */
@SuppressWarnings({"all"})
@SuppressFBWarnings
@ParametersAreNonnullByDefault
@Generated({"Immutables.generator", "SparkJni.sparkJniSingleton"})
@NotThreadSafe
public final class SparkJniSingletonBuilder {
  private static final long INIT_BIT_APP_NAME = 0x1L;
  private static final long INIT_BIT_NATIVE_PATH = 0x2L;
  private static final long INIT_BIT_JDK_PATH = 0x4L;
  private long initBits = 0x7L;

  private @Nullable String appName;
  private @Nullable String nativePath;
  private @Nullable String jdkPath;

  /**
   * Creates a {@code SparkJniSingletonBuilder} factory builder.
   */
  public SparkJniSingletonBuilder() {
  }

  /**
   * Initializes the value for the {@code appName} attribute.
   * @param appName The value for appName 
   * @return {@code this} builder for use in a chained invocation
   */
  public final SparkJniSingletonBuilder appName(String appName) {
    this.appName = Preconditions.checkNotNull(appName, "appName");
    initBits &= ~INIT_BIT_APP_NAME;
    return this;
  }

  /**
   * Initializes the value for the {@code nativePath} attribute.
   * @param nativePath The value for nativePath 
   * @return {@code this} builder for use in a chained invocation
   */
  public final SparkJniSingletonBuilder nativePath(String nativePath) {
    this.nativePath = Preconditions.checkNotNull(nativePath, "nativePath");
    initBits &= ~INIT_BIT_NATIVE_PATH;
    return this;
  }

  /**
   * Initializes the value for the {@code jdkPath} attribute.
   * @param jdkPath The value for jdkPath 
   * @return {@code this} builder for use in a chained invocation
   */
  public final SparkJniSingletonBuilder jdkPath(String jdkPath) {
    this.jdkPath = Preconditions.checkNotNull(jdkPath, "jdkPath");
    initBits &= ~INIT_BIT_JDK_PATH;
    return this;
  }

  /**
   * Invokes {@code org.heterojni.sparkjni.utils.SparkJni.sparkJniSingleton(..)} using the collected parameters and returns the result of the invocation
   * @return A result of type {@code org.heterojni.sparkjni.utils.SparkJni}
   * @throws java.lang.IllegalStateException if any required attributes are missing
   */
  public SparkJni build() {
    checkRequiredAttributes();
    return SparkJni.sparkJniSingleton(appName, nativePath, jdkPath);
  }

  private boolean appNameIsSet() {
    return (initBits & INIT_BIT_APP_NAME) == 0;
  }

  private boolean nativePathIsSet() {
    return (initBits & INIT_BIT_NATIVE_PATH) == 0;
  }

  private boolean jdkPathIsSet() {
    return (initBits & INIT_BIT_JDK_PATH) == 0;
  }

  private void checkRequiredAttributes() throws IllegalStateException {
    if (initBits != 0) {
      throw new IllegalStateException(formatRequiredAttributesMessage());
    }
  }

  private String formatRequiredAttributesMessage() {
    List<String> attributes = Lists.newArrayList();
    if (!appNameIsSet()) attributes.add("appName");
    if (!nativePathIsSet()) attributes.add("nativePath");
    if (!jdkPathIsSet()) attributes.add("jdkPath");
    return "Cannot build sparkJniSingleton, some of required attributes are not set " + attributes;
  }
}
