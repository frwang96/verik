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

import kotlin.system.exitProcess

internal class StatusPrinter {

    companion object {

        private val isConsole = (System.console() != null)

        fun info(message: String) {
            if (isConsole) {
                print("\u001B[1m") // ANSI bold
                print(message)
                print("\u001B[0m\n") // ANSI reset
            } else {
                println(message)
            }
        }

        fun error(exception: Exception): Nothing {
            println()
            if (isConsole) {
                print("\u001B[31m\u001B[1m") // ANSI red bold
                print("ERROR:")
                if (exception.message != null) print(" ${exception.message}")
                print("\u001B[0m\n") // ANSI reset
            } else {
                print("ERROR:")
                if (exception.message != null) println(" ${exception.message}")
            }
            println("${exception::class.simpleName} at")
            for (trace in exception.stackTrace) {
                println("\t$trace")
            }
            exitProcess(1)
        }
    }
}
