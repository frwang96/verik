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

package io.verik.compiler.ast.element.sv

import io.verik.compiler.ast.element.common.EAbstractInitializedProperty
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.Visitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.message.SourceLocation

class ESvProperty(
    override val location: SourceLocation,
    override var name: String,
    override var type: Type,
    override var initializer: EExpression?,
    val isComAssignment: Boolean,
    val isMutable: Boolean,
    val isStatic: Boolean?
) : EAbstractInitializedProperty() {

    init {
        initializer?.parent = this
    }

    fun isVirtual(): Boolean {
        return type.isSubtype(Core.Vk.C_ModuleInterface)
    }

    override fun accept(visitor: Visitor) {
        return visitor.visitSvProperty(this)
    }

    companion object {

        fun getTemporary(
            location: SourceLocation,
            type: Type,
            initializer: EExpression?,
            isMutable: Boolean,
            isStatic: Boolean?
        ): ESvProperty {
            return ESvProperty(
                location = location,
                name = "<tmp>",
                type = type,
                initializer = initializer,
                isComAssignment = false,
                isMutable = isMutable,
                isStatic = isStatic
            )
        }
    }
}
