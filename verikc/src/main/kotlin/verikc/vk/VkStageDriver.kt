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

package verikc.vk

import verikc.rs.ast.RsCompilationUnit
import verikc.vk.ast.VkCompilationUnit
import verikc.vk.check.*

object VkStageDriver {

    fun build(compilationUnit: RsCompilationUnit): VkCompilationUnit {
        return VkCompilationUnit(compilationUnit).also { check(it) }
    }

    private fun check(compilationUnit: VkCompilationUnit) {
        val componentTable = VkComponentTable()
        VkComponentTableBuilder.build(compilationUnit, componentTable)

        VkCheckerComponent.check(compilationUnit, componentTable)
        VkCheckerComponentInstance.check(compilationUnit, componentTable)
        VkCheckerCls.check(compilationUnit)
    }
}
