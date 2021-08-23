/*
 * Copyright (c) 2021 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.verik.compiler.ast.element.kt

import io.verik.compiler.ast.element.common.EAbstractProperty
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.property.SvSerializationType
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

class EKtProperty(
    override val location: SourceLocation,
    override var name: String,
    override var type: Type,
    override var initializer: EExpression?
) : EAbstractProperty() {

    override val serializationType = SvSerializationType.OTHER

    init {
        initializer?.parent = this
    }

    override fun accept(visitor: Visitor) {
        return visitor.visitKtProperty(this)
    }
}
