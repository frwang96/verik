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

class StatusPrinter {

    companion object {

        fun info(message: String) {
            println("INFO: $message")
        }

        fun warning(message: String) {
            print("\u001B[33m") // ANSI yellow
            print("WARNING: $message")
            print("\u001B[0m\n") // ANSI reset
        }

        fun error(message: String?, exception: Exception): Nothing {
            print("\u001B[31m") // ANSI red
            print("ERROR:")
            if (message != null) print(" $message")
            print("\u001B[0m\n") // ANSI reset
            println("${exception::class.simpleName} at")
            for (trace in exception.stackTrace) {
                println("\t$trace")
            }
            exitProcess(1)
        }

    }
}