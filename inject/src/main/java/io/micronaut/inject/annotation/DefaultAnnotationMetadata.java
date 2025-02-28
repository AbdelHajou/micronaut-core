/*
 * Copyright 2017-2020 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.inject.annotation;

import io.micronaut.context.exceptions.ConfigurationException;
import io.micronaut.core.annotation.AnnotationClassValue;
import io.micronaut.core.annotation.AnnotationMetadata;
import io.micronaut.core.annotation.AnnotationUtil;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.Internal;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.annotation.UsedByGeneratedCode;
import io.micronaut.core.convert.ConversionService;
import io.micronaut.core.reflect.ReflectionUtils;
import io.micronaut.core.type.Argument;
import io.micronaut.core.util.ArgumentUtils;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.core.util.StringUtils;
import io.micronaut.core.value.OptionalValues;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Array;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Default implementation of {@link AnnotationMetadata}.
 *
 * <p>
 * NOTE: Although required to be public This is an internal class and should not be referenced directly in user code
 * </p>
 *
 * @author Graeme Rocher
 * @since 1.0
 */
@Internal
public class DefaultAnnotationMetadata extends AbstractAnnotationMetadata implements AnnotationMetadata, Cloneable, EnvironmentAnnotationMetadata {

    @Nullable
    Map<String, Map<CharSequence, Object>> declaredAnnotations;
    @Nullable
    Map<String, Map<CharSequence, Object>> allAnnotations;
    @Nullable
    Map<String, Map<CharSequence, Object>> declaredStereotypes;
    @Nullable
    Map<String, Map<CharSequence, Object>> allStereotypes;
    @Nullable
    Map<String, List<String>> annotationsByStereotype;
    @Nullable
    Map<String, Map<CharSequence, Object>> annotationDefaultValues;
    @Nullable
    Map<String, String> repeated = null;
    @Nullable
    Set<String> sourceRetentionAnnotations;
    private Map<String, List> annotationValuesByType = new ConcurrentHashMap<>(2);

    private final boolean hasPropertyExpressions;
    // This should be removed in the next major version
    private final boolean useRepeatableDefaults;

    /**
     * Constructs empty annotation metadata.
     */
    @Internal
    protected DefaultAnnotationMetadata() {
        hasPropertyExpressions = false;
        useRepeatableDefaults = false;
    }

    /**
     * This constructor is designed to be used by compile time produced subclasses.
     *
     * @param declaredAnnotations     The directly declared annotations
     * @param declaredStereotypes     The directly declared stereotypes
     * @param allStereotypes          All of the stereotypes
     * @param allAnnotations          All of the annotations
     * @param annotationsByStereotype The annotations by stereotype
     */
    @Internal
    @UsedByGeneratedCode
    public DefaultAnnotationMetadata(
            @Nullable Map<String, Map<CharSequence, Object>> declaredAnnotations,
            @Nullable Map<String, Map<CharSequence, Object>> declaredStereotypes,
            @Nullable Map<String, Map<CharSequence, Object>> allStereotypes,
            @Nullable Map<String, Map<CharSequence, Object>> allAnnotations,
            @Nullable Map<String, List<String>> annotationsByStereotype) {
        this(declaredAnnotations, declaredStereotypes, allStereotypes, allAnnotations, annotationsByStereotype, true);
    }

    /**
     * This constructor is designed to be used by compile time produced subclasses.
     *
     * @param declaredAnnotations     The directly declared annotations
     * @param declaredStereotypes     The directly declared stereotypes
     * @param allStereotypes          All of the stereotypes
     * @param allAnnotations          All of the annotations
     * @param annotationsByStereotype The annotations by stereotype
     * @param hasPropertyExpressions  Whether property expressions exist in the metadata
     */
    @Internal
    @UsedByGeneratedCode
    public DefaultAnnotationMetadata(
            @Nullable Map<String, Map<CharSequence, Object>> declaredAnnotations,
            @Nullable Map<String, Map<CharSequence, Object>> declaredStereotypes,
            @Nullable Map<String, Map<CharSequence, Object>> allStereotypes,
            @Nullable Map<String, Map<CharSequence, Object>> allAnnotations,
            @Nullable Map<String, List<String>> annotationsByStereotype,
            boolean hasPropertyExpressions) {
        this(declaredAnnotations, declaredStereotypes, allStereotypes, allAnnotations, annotationsByStereotype, hasPropertyExpressions, false);
    }

    /**
     * This constructor is designed to be used by compile time produced subclasses.
     *
     * @param declaredAnnotations     The directly declared annotations
     * @param declaredStereotypes     The directly declared stereotypes
     * @param allStereotypes          All of the stereotypes
     * @param allAnnotations          All of the annotations
     * @param annotationsByStereotype The annotations by stereotype
     * @param hasPropertyExpressions  Whether property expressions exist in the metadata
     * @param useRepeatableDefaults   Use repeatable defaults
     */
    @Internal
    @UsedByGeneratedCode
    public DefaultAnnotationMetadata(
            @Nullable Map<String, Map<CharSequence, Object>> declaredAnnotations,
            @Nullable Map<String, Map<CharSequence, Object>> declaredStereotypes,
            @Nullable Map<String, Map<CharSequence, Object>> allStereotypes,
            @Nullable Map<String, Map<CharSequence, Object>> allAnnotations,
            @Nullable Map<String, List<String>> annotationsByStereotype,
            boolean hasPropertyExpressions,
            boolean useRepeatableDefaults) {
        super(declaredAnnotations, allAnnotations);
        this.declaredAnnotations = declaredAnnotations;
        this.declaredStereotypes = declaredStereotypes;
        this.allStereotypes = allStereotypes;
        this.allAnnotations = allAnnotations;
        this.annotationsByStereotype = annotationsByStereotype;
        this.hasPropertyExpressions = hasPropertyExpressions;
        this.useRepeatableDefaults = useRepeatableDefaults;
    }

    @NonNull
    @Override
    public AnnotationMetadata getDeclaredMetadata() {
        return new DefaultAnnotationMetadata(
                this.declaredAnnotations,
                this.declaredStereotypes,
                null,
                null,
                annotationsByStereotype,
                hasPropertyExpressions
        );
    }

    @Override
    public boolean hasPropertyExpressions() {
        return hasPropertyExpressions;
    }

    /**
     * @return The annotations that are source retention.
     */
    @Internal
    Set<String> getSourceRetentionAnnotations() {
        if (sourceRetentionAnnotations != null) {
            return Collections.unmodifiableSet(sourceRetentionAnnotations);
        }
        return Collections.emptySet();
    }

    @NonNull
    @Override
    public Map<String, Object> getDefaultValues(@NonNull String annotation) {
        ArgumentUtils.requireNonNull("annotation", annotation);
        return AnnotationMetadataSupport.getDefaultValues(annotation);
    }

    @Override
    public boolean isPresent(@NonNull String annotation, @NonNull String member) {
        boolean isPresent = false;
        if (allAnnotations != null && StringUtils.isNotEmpty(annotation)) {
            Map<CharSequence, Object> values = allAnnotations.get(annotation);
            if (values != null) {
                isPresent = values.containsKey(member);
            } else if (allStereotypes != null) {
                values = allStereotypes.get(annotation);
                if (values != null) {
                    isPresent = values.containsKey(member);
                }
            }
        }
        return isPresent;
    }

    @Override
    public <E extends Enum<E>> Optional<E> enumValue(@NonNull String annotation, Class<E> enumType) {
        return enumValue(annotation, VALUE_MEMBER, enumType, null);
    }

    @Override
    public <E extends Enum<E>> Optional<E> enumValue(@NonNull String annotation, @NonNull String member, Class<E> enumType) {
        return enumValue(annotation, member, enumType, null);
    }

    @Override
    public <E extends Enum<E>> Optional<E> enumValue(@NonNull Class<? extends Annotation> annotation, Class<E> enumType) {
        return enumValue(annotation, VALUE_MEMBER, enumType);
    }

    @Override
    public <E extends Enum<E>> Optional<E> enumValue(@NonNull Class<? extends Annotation> annotation, @NonNull String member, Class<E> enumType) {
        return enumValue(annotation, member, enumType, null);
    }

    /**
     * Retrieve the class value and optionally map its value.
     *
     * @param annotation  The annotation
     * @param member      The member
     * @param enumType    The enum type
     * @param valueMapper The value mapper
     * @param <E>         The enum type
     * @return The class value
     */
    @Override
    @Internal
    public <E extends Enum<E>> Optional<E> enumValue(@NonNull Class<? extends Annotation> annotation, @NonNull String member, Class<E> enumType, @Nullable Function<Object, Object> valueMapper) {
        ArgumentUtils.requireNonNull("annotation", annotation);
        ArgumentUtils.requireNonNull("member", member);
        final Repeatable repeatable = annotation.getAnnotation(Repeatable.class);
        if (repeatable != null) {
            Object v = getRawSingleValue(repeatable.value().getName(), VALUE_MEMBER, valueMapper);
            if (v instanceof AnnotationValue) {
                return ((AnnotationValue<?>) v).enumValue(member, enumType, valueMapper);
            }
            return Optional.empty();
        } else {
            return enumValue(annotation.getName(), member, enumType, valueMapper);
        }
    }

    @Override
    public <E extends Enum<E>> E[] enumValues(@NonNull String annotation, Class<E> enumType) {
        return enumValues(annotation, VALUE_MEMBER, enumType, null);
    }

    @Override
    public <E extends Enum<E>> E[] enumValues(@NonNull String annotation, @NonNull String member, Class<E> enumType) {
        return enumValues(annotation, member, enumType, null);
    }

    @Override
    public <E extends Enum<E>> E[] enumValues(@NonNull Class<? extends Annotation> annotation, Class<E> enumType) {
        return enumValues(annotation, VALUE_MEMBER, enumType, null);
    }

    @Override
    public <E extends Enum<E>> E[] enumValues(@NonNull Class<? extends Annotation> annotation, @NonNull String member, Class<E> enumType) {
        return enumValues(annotation, member, enumType, null);
    }

    @Override
    public <E extends Enum<E>> E[] enumValues(@NonNull Class<? extends Annotation> annotation, @NonNull String member, Class<E> enumType, @Nullable Function<Object, Object> valueMapper) {
        ArgumentUtils.requireNonNull("annotation", annotation);
        ArgumentUtils.requireNonNull("enumType", enumType);
        final Repeatable repeatable = annotation.getAnnotation(Repeatable.class);
        if (repeatable != null) {
            Object v = getRawValue(repeatable.value().getName(), member);
            if (v instanceof AnnotationValue) {
                return ((AnnotationValue<?>) v).enumValues(member, enumType);
            }
            return (E[]) Array.newInstance(enumType, 0);
        } else {
            Object v = getRawValue(annotation.getName(), member);
            return AnnotationValue.resolveEnumValues(enumType, v);
        }
    }

    @Override
    public <E extends Enum<E>> E[] enumValues(@NonNull String annotation, @NonNull String member, Class<E> enumType, @Nullable Function<Object, Object> valueMapper) {
        ArgumentUtils.requireNonNull("annotation", annotation);
        ArgumentUtils.requireNonNull("enumType", enumType);
        Object v = getRawValue(annotation, member);
        return AnnotationValue.resolveEnumValues(enumType, v);
    }

