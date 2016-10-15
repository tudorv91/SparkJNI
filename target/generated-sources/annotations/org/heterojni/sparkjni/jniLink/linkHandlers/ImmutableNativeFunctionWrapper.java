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
import org.heterojni.sparkjni.jniLink.linkContainers.FunctionSignatureMapper;

/**
 * Immutable implementation of {@link NativeFunctionWrapper}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableNativeFunctionWrapper.builder()}.
 */
@SuppressWarnings({"all"})
@SuppressFBWarnings
@ParametersAreNonnullByDefault
@Generated({"Immutables.generator", "NativeFunctionWrapper"})
@Immutable
public final class ImmutableNativeFunctionWrapper
    extends NativeFunctionWrapper {
  private final FunctionSignatureMapper functionSignatureMapper;

  private ImmutableNativeFunctionWrapper(FunctionSignatureMapper functionSignatureMapper) {
    this.functionSignatureMapper = functionSignatureMapper;
  }

  /**
   * @return The value of the {@code functionSignatureMapper} attribute
   */
  @Override
  public FunctionSignatureMapper functionSignatureMapper() {
    return functionSignatureMapper;
  }

  /**
   * Copy the current immutable object by setting a value for the {@link NativeFunctionWrapper#functionSignatureMapper() functionSignatureMapper} attribute.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for functionSignatureMapper
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableNativeFunctionWrapper withFunctionSignatureMapper(FunctionSignatureMapper value) {
    if (this.functionSignatureMapper == value) return this;
    FunctionSignatureMapper newValue = Preconditions.checkNotNull(value, "functionSignatureMapper");
    return new ImmutableNativeFunctionWrapper(newValue);
  }

  /**
   * This instance is equal to all instances of {@code ImmutableNativeFunctionWrapper} that have equal attribute values.
   * @return {@code true} if {@code this} is equal to {@code another} instance
   */
  @Override
  public boolean equals(@Nullable Object another) {
    if (this == another) return true;
    return another instanceof ImmutableNativeFunctionWrapper
        && equalTo((ImmutableNativeFunctionWrapper) another);
  }

  private boolean equalTo(ImmutableNativeFunctionWrapper another) {
    return functionSignatureMapper.equals(another.functionSignatureMapper);
  }

  /**
   * Computes a hash code from attributes: {@code functionSignatureMapper}.
   * @return hashCode value
   */
  @Override
  public int hashCode() {
    int h = 31;
    h = h * 17 + functionSignatureMapper.hashCode();
    return h;
  }

  /**
   * Prints the immutable value {@code NativeFunctionWrapper} with attribute values.
   * @return A string representation of the value
   */
  @Override
  public String toString() {
    return MoreObjects.toStringHelper("NativeFunctionWrapper")
        .omitNullValues()
        .add("functionSignatureMapper", functionSignatureMapper)
        .toString();
  }

  /**
   * Creates an immutable copy of a {@link NativeFunctionWrapper} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param instance The instance to copy
   * @return A copied immutable NativeFunctionWrapper instance
   */
  public static ImmutableNativeFunctionWrapper copyOf(NativeFunctionWrapper instance) {
    if (instance instanceof ImmutableNativeFunctionWrapper) {
      return (ImmutableNativeFunctionWrapper) instance;
    }
    return ImmutableNativeFunctionWrapper.builder()
        .from(instance)
        .build();
  }

  /**
   * Creates a builder for {@link ImmutableNativeFunctionWrapper ImmutableNativeFunctionWrapper}.
   * @return A new ImmutableNativeFunctionWrapper builder
   */
  public static ImmutableNativeFunctionWrapper.Builder builder() {
    return new ImmutableNativeFunctionWrapper.Builder();
  }

  /**
   * Builds instances of type {@link ImmutableNativeFunctionWrapper ImmutableNativeFunctionWrapper}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  @NotThreadSafe
  public static final class Builder {
    private static final long INIT_BIT_FUNCTION_SIGNATURE_MAPPER = 0x1L;
    private long initBits = 0x1L;

    private @Nullable FunctionSignatureMapper functionSignatureMapper;

    private Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code NativeFunctionWrapper} instance.
     * Regular attribute values will be replaced with those from the given instance.
     * Absent optional values will not replace present values.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder from(NativeFunctionWrapper instance) {
      Preconditions.checkNotNull(instance, "instance");
      functionSignatureMapper(instance.functionSignatureMapper());
      return this;
    }

    /**
     * Initializes the value for the {@link NativeFunctionWrapper#functionSignatureMapper() functionSignatureMapper} attribute.
     * @param functionSignatureMapper The value for functionSignatureMapper 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder functionSignatureMapper(FunctionSignatureMapper functionSignatureMapper) {
      this.functionSignatureMapper = Preconditions.checkNotNull(functionSignatureMapper, "functionSignatureMapper");
      initBits &= ~INIT_BIT_FUNCTION_SIGNATURE_MAPPER;
      return this;
    }

    /**
     * Builds a new {@link ImmutableNativeFunctionWrapper ImmutableNativeFunctionWrapper}.
     * @return An immutable instance of NativeFunctionWrapper
     * @throws java.lang.IllegalStateException if any required attributes are missing
     */
    public ImmutableNativeFunctionWrapper build() {
      if (initBits != 0) {
        throw new IllegalStateException(formatRequiredAttributesMessage());
      }
      return new ImmutableNativeFunctionWrapper(functionSignatureMapper);
    }

    private String formatRequiredAttributesMessage() {
      List<String> attributes = Lists.newArrayList();
      if ((initBits & INIT_BIT_FUNCTION_SIGNATURE_MAPPER) != 0) attributes.add("functionSignatureMapper");
      return "Cannot build NativeFunctionWrapper, some of required attributes are not set " + attributes;
    }
  }
}
