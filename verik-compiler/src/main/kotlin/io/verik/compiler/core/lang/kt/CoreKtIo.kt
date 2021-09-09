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

import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreKtFunctionDeclaration
import io.verik.compiler.core.common.CorePackage
import io.verik.compiler.core.common.CoreScope

object CoreKtIo : CoreScope(CorePackage.KT_IO) {

    val F_PRINT = CoreKtFunctionDeclaration(parent, "print")
    val F_PRINT_ANY = CoreKtFunctionDeclaration(parent, "print", Core.Kt.C_ANY)
    val F_PRINT_INT = CoreKtFunctionDeclaration(parent, "print", Core.Kt.C_INT)

    val F_PRINTLN = CoreKtFunctionDeclaration(parent, "println")
    val F_PRINTLN_ANY = CoreKtFunctionDeclaration(parent, "println", Core.Kt.C_ANY)
    val F_PRINTLN_INT = CoreKtFunctionDeclaration(parent, "println", Core.Kt.C_INT)
}
