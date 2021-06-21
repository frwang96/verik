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

package io.verik.compiler.interpret

import io.verik.compiler.ast.element.VkBaseClass
import io.verik.compiler.ast.element.VkKtClass
import io.verik.compiler.ast.element.VkModule
import io.verik.compiler.ast.element.VkSvClass
import io.verik.compiler.core.CoreClass

object ClassInterpreter {

    fun interpret(ktClass: VkKtClass): VkBaseClass {
        return if (ktClass.type.isType(CoreClass.Core.MODULE.toNoArgumentsType())) {
            VkModule(
                ktClass.location,
                ktClass.name,
                ktClass.type,
                ktClass.supertype,
                ktClass.typeParameters,
                ktClass.declarations
            )
        } else {
            VkSvClass(
                ktClass.location,
                ktClass.name,
                ktClass.type,
                ktClass.supertype,
                ktClass.typeParameters,
                ktClass.declarations
            )
        }
    }
}