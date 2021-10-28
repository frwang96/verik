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

package io.verik.compiler.core.declaration.kt

import io.verik.compiler.core.common.BasicCoreFunctionDeclaration
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CorePackage
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.target.common.Target

object CoreKtIo : CoreScope(CorePackage.KT_IO) {

    val F_print_Any = BasicCoreFunctionDeclaration(parent, "print", Target.F_write, Core.Kt.C_Any)

    val F_print_Boolean = BasicCoreFunctionDeclaration(parent, "print", Target.F_write, Core.Kt.C_Boolean)

    val F_print_Int = BasicCoreFunctionDeclaration(parent, "print", Target.F_write, Core.Kt.C_Int)

    val F_println = BasicCoreFunctionDeclaration(parent, "println", Target.F_display)

    val F_println_Any = BasicCoreFunctionDeclaration(parent, "println", Target.F_display, Core.Kt.C_Any)

    val F_println_Boolean = BasicCoreFunctionDeclaration(parent, "println", Target.F_display, Core.Kt.C_Boolean)

    val F_println_Int = BasicCoreFunctionDeclaration(parent, "println", Target.F_display, Core.Kt.C_Int)
}
