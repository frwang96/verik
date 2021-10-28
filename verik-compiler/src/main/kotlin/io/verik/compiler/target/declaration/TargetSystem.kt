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

object TargetSystem {

    val F_display = PrimitiveTargetFunctionDeclaration("\$display")
    val F_write = PrimitiveTargetFunctionDeclaration("\$write")
    val F_sformatf = PrimitiveTargetFunctionDeclaration("\$sformatf")
    val F_random = PrimitiveTargetFunctionDeclaration("\$random")
    val F_urandom = PrimitiveTargetFunctionDeclaration("\$urandom")
    val F_urandom_range = PrimitiveTargetFunctionDeclaration("\$urandom_range")
    val F_unsigned = PrimitiveTargetFunctionDeclaration("\$unsigned")
    val F_signed = PrimitiveTargetFunctionDeclaration("\$signed")
    val F_time = PrimitiveTargetFunctionDeclaration("\$time")
    val F_fatal = PrimitiveTargetFunctionDeclaration("\$fatal")
    val F_finish = PrimitiveTargetFunctionDeclaration("\$finish")
    val F_new = PrimitiveTargetFunctionDeclaration("new")
    val F_wait = PrimitiveTargetFunctionDeclaration("wait")
    val F_name = PrimitiveTargetFunctionDeclaration("name")
}
