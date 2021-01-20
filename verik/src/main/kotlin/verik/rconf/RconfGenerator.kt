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

package verik.rconf

import verik.data.*
import kotlin.system.exitProcess

internal class RconfGenerator {

    companion object {

        fun generate(list: RconfList, type: Data) {
            try {
                val entries = RconfExpander.expand(list)

                for (entry in entries) {
                    TypeChecker.check(type, entry)
                }

                for (entry in entries) {
                    println(entry.name)
                    println("    ${RconfFormatter.get_string(entry.data)}")
                    println("    ${RconfFormatter.get_encoding(entry.data)}")
                    println("    ${entry.count}")
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