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

import io.verik.compiler.core.Core
import io.verik.compiler.core.CoreKtFunctionDeclaration
import io.verik.compiler.core.CorePackage
import io.verik.compiler.core.CoreScope

object CoreKtIo : CoreScope(CorePackage.KT_IO) {

    val PRINT = CoreKtFunctionDeclaration(parent, "print")
    val PRINT_ANY = CoreKtFunctionDeclaration(parent, "print", Core.Kt.ANY)
    val PRINT_INT = CoreKtFunctionDeclaration(parent, "print", Core.Kt.INT)

    val PRINTLN = CoreKtFunctionDeclaration(parent, "println")
    val PRINTLN_ANY = CoreKtFunctionDeclaration(parent, "println", Core.Kt.ANY)
    val PRINTLN_INT = CoreKtFunctionDeclaration(parent, "println", Core.Kt.INT)
}