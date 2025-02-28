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
package io.micronaut.inject.qualifiers;

import io.micronaut.context.Qualifier;
import io.micronaut.context.annotation.Any;
import io.micronaut.context.annotation.Primary;
import io.micronaut.context.annotation.Type;
import io.micronaut.core.annotation.AnnotationMetadata;
import io.micronaut.core.annotation.AnnotationUtil;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.Internal;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.annotation.UsedByGeneratedCode;
import io.micronaut.core.type.Argument;
import io.micronaut.core.util.CollectionUtils;
import jakarta.inject.Named;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Factory for {@link io.micronaut.context.annotation.Bean} qualifiers.
 *
 * @author Graeme Rocher
 * @since 1.0
 */
public class Qualifiers {

    /**
     * Allows looking up the first matching instance.
     *
     * <p>This qualifier results on {@link io.micronaut.context.exceptions.NonUniqueBeanException} never being thrown as
     * the first matching instance will be returned.</p>
     *
     * @param <T> The generic type
     * @return The any qualifier.
     * @see Any
     * @since 3.0.0
     */
    @SuppressWarnings("unchecked")
    public static <T> Qualifier<T> any() {
        return AnyQualifier.INSTANCE;
    }

    /**
     * Build a qualifier for the given argument.
     *
     * @param argument The argument
     * @param <T>      The type
     * @return The resolved qualifier
     */
    @SuppressWarnings("unchecked")
    public static @Nullable
    <T> Qualifier<T> forArgument(@NonNull Argument<?> argument) {
        AnnotationMetadata annotationMetadata = Objects.requireNonNull(argument, "Argument cannot be null").getAnnotationMetadata();
        boolean hasMetadata = annotationMetadata != AnnotationMetadata.EMPTY_METADATA;

        List<String> qualifierTypes = hasMetadata ? annotationMetadata.getAnnotationNamesByStereotype(AnnotationUtil.QUALIFIER) : null;
        if (CollectionUtils.isNotEmpty(qualifierTypes)) {
            if (qualifierTypes.size() == 1) {
                return Qualifiers.byAnnotation(
                        annotationMetadata,
                        qualifierTypes.iterator().next()
                );
            } else {
                Qualifier[] qualifiers = new Qualifier[qualifierTypes.size()];
                int i = 0;
                for (String type : qualifierTypes) {
                    qualifiers[i++] = Qualifiers.byAnnotation(annotationMetadata, type);
                }
                return Qualifiers.byQualifiers(qualifiers);
            }
        }

        return null;
    }

    /**
     * Build a qualifier from other qualifiers.
     *
     * @param qualifiers The qualifiers
     * @param <T>        The component type
     * @return The qualifier
     */
    public static <T> Qualifier<T> byQualifiers(Qualifier<T>... qualifiers) {
        return new CompositeQualifier<>(qualifiers);
    }

    /**
     * Build a qualifier for the given name.
     *
     * @param name The name
     * @param <T>  The component type
     * @return The qualifier
     */
    @UsedByGeneratedCode
    public static <T> Qualifier<T> byName(String name) {
        return new NameQualifier<>(null, name);
    }

    /**
     * Build a qualifier for the given annotation.
     *
     * @param annotation The annotation
     * @param <T>        The component type
     * @return The qualifier
     */
    public static <T> Qualifier<T> byAnnotation(Annotation annotation) {
        Qualifier<T> qualifier = findCustomByType(AnnotationMetadata.EMPTY_METADATA, annotation.annotationType());
        if (qualifier != null) {
            return qualifier;
        }
        return new AnnotationQualifier<>(annotation);
    }

    /**
     * Build a qualifier for the given annotation.
     *
     * @param metadata The metadata
     * @param type     The annotation type
     * @param <T>      The component type
     * @return The qualifier
     */
    public static <T> Qualifier<T> byAnnotation(AnnotationMetadata metadata, Class<? extends Annotation> type) {
        Qualifier<T> instance = findCustomByType(metadata, type);
        if (instance != null) {
            return instance;
        }
        return AnnotationMetadataQualifier.fromType(metadata, type);
    }

