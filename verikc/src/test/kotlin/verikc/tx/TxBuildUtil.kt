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

    fun buildComponent(fileContext: String, string: String): String {
        val builder = TxSourceBuilder()
        val component = SvExtractUtil.extractComponent(fileContext, string)
        TxBuilderComponent.build(component, builder)
        return builder.toString()
    }

    fun buildModulePort(fileContext: String, string: String): String {
        val builder = TxSourceBuilder()
        val port = SvExtractUtil.extractModulePort(fileContext, string)
        TxBuilderPort.build(port).build(builder)
        builder.appendln()
        return builder.toString()
    }

    fun buildModuleComponentInstance(fileContext: String, moduleContext: String, string: String): String {
        val builder = TxSourceBuilder()
        val componentInstance = SvExtractUtil.extractModuleComponentInstance(fileContext, moduleContext, string)
        TxBuilderComponentInstance.build(componentInstance, builder)
        return builder.toString()
    }

    fun buildModuleActionBlock(fileContext: String, moduleContext: String, string: String): String {
        val builder = TxSourceBuilder()
        val actionBlock = SvExtractUtil.extractModuleActionBlock(fileContext, moduleContext, string)
        TxBuilderActionBlock.build(actionBlock, builder)
        return builder.toString()
    }

    fun buildModuleActionBlockExpression(fileContext: String, moduleContext: String, string: String): String {
        val builder = TxSourceBuilder()
        val expression = SvExtractUtil.extractModuleActionBlockExpression(fileContext, moduleContext, string)
        TxBuilderExpressionBase.build(expression, false, builder)
        return builder.toString()
    }

    fun buildModuleMethodBlock(fileContext: String, moduleContext: String, string: String): String {
        val builder = TxSourceBuilder()
        val methodBlock = SvExtractUtil.extractModuleMethodBlock(fileContext, moduleContext, string)
        TxBuilderMethodBlock.build(methodBlock, false, builder)
        return builder.toString()
    }

    fun buildBusComponentInstance(fileContext: String, busContext: String, string: String): String {
        val builder = TxSourceBuilder()
        val componentInstance = SvExtractUtil.extractBusComponentInstance(fileContext, busContext, string)
        TxBuilderComponentInstance.build(componentInstance, builder)
        return builder.toString()
    }

    fun buildPrimaryProperty(fileContext: String, string: String): String {
        val builder = TxSourceBuilder()
        val primaryProperty = SvExtractUtil.extractPrimaryProperty(fileContext, string)
        TxBuilderPrimaryProperty.build(primaryProperty, builder)
        return builder.toString()
    }

    fun buildEnum(fileContext: String, string: String): String {
        val builder = TxSourceBuilder()
        val enum = SvExtractUtil.extractEnum(fileContext, string)
        TxBuilderEnum.build(enum, builder)
        return builder.toString()
    }

    fun buildCls(fileContext: String, string: String): String {
        val builder = TxSourceBuilder()
        val cls = SvExtractUtil.extractCls(fileContext, string)
        TxBuilderCls.build(cls, builder)
        return builder.toString()
    }
}