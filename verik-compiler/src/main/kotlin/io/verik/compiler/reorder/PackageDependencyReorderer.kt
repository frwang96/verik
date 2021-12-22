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

package io.verik.compiler.reorder

import io.verik.compiler.ast.element.common.EBasicPackage
import io.verik.compiler.ast.element.common.ERootPackage
import io.verik.compiler.message.Messages

object PackageDependencyReorderer {

    fun reorder(dependencies: List<Dependency>) {
        dependencies.forEach {
            if (it.fromDeclaration is EBasicPackage && it.toDeclaration is ERootPackage) {
                Messages.PACKAGE_DEPENDENCY_ILLEGAL.on(it.element, it)
            }
        }
    }
}
