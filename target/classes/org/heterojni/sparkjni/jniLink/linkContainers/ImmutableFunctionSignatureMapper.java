package org.heterojni.sparkjni.jniLink.linkContainers;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.primitives.Booleans;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import javax.annotation.Generated;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * Immutable implementation of {@link FunctionSignatureMapper}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableFunctionSignatureMapper.builder()}.
 */
@SuppressWarnings({"all"})
@SuppressFBWarnings
@ParametersAreNonnullByDefault
@Generated({"Immutables.generator", "FunctionSignatureMapper"})
@Immutable
public final class ImmutableFunctionSignatureMapper
    implements FunctionSignatureMapper {
  private final ImmutableList<TypeMapper> parameterList;
  private final TypeMapper returnTypeMapper;
  private final EntityNameMapper functionNameMapper;
  private final boolean staticMethod;

  private ImmutableFunctionSignatureMapper(
      ImmutableList<TypeMapper> parameterList,
      TypeMapper returnTypeMapper,
      EntityNameMapper functionNameMapper,
      boolean staticMethod) {
    this.parameterList = parameterList;
    this.returnTypeMapper = returnTypeMapper;
    this.functionNameMapper = functionNameMapper;
    this.staticMethod = staticMethod;
  }

  /**
   * @return The value of the {@code parameterList} attribute
   */
  @Override
  public ImmutableList<TypeMapper> parameterList() {
    return parameterList;
  }

  /**
   * @return The value of the {@code returnTypeMapper} attribute
   */
  @Override
  public TypeMapper returnTypeMapper() {
    return returnTypeMapper;
  }

  /**
   * @return The value of the {@code functionNameMapper} attribute
   */
  @Override
  public EntityNameMapper functionNameMapper() {
    return functionNameMapper;
  }

  /**
   * @return The value of the {@code staticMethod} attribute
   */
  @Override
  public boolean staticMethod() {
    return staticMethod;
  }

  /**
   * Copy the current immutable object with elements that replace the content of {@link FunctionSignatureMapper#parameterList() parameterList}.
   * @param elements The elements to set
   * @return A modified copy of {@code this} object
   */
  public final ImmutableFunctionSignatureMapper withParameterList(TypeMapper... elements) {
    ImmutableList<TypeMapper> newValue = ImmutableList.copyOf(elements);
    return new ImmutableFunctionSignatureMapper(newValue, this.returnTypeMapper, this.functionNameMapper, this.staticMethod);
  }

  /**
   * Copy the current immutable object with elements that replace the content of {@link FunctionSignatureMapper#parameterList() parameterList}.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param elements An iterable of parameterList elements to set
   * @return A modified copy of {@code this} object
   */
  public final ImmutableFunctionSignatureMapper withParameterList(Iterable<? extends TypeMapper> elements) {
    if (this.parameterList == elements) return this;
    ImmutableList<TypeMapper> newValue = ImmutableList.copyOf(elements);
    return new ImmutableFunctionSignatureMapper(newValue, this.returnTypeMapper, this.functionNameMapper, this.staticMethod);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link FunctionSignatureMapper#returnTypeMapper() returnTypeMapper} attribute.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for returnTypeMapper
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableFunctionSignatureMapper withReturnTypeMapper(TypeMapper value) {
    if (this.returnTypeMapper == value) return this;
    TypeMapper newValue = Preconditions.checkNotNull(value, "returnTypeMapper");
    return new ImmutableFunctionSignatureMapper(this.parameterList, newValue, this.functionNameMapper, this.staticMethod);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link FunctionSignatureMapper#functionNameMapper() functionNameMapper} attribute.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for functionNameMapper
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableFunctionSignatureMapper withFunctionNameMapper(EntityNameMapper value) {
    if (this.functionNameMapper == value) return this;
    EntityNameMapper newValue = Preconditions.checkNotNull(value, "functionNameMapper");
    return new ImmutableFunctionSignatureMapper(this.parameterList, this.returnTypeMapper, newValue, this.staticMethod);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link FunctionSignatureMapper#staticMethod() staticMethod} attribute.
   * A value equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for staticMethod
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableFunctionSignatureMapper withStaticMethod(boolean value) {
    if (this.staticMethod == value) return this;
    return new ImmutableFunctionSignatureMapper(this.parameterList, this.returnTypeMapper, this.functionNameMapper, value);
  }

  /**
   * This instance is equal to all instances of {@code ImmutableFunctionSignatureMapper} that have equal attribute values.
   * @return {@code true} if {@code this} is equal to {@code another} instance
   */
  @Override
  public boolean equals(@Nullable Object another) {
    if (this == another) return true;
    return another instanceof ImmutableFunctionSignatureMapper
        && equalTo((ImmutableFunctionSignatureMapper) another);
  }

  private boolean equalTo(ImmutableFunctionSignatureMapper another) {
    return parameterList.equals(another.parameterList)
        && returnTypeMapper.equals(another.returnTypeMapper)
        && functionNameMapper.equals(another.functionNameMapper)
        && staticMethod == another.staticMethod;
  }

  /**
   * Computes a hash code from attributes: {@code parameterList}, {@code returnTypeMapper}, {@code functionNameMapper}, {@code staticMethod}.
   * @return hashCode value
   */
  @Override
  public int hashCode() {
    int h = 31;
    h = h * 17 + parameterList.hashCode();
    h = h * 17 + returnTypeMapper.hashCode();
    h = h * 17 + functionNameMapper.hashCode();
    h = h * 17 + Booleans.hashCode(staticMethod);
    return h;
  }

  /**
   * Prints the immutable value {@code FunctionSignatureMapper} with attribute values.
   * @return A string representation of the value
   */
  @Override
  public String toString() {
    return MoreObjects.toStringHelper("FunctionSignatureMapper")
        .omitNullValues()
        .add("parameterList", parameterList)
        .add("returnTypeMapper", returnTypeMapper)
        .add("functionNameMapper", functionNameMapper)
        .add("staticMethod", staticMethod)
        .toString();
  }

  /**
   * Creates an immutable copy of a {@link FunctionSignatureMapper} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param instance The instance to copy
   * @return A copied immutable FunctionSignatureMapper instance
   */
  public static ImmutableFunctionSignatureMapper copyOf(FunctionSignatureMapper instance) {
    if (instance instanceof ImmutableFunctionSignatureMapper) {
      return (ImmutableFunctionSignatureMapper) instance;
    }
    return ImmutableFunctionSignatureMapper.builder()
        .from(instance)
        .build();
  }

  /**
   * Creates a builder for {@link ImmutableFunctionSignatureMapper ImmutableFunctionSignatureMapper}.
   * @return A new ImmutableFunctionSignatureMapper builder
   */
  public static ImmutableFunctionSignatureMapper.Builder builder() {
    return new ImmutableFunctionSignatureMapper.Builder();
  }

  /**
   * Builds instances of type {@link ImmutableFunctionSignatureMapper ImmutableFunctionSignatureMapper}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  @NotThreadSafe
  public static final class Builder {
    private static final long INIT_BIT_RETURN_TYPE_MAPPER = 0x1L;
    private static final long INIT_BIT_FUNCTION_NAME_MAPPER = 0x2L;
    private static final long INIT_BIT_STATIC_METHOD = 0x4L;
    private long initBits = 0x7L;

    private ImmutableList.Builder<TypeMapper> parameterList = ImmutableList.builder();
    private @Nullable TypeMapper returnTypeMapper;
    private @Nullable EntityNameMapper functionNameMapper;
    private boolean staticMethod;

    private Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code FunctionSignatureMapper} instance.
     * Regular attribute values will be replaced with those from the given instance.
     * Absent optional values will not replace present values.
     * Collection elements and entries will be added, not replaced.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder from(FunctionSignatureMapper instance) {
      Preconditions.checkNotNull(instance, "instance");
      addAllParameterList(instance.parameterList());
      returnTypeMapper(instance.returnTypeMapper());
      functionNameMapper(instance.functionNameMapper());
      staticMethod(instance.staticMethod());
      return this;
    }

    /**
     * Adds one element to {@link FunctionSignatureMapper#parameterList() parameterList} list.
     * @param element A parameterList element
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder addParameterList(TypeMapper element) {
      this.parameterList.add(element);
      return this;
    }

    /**
     * Adds elements to {@link FunctionSignatureMapper#parameterList() parameterList} list.
     * @param elements An array of parameterList elements
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder addParameterList(TypeMapper... elements) {
      this.parameterList.add(elements);
      return this;
    }

    /**
     * Sets or replaces all elements for {@link FunctionSignatureMapper#parameterList() parameterList} list.
     * @param elements An iterable of parameterList elements
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder parameterList(Iterable<? extends TypeMapper> elements) {
      this.parameterList = ImmutableList.builder();
      return addAllParameterList(elements);
    }

    /**
     * Adds elements to {@link FunctionSignatureMapper#parameterList() parameterList} list.
     * @param elements An iterable of parameterList elements
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder addAllParameterList(Iterable<? extends TypeMapper> elements) {
      this.parameterList.addAll(elements);
      return this;
    }

    /**
     * Initializes the value for the {@link FunctionSignatureMapper#returnTypeMapper() returnTypeMapper} attribute.
     * @param returnTypeMapper The value for returnTypeMapper 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder returnTypeMapper(TypeMapper returnTypeMapper) {
      this.returnTypeMapper = Preconditions.checkNotNull(returnTypeMapper, "returnTypeMapper");
      initBits &= ~INIT_BIT_RETURN_TYPE_MAPPER;
      return this;
    }

    /**
     * Initializes the value for the {@link FunctionSignatureMapper#functionNameMapper() functionNameMapper} attribute.
     * @param functionNameMapper The value for functionNameMapper 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder functionNameMapper(EntityNameMapper functionNameMapper) {
      this.functionNameMapper = Preconditions.checkNotNull(functionNameMapper, "functionNameMapper");
      initBits &= ~INIT_BIT_FUNCTION_NAME_MAPPER;
      return this;
    }

    /**
     * Initializes the value for the {@link FunctionSignatureMapper#staticMethod() staticMethod} attribute.
     * @param staticMethod The value for staticMethod 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder staticMethod(boolean staticMethod) {
      this.staticMethod = staticMethod;
      initBits &= ~INIT_BIT_STATIC_METHOD;
      return this;
    }

    /**
     * Builds a new {@link ImmutableFunctionSignatureMapper ImmutableFunctionSignatureMapper}.
     * @return An immutable instance of FunctionSignatureMapper
     * @throws java.lang.IllegalStateException if any required attributes are missing
     */
    public ImmutableFunctionSignatureMapper build() {
      if (initBits != 0) {
        throw new IllegalStateException(formatRequiredAttributesMessage());
      }
      return new ImmutableFunctionSignatureMapper(parameterList.build(), returnTypeMapper, functionNameMapper, staticMethod);
    }

    private String formatRequiredAttributesMessage() {
      List<String> attributes = Lists.newArrayList();
      if ((initBits & INIT_BIT_RETURN_TYPE_MAPPER) != 0) attributes.add("returnTypeMapper");
      if ((initBits & INIT_BIT_FUNCTION_NAME_MAPPER) != 0) attributes.add("functionNameMapper");
      if ((initBits & INIT_BIT_STATIC_METHOD) != 0) attributes.add("staticMethod");
      return "Cannot build FunctionSignatureMapper, some of required attributes are not set " + attributes;
    }
  }
}
