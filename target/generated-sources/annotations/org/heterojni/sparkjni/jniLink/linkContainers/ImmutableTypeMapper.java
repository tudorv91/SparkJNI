package org.heterojni.sparkjni.jniLink.linkContainers;

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
import org.heterojni.sparkjni.utils.CppBean;

/**
 * Immutable implementation of {@link TypeMapper}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableTypeMapper.builder()}.
 */
@SuppressWarnings({"all"})
@SuppressFBWarnings
@ParametersAreNonnullByDefault
@Generated({"Immutables.generator", "TypeMapper"})
@Immutable
public final class ImmutableTypeMapper implements TypeMapper {
  private final Class javaType;
  private final CppBean cppType;
  private final String jniType;

  private ImmutableTypeMapper(Class javaType, CppBean cppType, String jniType) {
    this.javaType = javaType;
    this.cppType = cppType;
    this.jniType = jniType;
  }

  /**
   * @return The value of the {@code javaType} attribute
   */
  @Override
  public Class javaType() {
    return javaType;
  }

  /**
   * @return The value of the {@code cppType} attribute
   */
  @Override
  public CppBean cppType() {
    return cppType;
  }

  /**
   * @return The value of the {@code jniType} attribute
   */
  @Override
  public String jniType() {
    return jniType;
  }

  /**
   * Copy the current immutable object by setting a value for the {@link TypeMapper#javaType() javaType} attribute.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for javaType
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableTypeMapper withJavaType(Class value) {
    if (this.javaType == value) return this;
    Class newValue = Preconditions.checkNotNull(value, "javaType");
    return new ImmutableTypeMapper(newValue, this.cppType, this.jniType);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link TypeMapper#cppType() cppType} attribute.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for cppType
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableTypeMapper withCppType(CppBean value) {
    if (this.cppType == value) return this;
    CppBean newValue = Preconditions.checkNotNull(value, "cppType");
    return new ImmutableTypeMapper(this.javaType, newValue, this.jniType);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link TypeMapper#jniType() jniType} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for jniType
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableTypeMapper withJniType(String value) {
    if (this.jniType.equals(value)) return this;
    String newValue = Preconditions.checkNotNull(value, "jniType");
    return new ImmutableTypeMapper(this.javaType, this.cppType, newValue);
  }

  /**
   * This instance is equal to all instances of {@code ImmutableTypeMapper} that have equal attribute values.
   * @return {@code true} if {@code this} is equal to {@code another} instance
   */
  @Override
  public boolean equals(@Nullable Object another) {
    if (this == another) return true;
    return another instanceof ImmutableTypeMapper
        && equalTo((ImmutableTypeMapper) another);
  }

  private boolean equalTo(ImmutableTypeMapper another) {
    return javaType.equals(another.javaType)
        && cppType.equals(another.cppType)
        && jniType.equals(another.jniType);
  }

  /**
   * Computes a hash code from attributes: {@code javaType}, {@code cppType}, {@code jniType}.
   * @return hashCode value
   */
  @Override
  public int hashCode() {
    int h = 31;
    h = h * 17 + javaType.hashCode();
    h = h * 17 + cppType.hashCode();
    h = h * 17 + jniType.hashCode();
    return h;
  }

  /**
   * Prints the immutable value {@code TypeMapper} with attribute values.
   * @return A string representation of the value
   */
  @Override
  public String toString() {
    return MoreObjects.toStringHelper("TypeMapper")
        .omitNullValues()
        .add("javaType", javaType)
        .add("cppType", cppType)
        .add("jniType", jniType)
        .toString();
  }

  /**
   * Creates an immutable copy of a {@link TypeMapper} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param instance The instance to copy
   * @return A copied immutable TypeMapper instance
   */
  public static ImmutableTypeMapper copyOf(TypeMapper instance) {
    if (instance instanceof ImmutableTypeMapper) {
      return (ImmutableTypeMapper) instance;
    }
    return ImmutableTypeMapper.builder()
        .from(instance)
        .build();
  }

  /**
   * Creates a builder for {@link ImmutableTypeMapper ImmutableTypeMapper}.
   * @return A new ImmutableTypeMapper builder
   */
  public static ImmutableTypeMapper.Builder builder() {
    return new ImmutableTypeMapper.Builder();
  }

  /**
   * Builds instances of type {@link ImmutableTypeMapper ImmutableTypeMapper}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  @NotThreadSafe
  public static final class Builder {
    private static final long INIT_BIT_JAVA_TYPE = 0x1L;
    private static final long INIT_BIT_CPP_TYPE = 0x2L;
    private static final long INIT_BIT_JNI_TYPE = 0x4L;
    private long initBits = 0x7L;

    private @Nullable Class javaType;
    private @Nullable CppBean cppType;
    private @Nullable String jniType;

    private Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code TypeMapper} instance.
     * Regular attribute values will be replaced with those from the given instance.
     * Absent optional values will not replace present values.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder from(TypeMapper instance) {
      Preconditions.checkNotNull(instance, "instance");
      javaType(instance.javaType());
      cppType(instance.cppType());
      jniType(instance.jniType());
      return this;
    }

    /**
     * Initializes the value for the {@link TypeMapper#javaType() javaType} attribute.
     * @param javaType The value for javaType 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder javaType(Class javaType) {
      this.javaType = Preconditions.checkNotNull(javaType, "javaType");
      initBits &= ~INIT_BIT_JAVA_TYPE;
      return this;
    }

    /**
     * Initializes the value for the {@link TypeMapper#cppType() cppType} attribute.
     * @param cppType The value for cppType 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder cppType(CppBean cppType) {
      this.cppType = Preconditions.checkNotNull(cppType, "cppType");
      initBits &= ~INIT_BIT_CPP_TYPE;
      return this;
    }

    /**
     * Initializes the value for the {@link TypeMapper#jniType() jniType} attribute.
     * @param jniType The value for jniType 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder jniType(String jniType) {
      this.jniType = Preconditions.checkNotNull(jniType, "jniType");
      initBits &= ~INIT_BIT_JNI_TYPE;
      return this;
    }

    /**
     * Builds a new {@link ImmutableTypeMapper ImmutableTypeMapper}.
     * @return An immutable instance of TypeMapper
     * @throws java.lang.IllegalStateException if any required attributes are missing
     */
    public ImmutableTypeMapper build() {
      if (initBits != 0) {
        throw new IllegalStateException(formatRequiredAttributesMessage());
      }
      return new ImmutableTypeMapper(javaType, cppType, jniType);
    }

    private String formatRequiredAttributesMessage() {
      List<String> attributes = Lists.newArrayList();
      if ((initBits & INIT_BIT_JAVA_TYPE) != 0) attributes.add("javaType");
      if ((initBits & INIT_BIT_CPP_TYPE) != 0) attributes.add("cppType");
      if ((initBits & INIT_BIT_JNI_TYPE) != 0) attributes.add("jniType");
      return "Cannot build TypeMapper, some of required attributes are not set " + attributes;
    }
  }
}
