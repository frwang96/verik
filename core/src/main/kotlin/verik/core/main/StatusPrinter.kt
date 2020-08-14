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

package verik.core.main

import kotlin.system.exitProcess

object StatusPrinter {

    private val isConsole = (System.console() != null)
    private var lastWasInfo = false

    fun info(message: String, indent: Int = 0) {
        if (!lastWasInfo || indent == 0) println()
        lastWasInfo = true

        if (isConsole) {
            print("\u001B[1m") // ANSI bold
            repeat(indent) { print("    ") }
            print(message)
            print("\u001B[0m\n") // ANSI reset
        } else {
            repeat(indent) { print("    ") }
            println(message)
        }
    }

    fun warning(message: String) {
        if (lastWasInfo) println()
        lastWasInfo = false

        if (isConsole) {
            print("\u001B[33m\u001B[1m") // ANSI yellow bold
            print("WARNING: $message")
            print("\u001B[0m\n") // ANSI reset
        } else {
            println("WARNING: $message")
        }
    }

    fun error(exception: Exception): Nothing {
        println()
        if (isConsole) {
            print("\u001B[31m\u001B[1m") // ANSI red bold
            print("ERROR:")
            print(getFileLineString(exception))
            if (exception.message != null) print(" ${exception.message}")
            print("\u001B[0m\n") // ANSI reset
        } else {
            print("ERROR:")
            print(getFileLineString(exception))
            if (exception.message != null) println(" ${exception.message}")
        }
        println("${exception::class.simpleName} at")
        for (trace in exception.stackTrace) {
            println("\t$trace")
        }
        println()
        exitProcess(1)
    }

    private fun getFileLineString(exception: Exception): String {
        return if (exception is LineException) {
            val line = exception.line
            val file = exception.file
            if (file != null) {
                if (line != 0) " (${file.name}:$line)"
                else " (${file.name})"
            } else {
                if (line != 0) " ($line)"
                else ""
            }
        } else ""
    }
}