package org.heterojni.sparkjni.jniLink.linkHandlers;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.annotation.Generated;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.List;

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
  private final String kernelWrapperFileName;
  private final ImmutableList<UserNativeFunction> userNativeFunctions;
  private final String nativePath;

  private ImmutableKernelFile(
      String kernelWrapperFileName,
      ImmutableList<UserNativeFunction> userNativeFunctions,
      String nativePath) {
    this.kernelWrapperFileName = kernelWrapperFileName;
    this.userNativeFunctions = userNativeFunctions;
    this.nativePath = nativePath;
  }

  /**
   * @return The value of the {@code kernelWrapperFileName} attribute
   */
  @Override
  public String kernelWrapperFileName() {
    return kernelWrapperFileName;
  }

  /**
   * @return The value of the {@code userNativeFunctions} attribute
   */
  @Override
  public ImmutableList<UserNativeFunction> userNativeFunctions() {
    return userNativeFunctions;
  }

  /**
   * @return The value of the {@code nativePath} attribute
   */
  @Override
  public String nativePath() {
    return nativePath;
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
    return new ImmutableKernelFile(newValue, this.userNativeFunctions, this.nativePath);
  }

  /**
   * Copy the current immutable object with elements that replace the content of {@link KernelFile#userNativeFunctions() userNativeFunctions}.
   * @param elements The elements to set
   * @return A modified copy of {@code this} object
   */
  public final ImmutableKernelFile withUserNativeFunctions(UserNativeFunction... elements) {
    ImmutableList<UserNativeFunction> newValue = ImmutableList.copyOf(elements);
    return new ImmutableKernelFile(this.kernelWrapperFileName, newValue, this.nativePath);
  }

  /**
   * Copy the current immutable object with elements that replace the content of {@link KernelFile#userNativeFunctions() userNativeFunctions}.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param elements An iterable of userNativeFunctions elements to set
   * @return A modified copy of {@code this} object
   */
  public final ImmutableKernelFile withUserNativeFunctions(Iterable<? extends UserNativeFunction> elements) {
    if (this.userNativeFunctions == elements) return this;
    ImmutableList<UserNativeFunction> newValue = ImmutableList.copyOf(elements);
    return new ImmutableKernelFile(this.kernelWrapperFileName, newValue, this.nativePath);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link KernelFile#nativePath() nativePath} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for nativePath
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableKernelFile withNativePath(String value) {
    if (this.nativePath.equals(value)) return this;
    String newValue = Preconditions.checkNotNull(value, "nativePath");
    return new ImmutableKernelFile(this.kernelWrapperFileName, this.userNativeFunctions, newValue);
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
    return kernelWrapperFileName.equals(another.kernelWrapperFileName)
        && userNativeFunctions.equals(another.userNativeFunctions)
        && nativePath.equals(another.nativePath);
  }

  /**
   * Computes a hash code from attributes: {@code kernelWrapperFileName}, {@code userNativeFunctions}, {@code nativePath}.
   * @return hashCode value
   */
  @Override
  public int hashCode() {
    int h = 31;
    h = h * 17 + kernelWrapperFileName.hashCode();
    h = h * 17 + userNativeFunctions.hashCode();
    h = h * 17 + nativePath.hashCode();
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
        .add("kernelWrapperFileName", kernelWrapperFileName)
        .add("userNativeFunctions", userNativeFunctions)
        .add("nativePath", nativePath)
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
    private static final long INIT_BIT_KERNEL_WRAPPER_FILE_NAME = 0x1L;
    private static final long INIT_BIT_NATIVE_PATH = 0x2L;
    private long initBits = 0x3L;

    private @Nullable String kernelWrapperFileName;
    private ImmutableList.Builder<UserNativeFunction> userNativeFunctions = ImmutableList.builder();
    private @Nullable String nativePath;

    private Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code KernelFile} instance.
     * Regular attribute values will be replaced with those from the given instance.
     * Absent optional values will not replace present values.
     * Collection elements and entries will be added, not replaced.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder from(KernelFile instance) {
      Preconditions.checkNotNull(instance, "instance");
      kernelWrapperFileName(instance.kernelWrapperFileName());
      addAllUserNativeFunctions(instance.userNativeFunctions());
      nativePath(instance.nativePath());
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
     * Adds one element to {@link KernelFile#userNativeFunctions() userNativeFunctions} list.
     * @param element A userNativeFunctions element
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder addUserNativeFunctions(UserNativeFunction element) {
      this.userNativeFunctions.add(element);
      return this;
    }

    /**
     * Adds elements to {@link KernelFile#userNativeFunctions() userNativeFunctions} list.
     * @param elements An array of userNativeFunctions elements
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder addUserNativeFunctions(UserNativeFunction... elements) {
      this.userNativeFunctions.add(elements);
      return this;
    }

    /**
     * Sets or replaces all elements for {@link KernelFile#userNativeFunctions() userNativeFunctions} list.
     * @param elements An iterable of userNativeFunctions elements
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder userNativeFunctions(Iterable<? extends UserNativeFunction> elements) {
      this.userNativeFunctions = ImmutableList.builder();
      return addAllUserNativeFunctions(elements);
    }

    /**
     * Adds elements to {@link KernelFile#userNativeFunctions() userNativeFunctions} list.
     * @param elements An iterable of userNativeFunctions elements
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder addAllUserNativeFunctions(Iterable<? extends UserNativeFunction> elements) {
      this.userNativeFunctions.addAll(elements);
      return this;
    }

    /**
     * Initializes the value for the {@link KernelFile#nativePath() nativePath} attribute.
     * @param nativePath The value for nativePath 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder nativePath(String nativePath) {
      this.nativePath = Preconditions.checkNotNull(nativePath, "nativePath");
      initBits &= ~INIT_BIT_NATIVE_PATH;
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
      return new ImmutableKernelFile(kernelWrapperFileName, userNativeFunctions.build(), nativePath);
    }

    private String formatRequiredAttributesMessage() {
      List<String> attributes = Lists.newArrayList();
      if ((initBits & INIT_BIT_KERNEL_WRAPPER_FILE_NAME) != 0) attributes.add("kernelWrapperFileName");
      if ((initBits & INIT_BIT_NATIVE_PATH) != 0) attributes.add("nativePath");
      return "Cannot build KernelFile, some of required attributes are not set " + attributes;
    }
  }
}
