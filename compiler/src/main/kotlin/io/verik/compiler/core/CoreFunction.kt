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

package io.verik.compiler.core

import io.verik.compiler.ast.common.PackageName

object CoreFunction {

    object CORE {

        object NULL: CoreFunctionScope(PackageName.CORE) {

            val U_INT = CoreFunctionDeclaration(parent, "u", CoreClass.INT)
            val RANDOM = CoreFunctionDeclaration(parent, "random")
            val RANDOM_INT = CoreFunctionDeclaration(parent, "random", CoreClass.INT)
        }
    }

    object SV {

        object NULL: CoreFunctionScope(PackageName.SV) {

            val DISPLAY = CoreFunctionDeclaration(parent, "\$display")
            val RANDOM = CoreFunctionDeclaration(parent, "\$random")
        }
    }
}