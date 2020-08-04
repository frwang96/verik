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

package io.verik.stubs

import io.verik.common.data.*
import java.io.File

class StubWriter {

    companion object {

        fun writeStubs(args: Array<String>, type: _data, stubs: List<Stub>) {
            try {
                if (args.size != 2) throw IllegalArgumentException("expected project directory and test stubs file as parameters")

                val projectDir = File(args[0])
                if (!projectDir.exists()) throw IllegalArgumentException("project directory ${args[0]} not found")

                val stubsFile = File(args[1])

                StatusPrinter.info("expanding test stubs")
                val stubsExpanded = StubExpander.expand(stubs)

                StatusPrinter.info("processing ${stubsExpanded.size} test stubs")
                for (stub in stubsExpanded) {
                    TypeChecker.check(type, stub)
                }

                StatusPrinter.info("generating test stubs file ${stubsFile.relativeTo(projectDir)}")
                stubsFile.writeText(build(stubsExpanded))
            } catch (exception: Exception) {
                StatusPrinter.error(exception)
            }
        }

        private fun build(stubs: List<StubEntry>): String {
            val builder = StringBuilder()

            for (stub in stubs) {
                builder.appendln(stub.name)
                builder.appendln("    ${stub.count} ${stub.config}")
            }

            return builder.toString()
        }
    }
}