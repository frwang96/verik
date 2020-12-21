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

package verikc.sv

import verikc.ps.PsUtil
import verikc.sv.build.SvSourceBuilder

object SvUtil {

    fun extractModuleFIle(string: String): String {
        val builder = SvSourceBuilder()
        val file = PsUtil.extractModuleFile(string)
        file.build(builder)
        return builder.toString()
    }

    fun extractModule(fileContext: String, string: String): String {
        val builder = SvSourceBuilder()
        val module = PsUtil.extractModule(fileContext, string)
        module.build(builder)
        return builder.toString()
    }

    fun extractComponentInstance(fileContext: String, moduleContext: String, string: String): String {
        val builder = SvSourceBuilder()
        val componentInstance = PsUtil.extractComponentInstance(fileContext, moduleContext, string)
        componentInstance.build(builder)
        return builder.toString()
    }

    fun extractActionBlock(fileContext: String, moduleContext: String, string: String): String {
        val builder = SvSourceBuilder()
        val actionBlock = PsUtil.extractActionBlock(fileContext, moduleContext, string)
        actionBlock.build(builder)
        return builder.toString()
    }

    fun extractExpression(fileContext: String, moduleContext: String, string: String): String {
        val builder = SvSourceBuilder()
        val expression = PsUtil.extractExpression(fileContext, moduleContext, string)
        expression.build(builder)
        return builder.toString()
    }

    fun extractEnum(fileContext: String, string: String): String {
        val builder = SvSourceBuilder()
        val enum = PsUtil.extractEnum(fileContext, string)
        enum.build(builder)
        return builder.toString()
    }
}