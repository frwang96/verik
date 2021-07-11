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

package io.verik.compiler.core.lang.sv

import io.verik.compiler.core.CorePackage
import io.verik.compiler.core.CoreScope
import io.verik.compiler.core.CoreSvFunctionDeclaration

object CoreSv : CoreScope(CorePackage.SV) {

    val DISPLAY = CoreSvFunctionDeclaration(parent, "\$display")
    val WRITE = CoreSvFunctionDeclaration(parent, "\$write")
    val SFORMATF = CoreSvFunctionDeclaration(parent, "\$sformatf")
    val RANDOM = CoreSvFunctionDeclaration(parent, "\$random")
}