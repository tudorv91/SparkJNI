package org.heterojni.sparkjni.jniLink.linkContainers;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import javax.annotation.Generated;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * Immutable implementation of {@link JniHeaderContainer}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableJniHeaderContainer.builder()}.
 */
@SuppressWarnings({"all"})
@SuppressFBWarnings
@ParametersAreNonnullByDefault
@Generated({"Immutables.generator", "JniHeaderContainer"})
@Immutable
public final class ImmutableJniHeaderContainer
    implements JniHeaderContainer {
  private final ImmutableList<FunctionSignatureMapper> jniFunctions;
  private final String fileName;

  private ImmutableJniHeaderContainer(
      ImmutableList<FunctionSignatureMapper> jniFunctions,
      String fileName) {
    this.jniFunctions = jniFunctions;
    this.fileName = fileName;
  }

  /**
   * @return The value of the {@code jniFunctions} attribute
   */
  @Override
  public ImmutableList<FunctionSignatureMapper> jniFunctions() {
    return jniFunctions;
  }

  /**
   * @return The value of the {@code fileName} attribute
   */
  @Override
  public String fileName() {
    return fileName;
  }

  /**
   * Copy the current immutable object with elements that replace the content of {@link JniHeaderContainer#jniFunctions() jniFunctions}.
   * @param elements The elements to set
   * @return A modified copy of {@code this} object
   */
  public final ImmutableJniHeaderContainer withJniFunctions(FunctionSignatureMapper... elements) {
    ImmutableList<FunctionSignatureMapper> newValue = ImmutableList.copyOf(elements);
    return new ImmutableJniHeaderContainer(newValue, this.fileName);
  }

  /**
   * Copy the current immutable object with elements that replace the content of {@link JniHeaderContainer#jniFunctions() jniFunctions}.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param elements An iterable of jniFunctions elements to set
   * @return A modified copy of {@code this} object
   */
  public final ImmutableJniHeaderContainer withJniFunctions(Iterable<? extends FunctionSignatureMapper> elements) {
    if (this.jniFunctions == elements) return this;
    ImmutableList<FunctionSignatureMapper> newValue = ImmutableList.copyOf(elements);
    return new ImmutableJniHeaderContainer(newValue, this.fileName);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link JniHeaderContainer#fileName() fileName} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for fileName
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableJniHeaderContainer withFileName(String value) {
    if (this.fileName.equals(value)) return this;
    String newValue = Preconditions.checkNotNull(value, "fileName");
    return new ImmutableJniHeaderContainer(this.jniFunctions, newValue);
  }

  /**
   * This instance is equal to all instances of {@code ImmutableJniHeaderContainer} that have equal attribute values.
   * @return {@code true} if {@code this} is equal to {@code another} instance
   */
  @Override
  public boolean equals(@Nullable Object another) {
    if (this == another) return true;
    return another instanceof ImmutableJniHeaderContainer
        && equalTo((ImmutableJniHeaderContainer) another);
  }

  private boolean equalTo(ImmutableJniHeaderContainer another) {
    return jniFunctions.equals(another.jniFunctions)
        && fileName.equals(another.fileName);
  }

  /**
   * Computes a hash code from attributes: {@code jniFunctions}, {@code fileName}.
   * @return hashCode value
   */
  @Override
  public int hashCode() {
    int h = 31;
    h = h * 17 + jniFunctions.hashCode();
    h = h * 17 + fileName.hashCode();
    return h;
  }

  /**
   * Prints the immutable value {@code JniHeaderContainer} with attribute values.
   * @return A string representation of the value
   */
  @Override
  public String toString() {
    return MoreObjects.toStringHelper("JniHeaderContainer")
        .omitNullValues()
        .add("jniFunctions", jniFunctions)
        .add("fileName", fileName)
        .toString();
  }

  /**
   * Creates an immutable copy of a {@link JniHeaderContainer} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param instance The instance to copy
   * @return A copied immutable JniHeaderContainer instance
   */
  public static ImmutableJniHeaderContainer copyOf(JniHeaderContainer instance) {
    if (instance instanceof ImmutableJniHeaderContainer) {
      return (ImmutableJniHeaderContainer) instance;
    }
    return ImmutableJniHeaderContainer.builder()
        .from(instance)
        .build();
  }

  /**
   * Creates a builder for {@link ImmutableJniHeaderContainer ImmutableJniHeaderContainer}.
   * @return A new ImmutableJniHeaderContainer builder
   */
  public static ImmutableJniHeaderContainer.Builder builder() {
    return new ImmutableJniHeaderContainer.Builder();
  }

  /**
   * Builds instances of type {@link ImmutableJniHeaderContainer ImmutableJniHeaderContainer}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  @NotThreadSafe
  public static final class Builder {
    private static final long INIT_BIT_FILE_NAME = 0x1L;
    private long initBits = 0x1L;

    private ImmutableList.Builder<FunctionSignatureMapper> jniFunctions = ImmutableList.builder();
    private @Nullable String fileName;

    private Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code JniHeaderContainer} instance.
     * Regular attribute values will be replaced with those from the given instance.
     * Absent optional values will not replace present values.
     * Collection elements and entries will be added, not replaced.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder from(JniHeaderContainer instance) {
      Preconditions.checkNotNull(instance, "instance");
      addAllJniFunctions(instance.jniFunctions());
      fileName(instance.fileName());
      return this;
    }

    /**
     * Adds one element to {@link JniHeaderContainer#jniFunctions() jniFunctions} list.
     * @param element A jniFunctions element
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder addJniFunctions(FunctionSignatureMapper element) {
      this.jniFunctions.add(element);
      return this;
    }

    /**
     * Adds elements to {@link JniHeaderContainer#jniFunctions() jniFunctions} list.
     * @param elements An array of jniFunctions elements
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder addJniFunctions(FunctionSignatureMapper... elements) {
      this.jniFunctions.add(elements);
      return this;
    }

    /**
     * Sets or replaces all elements for {@link JniHeaderContainer#jniFunctions() jniFunctions} list.
     * @param elements An iterable of jniFunctions elements
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder jniFunctions(Iterable<? extends FunctionSignatureMapper> elements) {
      this.jniFunctions = ImmutableList.builder();
      return addAllJniFunctions(elements);
    }

    /**
     * Adds elements to {@link JniHeaderContainer#jniFunctions() jniFunctions} list.
     * @param elements An iterable of jniFunctions elements
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder addAllJniFunctions(Iterable<? extends FunctionSignatureMapper> elements) {
      this.jniFunctions.addAll(elements);
      return this;
    }

    /**
     * Initializes the value for the {@link JniHeaderContainer#fileName() fileName} attribute.
     * @param fileName The value for fileName 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder fileName(String fileName) {
      this.fileName = Preconditions.checkNotNull(fileName, "fileName");
      initBits &= ~INIT_BIT_FILE_NAME;
      return this;
    }

    /**
     * Builds a new {@link ImmutableJniHeaderContainer ImmutableJniHeaderContainer}.
     * @return An immutable instance of JniHeaderContainer
     * @throws java.lang.IllegalStateException if any required attributes are missing
     */
    public ImmutableJniHeaderContainer build() {
      if (initBits != 0) {
        throw new IllegalStateException(formatRequiredAttributesMessage());
      }
      return new ImmutableJniHeaderContainer(jniFunctions.build(), fileName);
    }

    private String formatRequiredAttributesMessage() {
      List<String> attributes = Lists.newArrayList();
      if ((initBits & INIT_BIT_FILE_NAME) != 0) attributes.add("fileName");
      return "Cannot build JniHeaderContainer, some of required attributes are not set " + attributes;
    }
  }
}
