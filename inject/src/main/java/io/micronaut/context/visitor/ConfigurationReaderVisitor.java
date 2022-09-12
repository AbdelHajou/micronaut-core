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
package io.micronaut.context.visitor;

import io.micronaut.context.annotation.ConfigurationReader;
import io.micronaut.core.annotation.Internal;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.inject.ast.ClassElement;
import io.micronaut.inject.visitor.TypeElementVisitor;
import io.micronaut.inject.visitor.VisitorContext;

/**
 * The visitor adds Validated annotation if one of the parameters is a constraint or @Valid.
 *
 * @author Denis Stepanov
 * @since 3.7.0
 */
@Internal
public class ConfigurationReaderVisitor implements TypeElementVisitor<ConfigurationReader, Object> {

    @NonNull
    @Override
    public VisitorKind getVisitorKind() {
        return VisitorKind.ISOLATING;
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void visitClass(ClassElement element, VisitorContext context) {
        System.out.println("LLLL" + element);
        if (!element.hasStereotype(ConfigurationReader.class)) {
            return;
        }
        if (element.hasStereotype(Introspected.class)) {
            return;
        }
        if (element.hasStereotype(ValidationVisitor.ANN_REQUIRES_VALIDATION)) {
            element.annotate(Introspected.class);
            System.out.println("BBB" + element);
        }
    }

}
