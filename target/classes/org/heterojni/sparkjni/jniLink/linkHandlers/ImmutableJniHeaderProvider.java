package org.heterojni.sparkjni.jniLink.linkHandlers;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.util.List;
import javax.annotation.Generated;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * Immutable implementation of {@link JniHeaderProvider}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableJniHeaderProvider.builder()}.
 */
@SuppressWarnings({"all"})
@SuppressFBWarnings
@ParametersAreNonnullByDefault
@Generated({"Immutables.generator", "JniHeaderProvider"})
@Immutable
public final class ImmutableJniHeaderProvider
    extends JniHeaderProvider {
  private final File jniHeaderFile;

  private ImmutableJniHeaderProvider(File jniHeaderFile) {
    this.jniHeaderFile = jniHeaderFile;
  }

  /**
   * @return The value of the {@code jniHeaderFile} attribute
   */
  @Override
  File jniHeaderFile() {
    return jniHeaderFile;
  }

  /**
   * Copy the current immutable object by setting a value for the {@link JniHeaderProvider#jniHeaderFile() jniHeaderFile} attribute.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for jniHeaderFile
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableJniHeaderProvider withJniHeaderFile(File value) {
    if (this.jniHeaderFile == value) return this;
    File newValue = Preconditions.checkNotNull(value, "jniHeaderFile");
    return new ImmutableJniHeaderProvider(newValue);
  }

  /**
   * This instance is equal to all instances of {@code ImmutableJniHeaderProvider} that have equal attribute values.
   * @return {@code true} if {@code this} is equal to {@code another} instance
   */
  @Override
  public boolean equals(@Nullable Object another) {
    if (this == another) return true;
    return another instanceof ImmutableJniHeaderProvider
        && equalTo((ImmutableJniHeaderProvider) another);
  }

  private boolean equalTo(ImmutableJniHeaderProvider another) {
    return jniHeaderFile.equals(another.jniHeaderFile);
  }

  /**
   * Computes a hash code from attributes: {@code jniHeaderFile}.
   * @return hashCode value
   */
  @Override
  public int hashCode() {
    int h = 31;
    h = h * 17 + jniHeaderFile.hashCode();
    return h;
  }

  /**
   * Prints the immutable value {@code JniHeaderProvider} with attribute values.
   * @return A string representation of the value
   */
  @Override
  public String toString() {
    return MoreObjects.toStringHelper("JniHeaderProvider")
        .omitNullValues()
        .add("jniHeaderFile", jniHeaderFile)
        .toString();
  }

  /**
   * Creates an immutable copy of a {@link JniHeaderProvider} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param instance The instance to copy
   * @return A copied immutable JniHeaderProvider instance
   */
  public static ImmutableJniHeaderProvider copyOf(JniHeaderProvider instance) {
    if (instance instanceof ImmutableJniHeaderProvider) {
      return (ImmutableJniHeaderProvider) instance;
    }
    return ImmutableJniHeaderProvider.builder()
        .from(instance)
        .build();
  }

  /**
   * Creates a builder for {@link ImmutableJniHeaderProvider ImmutableJniHeaderProvider}.
   * @return A new ImmutableJniHeaderProvider builder
   */
  public static ImmutableJniHeaderProvider.Builder builder() {
    return new ImmutableJniHeaderProvider.Builder();
  }

  /**
   * Builds instances of type {@link ImmutableJniHeaderProvider ImmutableJniHeaderProvider}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  @NotThreadSafe
  public static final class Builder {
    private static final long INIT_BIT_JNI_HEADER_FILE = 0x1L;
    private long initBits = 0x1L;

    private @Nullable File jniHeaderFile;

    private Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code JniHeaderProvider} instance.
     * Regular attribute values will be replaced with those from the given instance.
     * Absent optional values will not replace present values.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder from(JniHeaderProvider instance) {
      Preconditions.checkNotNull(instance, "instance");
      jniHeaderFile(instance.jniHeaderFile());
      return this;
    }

    /**
     * Initializes the value for the {@link JniHeaderProvider#jniHeaderFile() jniHeaderFile} attribute.
     * @param jniHeaderFile The value for jniHeaderFile 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder jniHeaderFile(File jniHeaderFile) {
      this.jniHeaderFile = Preconditions.checkNotNull(jniHeaderFile, "jniHeaderFile");
      initBits &= ~INIT_BIT_JNI_HEADER_FILE;
      return this;
    }

    /**
     * Builds a new {@link ImmutableJniHeaderProvider ImmutableJniHeaderProvider}.
     * @return An immutable instance of JniHeaderProvider
     * @throws java.lang.IllegalStateException if any required attributes are missing
     */
    public ImmutableJniHeaderProvider build() {
      if (initBits != 0) {
        throw new IllegalStateException(formatRequiredAttributesMessage());
      }
      return new ImmutableJniHeaderProvider(jniHeaderFile);
    }

    private String formatRequiredAttributesMessage() {
      List<String> attributes = Lists.newArrayList();
      if ((initBits & INIT_BIT_JNI_HEADER_FILE) != 0) attributes.add("jniHeaderFile");
      return "Cannot build JniHeaderProvider, some of required attributes are not set " + attributes;
    }
  }
}
