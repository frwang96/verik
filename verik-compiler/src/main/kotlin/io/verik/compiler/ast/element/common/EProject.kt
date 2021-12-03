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

package io.verik.compiler.ast.element.common

import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

class EProject(
    override val location: SourceLocation,
    val basicPackages: ArrayList<EBasicPackage>,
    val importedBasicPackages: ArrayList<EBasicPackage>,
    val rootPackage: ERootPackage,
    val importedRootPackage: ERootPackage
) : EElement() {

    init {
        basicPackages.forEach { it.parent = this }
        importedBasicPackages.forEach { it.parent = this }
        rootPackage.parent = this
        importedRootPackage.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitProject(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        basicPackages.forEach { it.accept(visitor) }
        importedBasicPackages.forEach { it.accept(visitor) }
        rootPackage.accept(visitor)
        importedRootPackage.accept(visitor)
    }

    fun files(): List<EFile> {
        return basicPackages.flatMap { it.files } +
            importedBasicPackages.flatMap { it.files } +
            rootPackage.files +
            importedRootPackage.files
    }

    fun nonImportedFiles(): List<EFile> {
        return basicPackages.flatMap { it.files } + rootPackage.files
    }
}
