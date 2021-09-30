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

package io.verik.compiler.core.sv

import io.verik.compiler.core.common.CorePackage
import io.verik.compiler.core.common.CoreScope
import io.verik.compiler.core.common.CoreSvFunctionDeclaration

object CoreSv : CoreScope(CorePackage.SV) {

    val F_display = CoreSvFunctionDeclaration(parent, "\$display")
    val F_write = CoreSvFunctionDeclaration(parent, "\$write")
    val F_sformatf = CoreSvFunctionDeclaration(parent, "\$sformatf")
    val F_random = CoreSvFunctionDeclaration(parent, "\$random")
    val F_urandom = CoreSvFunctionDeclaration(parent, "\$urandom")
    val F_urandom_range = CoreSvFunctionDeclaration(parent, "\$urandom_range")
    val F_time = CoreSvFunctionDeclaration(parent, "\$time")
    val F_fatal = CoreSvFunctionDeclaration(parent, "\$fatal")
    val F_finish = CoreSvFunctionDeclaration(parent, "\$finish")
    val F_new = CoreSvFunctionDeclaration(parent, "new")
    val F_name = CoreSvFunctionDeclaration(parent, "name")
}
