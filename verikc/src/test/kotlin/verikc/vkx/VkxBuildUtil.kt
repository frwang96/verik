/*
 * Copyright (c) 2020 Francis Wang
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

package verikc.vkx

import verikc.FILE_SYMBOL
import verikc.PKG_SYMBOL
import verikc.gex.GexGenerifyUtil
import verikc.vkx.ast.VkxCompilationUnit
import verikc.vkx.ast.VkxFile

object VkxBuildUtil {

    fun buildFile(string: String): VkxFile {
        val compilationUnit = buildCompilationUnit(string)
        return compilationUnit.pkg(PKG_SYMBOL).file(FILE_SYMBOL)
    }

    private fun buildCompilationUnit(string: String): VkxCompilationUnit {
        return VkxStageDriver.build(GexGenerifyUtil.generifyCompilationUnit(string))
    }
}