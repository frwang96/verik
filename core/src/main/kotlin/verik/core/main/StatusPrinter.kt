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

import verik.core.base.LineException
import verik.core.base.Symbol
import verik.core.base.SymbolContext
import verik.core.lang.Lang
import kotlin.system.exitProcess

object StatusPrinter {

    private val isConsole = (System.console() != null)
    private var lastWasInfo = false

    private val symbolRegex = Regex("\\[\\[(-?\\d+), (-?\\d+), (-?\\d+)]]")
    private var symbolContext: SymbolContext? = null

    fun setSymbolContext(symbolContext: SymbolContext?) {
        this.symbolContext = symbolContext
    }

    fun info(message: String, indent: Int = 0) {
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

    fun warning(message: String) {
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

    fun error(exception: Exception): Nothing {
        println()
        if (isConsole) {
            print("\u001B[31m\u001B[1m") // ANSI red bold
            print("ERROR:")
            print(getFileLineString(exception))
            if (exception.message != null) print(" ${substituteSymbols(exception.message!!)}")
            print("\u001B[0m\n") // ANSI reset
        } else {
            print("ERROR:")
            print(getFileLineString(exception))
            if (exception.message != null) println(" ${substituteSymbols(exception.message!!)}")
        }
        println("${exception::class.simpleName} at")
        for (trace in exception.stackTrace) {
            println("\t$trace")
        }
        println()
        exitProcess(1)
    }

    fun substituteSymbols(string: String): String {
        val matches = symbolRegex.findAll(string)
        var substitutedString = string
        for (match in matches) {
            val symbolString = substituteSymbol(Symbol(
                    match.groupValues[1].toInt(),
                    match.groupValues[2].toInt(),
                    match.groupValues[3].toInt()
            ))
            if (symbolString != null) {
                substitutedString = substitutedString.replace(match.groupValues[0], symbolString)
            }
        }
        return substitutedString
    }

    private fun substituteSymbol(symbol: Symbol): String? {
        return symbolContext?.identifier(symbol)
                ?: Lang.typeTable.identifier(symbol)
                ?: Lang.functionTable.identifier(symbol)
                ?: Lang.operatorTable.identifier(symbol)
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