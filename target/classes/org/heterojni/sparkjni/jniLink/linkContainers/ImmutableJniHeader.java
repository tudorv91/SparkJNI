package org.heterojni.sparkjni.jniLink.linkContainers;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
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
 * Immutable implementation of {@link JniHeader}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableJniHeader.builder()}.
 */
@SuppressWarnings({"all"})
@SuppressFBWarnings
@ParametersAreNonnullByDefault
@Generated({"Immutables.generator", "JniHeader"})
@Immutable
public final class ImmutableJniHeader extends JniHeader {
  private final File jniHeaderFile;
  private final ImmutableList<FunctionSignatureMapper> jniFunctions;
  private final String fileName;
  private final String fullyQualifiedJavaClassName;

  private ImmutableJniHeader(
      File jniHeaderFile,
      ImmutableList<FunctionSignatureMapper> jniFunctions,
      String fileName,
      String fullyQualifiedJavaClassName) {
    this.jniHeaderFile = jniHeaderFile;
    this.jniFunctions = jniFunctions;
    this.fileName = fileName;
    this.fullyQualifiedJavaClassName = fullyQualifiedJavaClassName;
  }

  /**
   * @return The value of the {@code jniHeaderFile} attribute
   */
  @Override
  public File jniHeaderFile() {
    return jniHeaderFile;
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
   * @return The value of the {@code fullyQualifiedJavaClassName} attribute
   */
  @Override
  public String fullyQualifiedJavaClassName() {
    return fullyQualifiedJavaClassName;
  }

  /**
   * Copy the current immutable object by setting a value for the {@link JniHeader#jniHeaderFile() jniHeaderFile} attribute.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for jniHeaderFile
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableJniHeader withJniHeaderFile(File value) {
    if (this.jniHeaderFile == value) return this;
    File newValue = Preconditions.checkNotNull(value, "jniHeaderFile");
    return new ImmutableJniHeader(newValue, this.jniFunctions, this.fileName, this.fullyQualifiedJavaClassName);
  }

  /**
   * Copy the current immutable object with elements that replace the content of {@link JniHeader#jniFunctions() jniFunctions}.
   * @param elements The elements to set
   * @return A modified copy of {@code this} object
   */
  public final ImmutableJniHeader withJniFunctions(FunctionSignatureMapper... elements) {
    ImmutableList<FunctionSignatureMapper> newValue = ImmutableList.copyOf(elements);
    return new ImmutableJniHeader(this.jniHeaderFile, newValue, this.fileName, this.fullyQualifiedJavaClassName);
  }

  /**
   * Copy the current immutable object with elements that replace the content of {@link JniHeader#jniFunctions() jniFunctions}.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param elements An iterable of jniFunctions elements to set
   * @return A modified copy of {@code this} object
   */
  public final ImmutableJniHeader withJniFunctions(Iterable<? extends FunctionSignatureMapper> elements) {
    if (this.jniFunctions == elements) return this;
    ImmutableList<FunctionSignatureMapper> newValue = ImmutableList.copyOf(elements);
    return new ImmutableJniHeader(this.jniHeaderFile, newValue, this.fileName, this.fullyQualifiedJavaClassName);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link JniHeader#fileName() fileName} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for fileName
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableJniHeader withFileName(String value) {
    if (this.fileName.equals(value)) return this;
    String newValue = Preconditions.checkNotNull(value, "fileName");
    return new ImmutableJniHeader(this.jniHeaderFile, this.jniFunctions, newValue, this.fullyQualifiedJavaClassName);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link JniHeader#fullyQualifiedJavaClassName() fullyQualifiedJavaClassName} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for fullyQualifiedJavaClassName
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableJniHeader withFullyQualifiedJavaClassName(String value) {
    if (this.fullyQualifiedJavaClassName.equals(value)) return this;
    String newValue = Preconditions.checkNotNull(value, "fullyQualifiedJavaClassName");
    return new ImmutableJniHeader(this.jniHeaderFile, this.jniFunctions, this.fileName, newValue);
  }

  /**
   * This instance is equal to all instances of {@code ImmutableJniHeader} that have equal attribute values.
   * @return {@code true} if {@code this} is equal to {@code another} instance
   */
  @Override
  public boolean equals(@Nullable Object another) {
    if (this == another) return true;
    return another instanceof ImmutableJniHeader
        && equalTo((ImmutableJniHeader) another);
  }

  private boolean equalTo(ImmutableJniHeader another) {
    return jniHeaderFile.equals(another.jniHeaderFile)
        && jniFunctions.equals(another.jniFunctions)
        && fileName.equals(another.fileName)
        && fullyQualifiedJavaClassName.equals(another.fullyQualifiedJavaClassName);
  }

  /**
   * Computes a hash code from attributes: {@code jniHeaderFile}, {@code jniFunctions}, {@code fileName}, {@code fullyQualifiedJavaClassName}.
   * @return hashCode value
   */
  @Override
  public int hashCode() {
    int h = 31;
    h = h * 17 + jniHeaderFile.hashCode();
    h = h * 17 + jniFunctions.hashCode();
    h = h * 17 + fileName.hashCode();
    h = h * 17 + fullyQualifiedJavaClassName.hashCode();
    return h;
  }

  /**
   * Prints the immutable value {@code JniHeader} with attribute values.
   * @return A string representation of the value
   */
  @Override
  public String toString() {
    return MoreObjects.toStringHelper("JniHeader")
        .omitNullValues()
        .add("jniHeaderFile", jniHeaderFile)
        .add("jniFunctions", jniFunctions)
        .add("fileName", fileName)
        .add("fullyQualifiedJavaClassName", fullyQualifiedJavaClassName)
        .toString();
  }

  /**
   * Creates an immutable copy of a {@link JniHeader} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param instance The instance to copy
   * @return A copied immutable JniHeader instance
   */
  public static ImmutableJniHeader copyOf(JniHeader instance) {
    if (instance instanceof ImmutableJniHeader) {
      return (ImmutableJniHeader) instance;
    }
    return ImmutableJniHeader.builder()
        .from(instance)
        .build();
  }

  /**
   * Creates a builder for {@link ImmutableJniHeader ImmutableJniHeader}.
   * @return A new ImmutableJniHeader builder
   */
  public static ImmutableJniHeader.Builder builder() {
    return new ImmutableJniHeader.Builder();
  }

  /**
   * Builds instances of type {@link ImmutableJniHeader ImmutableJniHeader}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  @NotThreadSafe
  public static final class Builder {
    private static final long INIT_BIT_JNI_HEADER_FILE = 0x1L;
    private static final long INIT_BIT_FILE_NAME = 0x2L;
    private static final long INIT_BIT_FULLY_QUALIFIED_JAVA_CLASS_NAME = 0x4L;
    private long initBits = 0x7L;

    private @Nullable File jniHeaderFile;
    private ImmutableList.Builder<FunctionSignatureMapper> jniFunctions = ImmutableList.builder();
    private @Nullable String fileName;
    private @Nullable String fullyQualifiedJavaClassName;

    private Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code JniHeader} instance.
     * Regular attribute values will be replaced with those from the given instance.
     * Absent optional values will not replace present values.
     * Collection elements and entries will be added, not replaced.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder from(JniHeader instance) {
      Preconditions.checkNotNull(instance, "instance");
      jniHeaderFile(instance.jniHeaderFile());
      addAllJniFunctions(instance.jniFunctions());
      fileName(instance.fileName());
      fullyQualifiedJavaClassName(instance.fullyQualifiedJavaClassName());
      return this;
    }

    /**
     * Initializes the value for the {@link JniHeader#jniHeaderFile() jniHeaderFile} attribute.
     * @param jniHeaderFile The value for jniHeaderFile 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder jniHeaderFile(File jniHeaderFile) {
      this.jniHeaderFile = Preconditions.checkNotNull(jniHeaderFile, "jniHeaderFile");
      initBits &= ~INIT_BIT_JNI_HEADER_FILE;
      return this;
    }

    /**
     * Adds one element to {@link JniHeader#jniFunctions() jniFunctions} list.
     * @param element A jniFunctions element
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder addJniFunctions(FunctionSignatureMapper element) {
      this.jniFunctions.add(element);
      return this;
    }

    /**
     * Adds elements to {@link JniHeader#jniFunctions() jniFunctions} list.
     * @param elements An array of jniFunctions elements
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder addJniFunctions(FunctionSignatureMapper... elements) {
      this.jniFunctions.add(elements);
      return this;
    }

    /**
     * Sets or replaces all elements for {@link JniHeader#jniFunctions() jniFunctions} list.
     * @param elements An iterable of jniFunctions elements
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder jniFunctions(Iterable<? extends FunctionSignatureMapper> elements) {
      this.jniFunctions = ImmutableList.builder();
      return addAllJniFunctions(elements);
    }

    /**
     * Adds elements to {@link JniHeader#jniFunctions() jniFunctions} list.
     * @param elements An iterable of jniFunctions elements
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder addAllJniFunctions(Iterable<? extends FunctionSignatureMapper> elements) {
      this.jniFunctions.addAll(elements);
      return this;
    }

    /**
     * Initializes the value for the {@link JniHeader#fileName() fileName} attribute.
     * @param fileName The value for fileName 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder fileName(String fileName) {
      this.fileName = Preconditions.checkNotNull(fileName, "fileName");
      initBits &= ~INIT_BIT_FILE_NAME;
      return this;
    }

    /**
     * Initializes the value for the {@link JniHeader#fullyQualifiedJavaClassName() fullyQualifiedJavaClassName} attribute.
     * @param fullyQualifiedJavaClassName The value for fullyQualifiedJavaClassName 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder fullyQualifiedJavaClassName(String fullyQualifiedJavaClassName) {
      this.fullyQualifiedJavaClassName = Preconditions.checkNotNull(fullyQualifiedJavaClassName, "fullyQualifiedJavaClassName");
      initBits &= ~INIT_BIT_FULLY_QUALIFIED_JAVA_CLASS_NAME;
      return this;
    }

    /**
     * Builds a new {@link ImmutableJniHeader ImmutableJniHeader}.
     * @return An immutable instance of JniHeader
     * @throws java.lang.IllegalStateException if any required attributes are missing
     */
    public ImmutableJniHeader build() {
      if (initBits != 0) {
        throw new IllegalStateException(formatRequiredAttributesMessage());
      }
      return new ImmutableJniHeader(jniHeaderFile, jniFunctions.build(), fileName, fullyQualifiedJavaClassName);
    }

    private String formatRequiredAttributesMessage() {
      List<String> attributes = Lists.newArrayList();
      if ((initBits & INIT_BIT_JNI_HEADER_FILE) != 0) attributes.add("jniHeaderFile");
      if ((initBits & INIT_BIT_FILE_NAME) != 0) attributes.add("fileName");
      if ((initBits & INIT_BIT_FULLY_QUALIFIED_JAVA_CLASS_NAME) != 0) attributes.add("fullyQualifiedJavaClassName");
      return "Cannot build JniHeader, some of required attributes are not set " + attributes;
    }
  }
}
