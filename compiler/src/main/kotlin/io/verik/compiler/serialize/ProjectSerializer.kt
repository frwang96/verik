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

package io.verik.compiler.serialize

import io.verik.compiler.ast.element.VkOutputFile
import io.verik.compiler.common.ElementParentChecker
import io.verik.compiler.common.ElementUtil
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.TextFile
import io.verik.compiler.main.messageCollector

object ProjectSerializer {

    fun serialize(projectContext: ProjectContext) {
        if (projectContext.vkFiles.isEmpty())
            messageCollector.error("Output files empty: No declarations found", null)

        messageCollector.info("Serialize: Check source locations", null)
        SourceLocationChecker.check(projectContext)

        messageCollector.info("Serialize: Check parent elements", null)
        ElementParentChecker.check(projectContext)

        messageCollector.info("Serialize: Serialize output files", null)
        val packageTextFiles = PackageFileSerializer.serialize(projectContext)
        val orderTextFile = OrderFileSerializer.serialize(projectContext, packageTextFiles)

        val outputTextFiles = ArrayList<TextFile>()
        outputTextFiles.addAll(packageTextFiles)
        outputTextFiles.add(orderTextFile)
        projectContext.vkFiles.forEach {
            val file = ElementUtil.cast<VkOutputFile>(it)
            if (file != null) {
                val sourceBuilder = SourceBuilder(projectContext, file)
                file.accept(SourceSerializerVisitor(sourceBuilder))
                outputTextFiles.add(sourceBuilder.toTextFile())
            }
        }
        projectContext.outputTextFiles = outputTextFiles
        messageCollector.flush()
    }
}