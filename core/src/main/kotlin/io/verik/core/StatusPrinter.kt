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

import java.io.File
import kotlin.system.exitProcess

class StatusPrinter {

    companion object {

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
            val source = if (exception is SourceLineException) exception.source else null
            val line = if (exception is LineException) exception.line else 0

            println()
            if (isConsole) {
                print("\u001B[31m\u001B[1m") // ANSI red bold
                print("ERROR:")
                print(getSourceLineString(source, line))
                if (exception.message != null) print(" ${exception.message}")
                print("\u001B[0m\n") // ANSI reset
            } else {
                print("ERROR:")
                print(getSourceLineString(source, line))
                if (exception.message != null) println(" ${exception.message}")
            }
            println("${exception::class.simpleName} at")
            for (trace in exception.stackTrace) {
                println("\t$trace")
            }
            println()
            exitProcess(1)
        }

        private fun getSourceLineString(source: File?, line: Int): String {
            return if (source != null) {
                if (line != 0) " (${source.name}:$line)"
                else " (${source.name})"
            } else {
                if (line != 0) " ($line)"
                else ""
            }
        }
    }
}