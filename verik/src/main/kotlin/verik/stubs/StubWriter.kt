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

package verik.stubs

import verik.base.*
import java.io.File

internal class StubWriter {

    companion object {

        fun writeStubs(args: Array<String>, list: _stub_list, reference: _any) {
            try {
                if (args.size != 2) throw IllegalArgumentException("expected project directory and test stubs file as parameters")

                val projectDir = File(args[0])
                if (!projectDir.exists()) {
                    throw IllegalArgumentException("project directory ${args[0]} not found")
                }
                val stubsFile = File(args[1])

                val stubsExpanded = StubExpander.expand(list)

                for (stub in stubsExpanded) {
                    TypeChecker.check(reference, stub)
                }

                stubsFile.parentFile.mkdirs()
                stubsFile.writeText(build(stubsExpanded))
            } catch (exception: Exception) {
                StatusPrinter.error(exception)
            }
        }

        private fun build(list: List<_stub_entry>): String {
            val builder = StringBuilder()

            for (entry in list) {
                builder.appendLine(entry.name)
                builder.appendLine("    ${ConfigFormatter.getString(entry.config)}")
                builder.appendLine("    ${ConfigFormatter.getEncoding(entry.config)}")
                builder.appendLine("    ${entry.count}")
            }

            return builder.toString()
        }
    }
}