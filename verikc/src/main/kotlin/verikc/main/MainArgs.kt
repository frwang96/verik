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

package verikc.main

import kotlin.system.exitProcess

enum class ExecutionType {
    CLEAN,
    HEADERS,
    GRADLE,
    COMPILE,
    RCONF,
    ALL;

    companion object {

        operator fun invoke(executionType: String): ExecutionType? {
            return when (executionType) {
                "clean" -> CLEAN
                "headers" -> HEADERS
                "gradle" -> GRADLE
                "compile" -> COMPILE
                "rconf" -> RCONF
                "all" -> ALL
                else -> null
            }
        }
    }
}

data class MainArgs(
    val executionTypes: List<ExecutionType>,
    val configPath: String
) {

    fun contains(executionType: ExecutionType): Boolean {
        return if (executionType == ExecutionType.CLEAN) {
            ExecutionType.CLEAN in executionTypes
        } else {
            executionType in executionTypes || ExecutionType.ALL in executionTypes
        }
    }

    companion object {

        operator fun invoke(args: Array<String>): MainArgs {
            val executionTypes = ArrayList<ExecutionType>()
            var configPath = "vkproject.yaml"
            var configFlag = false

            if (args.count { it == "-c" } > 1) error()
            if (args.isNotEmpty() && args.indexOfFirst { it == "-c" } == args.lastIndex) error()
            for (arg in args) {
                if (configFlag) {
                    configPath = arg
                    configFlag = false
                } else {
                    if (arg == "-c") {
                        configFlag = true
                    } else {
                        val executionType = ExecutionType(arg)
                        if (executionType != null) executionTypes.add(executionType)
                        else error()
                    }
                }
            }

            if (executionTypes.isEmpty()) executionTypes.add(ExecutionType.ALL)

            return MainArgs(executionTypes, configPath)
        }

        private fun error(): Nothing {
            println("usage: verikc [-c CONF] [clean|headers|gradle|compile|rconf|all]")
            exitProcess(1)
        }
    }
}
