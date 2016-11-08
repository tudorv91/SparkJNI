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
 * Immutable implementation of {@link JniRootContainer}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableJniRootContainer.builder()}.
 */
@SuppressWarnings({"all"})
@SuppressFBWarnings
@ParametersAreNonnullByDefault
@Generated({"Immutables.generator", "JniRootContainer"})
@Immutable
public final class ImmutableJniRootContainer
    implements JniRootContainer {
  private final String appName;
  private final ImmutableList<JniHeader> jniHeaders;
  private final String path;

  private ImmutableJniRootContainer(
      String appName,
      ImmutableList<JniHeader> jniHeaders,
      String path) {
    this.appName = appName;
    this.jniHeaders = jniHeaders;
    this.path = path;
  }

  /**
   * @return The value of the {@code appName} attribute
   */
  @Override
  public String appName() {
    return appName;
  }

  /**
   * @return The value of the {@code jniHeaders} attribute
   */
  @Override
  public ImmutableList<JniHeader> jniHeaders() {
    return jniHeaders;
  }

  /**
   * @return The value of the {@code path} attribute
   */
  @Override
  public String path() {
    return path;
  }

  /**
   * Copy the current immutable object by setting a value for the {@link JniRootContainer#appName() appName} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for appName
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableJniRootContainer withAppName(String value) {
    if (this.appName.equals(value)) return this;
    String newValue = Preconditions.checkNotNull(value, "appName");
    return new ImmutableJniRootContainer(newValue, this.jniHeaders, this.path);
  }

  /**
   * Copy the current immutable object with elements that replace the content of {@link JniRootContainer#jniHeaders() jniHeaders}.
   * @param elements The elements to set
   * @return A modified copy of {@code this} object
   */
  public final ImmutableJniRootContainer withJniHeaders(JniHeader... elements) {
    ImmutableList<JniHeader> newValue = ImmutableList.copyOf(elements);
    return new ImmutableJniRootContainer(this.appName, newValue, this.path);
  }

  /**
   * Copy the current immutable object with elements that replace the content of {@link JniRootContainer#jniHeaders() jniHeaders}.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param elements An iterable of jniHeaders elements to set
   * @return A modified copy of {@code this} object
   */
  public final ImmutableJniRootContainer withJniHeaders(Iterable<? extends JniHeader> elements) {
    if (this.jniHeaders == elements) return this;
    ImmutableList<JniHeader> newValue = ImmutableList.copyOf(elements);
    return new ImmutableJniRootContainer(this.appName, newValue, this.path);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link JniRootContainer#path() path} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for path
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableJniRootContainer withPath(String value) {
    if (this.path.equals(value)) return this;
    String newValue = Preconditions.checkNotNull(value, "path");
    return new ImmutableJniRootContainer(this.appName, this.jniHeaders, newValue);
  }

  /**
   * This instance is equal to all instances of {@code ImmutableJniRootContainer} that have equal attribute values.
   * @return {@code true} if {@code this} is equal to {@code another} instance
   */
  @Override
  public boolean equals(@Nullable Object another) {
    if (this == another) return true;
    return another instanceof ImmutableJniRootContainer
        && equalTo((ImmutableJniRootContainer) another);
  }

  private boolean equalTo(ImmutableJniRootContainer another) {
    return appName.equals(another.appName)
        && jniHeaders.equals(another.jniHeaders)
        && path.equals(another.path);
  }

  /**
   * Computes a hash code from attributes: {@code appName}, {@code jniHeaders}, {@code path}.
   * @return hashCode value
   */
  @Override
  public int hashCode() {
    int h = 31;
    h = h * 17 + appName.hashCode();
    h = h * 17 + jniHeaders.hashCode();
    h = h * 17 + path.hashCode();
    return h;
  }

  /**
   * Prints the immutable value {@code JniRootContainer} with attribute values.
   * @return A string representation of the value
   */
  @Override
  public String toString() {
    return MoreObjects.toStringHelper("JniRootContainer")
        .omitNullValues()
        .add("appName", appName)
        .add("jniHeaders", jniHeaders)
        .add("path", path)
        .toString();
  }

  /**
   * Creates an immutable copy of a {@link JniRootContainer} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param instance The instance to copy
   * @return A copied immutable JniRootContainer instance
   */
  public static ImmutableJniRootContainer copyOf(JniRootContainer instance) {
    if (instance instanceof ImmutableJniRootContainer) {
      return (ImmutableJniRootContainer) instance;
    }
    return ImmutableJniRootContainer.builder()
        .from(instance)
        .build();
  }

  /**
   * Creates a builder for {@link ImmutableJniRootContainer ImmutableJniRootContainer}.
   * @return A new ImmutableJniRootContainer builder
   */
  public static ImmutableJniRootContainer.Builder builder() {
    return new ImmutableJniRootContainer.Builder();
  }

  /**
   * Builds instances of type {@link ImmutableJniRootContainer ImmutableJniRootContainer}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  @NotThreadSafe
  public static final class Builder {
    private static final long INIT_BIT_APP_NAME = 0x1L;
    private static final long INIT_BIT_PATH = 0x2L;
    private long initBits = 0x3L;

    private @Nullable String appName;
    private ImmutableList.Builder<JniHeader> jniHeaders = ImmutableList.builder();
    private @Nullable String path;

    private Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code JniRootContainer} instance.
     * Regular attribute values will be replaced with those from the given instance.
     * Absent optional values will not replace present values.
     * Collection elements and entries will be added, not replaced.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder from(JniRootContainer instance) {
      Preconditions.checkNotNull(instance, "instance");
      appName(instance.appName());
      addAllJniHeaders(instance.jniHeaders());
      path(instance.path());
      return this;
    }

    /**
     * Initializes the value for the {@link JniRootContainer#appName() appName} attribute.
     * @param appName The value for appName 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder appName(String appName) {
      this.appName = Preconditions.checkNotNull(appName, "appName");
      initBits &= ~INIT_BIT_APP_NAME;
      return this;
    }

    /**
     * Adds one element to {@link JniRootContainer#jniHeaders() jniHeaders} list.
     * @param element A jniHeaders element
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder addJniHeaders(JniHeader element) {
      this.jniHeaders.add(element);
      return this;
    }

    /**
     * Adds elements to {@link JniRootContainer#jniHeaders() jniHeaders} list.
     * @param elements An array of jniHeaders elements
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder addJniHeaders(JniHeader... elements) {
      this.jniHeaders.add(elements);
      return this;
    }

    /**
     * Sets or replaces all elements for {@link JniRootContainer#jniHeaders() jniHeaders} list.
     * @param elements An iterable of jniHeaders elements
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder jniHeaders(Iterable<? extends JniHeader> elements) {
      this.jniHeaders = ImmutableList.builder();
      return addAllJniHeaders(elements);
    }

    /**
     * Adds elements to {@link JniRootContainer#jniHeaders() jniHeaders} list.
     * @param elements An iterable of jniHeaders elements
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder addAllJniHeaders(Iterable<? extends JniHeader> elements) {
      this.jniHeaders.addAll(elements);
      return this;
    }

    /**
     * Initializes the value for the {@link JniRootContainer#path() path} attribute.
     * @param path The value for path 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder path(String path) {
      this.path = Preconditions.checkNotNull(path, "path");
      initBits &= ~INIT_BIT_PATH;
      return this;
    }

    /**
     * Builds a new {@link ImmutableJniRootContainer ImmutableJniRootContainer}.
     * @return An immutable instance of JniRootContainer
     * @throws java.lang.IllegalStateException if any required attributes are missing
     */
    public ImmutableJniRootContainer build() {
      if (initBits != 0) {
        throw new IllegalStateException(formatRequiredAttributesMessage());
      }
      return new ImmutableJniRootContainer(appName, jniHeaders.build(), path);
    }

    private String formatRequiredAttributesMessage() {
      List<String> attributes = Lists.newArrayList();
      if ((initBits & INIT_BIT_APP_NAME) != 0) attributes.add("appName");
      if ((initBits & INIT_BIT_PATH) != 0) attributes.add("path");
      return "Cannot build JniRootContainer, some of required attributes are not set " + attributes;
    }
  }
}
