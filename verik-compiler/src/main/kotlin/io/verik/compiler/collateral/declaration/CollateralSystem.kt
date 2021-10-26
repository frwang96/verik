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

package io.verik.compiler.collateral.declaration

import io.verik.compiler.collateral.common.CollateralFunctionDeclaration

object CollateralSystem {

    val F_display = CollateralFunctionDeclaration("\$display")
    val F_write = CollateralFunctionDeclaration("\$write")
    val F_sformatf = CollateralFunctionDeclaration("\$sformatf")
    val F_random = CollateralFunctionDeclaration("\$random")
    val F_urandom = CollateralFunctionDeclaration("\$urandom")
    val F_urandom_range = CollateralFunctionDeclaration("\$urandom_range")
    val F_unsigned = CollateralFunctionDeclaration("\$unsigned")
    val F_signed = CollateralFunctionDeclaration("\$signed")
    val F_time = CollateralFunctionDeclaration("\$time")
    val F_fatal = CollateralFunctionDeclaration("\$fatal")
    val F_finish = CollateralFunctionDeclaration("\$finish")
    val F_new = CollateralFunctionDeclaration("new")
    val F_wait = CollateralFunctionDeclaration("wait")
    val F_name = CollateralFunctionDeclaration("name")
    val F_rsort = CollateralFunctionDeclaration("rsort")
}