    /**
     * <p>Build a qualifier for the given annotation. This qualifier will match a candidate under the following
     * circumstances:</p>
     *
     * <ul>
     * <li>If the {@code type} parameter is {@link Named} then the value of the {@link Named} annotation within the metadata is used to match the candidate by name</li>
     * <li>If the {@code type} parameter is {@link Type} then the value of the {@link Type} annotation is used to match the candidate by type</li>
     * </ul>
     *
     * @param metadata The metadata
     * @param type     The annotation type
     * @param <T>      The component type
     * @return The qualifier
     */
    public static <T> Qualifier<T> byAnnotation(AnnotationMetadata metadata, String type) {
        Qualifier<T> qualifier = findCustomByName(metadata, type);
        if (qualifier != null) {
            return qualifier;
        }
        return AnnotationMetadataQualifier.fromTypeName(metadata, type);
    }

    /**
     * <p>Build a qualifier for the given annotation value.
     *
     * @param metadata        The metadata
     * @param annotationValue The annotation value
     * @param <T>             The component type
     * @return The qualifier
     */
    public static <T extends Annotation> Qualifier<T> byAnnotation(AnnotationMetadata metadata, AnnotationValue<T> annotationValue) {
        Qualifier<T> qualifier = findCustomByName(metadata, annotationValue.getAnnotationName());
        if (qualifier != null) {
            return qualifier;
        }
        return AnnotationMetadataQualifier.fromValue(metadata, annotationValue);
    }

    /**
     * <p>Builds a qualifier that uses the given repeatable annotation.</p>
     *
     * @param metadata The metadata
     * @param repeatableType  The annotation repeatable type. That is the annotation specified to {@link java.lang.annotation.Repeatable#value()}
     * @param <T>      The component type
     * @return The qualifier
     */
    @UsedByGeneratedCode
    public static <T> Qualifier<T> byRepeatableAnnotation(AnnotationMetadata metadata, String repeatableType) {
        return new RepeatableAnnotationQualifier<>(metadata, repeatableType);
    }

    /**
     * <p>Build a qualifier for the given annotation. </p>
     *
     * <p>Unlike {@link #byAnnotation(io.micronaut.core.annotation.AnnotationMetadata, String)} this method will not attempt to pick the qualifier strategy to use at runtime based on the passed annotation name.</p>
     *
     * @param metadata The metadata
     * @param type     The annotation type
     * @param <T>      The component type
     * @return The qualifier
     * @since 3.1.0
     */
    @UsedByGeneratedCode
    @Internal
    public static <T> Qualifier<T> byAnnotationSimple(AnnotationMetadata metadata, String type) {
        Qualifier<T> qualifier = findCustomByName(metadata, type);
        if (qualifier != null) {
            return qualifier;
        }
        return AnnotationMetadataQualifier.fromTypeName(metadata, type);
    }

    /**
     * Build a qualifier for the given annotation.
     *
     * @param stereotype The stereotype
     * @param <T>        The component type
     * @return The qualifier
     */
    public static <T> Qualifier<T> byStereotype(Class<? extends Annotation> stereotype) {
        Qualifier<T> instance = findCustomByType(AnnotationMetadata.EMPTY_METADATA, stereotype);
        if (instance != null) {
            return instance;
        }
        return new AnnotationStereotypeQualifier<>(stereotype);
    }

    /**
     * Build a qualifier for the given annotation.
     *
     * @param stereotype The stereotype
     * @param <T>        The component type
     * @return The qualifier
     * @since 3.0.0
     */
    public static <T> Qualifier<T> byStereotype(String stereotype) {
        Qualifier<T> qualifier = findCustomByName(AnnotationMetadata.EMPTY_METADATA, stereotype);
        if (qualifier != null) {
            return qualifier;
        }
        return new NamedAnnotationStereotypeQualifier<>(stereotype);
    }

