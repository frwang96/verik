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

package io.verik.core

import kotlin.system.exitProcess

enum class ExecutionType {
    HEADERS,
    SRCS,
    STUBS,
    ALL;

    companion object {

        operator fun invoke(executionType: String): ExecutionType? {
            return when (executionType) {
                "headers" -> HEADERS
                "srcs" -> SRCS
                "stubs" -> STUBS
                "all" -> ALL
                else -> null
            }
        }
    }
}

data class MainArgs(
        val executionType: ExecutionType,
        val configPath: String
) {

    companion object {

        operator fun invoke(args: Array<String>): MainArgs {
            return when (args.size) {
                1 -> {
                    val executionType = ExecutionType(args[0])
                    if (executionType == null) error()
                    else MainArgs(executionType, "vkprojconf.yaml")
                }
                2 -> {
                    val executionType = ExecutionType(args[0])
                    if (executionType == null) error()
                    else MainArgs(executionType, args[1])
                }
                else -> error()
            }
        }

        private fun error(): Nothing {
            println("usage: verik <headers|srcs|stubs|all> [CONF]")
            exitProcess(1)
        }
    }
}