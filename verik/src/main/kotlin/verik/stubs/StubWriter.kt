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
import kotlin.system.exitProcess

internal class StubWriter {

    companion object {

        fun writeStubs(list: _stub_list, reference: _any) {
            try {
                val stubsExpanded = StubExpander.expand(list)

                for (stub in stubsExpanded) {
                    TypeChecker.check(reference, stub)
                }

                for (entry in stubsExpanded) {
                    kotlin.io.println(entry.name)
                    kotlin.io.println("    ${ConfigFormatter.getString(entry.config)}")
                    kotlin.io.println("    ${ConfigFormatter.getEncoding(entry.config)}")
                    kotlin.io.println("    ${entry.count}")
                }
            } catch (exception: Exception) {
                error(exception)
            }
        }

        private fun error(exception: Exception): Nothing {
            System.err.println(exception.message ?: "exception occurred")
            System.err.println("${exception::class.simpleName} at")
            for (trace in exception.stackTrace) {
                System.err.println("    $trace")
            }
            exitProcess(1)
        }
    }
}