    /**
     * Build a qualifier for the given generic type arguments.
     *
     * @param typeArguments The generic type arguments
     * @param <T>           The component type
     * @return The qualifier
     */
    public static <T> Qualifier<T> byTypeArguments(Class... typeArguments) {
        return new TypeArgumentQualifier<>(typeArguments);
    }

    /**
     * Build a qualifier for the given generic type argument name.
     *
     * @param typeName The name of the generic type argument
     * @param <T>      The component type
     * @return The qualifier
     * @since 3.0.0
     */
    public static @NonNull <T> Qualifier<T> byExactTypeArgumentName(@NonNull String typeName) {
        return new ExactTypeArgumentNameQualifier<>(typeName);
    }

    /**
     * Build a qualifier for the given generic type arguments. Only the closest
     * matches will be returned.
     *
     * @param typeArguments The generic type arguments
     * @param <T>           The component type
     * @return The qualifier
     */
    public static <T> Qualifier<T> byTypeArgumentsClosest(Class... typeArguments) {
        return new ClosestTypeArgumentQualifier<>(typeArguments);
    }

    /**
     * Build a qualifier for the given generic type arguments.
     *
     * @param typeArguments The generic type arguments
     * @param <T>           The component type
     * @return The qualifier
     */
    public static <T> Qualifier<T> byType(Class... typeArguments) {
        return new TypeAnnotationQualifier<>(typeArguments);
    }

    /**
     * Reduces bean definitions by the given interceptor binding.
     *
     * @param annotationMetadata The annotation metadata
     * @param <T>                The bean type
     * @return The qualifier
     */
    public static @NonNull
    <T> Qualifier<T> byInterceptorBinding(@NonNull AnnotationMetadata annotationMetadata) {
        return new InterceptorBindingQualifier<>(annotationMetadata);
    }

    /**
     * Reduces bean definitions by the given interceptor binding.
     *
     * @param binding The binding values to use
     * @param <T>     The bean type
     * @return The qualifier
     * @since 3.3.0
     */
    public static @NonNull
    <T> Qualifier<T> byInterceptorBindingValues(@NonNull Collection<AnnotationValue<?>> binding) {
        return new InterceptorBindingQualifier<>(binding);
    }

    @Nullable
    private static <T> Qualifier<T> findCustomByType(@NonNull AnnotationMetadata metadata, @NonNull Class<? extends Annotation> type) {
        if (Any.class == type) {
            //noinspection unchecked
            return AnyQualifier.INSTANCE;
        } else if (Primary.class == type) {
            //noinspection unchecked
            return PrimaryQualifier.INSTANCE;
        } else if (Type.class == type) {
            Optional<Class> aClass = metadata.classValue(type);
            if (aClass.isPresent()) {
                return byType(aClass.get());
            }
        } else if (Named.class == type || AnnotationUtil.NAMED.equals(type.getName())) {
            Optional<String> value = metadata.stringValue(type);
            if (value.isPresent()) {
                return byName(value.get());
            }
        }
        return null;
    }

    @Nullable
    private static <T> Qualifier<T> findCustomByName(@NonNull AnnotationMetadata metadata, @NonNull String type) {
        if (Type.NAME.equals(type)) {
            Optional<Class> aClass = metadata.classValue(type);
            if (aClass.isPresent()) {
                return byType(aClass.get());
            }
        } else if (Any.NAME.equals(type)) {
            //noinspection unchecked
            return AnyQualifier.INSTANCE;
        } else if (Qualifier.PRIMARY.equals(type)) {
            //noinspection unchecked
            return PrimaryQualifier.INSTANCE;
        } else if (Named.class.getName().equals(type) || AnnotationUtil.NAMED.equals(type)) {
            String n = metadata.stringValue(type).orElse(null);
            if (n != null) {
                return byName(n);
            }
        }
        return null;
    }

}
