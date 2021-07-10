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

import io.verik.compiler.common.ProjectPass
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.TextFile
import io.verik.compiler.main.m

object ProjectSerializer : ProjectPass {

    override fun pass(projectContext: ProjectContext) {
        m.info("Serialize: Serialize package files")
        val packageTextFiles = PackageFileSerializer.serialize(projectContext)
        m.flush()

        m.info("Serialize: Serialize order file")
        val orderTextFile = OrderFileSerializer.serialize(projectContext, packageTextFiles)
        m.flush()

        m.info("Serialize: Serialize source files")
        val outputTextFiles = ArrayList<TextFile>()
        outputTextFiles.addAll(packageTextFiles)
        outputTextFiles.add(orderTextFile)
        projectContext.verikFiles.forEach {
            val sourceBuilder = SourceBuilder(projectContext, it)
            it.accept(SerializerBaseVisitor(sourceBuilder))
            outputTextFiles.add(sourceBuilder.toTextFile())
        }
        projectContext.outputTextFiles = outputTextFiles
        m.flush()
    }
}