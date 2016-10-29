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
 * Immutable implementation of {@link JniMetaContainer}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableJniMetaContainer.builder()}.
 */
@SuppressWarnings({"all"})
@SuppressFBWarnings
@ParametersAreNonnullByDefault
@Generated({"Immutables.generator", "JniMetaContainer"})
@Immutable
public final class ImmutableJniMetaContainer
    implements JniMetaContainer {
  private final ImmutableList<JniHeader> jniHeaderContainers;
  private final String appName;
  private final String nativePathName;

  private ImmutableJniMetaContainer(
      ImmutableList<JniHeader> jniHeaderContainers,
      String appName,
      String nativePathName) {
    this.jniHeaderContainers = jniHeaderContainers;
    this.appName = appName;
    this.nativePathName = nativePathName;
  }

  /**
   * @return The value of the {@code jniHeaderContainers} attribute
   */
  @Override
  public ImmutableList<JniHeader> jniHeaderContainers() {
    return jniHeaderContainers;
  }

  /**
   * @return The value of the {@code appName} attribute
   */
  @Override
  public String appName() {
    return appName;
  }

  /**
   * @return The value of the {@code nativePathName} attribute
   */
  @Override
  public String nativePathName() {
    return nativePathName;
  }

  /**
   * Copy the current immutable object with elements that replace the content of {@link JniMetaContainer#jniHeaderContainers() jniHeaderContainers}.
   * @param elements The elements to set
   * @return A modified copy of {@code this} object
   */
  public final ImmutableJniMetaContainer withJniHeaderContainers(JniHeader... elements) {
    ImmutableList<JniHeader> newValue = ImmutableList.copyOf(elements);
    return new ImmutableJniMetaContainer(newValue, this.appName, this.nativePathName);
  }

  /**
   * Copy the current immutable object with elements that replace the content of {@link JniMetaContainer#jniHeaderContainers() jniHeaderContainers}.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param elements An iterable of jniHeaderContainers elements to set
   * @return A modified copy of {@code this} object
   */
  public final ImmutableJniMetaContainer withJniHeaderContainers(Iterable<? extends JniHeader> elements) {
    if (this.jniHeaderContainers == elements) return this;
    ImmutableList<JniHeader> newValue = ImmutableList.copyOf(elements);
    return new ImmutableJniMetaContainer(newValue, this.appName, this.nativePathName);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link JniMetaContainer#appName() appName} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for appName
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableJniMetaContainer withAppName(String value) {
    if (this.appName.equals(value)) return this;
    String newValue = Preconditions.checkNotNull(value, "appName");
    return new ImmutableJniMetaContainer(this.jniHeaderContainers, newValue, this.nativePathName);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link JniMetaContainer#nativePathName() nativePathName} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for nativePathName
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableJniMetaContainer withNativePathName(String value) {
    if (this.nativePathName.equals(value)) return this;
    String newValue = Preconditions.checkNotNull(value, "nativePathName");
    return new ImmutableJniMetaContainer(this.jniHeaderContainers, this.appName, newValue);
  }

  /**
   * This instance is equal to all instances of {@code ImmutableJniMetaContainer} that have equal attribute values.
   * @return {@code true} if {@code this} is equal to {@code another} instance
   */
  @Override
  public boolean equals(@Nullable Object another) {
    if (this == another) return true;
    return another instanceof ImmutableJniMetaContainer
        && equalTo((ImmutableJniMetaContainer) another);
  }

  private boolean equalTo(ImmutableJniMetaContainer another) {
    return jniHeaderContainers.equals(another.jniHeaderContainers)
        && appName.equals(another.appName)
        && nativePathName.equals(another.nativePathName);
  }

  /**
   * Computes a hash code from attributes: {@code jniHeaderContainers}, {@code appName}, {@code nativePathName}.
   * @return hashCode value
   */
  @Override
  public int hashCode() {
    int h = 31;
    h = h * 17 + jniHeaderContainers.hashCode();
    h = h * 17 + appName.hashCode();
    h = h * 17 + nativePathName.hashCode();
    return h;
  }

  /**
   * Prints the immutable value {@code JniMetaContainer} with attribute values.
   * @return A string representation of the value
   */
  @Override
  public String toString() {
    return MoreObjects.toStringHelper("JniMetaContainer")
        .omitNullValues()
        .add("jniHeaderContainers", jniHeaderContainers)
        .add("appName", appName)
        .add("nativePathName", nativePathName)
        .toString();
  }

  /**
   * Creates an immutable copy of a {@link JniMetaContainer} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param instance The instance to copy
   * @return A copied immutable JniMetaContainer instance
   */
  public static ImmutableJniMetaContainer copyOf(JniMetaContainer instance) {
    if (instance instanceof ImmutableJniMetaContainer) {
      return (ImmutableJniMetaContainer) instance;
    }
    return ImmutableJniMetaContainer.builder()
        .from(instance)
        .build();
  }

  /**
   * Creates a builder for {@link ImmutableJniMetaContainer ImmutableJniMetaContainer}.
   * @return A new ImmutableJniMetaContainer builder
   */
  public static ImmutableJniMetaContainer.Builder builder() {
    return new ImmutableJniMetaContainer.Builder();
  }

  /**
   * Builds instances of type {@link ImmutableJniMetaContainer ImmutableJniMetaContainer}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  @NotThreadSafe
  public static final class Builder {
    private static final long INIT_BIT_APP_NAME = 0x1L;
    private static final long INIT_BIT_NATIVE_PATH_NAME = 0x2L;
    private long initBits = 0x3L;

    private ImmutableList.Builder<JniHeader> jniHeaderContainers = ImmutableList.builder();
    private @Nullable String appName;
    private @Nullable String nativePathName;

    private Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code JniMetaContainer} instance.
     * Regular attribute values will be replaced with those from the given instance.
     * Absent optional values will not replace present values.
     * Collection elements and entries will be added, not replaced.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder from(JniMetaContainer instance) {
      Preconditions.checkNotNull(instance, "instance");
      addAllJniHeaderContainers(instance.jniHeaderContainers());
      appName(instance.appName());
      nativePathName(instance.nativePathName());
      return this;
    }

    /**
     * Adds one element to {@link JniMetaContainer#jniHeaderContainers() jniHeaderContainers} list.
     * @param element A jniHeaderContainers element
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder addJniHeaderContainers(JniHeader element) {
      this.jniHeaderContainers.add(element);
      return this;
    }

    /**
     * Adds elements to {@link JniMetaContainer#jniHeaderContainers() jniHeaderContainers} list.
     * @param elements An array of jniHeaderContainers elements
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder addJniHeaderContainers(JniHeader... elements) {
      this.jniHeaderContainers.add(elements);
      return this;
    }

    /**
     * Sets or replaces all elements for {@link JniMetaContainer#jniHeaderContainers() jniHeaderContainers} list.
     * @param elements An iterable of jniHeaderContainers elements
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder jniHeaderContainers(Iterable<? extends JniHeader> elements) {
      this.jniHeaderContainers = ImmutableList.builder();
      return addAllJniHeaderContainers(elements);
    }

    /**
     * Adds elements to {@link JniMetaContainer#jniHeaderContainers() jniHeaderContainers} list.
     * @param elements An iterable of jniHeaderContainers elements
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder addAllJniHeaderContainers(Iterable<? extends JniHeader> elements) {
      this.jniHeaderContainers.addAll(elements);
      return this;
    }

    /**
     * Initializes the value for the {@link JniMetaContainer#appName() appName} attribute.
     * @param appName The value for appName 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder appName(String appName) {
      this.appName = Preconditions.checkNotNull(appName, "appName");
      initBits &= ~INIT_BIT_APP_NAME;
      return this;
    }

    /**
     * Initializes the value for the {@link JniMetaContainer#nativePathName() nativePathName} attribute.
     * @param nativePathName The value for nativePathName 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder nativePathName(String nativePathName) {
      this.nativePathName = Preconditions.checkNotNull(nativePathName, "nativePathName");
      initBits &= ~INIT_BIT_NATIVE_PATH_NAME;
      return this;
    }

    /**
     * Builds a new {@link ImmutableJniMetaContainer ImmutableJniMetaContainer}.
     * @return An immutable instance of JniMetaContainer
     * @throws java.lang.IllegalStateException if any required attributes are missing
     */
    public ImmutableJniMetaContainer build() {
      if (initBits != 0) {
        throw new IllegalStateException(formatRequiredAttributesMessage());
      }
      return new ImmutableJniMetaContainer(jniHeaderContainers.build(), appName, nativePathName);
    }

    private String formatRequiredAttributesMessage() {
      List<String> attributes = Lists.newArrayList();
      if ((initBits & INIT_BIT_APP_NAME) != 0) attributes.add("appName");
      if ((initBits & INIT_BIT_NATIVE_PATH_NAME) != 0) attributes.add("nativePathName");
      return "Cannot build JniMetaContainer, some of required attributes are not set " + attributes;
    }
  }
}
