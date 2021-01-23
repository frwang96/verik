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

package verikc.al

import verikc.al.ast.AlFile
import verikc.base.config.FileConfig

object AlFileParser {

    fun parse(fileConfig: FileConfig): AlFile {
        val txtFile = fileConfig.file.readText()
        val hash = AlTreeSerializer.hash(txtFile)

        var kotlinFile = AlTreeSerializer.deserialize(fileConfig.symbol, fileConfig.cacheFile, hash)
        if (kotlinFile == null) {
            kotlinFile = AlTreeParser.parseKotlinFile(fileConfig.symbol, txtFile)
            fileConfig.cacheFile.parentFile.mkdirs()
            fileConfig.cacheFile.writeBytes(AlTreeSerializer.serialize(kotlinFile, hash))
        }

        return AlFile(fileConfig, kotlinFile)
    }
}