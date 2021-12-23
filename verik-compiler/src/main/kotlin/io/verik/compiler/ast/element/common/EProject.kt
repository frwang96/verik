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
import io.verik.compiler.core.common.Core
import io.verik.compiler.message.SourceLocation

class EProject(
    override val location: SourceLocation,
    val nativeRegularPackages: ArrayList<EPackage>,
    val nativeRootPackage: EPackage,
    val importedRegularPackages: ArrayList<EPackage>,
    val importedRootPackage: EPackage
) : EDeclaration() {

    override var name = "<project>"

    override var type = Core.Kt.C_Unit.toType()

    init {
        nativeRegularPackages.forEach { it.parent = this }
        nativeRootPackage.parent = this
        importedRegularPackages.forEach { it.parent = this }
        importedRootPackage.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitProject(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        nativeRegularPackages.forEach { it.accept(visitor) }
        nativeRootPackage.accept(visitor)
        importedRegularPackages.forEach { it.accept(visitor) }
        importedRootPackage.accept(visitor)
    }

    fun packages(): List<EPackage> {
        return nativeRegularPackages + nativeRootPackage + importedRegularPackages + importedRootPackage
    }

    fun files(): List<EFile> {
        return nativeRegularPackages.flatMap { it.files } +
            nativeRootPackage.files +
            importedRegularPackages.flatMap { it.files } +
            importedRootPackage.files
    }

    fun nonImportedFiles(): List<EFile> {
        return nativeRegularPackages.flatMap { it.files } + nativeRootPackage.files
    }
}
