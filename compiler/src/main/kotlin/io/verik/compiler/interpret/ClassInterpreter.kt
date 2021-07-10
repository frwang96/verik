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

import io.verik.compiler.ast.element.common.CAbstractClass
import io.verik.compiler.ast.element.kt.KBasicClass
import io.verik.compiler.ast.element.sv.SBasicClass
import io.verik.compiler.ast.element.sv.SModule
import io.verik.compiler.core.CoreClass

object ClassInterpreter {

    fun interpret(basicClass: KBasicClass): CAbstractClass {
        return if (basicClass.type.isType(CoreClass.Core.MODULE.toNoArgumentsType())) {
            SModule(
                basicClass.location,
                basicClass.name,
                basicClass.type,
                basicClass.supertype,
                basicClass.typeParameters,
                basicClass.declarations
            )
        } else {
            SBasicClass(
                basicClass.location,
                basicClass.name,
                basicClass.type,
                basicClass.supertype,
                basicClass.typeParameters,
                basicClass.declarations
            )
        }
    }
}