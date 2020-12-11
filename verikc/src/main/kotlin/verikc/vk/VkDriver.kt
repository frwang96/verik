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

package verikc.vk

import verikc.kt.ast.KtCompilationUnit
import verikc.vk.ast.VkCompilationUnit
import verikc.vk.ast.VkFile
import verikc.vk.ast.VkPkg

object VkDriver {

    fun drive(compilationUnit: KtCompilationUnit): VkCompilationUnit {
        val pkgs = ArrayList<VkPkg>()
        for (pkg in compilationUnit.pkgs) {
            val files = ArrayList<VkFile>()
            for (file in pkg.files) {
                files.add(VkFile(file))
            }
            pkgs.add(VkPkg(pkg.pkgSymbol, files))
        }
        return VkCompilationUnit(pkgs)
    }
}
