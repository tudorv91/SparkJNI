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

/**
 * Immutable implementation of {@link EntityNameMapper}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableEntityNameMapper.builder()}.
 */
@SuppressWarnings({"all"})
@SuppressFBWarnings
@ParametersAreNonnullByDefault
@Generated({"Immutables.generator", "EntityNameMapper"})
@Immutable
public final class ImmutableEntityNameMapper
    implements EntityNameMapper {
  private final String javaName;
  private final String jniName;
  private final String cppName;

  private ImmutableEntityNameMapper(String javaName, String jniName, String cppName) {
    this.javaName = javaName;
    this.jniName = jniName;
    this.cppName = cppName;
  }

  /**
   * @return The value of the {@code javaName} attribute
   */
  @Override
  public String javaName() {
    return javaName;
  }

  /**
   * @return The value of the {@code jniName} attribute
   */
  @Override
  public String jniName() {
    return jniName;
  }

  /**
   * @return The value of the {@code cppName} attribute
   */
  @Override
  public String cppName() {
    return cppName;
  }

  /**
   * Copy the current immutable object by setting a value for the {@link EntityNameMapper#javaName() javaName} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for javaName
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableEntityNameMapper withJavaName(String value) {
    if (this.javaName.equals(value)) return this;
    String newValue = Preconditions.checkNotNull(value, "javaName");
    return new ImmutableEntityNameMapper(newValue, this.jniName, this.cppName);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link EntityNameMapper#jniName() jniName} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for jniName
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableEntityNameMapper withJniName(String value) {
    if (this.jniName.equals(value)) return this;
    String newValue = Preconditions.checkNotNull(value, "jniName");
    return new ImmutableEntityNameMapper(this.javaName, newValue, this.cppName);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link EntityNameMapper#cppName() cppName} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for cppName
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableEntityNameMapper withCppName(String value) {
    if (this.cppName.equals(value)) return this;
    String newValue = Preconditions.checkNotNull(value, "cppName");
    return new ImmutableEntityNameMapper(this.javaName, this.jniName, newValue);
  }

  /**
   * This instance is equal to all instances of {@code ImmutableEntityNameMapper} that have equal attribute values.
   * @return {@code true} if {@code this} is equal to {@code another} instance
   */
  @Override
  public boolean equals(@Nullable Object another) {
    if (this == another) return true;
    return another instanceof ImmutableEntityNameMapper
        && equalTo((ImmutableEntityNameMapper) another);
  }

  private boolean equalTo(ImmutableEntityNameMapper another) {
    return javaName.equals(another.javaName)
        && jniName.equals(another.jniName)
        && cppName.equals(another.cppName);
  }

  /**
   * Computes a hash code from attributes: {@code javaName}, {@code jniName}, {@code cppName}.
   * @return hashCode value
   */
  @Override
  public int hashCode() {
    int h = 31;
    h = h * 17 + javaName.hashCode();
    h = h * 17 + jniName.hashCode();
    h = h * 17 + cppName.hashCode();
    return h;
  }

  /**
   * Prints the immutable value {@code EntityNameMapper} with attribute values.
   * @return A string representation of the value
   */
  @Override
  public String toString() {
    return MoreObjects.toStringHelper("EntityNameMapper")
        .omitNullValues()
        .add("javaName", javaName)
        .add("jniName", jniName)
        .add("cppName", cppName)
        .toString();
  }

  /**
   * Creates an immutable copy of a {@link EntityNameMapper} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param instance The instance to copy
   * @return A copied immutable EntityNameMapper instance
   */
  public static ImmutableEntityNameMapper copyOf(EntityNameMapper instance) {
    if (instance instanceof ImmutableEntityNameMapper) {
      return (ImmutableEntityNameMapper) instance;
    }
    return ImmutableEntityNameMapper.builder()
        .from(instance)
        .build();
  }

  /**
   * Creates a builder for {@link ImmutableEntityNameMapper ImmutableEntityNameMapper}.
   * @return A new ImmutableEntityNameMapper builder
   */
  public static ImmutableEntityNameMapper.Builder builder() {
    return new ImmutableEntityNameMapper.Builder();
  }

  /**
   * Builds instances of type {@link ImmutableEntityNameMapper ImmutableEntityNameMapper}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  @NotThreadSafe
  public static final class Builder {
    private static final long INIT_BIT_JAVA_NAME = 0x1L;
    private static final long INIT_BIT_JNI_NAME = 0x2L;
    private static final long INIT_BIT_CPP_NAME = 0x4L;
    private long initBits = 0x7L;

    private @Nullable String javaName;
    private @Nullable String jniName;
    private @Nullable String cppName;

    private Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code EntityNameMapper} instance.
     * Regular attribute values will be replaced with those from the given instance.
     * Absent optional values will not replace present values.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder from(EntityNameMapper instance) {
      Preconditions.checkNotNull(instance, "instance");
      javaName(instance.javaName());
      jniName(instance.jniName());
      cppName(instance.cppName());
      return this;
    }

    /**
     * Initializes the value for the {@link EntityNameMapper#javaName() javaName} attribute.
     * @param javaName The value for javaName 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder javaName(String javaName) {
      this.javaName = Preconditions.checkNotNull(javaName, "javaName");
      initBits &= ~INIT_BIT_JAVA_NAME;
      return this;
    }

    /**
     * Initializes the value for the {@link EntityNameMapper#jniName() jniName} attribute.
     * @param jniName The value for jniName 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder jniName(String jniName) {
      this.jniName = Preconditions.checkNotNull(jniName, "jniName");
      initBits &= ~INIT_BIT_JNI_NAME;
      return this;
    }

    /**
     * Initializes the value for the {@link EntityNameMapper#cppName() cppName} attribute.
     * @param cppName The value for cppName 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder cppName(String cppName) {
      this.cppName = Preconditions.checkNotNull(cppName, "cppName");
      initBits &= ~INIT_BIT_CPP_NAME;
      return this;
    }

    /**
     * Builds a new {@link ImmutableEntityNameMapper ImmutableEntityNameMapper}.
     * @return An immutable instance of EntityNameMapper
     * @throws java.lang.IllegalStateException if any required attributes are missing
     */
    public ImmutableEntityNameMapper build() {
      if (initBits != 0) {
        throw new IllegalStateException(formatRequiredAttributesMessage());
      }
      return new ImmutableEntityNameMapper(javaName, jniName, cppName);
    }

    private String formatRequiredAttributesMessage() {
      List<String> attributes = Lists.newArrayList();
      if ((initBits & INIT_BIT_JAVA_NAME) != 0) attributes.add("javaName");
      if ((initBits & INIT_BIT_JNI_NAME) != 0) attributes.add("jniName");
      if ((initBits & INIT_BIT_CPP_NAME) != 0) attributes.add("cppName");
      return "Cannot build EntityNameMapper, some of required attributes are not set " + attributes;
    }
  }
}
