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

package verik.core.vk

import verik.core.kt.KtUtil
import verik.core.vk.ast.*

object VkUtil {

    val EXPRESSION_NULL = VkExpression(KtUtil.EXPRESSION_NULL)

    fun parseFile(string: String): VkFile {
        return VkFile(KtUtil.resolveFile(string))
    }

    fun parseModule(string: String): VkModule {
        return VkModule(KtUtil.resolveDeclaration(string))
    }

    fun parseEnum(string: String): VkEnum {
        return VkEnum(KtUtil.resolveDeclaration(string))
    }

    fun parsePort(string: String): VkPort {
        return VkPort(KtUtil.resolveDeclaration(string))
    }

    fun parseActionBlock(string: String): VkActionBlock {
        return VkActionBlock(KtUtil.resolveDeclaration(string))
    }

    fun parsePrimaryProperty(string: String): VkPrimaryProperty {
        return VkPrimaryProperty(KtUtil.resolveDeclaration(string))
    }

    fun parseExpression(string: String): VkExpression {
        return VkExpression(KtUtil.resolveExpression(string))
    }
}
