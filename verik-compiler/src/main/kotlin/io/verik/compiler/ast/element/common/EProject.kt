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

package io.verik.compiler.ast.element.common

import io.verik.compiler.ast.element.declaration.common.EFile
import io.verik.compiler.ast.element.declaration.common.EPackage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

class EProject(
    override val location: SourceLocation,
    var regularNonRootPackages: ArrayList<EPackage>,
    val regularRootPackage: EPackage,
    val importedNonRootPackages: ArrayList<EPackage>,
    val importedRootPackage: EPackage
) : EElement() {

    init {
        regularNonRootPackages.forEach { it.parent = this }
        regularRootPackage.parent = this
        importedNonRootPackages.forEach { it.parent = this }
        importedRootPackage.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitProject(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        regularNonRootPackages.forEach { it.accept(visitor) }
        regularRootPackage.accept(visitor)
        importedNonRootPackages.forEach { it.accept(visitor) }
        importedRootPackage.accept(visitor)
    }

    fun packages(): List<EPackage> {
        return regularNonRootPackages + regularRootPackage + importedNonRootPackages + importedRootPackage
    }

    fun regularPackages(): List<EPackage> {
        return regularNonRootPackages + regularRootPackage
    }

    fun files(): List<EFile> {
        return regularNonRootPackages.flatMap { it.files } +
            regularRootPackage.files +
            importedNonRootPackages.flatMap { it.files } +
            importedRootPackage.files
    }

    fun regularFiles(): List<EFile> {
        return regularNonRootPackages.flatMap { it.files } + regularRootPackage.files
    }
}
