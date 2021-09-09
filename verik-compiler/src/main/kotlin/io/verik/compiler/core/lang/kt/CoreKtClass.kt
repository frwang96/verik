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

package io.verik.compiler.core.lang.kt

import io.verik.compiler.core.common.CoreClassDeclaration
import io.verik.compiler.core.common.CorePackage
import io.verik.compiler.core.common.CoreScope

object CoreKtClass : CoreScope(CorePackage.KT) {

    val C_ANY = CoreClassDeclaration(parent, "Any", null)
    val C_NOTHING = CoreClassDeclaration(parent, "Nothing", null)
    val C_FUNCTION = CoreClassDeclaration(parent, "Function", null)
    val C_UNIT = CoreClassDeclaration(parent, "Unit", C_ANY)
    val C_INT = CoreClassDeclaration(parent, "Int", C_ANY)
    val C_BOOLEAN = CoreClassDeclaration(parent, "Boolean", C_ANY)
    val C_STRING = CoreClassDeclaration(parent, "String", C_ANY)
    val C_ENUM = CoreClassDeclaration(parent, "Enum", C_ANY)
}
