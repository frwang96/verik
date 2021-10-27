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

import io.verik.compiler.target.common.TargetFunctionDeclaration

object TargetSystem {

    val F_display = TargetFunctionDeclaration("\$display")
    val F_write = TargetFunctionDeclaration("\$write")
    val F_sformatf = TargetFunctionDeclaration("\$sformatf")
    val F_random = TargetFunctionDeclaration("\$random")
    val F_urandom = TargetFunctionDeclaration("\$urandom")
    val F_urandom_range = TargetFunctionDeclaration("\$urandom_range")
    val F_unsigned = TargetFunctionDeclaration("\$unsigned")
    val F_signed = TargetFunctionDeclaration("\$signed")
    val F_time = TargetFunctionDeclaration("\$time")
    val F_fatal = TargetFunctionDeclaration("\$fatal")
    val F_finish = TargetFunctionDeclaration("\$finish")
    val F_new = TargetFunctionDeclaration("new")
    val F_wait = TargetFunctionDeclaration("wait")
    val F_name = TargetFunctionDeclaration("name")
    val F_rsort = TargetFunctionDeclaration("rsort")
}
