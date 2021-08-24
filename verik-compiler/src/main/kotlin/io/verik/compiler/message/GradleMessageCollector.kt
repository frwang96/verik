/*
 * Copyright (c) 2021 Francis Wang
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

package io.verik.compiler.message

import io.verik.compiler.main.Config

class GradleMessageCollector(config: Config) : MessageCollector() {

    private val verbose = config.verbose
    private val debug = config.debug

    private val MAX_ERROR_COUNT = 20

    override fun fatal(message: String, location: SourceLocation?): Nothing {
        error(message, location)
        throw MessageCollectorException()
    }

    override fun error(message: String, location: SourceLocation?) {
        super.error(message, location)
        print("e: ")
        printMessage(message, location)
        if (debug) printStackTrace()
        if (errorCount >= MAX_ERROR_COUNT) throw MessageCollectorException()
    }

    override fun warning(message: String, location: SourceLocation?) {
        print("w: ")
        printMessage(message, location)
        if (debug) printStackTrace()
    }

    override fun log(message: String) {
        if (verbose) {
            print("i: ")
            printMessage(message, null)
        }
    }

    override fun flush() {
        if (errorCount != 0) throw MessageCollectorException()
    }

    private fun printMessage(message: String, location: SourceLocation?) {
        if (location != null) {
            print("${location.path}: (${location.line}, ${location.column}): ")
        }
        println(message)
    }

    private fun printStackTrace() {
        Thread.currentThread().stackTrace.forEach {
            if (it.className.startsWith("io.verik") && !it.className.contains("MessageCollector"))
                println("at $it")
        }
    }
}
