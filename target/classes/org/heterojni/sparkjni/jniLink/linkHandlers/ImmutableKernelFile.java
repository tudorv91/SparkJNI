package org.heterojni.sparkjni.jniLink.linkHandlers;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import javax.annotation.Generated;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import org.heterojni.sparkjni.jniLink.linkContainers.JniRootContainer;

/**
 * Immutable implementation of {@link KernelFile}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableKernelFile.builder()}.
 */
@SuppressWarnings({"all"})
@SuppressFBWarnings
@ParametersAreNonnullByDefault
@Generated({"Immutables.generator", "KernelFile"})
@Immutable
public final class ImmutableKernelFile extends KernelFile {
  private final JniRootContainer jniRootContainer;
  private final String kernelWrapperFileName;

  private ImmutableKernelFile(
      JniRootContainer jniRootContainer,
      String kernelWrapperFileName) {
    this.jniRootContainer = jniRootContainer;
    this.kernelWrapperFileName = kernelWrapperFileName;
  }

  /**
   * @return The value of the {@code jniRootContainer} attribute
   */
  @Override
  public JniRootContainer jniRootContainer() {
    return jniRootContainer;
  }

  /**
   * @return The value of the {@code kernelWrapperFileName} attribute
   */
  @Override
  public String kernelWrapperFileName() {
    return kernelWrapperFileName;
  }

  /**
   * Copy the current immutable object by setting a value for the {@link KernelFile#jniRootContainer() jniRootContainer} attribute.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for jniRootContainer
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableKernelFile withJniRootContainer(JniRootContainer value) {
    if (this.jniRootContainer == value) return this;
    JniRootContainer newValue = Preconditions.checkNotNull(value, "jniRootContainer");
    return new ImmutableKernelFile(newValue, this.kernelWrapperFileName);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link KernelFile#kernelWrapperFileName() kernelWrapperFileName} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for kernelWrapperFileName
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableKernelFile withKernelWrapperFileName(String value) {
    if (this.kernelWrapperFileName.equals(value)) return this;
    String newValue = Preconditions.checkNotNull(value, "kernelWrapperFileName");
    return new ImmutableKernelFile(this.jniRootContainer, newValue);
  }

  /**
   * This instance is equal to all instances of {@code ImmutableKernelFile} that have equal attribute values.
   * @return {@code true} if {@code this} is equal to {@code another} instance
   */
  @Override
  public boolean equals(@Nullable Object another) {
    if (this == another) return true;
    return another instanceof ImmutableKernelFile
        && equalTo((ImmutableKernelFile) another);
  }

  private boolean equalTo(ImmutableKernelFile another) {
    return jniRootContainer.equals(another.jniRootContainer)
        && kernelWrapperFileName.equals(another.kernelWrapperFileName);
  }

  /**
   * Computes a hash code from attributes: {@code jniRootContainer}, {@code kernelWrapperFileName}.
   * @return hashCode value
   */
  @Override
  public int hashCode() {
    int h = 31;
    h = h * 17 + jniRootContainer.hashCode();
    h = h * 17 + kernelWrapperFileName.hashCode();
    return h;
  }

  /**
   * Prints the immutable value {@code KernelFile} with attribute values.
   * @return A string representation of the value
   */
  @Override
  public String toString() {
    return MoreObjects.toStringHelper("KernelFile")
        .omitNullValues()
        .add("jniRootContainer", jniRootContainer)
        .add("kernelWrapperFileName", kernelWrapperFileName)
        .toString();
  }

  /**
   * Creates an immutable copy of a {@link KernelFile} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param instance The instance to copy
   * @return A copied immutable KernelFile instance
   */
  public static ImmutableKernelFile copyOf(KernelFile instance) {
    if (instance instanceof ImmutableKernelFile) {
      return (ImmutableKernelFile) instance;
    }
    return ImmutableKernelFile.builder()
        .from(instance)
        .build();
  }

  /**
   * Creates a builder for {@link ImmutableKernelFile ImmutableKernelFile}.
   * @return A new ImmutableKernelFile builder
   */
  public static ImmutableKernelFile.Builder builder() {
    return new ImmutableKernelFile.Builder();
  }

  /**
   * Builds instances of type {@link ImmutableKernelFile ImmutableKernelFile}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  @NotThreadSafe
  public static final class Builder {
    private static final long INIT_BIT_JNI_ROOT_CONTAINER = 0x1L;
    private static final long INIT_BIT_KERNEL_WRAPPER_FILE_NAME = 0x2L;
    private long initBits = 0x3L;

    private @Nullable JniRootContainer jniRootContainer;
    private @Nullable String kernelWrapperFileName;

    private Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code KernelFile} instance.
     * Regular attribute values will be replaced with those from the given instance.
     * Absent optional values will not replace present values.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder from(KernelFile instance) {
      Preconditions.checkNotNull(instance, "instance");
      jniRootContainer(instance.jniRootContainer());
      kernelWrapperFileName(instance.kernelWrapperFileName());
      return this;
    }

    /**
     * Initializes the value for the {@link KernelFile#jniRootContainer() jniRootContainer} attribute.
     * @param jniRootContainer The value for jniRootContainer 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder jniRootContainer(JniRootContainer jniRootContainer) {
      this.jniRootContainer = Preconditions.checkNotNull(jniRootContainer, "jniRootContainer");
      initBits &= ~INIT_BIT_JNI_ROOT_CONTAINER;
      return this;
    }

    /**
     * Initializes the value for the {@link KernelFile#kernelWrapperFileName() kernelWrapperFileName} attribute.
     * @param kernelWrapperFileName The value for kernelWrapperFileName 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder kernelWrapperFileName(String kernelWrapperFileName) {
      this.kernelWrapperFileName = Preconditions.checkNotNull(kernelWrapperFileName, "kernelWrapperFileName");
      initBits &= ~INIT_BIT_KERNEL_WRAPPER_FILE_NAME;
      return this;
    }

    /**
     * Builds a new {@link ImmutableKernelFile ImmutableKernelFile}.
     * @return An immutable instance of KernelFile
     * @throws java.lang.IllegalStateException if any required attributes are missing
     */
    public ImmutableKernelFile build() {
      if (initBits != 0) {
        throw new IllegalStateException(formatRequiredAttributesMessage());
      }
      return new ImmutableKernelFile(jniRootContainer, kernelWrapperFileName);
    }

    private String formatRequiredAttributesMessage() {
      List<String> attributes = Lists.newArrayList();
      if ((initBits & INIT_BIT_JNI_ROOT_CONTAINER) != 0) attributes.add("jniRootContainer");
      if ((initBits & INIT_BIT_KERNEL_WRAPPER_FILE_NAME) != 0) attributes.add("kernelWrapperFileName");
      return "Cannot build KernelFile, some of required attributes are not set " + attributes;
    }
  }
}
