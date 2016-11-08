package org.heterojni.sparkjni.jniLink.linkHandlers;

import com.google.common.base.Preconditions;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javax.annotation.Generated;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * Immutable implementation of {@link JniRootContainerProvider}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableJniRootContainerProvider.builder()}.
 */
@SuppressWarnings({"all"})
@SuppressFBWarnings
@ParametersAreNonnullByDefault
@Generated({"Immutables.generator", "JniRootContainerProvider"})
@Immutable
public final class ImmutableJniRootContainerProvider
    extends JniRootContainerProvider {

  private ImmutableJniRootContainerProvider(ImmutableJniRootContainerProvider.Builder builder) {
  }

  /**
   * This instance is equal to all instances of {@code ImmutableJniRootContainerProvider} that have equal attribute values.
   * @return {@code true} if {@code this} is equal to {@code another} instance
   */
  @Override
  public boolean equals(@Nullable Object another) {
    if (this == another) return true;
    return another instanceof ImmutableJniRootContainerProvider
        && equalTo((ImmutableJniRootContainerProvider) another);
  }

  private boolean equalTo(ImmutableJniRootContainerProvider another) {
    return true;
  }

  /**
   * Returns a constant hash code value.
   * @return hashCode value
   */
  @Override
  public int hashCode() {
    return 1407272350;
  }

  /**
   * Prints the immutable value {@code JniRootContainerProvider}.
   * @return A string representation of the value
   */
  @Override
  public String toString() {
    return "JniRootContainerProvider{}";
  }

  /**
   * Creates an immutable copy of a {@link JniRootContainerProvider} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param instance The instance to copy
   * @return A copied immutable JniRootContainerProvider instance
   */
  public static ImmutableJniRootContainerProvider copyOf(JniRootContainerProvider instance) {
    if (instance instanceof ImmutableJniRootContainerProvider) {
      return (ImmutableJniRootContainerProvider) instance;
    }
    return ImmutableJniRootContainerProvider.builder()
        .from(instance)
        .build();
  }

  /**
   * Creates a builder for {@link ImmutableJniRootContainerProvider ImmutableJniRootContainerProvider}.
   * @return A new ImmutableJniRootContainerProvider builder
   */
  public static ImmutableJniRootContainerProvider.Builder builder() {
    return new ImmutableJniRootContainerProvider.Builder();
  }

  /**
   * Builds instances of type {@link ImmutableJniRootContainerProvider ImmutableJniRootContainerProvider}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  @NotThreadSafe
  public static final class Builder {

    private Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code JniRootContainerProvider} instance.
     * Regular attribute values will be replaced with those from the given instance.
     * Absent optional values will not replace present values.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder from(JniRootContainerProvider instance) {
      Preconditions.checkNotNull(instance, "instance");
      return this;
    }

    /**
     * Builds a new {@link ImmutableJniRootContainerProvider ImmutableJniRootContainerProvider}.
     * @return An immutable instance of JniRootContainerProvider
     * @throws java.lang.IllegalStateException if any required attributes are missing
     */
    public ImmutableJniRootContainerProvider build() {
      return new ImmutableJniRootContainerProvider(this);
    }
  }
}
