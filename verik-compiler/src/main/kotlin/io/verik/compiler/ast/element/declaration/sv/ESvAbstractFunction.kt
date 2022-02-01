/*
 * Copyright (c) 2022 Francis Wang
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

package io.verik.compiler.ast.element.declaration.sv

import io.verik.compiler.ast.element.declaration.common.EAbstractFunction
import io.verik.compiler.common.TreeVisitor

abstract class ESvAbstractFunction : EAbstractFunction() {

    abstract var valueParameters: ArrayList<ESvValueParameter>

    override fun acceptChildren(visitor: TreeVisitor) {
        super.acceptChildren(visitor)
        valueParameters.forEach { it.accept(visitor) }
    }
}