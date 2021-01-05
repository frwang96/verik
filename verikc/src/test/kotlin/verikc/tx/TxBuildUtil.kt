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

package verikc.tx

import verikc.sv.SvExtractUtil
import verikc.tx.build.*

object TxBuildUtil {

    fun buildModule(fileContext: String, string: String): String {
        val builder = TxSourceBuilder()
        val module = SvExtractUtil.extractModule(fileContext, string)
        TxBuilderModule.build(module, builder)
        return builder.toString()
    }

    fun buildComponentInstance(fileContext: String, moduleContext: String, string: String): String {
        val builder = TxSourceBuilder()
        val componentInstance = SvExtractUtil.extractComponentInstance(fileContext, moduleContext, string)
        TxBuilderComponentInstance.build(componentInstance, builder)
        return builder.toString()
    }

    fun buildActionBlock(fileContext: String, moduleContext: String, string: String): String {
        val builder = TxSourceBuilder()
        val actionBlock = SvExtractUtil.extractActionBlock(fileContext, moduleContext, string)
        TxBuilderActionBlock.build(actionBlock, builder)
        return builder.toString()
    }

    fun buildMethodBlock(fileContext: String, moduleContext: String, string: String): String {
        val builder = TxSourceBuilder()
        val methodBlock = SvExtractUtil.extractMethodBlock(fileContext, moduleContext, string)
        TxBuilderMethodBlock.build(methodBlock, builder)
        return builder.toString()
    }

    fun buildBlock(fileContext: String, moduleContext: String, string: String): String {
        val builder = TxSourceBuilder()
        val block = SvExtractUtil.extractBlock(fileContext, moduleContext, string)
        TxBuilderBlock.buildBlock(block, null, builder)
        return builder.toString()
    }

    fun buildExpression(fileContext: String, moduleContext: String, string: String): String {
        val builder = TxSourceBuilder()
        val expression = SvExtractUtil.extractExpression(fileContext, moduleContext, string)
        TxBuilderExpressionBase.build(expression, builder)
        return builder.toString()
    }

    fun buildEnum(fileContext: String, string: String): String {
        val builder = TxSourceBuilder()
        val enum = SvExtractUtil.extractEnum(fileContext, string)
        TxBuilderEnum.build(enum, builder)
        return builder.toString()
    }
}