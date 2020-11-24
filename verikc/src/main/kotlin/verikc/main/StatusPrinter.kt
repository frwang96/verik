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

import verikc.base.SymbolContext
import verikc.base.ast.LineException
import verikc.base.ast.Symbol
import kotlin.system.exitProcess

object StatusPrinter {

    private val isConsole = (System.console() != null)
    private var lastWasInfo = false

    private val symbolRegex = Regex("\\[\\[(-?\\d+), (-?\\d+), (-?\\d+)]]")
    private var symbolContext: SymbolContext? = null

    fun setSymbolContext(symbolContext: SymbolContext?) {
        this.symbolContext = symbolContext
    }

    @Suppress("unused")
    fun log(message: Any) {
        synchronized(this) {
            println(substituteSymbols(message.toString()))
        }
    }

    fun info(message: String, indent: Int = 0) {
        synchronized(this) {
            if (!lastWasInfo || indent == 0) println()
            lastWasInfo = true

            if (isConsole) {
                print("\u001B[1m") // ANSI bold
                repeat(indent) { print("    ") }
                print(substituteSymbols(message))
                print("\u001B[0m\n") // ANSI reset
            } else {
                repeat(indent) { print("    ") }
                println(substituteSymbols(message))
            }
        }
    }

    fun warning(message: String) {
        synchronized(this) {
            if (lastWasInfo) println()
            lastWasInfo = false

            if (isConsole) {
                print("\u001B[33m\u001B[1m") // ANSI yellow bold
                print("WARNING: ${substituteSymbols(message)}")
                print("\u001B[0m\n") // ANSI reset
            } else {
                println("WARNING: ${substituteSymbols(message)}")
            }
        }
    }

    fun error(exception: Exception): Nothing {
        synchronized(this) {
            var message = getFileLineString(exception)
            if (exception.message != null) message += " " + substituteSymbols(exception.message!!)
            errorMessage(message)
            println("${exception::class.simpleName} at")
            for (trace in exception.stackTrace) {
                println("    $trace")
            }
            println()
            exitProcess(1)
        }
    }

    fun errorMessage(message: String) {
        synchronized(this) {
            println()
            if (isConsole) {
                print("\u001B[31m\u001B[1m") // ANSI red bold
                print("ERROR: ${substituteSymbols(message)}")
                print("\u001B[0m\n") // ANSI reset
            } else {
                println("ERROR: ${substituteSymbols(message)}")
            }
        }
    }

    fun substituteSymbols(string: String): String {
        val matches = symbolRegex.findAll(string)
        var substitutedString = string
        for (match in matches) {
            val symbol = Symbol(
                    match.groupValues[1].toInt(),
                    match.groupValues[2].toInt(),
                    match.groupValues[3].toInt()
            )
            val symbolString = symbolContext?.identifier(symbol)
            if (symbolString != null) {
                substitutedString = substitutedString.replace(match.groupValues[0], symbolString)
            }
        }
        return substitutedString
    }

    private fun getFileLineString(exception: Exception): String {
        return if (exception is LineException) {
            val line = exception.line
            val file = exception.file?.let { symbolContext?.identifier(it) }
            when {
                file != null && line != 0 -> " ($file:$line)"
                file != null && line == 0 -> " ($file)"
                file == null && line != 0 -> " ($line)"
                else -> ""
            }
        } else ""
    }
}
