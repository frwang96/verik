/*
 * Copyright 2020 Francis Wang
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

package io.verik.core.vk

import io.verik.core.kt.KtFile
import io.verik.core.main.LineException
import io.verik.core.sv.SvxFile
import io.verik.core.symbol.Symbol

data class VkxFile(
        val file: Symbol,
        val declarations: List<VkxDeclaration>
) {

    fun extract(): SvxFile {
        val modules = declarations.map {
            if (it is VkxComponent) it.extractModule()
            else throw LineException("declaration extraction not supported", it)
        }
        return SvxFile(modules)
    }

    companion object {

        operator fun invoke(file: KtFile): VkxFile {
            val declarations = ArrayList<VkxDeclaration>()

            for (declaration in file.declarations) {
                when {
                    VkxComponent.isComponent(declaration) -> declarations.add(VkxComponent(declaration))
                    else -> throw LineException("top level declaration not supported", declaration)
                }
            }

            return VkxFile(
                    file.file,
                    declarations
            )
        }
    }
}