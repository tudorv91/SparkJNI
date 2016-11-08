package org.heterojni.sparkjni.jniLink.linkHandlers;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Generated;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * Immutable implementation of {@link FunctionSignatureMapperProvider}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableFunctionSignatureMapperProvider.builder()}.
 */
@SuppressWarnings({"all"})
@SuppressFBWarnings
@ParametersAreNonnullByDefault
@Generated({"Immutables.generator", "FunctionSignatureMapperProvider"})
@Immutable
public final class ImmutableFunctionSignatureMapperProvider
    extends FunctionSignatureMapperProvider {
  private final String[] tokens;
  private final String parametersLine;
  private final String fullyQualifiedJavaClass;

  private ImmutableFunctionSignatureMapperProvider(
      String[] tokens,
      String parametersLine,
      String fullyQualifiedJavaClass) {
    this.tokens = tokens;
    this.parametersLine = parametersLine;
    this.fullyQualifiedJavaClass = fullyQualifiedJavaClass;
  }

  /**
   * @return A cloned {@code tokens} array
   */
  @Override
  String[] tokens() {
    return tokens.clone();
  }

  /**
   * @return The value of the {@code parametersLine} attribute
   */
  @Override
  String parametersLine() {
    return parametersLine;
  }

  /**
   * @return The value of the {@code fullyQualifiedJavaClass} attribute
   */
  @Override
  String fullyQualifiedJavaClass() {
    return fullyQualifiedJavaClass;
  }

  /**
   * Copy the current immutable object with elements that replace the content of {@link FunctionSignatureMapperProvider#tokens() tokens}.
   * The array is cloned before being saved as attribute values.
   * @param elements The non-null elements for tokens
   * @return A modified copy of {@code this} object
   */
  public final ImmutableFunctionSignatureMapperProvider withTokens(String... elements) {
    String[] newValue = elements.clone();
    return new ImmutableFunctionSignatureMapperProvider(newValue, this.parametersLine, this.fullyQualifiedJavaClass);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link FunctionSignatureMapperProvider#parametersLine() parametersLine} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for parametersLine
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableFunctionSignatureMapperProvider withParametersLine(String value) {
    if (this.parametersLine.equals(value)) return this;
    String newValue = Preconditions.checkNotNull(value, "parametersLine");
    return new ImmutableFunctionSignatureMapperProvider(this.tokens, newValue, this.fullyQualifiedJavaClass);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link FunctionSignatureMapperProvider#fullyQualifiedJavaClass() fullyQualifiedJavaClass} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for fullyQualifiedJavaClass
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableFunctionSignatureMapperProvider withFullyQualifiedJavaClass(String value) {
    if (this.fullyQualifiedJavaClass.equals(value)) return this;
    String newValue = Preconditions.checkNotNull(value, "fullyQualifiedJavaClass");
    return new ImmutableFunctionSignatureMapperProvider(this.tokens, this.parametersLine, newValue);
  }

  /**
   * This instance is equal to all instances of {@code ImmutableFunctionSignatureMapperProvider} that have equal attribute values.
   * @return {@code true} if {@code this} is equal to {@code another} instance
   */
  @Override
  public boolean equals(@Nullable Object another) {
    if (this == another) return true;
    return another instanceof ImmutableFunctionSignatureMapperProvider
        && equalTo((ImmutableFunctionSignatureMapperProvider) another);
  }

  private boolean equalTo(ImmutableFunctionSignatureMapperProvider another) {
    return Arrays.equals(tokens, another.tokens)
        && parametersLine.equals(another.parametersLine)
        && fullyQualifiedJavaClass.equals(another.fullyQualifiedJavaClass);
  }

  /**
   * Computes a hash code from attributes: {@code tokens}, {@code parametersLine}, {@code fullyQualifiedJavaClass}.
   * @return hashCode value
   */
  @Override
  public int hashCode() {
    int h = 31;
    h = h * 17 + Arrays.hashCode(tokens);
    h = h * 17 + parametersLine.hashCode();
    h = h * 17 + fullyQualifiedJavaClass.hashCode();
    return h;
  }

  /**
   * Prints the immutable value {@code FunctionSignatureMapperProvider} with attribute values.
   * @return A string representation of the value
   */
  @Override
  public String toString() {
    return MoreObjects.toStringHelper("FunctionSignatureMapperProvider")
        .omitNullValues()
        .add("tokens", Arrays.toString(tokens))
        .add("parametersLine", parametersLine)
        .add("fullyQualifiedJavaClass", fullyQualifiedJavaClass)
        .toString();
  }

  /**
   * Creates an immutable copy of a {@link FunctionSignatureMapperProvider} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param instance The instance to copy
   * @return A copied immutable FunctionSignatureMapperProvider instance
   */
  public static ImmutableFunctionSignatureMapperProvider copyOf(FunctionSignatureMapperProvider instance) {
    if (instance instanceof ImmutableFunctionSignatureMapperProvider) {
      return (ImmutableFunctionSignatureMapperProvider) instance;
    }
    return ImmutableFunctionSignatureMapperProvider.builder()
        .from(instance)
        .build();
  }

  /**
   * Creates a builder for {@link ImmutableFunctionSignatureMapperProvider ImmutableFunctionSignatureMapperProvider}.
   * @return A new ImmutableFunctionSignatureMapperProvider builder
   */
  public static ImmutableFunctionSignatureMapperProvider.Builder builder() {
    return new ImmutableFunctionSignatureMapperProvider.Builder();
  }

  /**
   * Builds instances of type {@link ImmutableFunctionSignatureMapperProvider ImmutableFunctionSignatureMapperProvider}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  @NotThreadSafe
  public static final class Builder {
    private static final long INIT_BIT_TOKENS = 0x1L;
    private static final long INIT_BIT_PARAMETERS_LINE = 0x2L;
    private static final long INIT_BIT_FULLY_QUALIFIED_JAVA_CLASS = 0x4L;
    private long initBits = 0x7L;

    private @Nullable String[] tokens;
    private @Nullable String parametersLine;
    private @Nullable String fullyQualifiedJavaClass;

    private Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code FunctionSignatureMapperProvider} instance.
     * Regular attribute values will be replaced with those from the given instance.
     * Absent optional values will not replace present values.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder from(FunctionSignatureMapperProvider instance) {
      Preconditions.checkNotNull(instance, "instance");
      tokens(instance.tokens());
      parametersLine(instance.parametersLine());
      fullyQualifiedJavaClass(instance.fullyQualifiedJavaClass());
      return this;
    }

    /**
     * Initializes the value for the {@link FunctionSignatureMapperProvider#tokens() tokens} attribute.
     * @param tokens The elements for tokens
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder tokens(String... tokens) {
      this.tokens = tokens.clone();
      initBits &= ~INIT_BIT_TOKENS;
      return this;
    }

    /**
     * Initializes the value for the {@link FunctionSignatureMapperProvider#parametersLine() parametersLine} attribute.
     * @param parametersLine The value for parametersLine 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder parametersLine(String parametersLine) {
      this.parametersLine = Preconditions.checkNotNull(parametersLine, "parametersLine");
      initBits &= ~INIT_BIT_PARAMETERS_LINE;
      return this;
    }

    /**
     * Initializes the value for the {@link FunctionSignatureMapperProvider#fullyQualifiedJavaClass() fullyQualifiedJavaClass} attribute.
     * @param fullyQualifiedJavaClass The value for fullyQualifiedJavaClass 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder fullyQualifiedJavaClass(String fullyQualifiedJavaClass) {
      this.fullyQualifiedJavaClass = Preconditions.checkNotNull(fullyQualifiedJavaClass, "fullyQualifiedJavaClass");
      initBits &= ~INIT_BIT_FULLY_QUALIFIED_JAVA_CLASS;
      return this;
    }

    /**
     * Builds a new {@link ImmutableFunctionSignatureMapperProvider ImmutableFunctionSignatureMapperProvider}.
     * @return An immutable instance of FunctionSignatureMapperProvider
     * @throws java.lang.IllegalStateException if any required attributes are missing
     */
    public ImmutableFunctionSignatureMapperProvider build() {
      if (initBits != 0) {
        throw new IllegalStateException(formatRequiredAttributesMessage());
      }
      return new ImmutableFunctionSignatureMapperProvider(tokens, parametersLine, fullyQualifiedJavaClass);
    }

    private String formatRequiredAttributesMessage() {
      List<String> attributes = Lists.newArrayList();
      if ((initBits & INIT_BIT_TOKENS) != 0) attributes.add("tokens");
      if ((initBits & INIT_BIT_PARAMETERS_LINE) != 0) attributes.add("parametersLine");
      if ((initBits & INIT_BIT_FULLY_QUALIFIED_JAVA_CLASS) != 0) attributes.add("fullyQualifiedJavaClass");
      return "Cannot build FunctionSignatureMapperProvider, some of required attributes are not set " + attributes;
    }
  }
}