    /**
     * Retrieve the class value and optionally map its value.
     *
     * @param annotation  The annotation
     * @param member      The member
     * @param enumType    The enum type
     * @param valueMapper The value mapper
     * @param <E>         The enum type
     * @return The class value
     */
    @Override
    @Internal
    public <E extends Enum<E>> Optional<E> enumValue(@NonNull String annotation, @NonNull String member, Class<E> enumType, @Nullable Function<Object, Object> valueMapper) {
        Object rawValue = getRawSingleValue(annotation, member, valueMapper);
        return enumValueOf(enumType, rawValue);
    }

    private <E extends Enum<E>> Optional<E> enumValueOf(Class<E> enumType, Object rawValue) {
        if (rawValue != null) {
            if (enumType.isInstance(rawValue)) {
                return Optional.of((E) rawValue);
            } else {
                try {
                    return Optional.of(Enum.valueOf(enumType, rawValue.toString()));
                } catch (Exception e) {
                    return Optional.empty();
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public <T> Class<T>[] classValues(@NonNull String annotation, @NonNull String member) {
        ArgumentUtils.requireNonNull("annotation", annotation);
        ArgumentUtils.requireNonNull("member", member);

        Object rawSingleValue = getRawValue(annotation, member);
        //noinspection unchecked
        Class<T>[] classes = (Class<T>[]) AnnotationValue.resolveClassValues(rawSingleValue);
        if (classes != null) {
            return classes;
        }
        //noinspection unchecked
        return ReflectionUtils.EMPTY_CLASS_ARRAY;
    }

    @Override
    public <T> Class<T>[] classValues(@NonNull Class<? extends Annotation> annotation, @NonNull String member) {
        ArgumentUtils.requireNonNull("annotation", annotation);
        ArgumentUtils.requireNonNull("member", member);
        final Repeatable repeatable = annotation.getAnnotation(Repeatable.class);
        if (repeatable != null) {
            Object v = getRawSingleValue(repeatable.value().getName(), member, null);
            if (v instanceof AnnotationValue) {
                Class<?>[] classes = ((AnnotationValue<?>) v).classValues(member);
                return (Class<T>[]) classes;
            }
            return ReflectionUtils.EMPTY_CLASS_ARRAY;
        } else {
            return classValues(annotation.getName(), member);
        }
    }

    @NonNull
    @Override
    public Optional<Class> classValue(@NonNull Class<? extends Annotation> annotation, @NonNull String member) {
        return classValue(annotation, member, null);
    }

    /**
     * Retrieve the class value and optionally map its value.
     *
     * @param annotation  The annotation
     * @param member      The member
     * @param valueMapper The value mapper
     * @return The class value
     */
    @Override
    public Optional<Class> classValue(@NonNull Class<? extends Annotation> annotation, @NonNull String member, Function<Object, Object> valueMapper) {
        ArgumentUtils.requireNonNull("annotation", annotation);
        ArgumentUtils.requireNonNull("member", member);
        final Repeatable repeatable = annotation.getAnnotation(Repeatable.class);
        if (repeatable != null) {
            Object v = getRawSingleValue(repeatable.value().getName(), member, valueMapper);
            if (v instanceof AnnotationValue) {
                return (Optional) ((AnnotationValue<?>) v).classValue(member, valueMapper);
            }
            return Optional.empty();
        } else {
            return classValue(annotation.getName(), member, valueMapper);
        }
    }

    @NonNull
    @Override
    public Optional<Class> classValue(@NonNull String annotation, @NonNull String member) {
        ArgumentUtils.requireNonNull("annotation", annotation);
        ArgumentUtils.requireNonNull("member", member);
        return classValue(annotation, member, null);
    }


    /**
     * Retrieve the class value and optionally map its value.
     *
     * @param annotation  The annotation
     * @param member      The member
     * @param valueMapper The value mapper
     * @return The class value
     */
    @Override
    @Internal
    public Optional<Class> classValue(@NonNull String annotation, @NonNull String member, @Nullable Function<Object, Object> valueMapper) {
        Object rawValue = getRawSingleValue(annotation, member, valueMapper);

        if (rawValue instanceof AnnotationClassValue) {
            return ((AnnotationClassValue) rawValue).getType();
        } else if (rawValue instanceof Class) {
            return Optional.of((Class) rawValue);
        } else if (rawValue != null) {
            return ConversionService.SHARED.convert(rawValue, Class.class);
        }
        return Optional.empty();
    }

    @NonNull
    @Override
    public OptionalInt intValue(@NonNull String annotation, @NonNull String member) {
        ArgumentUtils.requireNonNull("annotation", annotation);
        ArgumentUtils.requireNonNull("member", member);

        return intValue(annotation, member, null);
    }

    @NonNull
    @Override
    public OptionalInt intValue(@NonNull Class<? extends Annotation> annotation, @NonNull String member) {
        return intValue(annotation, member, null);
    }

    /**
     * Retrieve the int value and optionally map its value.
     *
     * @param annotation  The annotation
     * @param member      The member
     * @param valueMapper The value mapper
     * @return The int value
     */
    @Override
    @Internal
    public OptionalInt intValue(@NonNull Class<? extends Annotation> annotation, @NonNull String member, @Nullable Function<Object, Object> valueMapper) {
        ArgumentUtils.requireNonNull("annotation", annotation);
        ArgumentUtils.requireNonNull("member", member);
        final Repeatable repeatable = annotation.getAnnotation(Repeatable.class);
        if (repeatable != null) {
            Object v = getRawSingleValue(repeatable.value().getName(), VALUE_MEMBER, valueMapper);
            if (v instanceof AnnotationValue) {
                return ((AnnotationValue<?>) v).intValue(member, valueMapper);
            }
            return OptionalInt.empty();
        } else {
            return intValue(annotation.getName(), member, valueMapper);
        }
    }

    @Override
    public Optional<Boolean> booleanValue(@NonNull String annotation, @NonNull String member) {
        ArgumentUtils.requireNonNull("annotation", annotation);
        ArgumentUtils.requireNonNull("member", member);

        return booleanValue(annotation, member, null);
    }

    @Override
    public Optional<Boolean> booleanValue(@NonNull Class<? extends Annotation> annotation, @NonNull String member) {
        return booleanValue(annotation, member, null);
    }

    /**
     * Retrieve the boolean value and optionally map its value.
     *
     * @param annotation  The annotation
     * @param member      The member
     * @param valueMapper The value mapper
     * @return The boolean value
     */
    @Override
    public Optional<Boolean> booleanValue(@NonNull Class<? extends Annotation> annotation, @NonNull String member, Function<Object, Object> valueMapper) {
        ArgumentUtils.requireNonNull("annotation", annotation);
        ArgumentUtils.requireNonNull("member", member);
        final Repeatable repeatable = annotation.getAnnotation(Repeatable.class);
        if (repeatable != null) {
            Object v = getRawSingleValue(repeatable.value().getName(), VALUE_MEMBER, null);
            if (v instanceof AnnotationValue) {
                return ((AnnotationValue<?>) v).booleanValue(member, valueMapper);
            }
            return Optional.empty();
        } else {
            return booleanValue(annotation.getName(), member, valueMapper);
        }
    }

    /**
     * Retrieve the boolean value and optionally map its value.
     *
     * @param annotation  The annotation
     * @param member      The member
     * @param valueMapper The value mapper
     * @return The boolean value
     */
    @Override
    @NonNull
    public Optional<Boolean> booleanValue(@NonNull String annotation, @NonNull String member, @Nullable Function<Object, Object> valueMapper) {
        Object rawValue = getRawSingleValue(annotation, member, valueMapper);
        if (rawValue instanceof Boolean) {
            return Optional.of((Boolean) rawValue);
        } else if (rawValue != null) {
            return Optional.of(StringUtils.isTrue(rawValue.toString()));
        }
        return Optional.empty();
    }

    @NonNull
    @Override
    public OptionalLong longValue(@NonNull String annotation, @NonNull String member) {
        ArgumentUtils.requireNonNull("annotation", annotation);
        ArgumentUtils.requireNonNull("member", member);

        return longValue(annotation, member, null);
    }

    @NonNull
    @Override
    public OptionalLong longValue(@NonNull Class<? extends Annotation> annotation, @NonNull String member) {
        return longValue(annotation, member, null);
    }

    /**
     * Retrieve the long value and optionally map its value.
     *
     * @param annotation  The annotation
     * @param member      The member
     * @param valueMapper The value mapper
     * @return The long value
     */
    @Override
    @Internal
    public OptionalLong longValue(@NonNull Class<? extends Annotation> annotation, @NonNull String member, @Nullable Function<Object, Object> valueMapper) {
        ArgumentUtils.requireNonNull("annotation", annotation);
        ArgumentUtils.requireNonNull("member", member);
        final Repeatable repeatable = annotation.getAnnotation(Repeatable.class);
        if (repeatable != null) {
            Object v = getRawSingleValue(repeatable.value().getName(), VALUE_MEMBER, valueMapper);
            if (v instanceof AnnotationValue) {
                return ((AnnotationValue<?>) v).longValue(member, valueMapper);
            }
            return OptionalLong.empty();
        } else {
            return longValue(annotation.getName(), member, valueMapper);
        }
    }

    /**
     * Retrieve the long value and optionally map its value.
     *
     * @param annotation  The annotation
     * @param member      The member
     * @param valueMapper The value mapper
     * @return The long value
     */
    @Override
    @NonNull
    public OptionalLong longValue(@NonNull String annotation, @NonNull String member, @Nullable Function<Object, Object> valueMapper) {
        Object rawValue = getRawSingleValue(annotation, member, valueMapper);
        if (rawValue instanceof Number) {
            return OptionalLong.of(((Number) rawValue).longValue());
        } else if (rawValue instanceof CharSequence) {
            final String str = rawValue.toString();
            if (StringUtils.isNotEmpty(str)) {
                try {
                    final long i = Long.parseLong(str);
                    return OptionalLong.of(i);
                } catch (NumberFormatException e) {
                    throw new ConfigurationException("Invalid value [" + str + "] of [" + member + "] of annotation [" + annotation + "]: " + e.getMessage(), e);
                }
            }

        }
        return OptionalLong.empty();
    }

    /**
     * Retrieve the int value and optionally map its value.
     *
     * @param annotation  The annotation
     * @param member      The member
     * @param valueMapper The value mapper
     * @return The int value
     */
    @Override
    @NonNull
    public OptionalInt intValue(@NonNull String annotation, @NonNull String member, @Nullable Function<Object, Object> valueMapper) {
        Object rawValue = getRawSingleValue(annotation, member, valueMapper);
        if (rawValue instanceof Number) {
            return OptionalInt.of(((Number) rawValue).intValue());
        } else if (rawValue instanceof CharSequence) {
            final String str = rawValue.toString();
            if (StringUtils.isNotEmpty(str)) {
                try {
                    final int i = Integer.parseInt(str);
                    return OptionalInt.of(i);
                } catch (NumberFormatException e) {
                    throw new ConfigurationException("Invalid value [" + str + "] of [" + member + "] of annotation [" + annotation + "]: " + e.getMessage(), e);
                }
            }

        }
        return OptionalInt.empty();
    }

    @NonNull
    @Override
    public Optional<String> stringValue(@NonNull Class<? extends Annotation> annotation, @NonNull String member) {
        return stringValue(annotation, member, null);
    }

    /**
     * Retrieve the string value and optionally map its value.
     *
     * @param annotation  The annotation
     * @param member      The member
     * @param valueMapper The value mapper
     * @return The int value
     */
    @Override
    public Optional<String> stringValue(@NonNull Class<? extends Annotation> annotation, @NonNull String member, Function<Object, Object> valueMapper) {
        ArgumentUtils.requireNonNull("annotation", annotation);
        final Repeatable repeatable = annotation.getAnnotation(Repeatable.class);
        if (repeatable != null) {
            Object v = getRawSingleValue(repeatable.value().getName(), VALUE_MEMBER, valueMapper);
            if (v instanceof AnnotationValue) {
                return ((AnnotationValue<?>) v).stringValue(member, valueMapper);
            }
            return Optional.empty();
        } else {
            return stringValue(annotation.getName(), member, valueMapper);
        }
    }

    @NonNull
    @Override
    public String[] stringValues(@NonNull Class<? extends Annotation> annotation, @NonNull String member) {
        return stringValues(annotation.getName(), member, null);
    }

    @NonNull
    @Override
    public String[] stringValues(@NonNull String annotation, @NonNull String member) {
        return stringValues(annotation, member, null);
    }

    /**
     * Retrieve the string value and optionally map its value.
     *
     * @param annotation  The annotation
     * @param member      The member
     * @param valueMapper The value mapper
     * @return The int value
     */
    @Override
    @NonNull
    public String[] stringValues(@NonNull Class<? extends Annotation> annotation, @NonNull String member, Function<Object, Object> valueMapper) {
        ArgumentUtils.requireNonNull("annotation", annotation);
        final Repeatable repeatable = annotation.getAnnotation(Repeatable.class);
        if (repeatable != null) {
            Object v = getRawValue(repeatable.value().getName(), member);
            if (v instanceof AnnotationValue) {
                return ((AnnotationValue<?>) v).stringValues(member, valueMapper);
            }
            return StringUtils.EMPTY_STRING_ARRAY;
        } else {
            Object v = getRawValue(annotation.getName(), member);
            String[] strings = AnnotationValue.resolveStringValues(v, valueMapper);
            return strings != null ? strings : StringUtils.EMPTY_STRING_ARRAY;
        }
    }


    /**
     * Retrieve the string value and optionally map its value.
     *
     * @param annotation  The annotation
     * @param member      The member
     * @param valueMapper The value mapper
     * @return The int value
     */
    @Override
    @NonNull
    public String[] stringValues(@NonNull String annotation, @NonNull String member, Function<Object, Object> valueMapper) {
        ArgumentUtils.requireNonNull("annotation", annotation);
        Object v = getRawValue(annotation, member);
        String[] strings = AnnotationValue.resolveStringValues(v, valueMapper);
        return strings != null ? strings : StringUtils.EMPTY_STRING_ARRAY;
    }

    @NonNull
    @Override
    public Optional<String> stringValue(@NonNull String annotation, @NonNull String member) {
        ArgumentUtils.requireNonNull("annotation", annotation);
        ArgumentUtils.requireNonNull("member", member);
        return stringValue(annotation, member, null);
    }

    /**
     * Retrieve the string value and optionally map its value.
     *
     * @param annotation  The annotation
     * @param member      The member
     * @param valueMapper The value mapper
     * @return The string value
     */
    @Override
    @NonNull
    public Optional<String> stringValue(@NonNull String annotation, @NonNull String member, @Nullable Function<Object, Object> valueMapper) {
        Object rawValue = getRawSingleValue(annotation, member, valueMapper);
        if (rawValue instanceof CharSequence) {
            return Optional.of(rawValue.toString());
        } else if (rawValue instanceof Class) {
            String name = ((Class) rawValue).getName();
            return Optional.of(name);
        } else if (rawValue != null) {
            return Optional.of(rawValue.toString());
        }
        return Optional.empty();
    }

    @Override
    public boolean isTrue(@NonNull Class<? extends Annotation> annotation, @NonNull String member) {
        return isTrue(annotation, member, null);
    }

    /**
     * Retrieve the boolean value and optionally map its value.
     *
     * @param annotation  The annotation
     * @param member      The member
     * @param valueMapper The value mapper
     * @return The boolean value
     */
    @Override
    public boolean isTrue(@NonNull Class<? extends Annotation> annotation, @NonNull String member, Function<Object, Object> valueMapper) {
        ArgumentUtils.requireNonNull("annotation", annotation);
        ArgumentUtils.requireNonNull("member", member);
        final Repeatable repeatable = annotation.getAnnotation(Repeatable.class);
        if (repeatable != null) {
            Object v = getRawSingleValue(repeatable.value().getName(), VALUE_MEMBER, valueMapper);
            if (v instanceof AnnotationValue) {
                return ((AnnotationValue<?>) v).isTrue(member, valueMapper);
            }
            return false;
        } else {
            return isTrue(annotation.getName(), member, valueMapper);
        }
    }

    @Override
    public boolean isTrue(@NonNull String annotation, @NonNull String member) {
        ArgumentUtils.requireNonNull("annotation", annotation);
        ArgumentUtils.requireNonNull("member", member);

        return isTrue(annotation, member, null);
    }

    /**
     * Retrieve the boolean value and optionally map its value.
     *
     * @param annotation  The annotation
     * @param member      The member
     * @param valueMapper The value mapper
     * @return The boolean value
     */
    @Override
    public boolean isTrue(@NonNull String annotation, @NonNull String member, @Nullable Function<Object, Object> valueMapper) {
        Object rawValue = getRawSingleValue(annotation, member, valueMapper);

        if (rawValue instanceof Boolean) {
            return (Boolean) rawValue;
        } else if (rawValue != null) {
            String booleanString = rawValue.toString().toLowerCase(Locale.ENGLISH);
            return StringUtils.isTrue(booleanString);
        }
        return false;
    }

    @Override
    public boolean isFalse(@NonNull String annotation, @NonNull String member) {
        return !isTrue(annotation, member);
    }

    @NonNull
    @Override
    public OptionalDouble doubleValue(@NonNull String annotation, @NonNull String member) {
        ArgumentUtils.requireNonNull("annotation", annotation);
        ArgumentUtils.requireNonNull("member", member);
        return doubleValue(annotation, member, null);
    }

    @NonNull
    @Override
    public OptionalDouble doubleValue(@NonNull Class<? extends Annotation> annotation, @NonNull String member) {
        return doubleValue(annotation, member, null);
    }

    /**
     * Retrieve the double value and optionally map its value.
     *
     * @param annotation  The annotation
     * @param member      The member
     * @param valueMapper The value mapper
     * @return The double value
     */
    @Override
    @Internal
    public OptionalDouble doubleValue(@NonNull Class<? extends Annotation> annotation, @NonNull String member, @Nullable Function<Object, Object> valueMapper) {
        ArgumentUtils.requireNonNull("annotation", annotation);
        ArgumentUtils.requireNonNull("member", member);
        final Repeatable repeatable = annotation.getAnnotation(Repeatable.class);
        if (repeatable != null) {
            Object v = getRawSingleValue(repeatable.value().getName(), VALUE_MEMBER, valueMapper);
            if (v instanceof AnnotationValue) {
                return ((AnnotationValue<?>) v).doubleValue(member, valueMapper);
            }
            return OptionalDouble.empty();
        } else {
            return doubleValue(annotation.getName(), member);
        }
    }

    /**
     * Retrieve the double value and optionally map its value.
     *
     * @param annotation  The annotation
     * @param member      The member
     * @param valueMapper The value mapper
     * @return The double value
     */
    @Override
    @NonNull
    @Internal
    public OptionalDouble doubleValue(@NonNull String annotation, @NonNull String member, Function<Object, Object> valueMapper) {
        Object rawValue = getRawSingleValue(annotation, member, valueMapper);
        if (rawValue instanceof Number) {
            return OptionalDouble.of(((Number) rawValue).doubleValue());
        } else if (rawValue instanceof CharSequence) {
            final String str = rawValue.toString();
            if (StringUtils.isNotEmpty(str)) {
                try {
                    final double i = Double.parseDouble(str);
                    return OptionalDouble.of(i);
                } catch (NumberFormatException e) {
                    throw new ConfigurationException("Invalid value [" + str + "] of member [" + member + "] of annotation [" + annotation + "]: " + e.getMessage(), e);
                }
            }

        }
        return OptionalDouble.empty();
    }

    @Override
    public @NonNull
    <T> Optional<T> getValue(@NonNull Class<? extends Annotation> annotation, @NonNull String member, @NonNull Class<T> requiredType) {
        ArgumentUtils.requireNonNull("annotation", annotation);
        ArgumentUtils.requireNonNull("member", member);
        ArgumentUtils.requireNonNull("requiredType", requiredType);

        final Repeatable repeatable = annotation.getAnnotation(Repeatable.class);
        final boolean isRepeatable = repeatable != null;
        if (isRepeatable) {
            List<? extends AnnotationValue<? extends Annotation>> values = getAnnotationValuesByType(annotation);
            if (!values.isEmpty()) {
                return values.iterator().next().get(member, requiredType);
            } else {
                return Optional.empty();
            }
        } else {
            return getValue(annotation.getName(), member, requiredType);
        }
    }

    @Override
    public @NonNull
    <T> Optional<T> getValue(@NonNull String annotation, @NonNull String member, @NonNull Argument<T> requiredType) {
        return getValue(annotation, member, requiredType, null);
    }

    /**
     * Resolves the given value performing type conversion as necessary.
     *
     * @param annotation   The annotation
     * @param member       The member
     * @param requiredType The required type
     * @param valueMapper  The value mapper
     * @param <T>          The generic type
     * @return The resolved value
     */
    @Override
    @NonNull
    public <T> Optional<T> getValue(@NonNull String annotation, @NonNull String member, @NonNull Argument<T> requiredType, @Nullable Function<Object, Object> valueMapper) {
        ArgumentUtils.requireNonNull("annotation", annotation);
        ArgumentUtils.requireNonNull("member", member);
        ArgumentUtils.requireNonNull("requiredType", requiredType);
        Optional<T> resolved = Optional.empty();
        if (allAnnotations != null && StringUtils.isNotEmpty(annotation)) {
            Map<CharSequence, Object> values = allAnnotations.get(annotation);
            if (values != null) {
                Object rawValue = values.get(member);
                if (rawValue != null) {
                    if (valueMapper != null) {
                        rawValue = valueMapper.apply(rawValue);
                    }
                    resolved = ConversionService.SHARED.convert(
                            rawValue, requiredType
                    );
                }
            } else if (allStereotypes != null) {
                values = allStereotypes.get(annotation);
                if (values != null) {
                    Object rawValue = values.get(member);
                    if (rawValue != null) {
                        if (valueMapper != null) {
                            rawValue = valueMapper.apply(rawValue);
                        }
                        resolved = ConversionService.SHARED.convert(
                                rawValue, requiredType
                        );
                    }
                }
            }
        }

        if (!resolved.isPresent() && hasStereotype(annotation)) {
            return getDefaultValue(annotation, member, requiredType);
        }

        return resolved;
    }

    @Override
    public @NonNull
    <T> Optional<T> getDefaultValue(@NonNull String annotation, @NonNull String member, @NonNull Class<T> requiredType) {
        ArgumentUtils.requireNonNull("annotation", annotation);
        ArgumentUtils.requireNonNull("member", member);
        ArgumentUtils.requireNonNull("requiredType", requiredType);

        Map<String, Object> defaultValues = getDefaultValues(annotation);
        if (defaultValues.containsKey(member)) {
            final Object v = defaultValues.get(member);
            if (requiredType.isInstance(v)) {
                return (Optional<T>) Optional.of(v);
            } else {
                return ConversionService.SHARED.convert(v, requiredType);
            }
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NonNull
    <T extends Annotation> List<AnnotationValue<T>> getAnnotationValuesByType(@Nullable Class<T> annotationType) {
        if (annotationType != null) {
            final String annotationTypeName = annotationType.getName();
            List<AnnotationValue<T>> results = annotationValuesByType.get(annotationTypeName);
            if (results == null) {

                results = resolveAnnotationValuesByType(annotationType, allAnnotations, allStereotypes);
                if (results != null) {
                    return results;
                } else if (allAnnotations != null) {
                    final Map<CharSequence, Object> values = allAnnotations.get(annotationTypeName);
                    if (values != null) {
                        results = Collections.singletonList(new AnnotationValue<>(annotationTypeName, values));
                    }
                }

                if (results == null) {
                    results = Collections.emptyList();
                }
                annotationValuesByType.put(annotationTypeName, results);
            }
            return results;
        }
        return Collections.emptyList();
    }

    @Override
    public <T extends Annotation> List<AnnotationValue<T>> getAnnotationValuesByName(String annotationType) {
        if (annotationType != null) {
            String repeatableTypeName = getRepeatedName(annotationType);
            if (repeatableTypeName == null) {
                repeatableTypeName = AnnotationMetadataSupport.getRepeatableAnnotation(annotationType);
            }
            if (repeatableTypeName != null) {

                List<AnnotationValue<T>> results =
                        resolveRepeatableAnnotations(repeatableTypeName,
                                                     allAnnotations,
                                                     allStereotypes
                );
                if (results != null) {
                    return results;
                } else if (allAnnotations != null) {
                    final Map<CharSequence, Object> values = allAnnotations.get(annotationType);
                    if (values != null) {
                        results = Collections.singletonList(new AnnotationValue<>(annotationType, values));
                    }
                }

                if (results == null) {
                    results = Collections.emptyList();
                }
                annotationValuesByType.put(annotationType, results);
            }
        }
        return Collections.emptyList();
    }

    @Override
    public @NonNull
    <T extends Annotation> List<AnnotationValue<T>> getDeclaredAnnotationValuesByType(@NonNull Class<T> annotationType) {
        if (annotationType != null) {
            Map<String, Map<CharSequence, Object>> sourceAnnotations = this.declaredAnnotations;
            Map<String, Map<CharSequence, Object>> sourceStereotypes = this.declaredStereotypes;

            List<AnnotationValue<T>> results = resolveAnnotationValuesByType(annotationType, sourceAnnotations, sourceStereotypes);
            if (results != null) {
                return results;
            }
        }
        return Collections.emptyList();
    }

    @Override
    public <T extends Annotation> List<AnnotationValue<T>> getDeclaredAnnotationValuesByName(String annotationType) {
        if (annotationType != null) {
            Map<String, Map<CharSequence, Object>> sourceAnnotations = this.declaredAnnotations;
            Map<String, Map<CharSequence, Object>> sourceStereotypes = this.declaredStereotypes;
            String repeatableTypeName = getRepeatedName(annotationType);
            if (repeatableTypeName == null) {
                repeatableTypeName = AnnotationMetadataSupport.getRepeatableAnnotation(annotationType);
            }
            List<AnnotationValue<T>> results =
                    resolveRepeatableAnnotations(repeatableTypeName,
                                                 sourceAnnotations,
                                                 sourceStereotypes
                    );
            if (results != null) {
                return results;
            }
        }
        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Annotation> T[] synthesizeAnnotationsByType(@NonNull Class<T> annotationClass) {

        if (annotationClass != null) {
            List<AnnotationValue<T>> values = getAnnotationValuesByType(annotationClass);

            return values.stream()
                    .map(entries -> AnnotationMetadataSupport.buildAnnotation(annotationClass, entries))
                    .toArray(value -> (T[]) Array.newInstance(annotationClass, value));
        }

        //noinspection unchecked
        return (T[]) AnnotationUtil.ZERO_ANNOTATIONS;
    }

    @Override
    public <T extends Annotation> T[] synthesizeDeclaredAnnotationsByType(@NonNull Class<T> annotationClass) {
        if (annotationClass != null) {
            List<AnnotationValue<T>> values = getAnnotationValuesByType(annotationClass);

            return values.stream()
                    .map(entries -> AnnotationMetadataSupport.buildAnnotation(annotationClass, entries))
                    .toArray(value -> (T[]) Array.newInstance(annotationClass, value));
        }

        //noinspection unchecked
        return (T[]) AnnotationUtil.ZERO_ANNOTATIONS;
    }

    @Override
    public boolean isEmpty() {
        return allAnnotations == null || allAnnotations.isEmpty();
    }

    @Override
    public boolean hasDeclaredAnnotation(String annotation) {
        return declaredAnnotations != null && StringUtils.isNotEmpty(annotation) && declaredAnnotations.containsKey(annotation);
    }

    @Override
    public boolean hasAnnotation(String annotation) {
        return hasDeclaredAnnotation(annotation) || (allAnnotations != null && StringUtils.isNotEmpty(annotation) && allAnnotations.containsKey(annotation));
    }

    @Override
    public boolean hasStereotype(String annotation) {
        return hasAnnotation(annotation) || (allStereotypes != null && StringUtils.isNotEmpty(annotation) && allStereotypes.containsKey(annotation));
    }

    @Override
    public boolean hasDeclaredStereotype(String annotation) {
        return hasDeclaredAnnotation(annotation) || (declaredStereotypes != null && StringUtils.isNotEmpty(annotation) && declaredStereotypes.containsKey(annotation));
    }

    @NonNull
    @Override
    public Optional<Class<? extends Annotation>> getAnnotationTypeByStereotype(@Nullable String stereotype) {
        if (stereotype != null) {
            if (annotationsByStereotype != null) {
                List<String> annotations = annotationsByStereotype.get(stereotype);
                if (CollectionUtils.isNotEmpty(annotations)) {
                    return getAnnotationType(annotations.get(0));
                }
            }
            if (allAnnotations != null && allAnnotations.containsKey(stereotype)) {
                return getAnnotationType(stereotype);
            }
            if (declaredAnnotations != null && declaredAnnotations.containsKey(stereotype)) {
                return getAnnotationType(stereotype);
            }
        }
        return Optional.empty();
    }

    @NonNull
    @Override
    public Optional<String> getAnnotationNameByStereotype(@Nullable String stereotype) {
        if (stereotype != null) {
            if (annotationsByStereotype != null) {
                List<String> annotations = annotationsByStereotype.get(stereotype);
                if (CollectionUtils.isNotEmpty(annotations)) {
                    return Optional.of(annotations.get(0));
                }
            }
            if (allAnnotations != null && allAnnotations.containsKey(stereotype)) {
                return Optional.of(stereotype);
            }
            if (declaredAnnotations != null && declaredAnnotations.containsKey(stereotype)) {
                return Optional.of(stereotype);
            }
        }
        return Optional.empty();
    }

    @Override
    public @NonNull
    List<String> getAnnotationNamesByStereotype(@Nullable String stereotype) {
        if (stereotype == null) {
            return Collections.emptyList();
        }
        if (annotationsByStereotype != null) {
            List<String> annotations = annotationsByStereotype.get(stereotype);
            if (annotations != null) {
                return Collections.unmodifiableList(annotations);
            }
        }
        if (allAnnotations != null && allAnnotations.containsKey(stereotype)) {
            return StringUtils.internListOf(stereotype);
        }
        if (declaredAnnotations != null && declaredAnnotations.containsKey(stereotype)) {
            return StringUtils.internListOf(stereotype);
        }
        return Collections.emptyList();
    }

    @Override
    public <T extends Annotation> List<AnnotationValue<T>> getAnnotationValuesByStereotype(String stereotype) {
        if (stereotype == null) {
            return Collections.emptyList();
        }
        if (annotationsByStereotype != null) {
            List<String> annotations = annotationsByStereotype.get(stereotype);
            if (annotations != null) {
                List<AnnotationValue<T>> result = new ArrayList<>(annotations.size());
                for (String annotation : annotations) {
                    String repeatableTypeName = getRepeatedName(annotation);
                    if (repeatableTypeName == null) {
                        repeatableTypeName = AnnotationMetadataSupport.getRepeatableAnnotation(annotation);
                    }
                    if (repeatableTypeName != null) {
                        List<AnnotationValue<T>> results =
                            resolveRepeatableAnnotations(repeatableTypeName,
                                allAnnotations,
                                allStereotypes
                            );
                        if (results != null) {
                            result.addAll(results);
                        }
                    } else {
                        result.add(getAnnotation(annotation));
                    }
                }
                return Collections.unmodifiableList(result);
            }
        }
        if (allAnnotations != null) {
            return getAnnotationValuesByName(stereotype);
        }
        if (declaredAnnotations != null) {
            return getDeclaredAnnotationValuesByName(stereotype);
        }
        return Collections.emptyList();
    }

    @Override
    public @NonNull
    Set<String> getAnnotationNames() {
        if (allAnnotations != null) {
            return allAnnotations.keySet();
        }
        return Collections.emptySet();
    }

    @Override
    public Set<String> getStereotypeAnnotationNames() {
        if (allStereotypes != null) {
            return Collections.unmodifiableSet(allStereotypes.keySet());
        }
        return Collections.emptySet();
    }

    @Override
    public Set<String> getDeclaredStereotypeAnnotationNames() {
        if (declaredStereotypes != null) {
            return Collections.unmodifiableSet(declaredStereotypes.keySet());
        }
        return Collections.emptySet();
    }

    @Override
    public @NonNull
    Set<String> getDeclaredAnnotationNames() {
        if (declaredAnnotations != null) {
            return declaredAnnotations.keySet();
        }
        return Collections.emptySet();
    }

    @Override
    public @NonNull
    List<String> getDeclaredAnnotationNamesByStereotype(@Nullable String stereotype) {
        if (stereotype == null) {
            return Collections.emptyList();
        }
        if (annotationsByStereotype != null) {
            List<String> annotations = annotationsByStereotype.get(stereotype);
            if (annotations != null) {
                annotations = new ArrayList<>(annotations);
                if (declaredAnnotations != null) {
                    annotations.removeIf(s -> !declaredAnnotations.containsKey(s));
                    return Collections.unmodifiableList(annotations);
                } else {
                    // no declared
                    return Collections.emptyList();
                }
            }
        }
        if (declaredAnnotations != null && declaredAnnotations.containsKey(stereotype)) {
            return StringUtils.internListOf(stereotype);
        }
        return Collections.emptyList();
    }

    @Override
    public @NonNull
    Optional<Class<? extends Annotation>> getAnnotationType(@NonNull String name) {
        return AnnotationMetadataSupport.getAnnotationType(name);
    }

    @Override
    public @NonNull
    Optional<Class<? extends Annotation>> getAnnotationType(@NonNull String name, @NonNull ClassLoader classLoader) {
        return AnnotationMetadataSupport.getAnnotationType(name, classLoader);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public @NonNull
    <T extends Annotation> Optional<AnnotationValue<T>> findAnnotation(@NonNull String annotation) {
        ArgumentUtils.requireNonNull("annotation", annotation);
        if (allAnnotations != null && StringUtils.isNotEmpty(annotation)) {
            Map<CharSequence, Object> values = allAnnotations.get(annotation);
            if (values != null) {
                return Optional.of(new AnnotationValue<>(annotation, values, getDefaultValues(annotation)));
            } else if (allStereotypes != null) {
                values = allStereotypes.get(annotation);
                if (values != null) {
                    return Optional.of(new AnnotationValue<>(annotation, values, getDefaultValues(annotation)));
                }
            }
        }
        return Optional.empty();
    }

    @SuppressWarnings("Duplicates")
    @Override
    public @NonNull
    <T extends Annotation> Optional<AnnotationValue<T>> findDeclaredAnnotation(@NonNull String annotation) {
        ArgumentUtils.requireNonNull("annotation", annotation);
        if (declaredAnnotations != null && StringUtils.isNotEmpty(annotation)) {
            Map<CharSequence, Object> values = declaredAnnotations.get(annotation);
            if (values != null) {
                return Optional.of(new AnnotationValue<>(annotation, values, getDefaultValues(annotation)));
            } else if (declaredStereotypes != null) {
                values = declaredStereotypes.get(annotation);
                if (values != null) {
                    return Optional.of(new AnnotationValue<>(annotation, values, getDefaultValues(annotation)));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public @NonNull
    <T> OptionalValues<T> getValues(@NonNull String annotation, @NonNull Class<T> valueType) {
        ArgumentUtils.requireNonNull("annotation", annotation);
        ArgumentUtils.requireNonNull("valueType", valueType);
        if (allAnnotations != null && StringUtils.isNotEmpty(annotation)) {
            Map<CharSequence, Object> values = allAnnotations.get(annotation);
            if (values != null) {
                return OptionalValues.of(valueType, values);
            } else if (allStereotypes != null) {
                values = allStereotypes.get(annotation);
                if (values != null) {
                    return OptionalValues.of(valueType, values);
                }
            }
        }
        return OptionalValues.empty();
    }

    @NonNull
    @Override
    public Map<CharSequence, Object> getValues(@NonNull String annotation) {
        ArgumentUtils.requireNonNull("annotation", annotation);
        if (allAnnotations != null && StringUtils.isNotEmpty(annotation)) {
            Map<CharSequence, Object> values = allAnnotations.get(annotation);
            if (values != null) {
                return Collections.unmodifiableMap(values);
            } else if (allStereotypes != null) {
                values = allStereotypes.get(annotation);
                if (values != null) {
                    return Collections.unmodifiableMap(values);
                }
            }
        }
        return Collections.emptyMap();
    }

    @Override
    public @NonNull
    <T> Optional<T> getDefaultValue(@NonNull String annotation, @NonNull String member, @NonNull Argument<T> requiredType) {
        ArgumentUtils.requireNonNull("annotation", annotation);
        ArgumentUtils.requireNonNull("member", member);
        ArgumentUtils.requireNonNull("requiredType", requiredType);
        // Note this method should never reference the "annotationDefaultValues" field, which is used only at compile time
        Map<String, Object> defaultValues = getDefaultValues(annotation);
        if (defaultValues.containsKey(member)) {
            return ConversionService.SHARED.convert(defaultValues.get(member), requiredType);
        }
        return Optional.empty();
    }

    @Override
    public boolean isRepeatableAnnotation(Class<? extends Annotation> annotation) {
        if (useRepeatableDefaults) {
            return isRepeatableAnnotation(annotation.getName());
        } else {
            return super.isRepeatableAnnotation(annotation);
        }
    }

    @Override
    public boolean isRepeatableAnnotation(String annotation) {
        return AnnotationMetadataSupport.getRepeatableAnnotation(annotation) != null;
    }

    @Override
    public Optional<String> findRepeatableAnnotation(Class<? extends Annotation> annotation) {
        if (useRepeatableDefaults) {
            return findRepeatableAnnotation(annotation.getName());
        } else {
            return super.findRepeatableAnnotation(annotation);
        }
    }

    @Override
    public Optional<String> findRepeatableAnnotation(String annotation) {
        return Optional.ofNullable(AnnotationMetadataSupport.getRepeatableAnnotation(annotation));
    }

    @Override
    public AnnotationMetadata copyAnnotationMetadata() {
        return clone();
    }

    @Override
    public DefaultAnnotationMetadata clone() {
        DefaultAnnotationMetadata cloned = new DefaultAnnotationMetadata(
                declaredAnnotations != null ? cloneMapOfMapValue(declaredAnnotations) : null,
                declaredStereotypes != null ? cloneMapOfMapValue(declaredStereotypes) : null,
                allStereotypes != null ? cloneMapOfMapValue(allStereotypes) : null,
                allAnnotations != null ? cloneMapOfMapValue(allAnnotations) : null,
                annotationsByStereotype != null ? cloneMapOfListValue(annotationsByStereotype) : null,
                hasPropertyExpressions
        );
        if (repeated != null) {
            cloned.repeated = new HashMap<>(repeated);
        }
        if (sourceRetentionAnnotations != null) {
            cloned.sourceRetentionAnnotations = new HashSet<>(sourceRetentionAnnotations);
        }
        if (annotationDefaultValues != null) {
            cloned.annotationDefaultValues = cloneMapOfMapValue(annotationDefaultValues);
        }
        return cloned;
    }

    protected final <X, Y, K> Map<K, Map<X, Y>> cloneMapOfMapValue(Map<K, Map<X, Y>> toClone) {
        return toClone.entrySet().stream()
                .map(e -> new AbstractMap.SimpleEntry<>(e.getKey(), cloneMap(e.getValue())))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue, (a, b) -> a, LinkedHashMap::new));
    }

    protected final <K, V> Map<K, List<V>> cloneMapOfListValue(Map<K, List<V>> toClone) {
        return toClone.entrySet().stream()
                .map(e -> new AbstractMap.SimpleEntry<>(e.getKey(), new ArrayList<>(e.getValue())))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue, (a, b) -> a, LinkedHashMap::new));
    }

    protected final <K, V> Map<K, V> cloneMap(Map<K, V> map) {
        Map<K, V> newMap;
        if (map instanceof LinkedHashMap) {
            newMap = (Map<K, V>) ((LinkedHashMap<K, V>) map).clone();
        } else {
            newMap = new LinkedHashMap<>(map);
        }
        for (Map.Entry<K, V> entry : newMap.entrySet()) {
            if (entry.getValue() instanceof Set) {
                LinkedHashSet<Object> newValue = new LinkedHashSet<>((Collection) entry.getValue());
                entry.setValue((V) newValue);
            }
        }
        return new HashMap<>(newMap);
    }

    /**
     * Adds an annotation and its member values, if the annotation already exists the data will be merged with existing
     * values replaced.
     *
     * @param annotation The annotation
     * @param values     The values
     */
    @SuppressWarnings("WeakerAccess")
    protected void addAnnotation(String annotation, Map<CharSequence, Object> values) {
        addAnnotation(annotation, values, RetentionPolicy.RUNTIME);
    }


    /**
     * Adds an annotation and its member values, if the annotation already exists the data will be merged with existing
     * values replaced.
     *
     * @param annotation      The annotation
     * @param values          The values
     * @param retentionPolicy The retention policy
     */
    @SuppressWarnings("WeakerAccess")
    protected void addAnnotation(String annotation, Map<CharSequence, Object> values, RetentionPolicy retentionPolicy) {
        if (annotation != null) {
            String repeatedName = getRepeatedName(annotation);
            Object v = values.get(AnnotationMetadata.VALUE_MEMBER);
            if (v instanceof io.micronaut.core.annotation.AnnotationValue[]) {
                io.micronaut.core.annotation.AnnotationValue[] avs = (io.micronaut.core.annotation.AnnotationValue[]) v;
                for (io.micronaut.core.annotation.AnnotationValue av : avs) {
                    addRepeatable(annotation, av);
                }
            } else if (v instanceof Iterable && repeatedName != null) {
                Iterable i = (Iterable) v;
                for (Object o : i) {
                    if (o instanceof io.micronaut.core.annotation.AnnotationValue) {
                        addRepeatable(annotation, ((io.micronaut.core.annotation.AnnotationValue) o));
                    }
                }
            } else {
                Map<String, Map<CharSequence, Object>> allAnnotations = getAllAnnotations();
                addAnnotation(annotation, values, null, allAnnotations, false, retentionPolicy);
            }
        }
    }

    /**
     * Adds an annotation directly declared on the element and its member values, if the annotation already exists the
     * data will be merged with existing values replaced.
     *
     * @param annotation The annotation
     * @param values     The values
     */
    protected final void addDefaultAnnotationValues(String annotation, Map<CharSequence, Object> values) {
        if (annotation != null) {
            Map<String, Map<CharSequence, Object>> annotationDefaults = this.annotationDefaultValues;
            if (annotationDefaults == null) {
                this.annotationDefaultValues = new LinkedHashMap<>();
                annotationDefaults = this.annotationDefaultValues;
            }

            putValues(annotation, values, annotationDefaults);
        }
    }

    /**
     * Returns whether annotation defaults are registered for the give annotation. Used by generated byte code. DO NOT REMOVE.
     *
     * @param annotation The annotation name
     * @return True if defaults have already been registered
     */
    @SuppressWarnings("unused")
    @Internal
    @UsedByGeneratedCode
    public static boolean areAnnotationDefaultsRegistered(String annotation) {
        return AnnotationMetadataSupport.hasDefaultValues(annotation);
    }

    /**
     * Registers annotation default values. Used by generated byte code. DO NOT REMOVE.
     *
     * @param annotation    The annotation name
     * @param defaultValues The default values
     */
    @SuppressWarnings("unused")
    @Internal
    @UsedByGeneratedCode
    public static void registerAnnotationDefaults(String annotation, Map<String, Object> defaultValues) {
        AnnotationMetadataSupport.registerDefaultValues(annotation, defaultValues);
    }

    /**
     * Registers annotation default values. Used by generated byte code. DO NOT REMOVE.
     *
     * @param annotation    The annotation name
     * @param defaultValues The default values
     */
    @SuppressWarnings("unused")
    @Internal
    @UsedByGeneratedCode
    public static void registerAnnotationDefaults(AnnotationClassValue<?> annotation, Map<String, Object> defaultValues) {
        AnnotationMetadataSupport.registerDefaultValues(annotation, defaultValues);
    }

    /**
     * Registers annotation default values. Used by generated byte code. DO NOT REMOVE.
     *
     * @param annotation The annotation
     */
    @SuppressWarnings("unused")
    @Internal
    @UsedByGeneratedCode
    public static void registerAnnotationType(AnnotationClassValue<?> annotation) {
        AnnotationMetadataSupport.registerAnnotationType(annotation);
    }

    /**
     * Registers repeatable annotations. Annotation container -> annotations item. Used by generated byte code. DO NOT REMOVE.
     *
     * @param repeatableAnnotations The annotation
     */
    @SuppressWarnings("unused")
    @Internal
    @UsedByGeneratedCode
    public static void registerRepeatableAnnotations(Map<String, String> repeatableAnnotations) {
        AnnotationMetadataSupport.registerRepeatableAnnotations(repeatableAnnotations);
    }

    /**
     * Adds a repeatable annotation value. If a value already exists will be added
     *
     * @param annotationName  The annotation name
     * @param annotationValue The annotation value
     */
    protected void addRepeatable(String annotationName, io.micronaut.core.annotation.AnnotationValue annotationValue) {
        addRepeatable(annotationName, annotationValue, annotationValue.getRetentionPolicy());
    }

    /**
     * Adds a repeatable annotation value. If a value already exists will be added
     *
     * @param annotationName  The annotation name
     * @param annotationValue The annotation value
     * @param retentionPolicy The retention policy
     */
    protected void addRepeatable(String annotationName, io.micronaut.core.annotation.AnnotationValue annotationValue, RetentionPolicy retentionPolicy) {
        if (StringUtils.isNotEmpty(annotationName) && annotationValue != null) {
            Map<String, Map<CharSequence, Object>> allAnnotations = getAllAnnotations();

            addRepeatableInternal(annotationName, annotationValue, allAnnotations, retentionPolicy);
        }
    }

    /**
     * Adds a repeatable stereotype value. If a value already exists will be added
     *
     * @param parents         The parent annotations
     * @param stereotype      The annotation name
     * @param annotationValue The annotation value
     */
    protected void addRepeatableStereotype(List<String> parents, String stereotype, io.micronaut.core.annotation.AnnotationValue annotationValue) {
        Map<String, Map<CharSequence, Object>> allStereotypes = getAllStereotypes();
        List<String> annotationList = getAnnotationsByStereotypeInternal(stereotype);
        for (String parentAnnotation : parents) {
            if (!annotationList.contains(parentAnnotation)) {
                annotationList.add(parentAnnotation);
            }
        }

        addRepeatableInternal(stereotype, annotationValue, allStereotypes, RetentionPolicy.RUNTIME);
    }

    /**
     * Adds a repeatable declared stereotype value. If a value already exists will be added
     *
     * @param parents         The parent annotations
     * @param stereotype      The annotation name
     * @param annotationValue The annotation value
     */
    protected void addDeclaredRepeatableStereotype(List<String> parents, String stereotype, io.micronaut.core.annotation.AnnotationValue annotationValue) {
        Map<String, Map<CharSequence, Object>> declaredStereotypes = getDeclaredStereotypesInternal();
        List<String> annotationList = getAnnotationsByStereotypeInternal(stereotype);
        for (String parentAnnotation : parents) {
            if (!annotationList.contains(parentAnnotation)) {
                annotationList.add(parentAnnotation);
            }
        }

        addRepeatableInternal(stereotype, annotationValue, declaredStereotypes, RetentionPolicy.RUNTIME);
        addRepeatableInternal(stereotype, annotationValue, getAllStereotypes(), RetentionPolicy.RUNTIME);
    }

    /**
     * Adds a repeatable annotation value. If a value already exists will be added
     *
     * @param annotationName  The annotation name
     * @param annotationValue The annotation value
     */
    protected void addDeclaredRepeatable(String annotationName, io.micronaut.core.annotation.AnnotationValue annotationValue) {
        addDeclaredRepeatable(annotationName, annotationValue, annotationValue.getRetentionPolicy());
    }

    /**
     * Adds a repeatable annotation value. If a value already exists will be added
     *
     * @param annotationName  The annotation name
     * @param annotationValue The annotation value
     * @param retentionPolicy The retention policy
     */
    protected void addDeclaredRepeatable(String annotationName, io.micronaut.core.annotation.AnnotationValue annotationValue, RetentionPolicy retentionPolicy) {
        if (StringUtils.isNotEmpty(annotationName) && annotationValue != null) {
            Map<String, Map<CharSequence, Object>> allAnnotations = getDeclaredAnnotationsInternal();

            addRepeatableInternal(annotationName, annotationValue, allAnnotations, retentionPolicy);

            addRepeatable(annotationName, annotationValue);
        }
    }

    /**
     * Adds a stereotype and its member values, if the annotation already exists the data will be merged with existing
     * values replaced.
     *
     * @param parentAnnotations The parent annotations
     * @param stereotype        The annotation
     * @param values            The values
     */
    @SuppressWarnings("WeakerAccess")
    protected final void addStereotype(List<String> parentAnnotations, String stereotype, Map<CharSequence, Object> values) {
        addStereotype(parentAnnotations, stereotype, values, RetentionPolicy.RUNTIME);
    }


    /**
     * Adds a stereotype and its member values, if the annotation already exists the data will be merged with existing
     * values replaced.
     *
     * @param parentAnnotations The parent annotations
     * @param stereotype        The annotation
     * @param values            The values
     * @param retentionPolicy   The retention policy
     */
    @SuppressWarnings("WeakerAccess")
    protected final void addStereotype(List<String> parentAnnotations, String stereotype, Map<CharSequence, Object> values, RetentionPolicy retentionPolicy) {
        if (stereotype != null) {
            String repeatedName = getRepeatedName(stereotype);
            if (repeatedName != null) {
                Object v = values.get(AnnotationMetadata.VALUE_MEMBER);
                if (v instanceof io.micronaut.core.annotation.AnnotationValue[]) {
                    io.micronaut.core.annotation.AnnotationValue[] avs = (io.micronaut.core.annotation.AnnotationValue[]) v;
                    for (io.micronaut.core.annotation.AnnotationValue av : avs) {
                        addRepeatableStereotype(parentAnnotations, stereotype, av);
                    }
                } else if (v instanceof Iterable) {
                    Iterable i = (Iterable) v;
                    for (Object o : i) {
                        if (o instanceof io.micronaut.core.annotation.AnnotationValue) {
                            addRepeatableStereotype(parentAnnotations, stereotype, (io.micronaut.core.annotation.AnnotationValue) o);
                        }
                    }
                }
            } else {
                Map<String, Map<CharSequence, Object>> allStereotypes = getAllStereotypes();
                List<String> annotationList = getAnnotationsByStereotypeInternal(stereotype);
                if (!parentAnnotations.isEmpty()) {
                    final String parentAnnotation = CollectionUtils.last(parentAnnotations);
                    if (!annotationList.contains(parentAnnotation)) {
                        annotationList.add(parentAnnotation);
                    }
                }

                // add to stereotypes
                addAnnotation(
                        stereotype,
                        values,
                        null,
                        allStereotypes,
                        false,
                        retentionPolicy
                );
            }
        }
    }

    /**
     * Adds a stereotype and its member values, if the annotation already exists the data will be merged with existing
     * values replaced.
     *
     * @param parentAnnotations The parent annotations
     * @param stereotype        The annotation
     * @param values            The values
     */
    @SuppressWarnings("WeakerAccess")
    protected void addDeclaredStereotype(List<String> parentAnnotations, String stereotype, Map<CharSequence, Object> values) {
        addDeclaredStereotype(parentAnnotations, stereotype, values, RetentionPolicy.RUNTIME);
    }

    /**
     * Adds a stereotype and its member values, if the annotation already exists the data will be merged with existing
     * values replaced.
     *
     * @param parentAnnotations The parent annotations
     * @param stereotype        The annotation
     * @param values            The values
     * @param retentionPolicy   The retention policy
     */
    @SuppressWarnings("WeakerAccess")
    protected void addDeclaredStereotype(List<String> parentAnnotations, String stereotype, Map<CharSequence, Object> values, RetentionPolicy retentionPolicy) {
        if (stereotype != null) {
            String repeatedName = getRepeatedName(stereotype);
            if (repeatedName != null) {
                Object v = values.get(AnnotationMetadata.VALUE_MEMBER);
                if (v instanceof io.micronaut.core.annotation.AnnotationValue[]) {
                    io.micronaut.core.annotation.AnnotationValue[] avs = (io.micronaut.core.annotation.AnnotationValue[]) v;
                    for (io.micronaut.core.annotation.AnnotationValue av : avs) {
                        addDeclaredRepeatableStereotype(parentAnnotations, stereotype, av);
                    }
                } else if (v instanceof Iterable) {
                    Iterable i = (Iterable) v;
                    for (Object o : i) {
                        if (o instanceof io.micronaut.core.annotation.AnnotationValue) {
                            addDeclaredRepeatableStereotype(parentAnnotations, stereotype, (io.micronaut.core.annotation.AnnotationValue) o);
                        }
                    }
                }
            } else {
                Map<String, Map<CharSequence, Object>> declaredStereotypes = getDeclaredStereotypesInternal();
                Map<String, Map<CharSequence, Object>> allStereotypes = getAllStereotypes();
                List<String> annotationList = getAnnotationsByStereotypeInternal(stereotype);
                if (!parentAnnotations.isEmpty()) {
                    final String parentAnnotation = CollectionUtils.last(parentAnnotations);
                    if (!annotationList.contains(parentAnnotation)) {
                        annotationList.add(parentAnnotation);
                    }
                }

                addAnnotation(
                        stereotype,
                        values,
                        declaredStereotypes,
                        allStereotypes,
                        true,
                        retentionPolicy
                );
            }

        }
    }

    /**
     * Adds an annotation directly declared on the element and its member values, if the annotation already exists the
     * data will be merged with existing values replaced.
     *
     * @param annotation The annotation
     * @param values     The values
     */
    protected void addDeclaredAnnotation(String annotation, Map<CharSequence, Object> values) {
        addDeclaredAnnotation(annotation, values, RetentionPolicy.RUNTIME);
    }

    /**
     * Adds an annotation directly declared on the element and its member values, if the annotation already exists the
     * data will be merged with existing values replaced.
     *
     * @param annotation      The annotation
     * @param values          The values
     * @param retentionPolicy The retention policy
     */
    protected void addDeclaredAnnotation(String annotation, Map<CharSequence, Object> values, RetentionPolicy retentionPolicy) {
        if (annotation != null) {
            boolean hasOtherMembers = false;
            String repeatedName = getRepeatedName(annotation);
            if (repeatedName != null) {
                for (Map.Entry<CharSequence, Object> entry: values.entrySet()) {
                    if (entry.getKey().equals(AnnotationMetadata.VALUE_MEMBER)) {
                        Object v = entry.getValue();
                        if (v instanceof io.micronaut.core.annotation.AnnotationValue[]) {
                            io.micronaut.core.annotation.AnnotationValue[] avs = (io.micronaut.core.annotation.AnnotationValue[]) v;
                            for (io.micronaut.core.annotation.AnnotationValue av : avs) {
                                addDeclaredRepeatable(annotation, av);
                            }
                        } else if (v instanceof Iterable) {
                            Iterable i = (Iterable) v;
                            for (Object o : i) {
                                if (o instanceof io.micronaut.core.annotation.AnnotationValue) {
                                    addDeclaredRepeatable(annotation, ((io.micronaut.core.annotation.AnnotationValue) o));
                                }
                            }
                        }
                    } else {
                        hasOtherMembers = true;
                    }
                }
            }

            if (repeatedName == null || hasOtherMembers) {
                Map<String, Map<CharSequence, Object>> declaredAnnotations = getDeclaredAnnotationsInternal();
                Map<String, Map<CharSequence, Object>> allAnnotations = getAllAnnotations();
                addAnnotation(annotation, values, declaredAnnotations, allAnnotations, true, retentionPolicy);
            }
        }
    }

    /**
     * Dump the values.
     */
    @SuppressWarnings("unused")
    @Internal
    void dump() {
        System.out.println("declaredAnnotations = " + declaredAnnotations);
        System.out.println("declaredStereotypes = " + declaredStereotypes);
        System.out.println("allAnnotations = " + allAnnotations);
        System.out.println("allStereotypes = " + allStereotypes);
        System.out.println("annotationsByStereotype = " + annotationsByStereotype);
    }

    private <T extends Annotation> List<io.micronaut.core.annotation.AnnotationValue<T>> resolveAnnotationValuesByType(Class<T> annotationType, Map<String, Map<CharSequence, Object>> sourceAnnotations, Map<String, Map<CharSequence, Object>> sourceStereotypes) {
        Repeatable repeatable = annotationType.getAnnotation(Repeatable.class);
        if (repeatable != null) {
            Class<? extends Annotation> repeatableType = repeatable.value();
            final String repeatableTypeName = repeatableType.getName();
            return resolveRepeatableAnnotations(repeatableTypeName,
                                                sourceStereotypes,
                                                sourceAnnotations
            );
        }
        return null;
    }

    @Nullable
    private <T extends Annotation> List<AnnotationValue<T>> resolveRepeatableAnnotations(String repeatableTypeName,
                                                                                         Map<String, Map<CharSequence, Object>> sourceStereotypes,
                                                                                         Map<String, Map<CharSequence, Object>> sourceAnnotations) {
        if (hasStereotype(repeatableTypeName)) {
            List<AnnotationValue<T>> results = new ArrayList<>();
            if (sourceAnnotations != null) {
                Map<CharSequence, Object> values = sourceAnnotations.get(repeatableTypeName);
                addAnnotationValuesFromData(results, values);
            }

            if (sourceStereotypes != null) {
                Map<CharSequence, Object> values = sourceStereotypes.get(repeatableTypeName);
                addAnnotationValuesFromData(results, values);
            }

            return results;
        }
        return null;
    }

    private void addAnnotation(String annotation,
                               Map<CharSequence, Object> values,
                               Map<String, Map<CharSequence, Object>> declaredAnnotations,
                               Map<String, Map<CharSequence, Object>> allAnnotations,
                               boolean isDeclared,
                               RetentionPolicy retentionPolicy) {
        if (isDeclared && declaredAnnotations != null) {
            putValues(annotation, values, declaredAnnotations);
        }
        putValues(annotation, values, allAnnotations);

        // Annotations with retention CLASS need not be retained at run time
        if (retentionPolicy == RetentionPolicy.SOURCE || retentionPolicy == RetentionPolicy.CLASS) {
            addSourceRetentionAnnotation(annotation);
        }
    }

    private void addSourceRetentionAnnotation(String annotation) {
        if (sourceRetentionAnnotations == null) {
            sourceRetentionAnnotations = new HashSet<>(5);
        }
        sourceRetentionAnnotations.add(annotation);
    }

    private void putValues(String annotation, Map<CharSequence, Object> values, Map<String, Map<CharSequence, Object>> currentAnnotationValues) {
        Map<CharSequence, Object> existing = currentAnnotationValues.get(annotation);
        boolean hasValues = CollectionUtils.isNotEmpty(values);
        if (existing != null && hasValues) {
            if (existing.isEmpty()) {
                existing = new LinkedHashMap<>();
                currentAnnotationValues.put(annotation, existing);
            }
            for (CharSequence key : values.keySet()) {
                if (!existing.containsKey(key)) {
                    existing.put(key, values.get(key));
                }
            }
        } else {
            if (!hasValues) {
                existing = existing == null ? new LinkedHashMap<>(3) : existing;
            } else {
                existing = new LinkedHashMap<>(values.size());
                existing.putAll(values);
            }
            currentAnnotationValues.put(annotation, existing);
        }
    }

    @SuppressWarnings("MagicNumber")
    private Map<String, Map<CharSequence, Object>> getAllStereotypes() {
        Map<String, Map<CharSequence, Object>> stereotypes = this.allStereotypes;
        if (stereotypes == null) {
            stereotypes = new HashMap<>(3);
            this.allStereotypes = stereotypes;
        }
        return stereotypes;
    }

    @SuppressWarnings("MagicNumber")
    private Map<String, Map<CharSequence, Object>> getDeclaredStereotypesInternal() {
        Map<String, Map<CharSequence, Object>> stereotypes = this.declaredStereotypes;
        if (stereotypes == null) {
            stereotypes = new HashMap<>(3);
            this.declaredStereotypes = stereotypes;
        }
        return stereotypes;
    }

    @SuppressWarnings("MagicNumber")
    private Map<String, Map<CharSequence, Object>> getAllAnnotations() {
        Map<String, Map<CharSequence, Object>> annotations = this.allAnnotations;
        if (annotations == null) {
            annotations = new HashMap<>(3);
            this.allAnnotations = annotations;
        }
        return annotations;
    }

    @SuppressWarnings("MagicNumber")
    private Map<String, Map<CharSequence, Object>> getDeclaredAnnotationsInternal() {
        Map<String, Map<CharSequence, Object>> annotations = this.declaredAnnotations;
        if (annotations == null) {
            annotations = new HashMap<>(3);
            this.declaredAnnotations = annotations;
        }
        return annotations;
    }

    private List<String> getAnnotationsByStereotypeInternal(String stereotype) {
        return getAnnotationsByStereotypeInternal().computeIfAbsent(stereotype, s -> new ArrayList<>());
    }

    private String getRepeatedName(String annotation) {
        if (repeated != null) {
            return repeated.get(annotation);
        }
        return null;
    }

    @SuppressWarnings("MagicNumber")
    private Map<String, List<String>> getAnnotationsByStereotypeInternal() {
        Map<String, List<String>> annotations = this.annotationsByStereotype;
        if (annotations == null) {
            annotations = new HashMap<>(3);
            this.annotationsByStereotype = annotations;
        }
        return annotations;
    }

    private @Nullable
    Object getRawSingleValue(@NonNull String annotation, @NonNull String member, @Nullable Function<Object, Object> valueMapper) {
        Object rawValue = getRawValue(annotation, member);
        if (rawValue != null) {
            if (rawValue.getClass().isArray()) {
                int len = Array.getLength(rawValue);
                if (len > 0) {
                    rawValue = Array.get(rawValue, 0);
                }
            } else if (rawValue instanceof Iterable) {
                Iterator i = ((Iterable) rawValue).iterator();
                if (i.hasNext()) {
                    rawValue = i.next();
                }
            }
        }
        if (valueMapper != null && rawValue instanceof CharSequence) {
            return valueMapper.apply(rawValue);
        } else {
            return rawValue;
        }
    }

    @Nullable
    private Object getRawValue(@NonNull String annotation, @NonNull String member) {
        Object rawValue = null;
        if (allAnnotations != null && StringUtils.isNotEmpty(annotation)) {
            Map<CharSequence, Object> values = allAnnotations.get(annotation);
            if (values != null) {
                rawValue = values.get(member);
            } else if (allStereotypes != null) {
                values = allStereotypes.get(annotation);
                if (values != null) {
                    rawValue = values.get(member);
                }
            }
        }
        return rawValue;
    }

    private void addRepeatableInternal(
            String annotationName,
            io.micronaut.core.annotation.AnnotationValue annotationValue,
            Map<String, Map<CharSequence, Object>> allAnnotations,
            RetentionPolicy retentionPolicy) {
        addRepeatableInternal(annotationName, AnnotationMetadata.VALUE_MEMBER, annotationValue, allAnnotations, retentionPolicy);
    }

    private void addRepeatableInternal(
            String annotationName,
            String member,
            io.micronaut.core.annotation.AnnotationValue annotationValue,
            Map<String, Map<CharSequence, Object>> allAnnotations,
            RetentionPolicy retentionPolicy) {
        if (repeated == null) {
            repeated = new HashMap<>(2);
        }

        repeated.put(annotationName, annotationValue.getAnnotationName());
        if (retentionPolicy == RetentionPolicy.SOURCE) {
            addSourceRetentionAnnotation(annotationName);
        }

        Map<CharSequence, Object> values = allAnnotations.computeIfAbsent(annotationName, s -> new HashMap<>());
        Object v = values.get(member);
        if (v != null) {
            if (v.getClass().isArray()) {
                Object[] array = (Object[]) v;
                Set newValues = new LinkedHashSet(array.length + 1);
                newValues.addAll(Arrays.asList(array));
                newValues.add(annotationValue);
                values.put(member, newValues);
            } else if (v instanceof Collection) {
                ((Collection) v).add(annotationValue);
            }
        } else {
            Set<Object> newValues = new LinkedHashSet<>(2);
            newValues.add(annotationValue);
            values.put(member, newValues);
        }
    }

    /**
     * <p>Sets a member of the given {@link AnnotationMetadata} return a new annotation metadata instance without
     * mutating the existing.</p>
     *
     * <p>WARNING: for internal use only be the framework</p>
     *
     * @param annotationMetadata The metadata
     * @param annotationName     The annotation name
     * @param member             The member
     * @param value              The value
     * @return The metadata
     */
    @Internal
    public static AnnotationMetadata mutateMember(
            AnnotationMetadata annotationMetadata,
            String annotationName,
            String member,
            Object value) {

        return mutateMember(annotationMetadata, annotationName, Collections.singletonMap(member, value));
    }

    /**
     * Include the annotation metadata from the other instance of {@link DefaultAnnotationMetadata}.
     * @param annotationMetadata The annotation metadata
     * @since 4.0.0
     */
    @Internal
    protected void addAnnotationMetadata(DefaultAnnotationMetadata annotationMetadata) {
        if (annotationMetadata.declaredAnnotations != null && !annotationMetadata.declaredAnnotations.isEmpty()) {
            if (declaredAnnotations == null) {
                declaredAnnotations = new LinkedHashMap<>();
            }
            for (Map.Entry<String, Map<CharSequence, Object>> entry : annotationMetadata.declaredAnnotations.entrySet()) {
                putValues(entry.getKey(), entry.getValue(), declaredAnnotations);
            }
        }
        if (annotationMetadata.declaredStereotypes != null && !annotationMetadata.declaredStereotypes.isEmpty()) {
            if (declaredStereotypes == null) {
                declaredStereotypes = new LinkedHashMap<>();
            }
            for (Map.Entry<String, Map<CharSequence, Object>> entry : annotationMetadata.declaredStereotypes.entrySet()) {
                putValues(entry.getKey(), entry.getValue(), declaredStereotypes);
            }
        }
        if (annotationMetadata.allStereotypes != null && !annotationMetadata.allStereotypes.isEmpty()) {
            if (allStereotypes == null) {
                allStereotypes = new LinkedHashMap<>();
            }
            for (Map.Entry<String, Map<CharSequence, Object>> entry : annotationMetadata.allStereotypes.entrySet()) {
                putValues(entry.getKey(), entry.getValue(), allStereotypes);
            }
        }
        if (annotationMetadata.allAnnotations != null && !annotationMetadata.allAnnotations.isEmpty()) {
            if (allAnnotations == null) {
                allAnnotations = new LinkedHashMap<>();
            }
            for (Map.Entry<String, Map<CharSequence, Object>> entry : annotationMetadata.allAnnotations.entrySet()) {
                putValues(entry.getKey(), entry.getValue(), allAnnotations);
            }
        }
        Map<String, List<String>> source = annotationMetadata.annotationsByStereotype;
        if (source != null && !source.isEmpty()) {
            if (annotationsByStereotype == null) {
                annotationsByStereotype = new LinkedHashMap<>();
            }
            for (Map.Entry<String, List<String>> entry : source.entrySet()) {
                String ann = entry.getKey();
                List<String> prevValues = annotationsByStereotype.get(ann);
                if (prevValues == null) {
                    annotationsByStereotype.put(ann, new ArrayList<>(entry.getValue()));
                } else {
                    Set<String> prevValuesSet = new LinkedHashSet<>(prevValues);
                    prevValuesSet.addAll(entry.getValue());
                    annotationsByStereotype.put(ann, new ArrayList<>(prevValuesSet));
                }
            }
        }
        if (annotationMetadata.repeated != null) {
            if (repeated == null) {
                repeated = new LinkedHashMap<>(annotationMetadata.repeated);
            } else {
                repeated.putAll(annotationMetadata.repeated);
            }
        }
        if (annotationMetadata.sourceRetentionAnnotations != null) {
            if (sourceRetentionAnnotations == null) {
                sourceRetentionAnnotations = new HashSet<>(annotationMetadata.sourceRetentionAnnotations);
            } else {
                sourceRetentionAnnotations.addAll(annotationMetadata.sourceRetentionAnnotations);
            }
        }
        if (annotationMetadata.annotationDefaultValues != null) {
            if (annotationDefaultValues == null) {
                annotationDefaultValues = new LinkedHashMap<>(annotationMetadata.annotationDefaultValues);
            } else {
                // No need to merge values
                annotationDefaultValues.putAll(annotationMetadata.annotationDefaultValues);
            }
        }
    }

    /**
     * Contributes defaults to the given target.
     *
     * <p>WARNING: for internal use only be the framework</p>
     *
     * @param target The target
     * @param source The source
     */
    @Internal
    public static void contributeDefaults(AnnotationMetadata target, AnnotationMetadata source) {
        source = source.getTargetAnnotationMetadata();
        if (source instanceof AnnotationMetadataHierarchy) {
            source = ((AnnotationMetadataHierarchy) source).merge();
        }
        if (target instanceof DefaultAnnotationMetadata && source instanceof DefaultAnnotationMetadata) {
            DefaultAnnotationMetadata damTarget = (DefaultAnnotationMetadata) target;
            DefaultAnnotationMetadata damSource = (DefaultAnnotationMetadata) source;
            final Map<String, Map<CharSequence, Object>> existingDefaults = damTarget.annotationDefaultValues;
            if (existingDefaults != null) {
                final Map<String, Map<CharSequence, Object>> additionalDefaults = damSource.annotationDefaultValues;
                if (additionalDefaults != null) {
                    existingDefaults.putAll(
                            additionalDefaults
                    );
                }
            } else {
                final Map<String, Map<CharSequence, Object>> additionalDefaults = damSource.annotationDefaultValues;
                if (additionalDefaults != null) {
                    additionalDefaults.forEach(damTarget::addDefaultAnnotationValues);
                }
            }
        }
        contributeRepeatable(target, source);
    }

    /**
     * Contributes repeatable annotation metadata to the given target.
     *
     * <p>WARNING: for internal use only be the framework</p>
     *
     * @param target The target
     * @param source The source
     */
    @Internal
    public static void contributeRepeatable(AnnotationMetadata target, AnnotationMetadata source) {
        source = source.getTargetAnnotationMetadata();
        if (source instanceof AnnotationMetadataHierarchy) {
            source = ((AnnotationMetadataHierarchy) source).merge();
        }
        if (target instanceof DefaultAnnotationMetadata && source instanceof DefaultAnnotationMetadata) {
            DefaultAnnotationMetadata damTarget = (DefaultAnnotationMetadata) target;
            DefaultAnnotationMetadata damSource = (DefaultAnnotationMetadata) source;
            if (damSource.repeated != null && !damSource.repeated.isEmpty()) {
                if (damTarget.repeated == null) {
                    damTarget.repeated = new HashMap<>(damSource.repeated);
                } else {
                    damTarget.repeated.putAll(damSource.repeated);
                }
            }
        }
    }

    /**
     * <p>Sets a member of the given {@link AnnotationMetadata} return a new annotation metadata instance without
     * mutating the existing.</p>
     *
     * <p>WARNING: for internal use only be the framework</p>
     *
     * @param annotationMetadata The metadata
     * @param annotationName     The annotation name
     * @param members            The key/value set of members and values
     * @return The metadata
     */
    @Internal
    public static AnnotationMetadata mutateMember(
            AnnotationMetadata annotationMetadata,
            String annotationName,
            Map<CharSequence, Object> members) {
        if (StringUtils.isEmpty(annotationName)) {
            throw new IllegalArgumentException("Argument [annotationName] cannot be blank");
        }
        if (!members.isEmpty()) {
            for (Map.Entry<CharSequence, Object> entry : members.entrySet()) {
                if (StringUtils.isEmpty(entry.getKey())) {
                    throw new IllegalArgumentException("Argument [members] cannot have a blank key");
                }
                if (entry.getValue() == null) {
                    throw new IllegalArgumentException("Argument [members] cannot have a null value. Key [" + entry.getKey() + "]");
                }
            }
        }
        if (!(annotationMetadata instanceof DefaultAnnotationMetadata)) {
            MutableAnnotationMetadata mutableAnnotationMetadata = new MutableAnnotationMetadata();
            mutableAnnotationMetadata.addDeclaredAnnotation(annotationName, members);
            return mutableAnnotationMetadata;
        } else {
            DefaultAnnotationMetadata defaultMetadata = (DefaultAnnotationMetadata) annotationMetadata;

            defaultMetadata = defaultMetadata.clone();

            defaultMetadata
                    .addDeclaredAnnotation(annotationName, members);

            return defaultMetadata;
        }
    }

    /**
     * Removes an annotation for the given predicate.
     * @param predicate The predicate
     * @param <A> The annotation
     */
    protected <A extends Annotation> void removeAnnotationIf(
            @NonNull Predicate<AnnotationValue<A>> predicate) {
        removeAnnotationsIf(predicate, this.declaredAnnotations);
        removeAnnotationsIf(predicate, this.allAnnotations);
    }

    private <A extends Annotation> void removeAnnotationsIf(@NonNull Predicate<AnnotationValue<A>> predicate, Map<String, Map<CharSequence, Object>> annotations) {
        if (annotations != null) {
            annotations.entrySet().removeIf(entry -> {
                final String annotationName = entry.getKey();
                if (predicate.test(new AnnotationValue<>(annotationName, entry.getValue()))) {
                    removeFromStereotypes(annotationName, annotations);
                    return true;
                }
                return false;
            });
        }
    }

    /**
     * Removes an annotation for the given annotation type.
     * @param annotationType The annotation type
     * @since 3.0.0
     */
    protected void removeAnnotation(String annotationType) {
        if (annotationType != null) {
            if (annotationDefaultValues != null) {
                this.annotationDefaultValues.remove(annotationType);
            }
            if (allAnnotations != null) {
                this.allAnnotations.remove(annotationType);
            }
            final Map<String, Map<CharSequence, Object>> declaredAnnotations = this.declaredAnnotations;
            if (declaredAnnotations != null) {
                declaredAnnotations.remove(annotationType);
                removeFromStereotypes(annotationType, declaredAnnotations);
            }
            if (this.repeated != null) {
                this.repeated.remove(annotationType);
            }
        }
    }

    /**
     * Removes a stereotype annotation for the given annotation type.
     * @param annotationType The annotation type
     * @since 3.0.0
     */
    protected void removeStereotype(String annotationType) {
        if (annotationType != null) {
            if (annotationsByStereotype != null && annotationsByStereotype.remove(annotationType) != null) {
                if (allStereotypes != null) {
                    this.allStereotypes.remove(annotationType);
                }
                if (declaredStereotypes != null) {
                    this.declaredStereotypes.remove(annotationType);
                }

                final Iterator<Map.Entry<String, List<String>>> i = annotationsByStereotype.entrySet().iterator();
                while (i.hasNext()) {
                    Map.Entry<String, List<String>> entry = i.next();
                    final List<String> value = entry.getValue();
                    if (value.remove(annotationType)) {
                        if (value.isEmpty()) {
                            i.remove();
                        }
                    }
                }
            }
        }
    }

    private void removeFromStereotypes(String annotationType, Map<String, Map<CharSequence, Object>> declaredAnnotations) {
        if (annotationsByStereotype != null) {
            final Iterator<Map.Entry<String, List<String>>> i = annotationsByStereotype.entrySet().iterator();
            Set<String> toBeRemoved = CollectionUtils.setOf(annotationType);
            while (i.hasNext()) {
                final Map.Entry<String, List<String>> entry = i.next();
                final String stereotypeName = entry.getKey();
                final List<String> value = entry.getValue();
                if (value.removeAll(toBeRemoved)) {
                    if (value.isEmpty()) {
                        toBeRemoved.add(stereotypeName);
                        i.remove();
                        if (allStereotypes != null) {
                            this.allStereotypes.remove(stereotypeName);
                        }
                        if (declaredStereotypes != null) {
                            this.declaredStereotypes.remove(stereotypeName);
                        }
                        if (annotationDefaultValues != null) {
                            annotationDefaultValues.remove(stereotypeName);
                        }
                    }

                    if (AnnotationUtil.ANN_AROUND.equals(stereotypeName) || AnnotationUtil.ANN_INTRODUCTION.equals(stereotypeName) || AnnotationUtil.ANN_AROUND_CONSTRUCT.equals(stereotypeName)) {
                        // purge from interceptor binding
                        purgeInterceptorBindings(declaredAnnotations, toBeRemoved);
                        purgeInterceptorBindings(this.allAnnotations, toBeRemoved);
                    }
                }
            }
        }
    }

    private void purgeInterceptorBindings(Map<String, Map<CharSequence, Object>> declaredAnnotations, Set<String> toBeRemoved) {
        if (declaredAnnotations != null) {
            final Map<CharSequence, Object> v = declaredAnnotations.get(AnnotationUtil.ANN_INTERCEPTOR_BINDINGS);
            if (v != null) {
                final Object o = v.get(AnnotationMetadata.VALUE_MEMBER);
                if (o instanceof Collection) {
                    Collection<AnnotationValue<?>> col = (Collection) o;
                    col.removeIf(av -> Arrays.stream(av.annotationClassValues(AnnotationMetadata.VALUE_MEMBER))
                            .anyMatch(acv -> toBeRemoved.contains(acv.getName())));

                    if (col.isEmpty()) {
                        declaredAnnotations.remove(AnnotationUtil.ANN_INTERCEPTOR_BINDINGS);
                    }
                }
            }
        }
    }
}
