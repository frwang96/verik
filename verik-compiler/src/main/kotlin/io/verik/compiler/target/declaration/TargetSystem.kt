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

package io.verik.compiler.target.declaration

import io.verik.compiler.target.common.PrimitiveTargetFunctionDeclaration
import io.verik.compiler.target.common.TargetPackage
import io.verik.compiler.target.common.TargetScope

object TargetSystem : TargetScope(TargetPackage) {

    val F_cast = PrimitiveTargetFunctionDeclaration(parent, "\$cast")
    val F_display = PrimitiveTargetFunctionDeclaration(parent, "\$display")
    val F_write = PrimitiveTargetFunctionDeclaration(parent, "\$write")
    val F_sformatf = PrimitiveTargetFunctionDeclaration(parent, "\$sformatf")
    val F_random = PrimitiveTargetFunctionDeclaration(parent, "\$random")
    val F_urandom = PrimitiveTargetFunctionDeclaration(parent, "\$urandom")
    val F_urandom_range = PrimitiveTargetFunctionDeclaration(parent, "\$urandom_range")
    val F_unsigned = PrimitiveTargetFunctionDeclaration(parent, "\$unsigned")
    val F_signed = PrimitiveTargetFunctionDeclaration(parent, "\$signed")
    val F_time = PrimitiveTargetFunctionDeclaration(parent, "\$time")
    val F_fatal = PrimitiveTargetFunctionDeclaration(parent, "\$fatal")
    val F_error = PrimitiveTargetFunctionDeclaration(parent, "\$error")
    val F_finish = PrimitiveTargetFunctionDeclaration(parent, "\$finish")
    val F_new = PrimitiveTargetFunctionDeclaration(parent, "new")
    val F_wait = PrimitiveTargetFunctionDeclaration(parent, "wait")
    val F_name = PrimitiveTargetFunctionDeclaration(parent, "name")
}
