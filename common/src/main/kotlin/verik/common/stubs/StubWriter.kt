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

package verik.common.stubs

import java.io.File

internal class StubWriter {

    companion object {

        fun writeStubs(args: Array<String>, reference: Any, stubs: List<Stub>) {
            try {
                if (args.size != 2) throw IllegalArgumentException("expected project directory and test stubs file as parameters")

                val projectDir = File(args[0])
                if (!projectDir.exists()) {
                    throw IllegalArgumentException("project directory ${args[0]} not found")
                }
                val stubsFile = File(args[1])

                val stubsExpanded = StubExpander.expand(stubs)

                StatusPrinter.info("processing ${stubsExpanded.size} test stubs")
                for (stub in stubsExpanded) {
                    TypeChecker.check(reference, stub)
                }

                StatusPrinter.info("writing ${stubsFile.relativeTo(projectDir)}")
                stubsFile.writeText(build(stubsExpanded))
            } catch (exception: Exception) {
                StatusPrinter.error(exception)
            }
        }

        private fun build(stubs: List<StubEntry>): String {
            val builder = StringBuilder()

            for (stub in stubs) {
                builder.appendLine(stub.name)
                builder.appendLine("    ${ConfigFormatter.getString(stub.config)}")
                builder.appendLine("    ${ConfigFormatter.getEncoding(stub.config)}")
                builder.appendLine("    ${stub.count}")
            }

            return builder.toString()
        }
    }
}