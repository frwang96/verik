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

package io.verik.compiler.cast

import io.verik.compiler.ast.*
import io.verik.compiler.util.ElementUtil
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtVisitor
import java.nio.file.Paths

object CasterVisitor: KtVisitor<VkElement, Unit>() {

    override fun visitKtFile(file: KtFile, data: Unit): VkElement {
        val location = CasterUtil.getMessageLocation(file)
        val path = Paths.get(file.virtualFilePath)
        val packageName = Name(file.packageFqName.toString())
        val declarations = file.declarations.mapNotNull { ElementUtil.cast<VkDeclaration>(it.accept(this, Unit)) }
        return VkFile(location, path, packageName).also { it.declarations.addAll(declarations) }
    }

    override fun visitClassOrObject(classOrObject: KtClassOrObject, data: Unit?): VkElement {
        val name = Name(classOrObject.nameAsSafeName.identifier)
        val location = CasterUtil.getMessageLocation(classOrObject)
        return VkClass(name, location)
    }
}