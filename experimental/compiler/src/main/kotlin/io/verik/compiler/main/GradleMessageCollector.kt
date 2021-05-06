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

package io.verik.compiler.main

import org.gradle.api.GradleException

class GradleMessageCollector(config: Config): MessageCollector() {

    val verbose = config.verbose
    val printStackTrace = config.printStackTrace

    private val MAX_ERROR_COUNT = 20

    override fun error(message: String, location: MessageLocation?) {
        super.error(message, location)
        print("e: ")
        printMessage(message, location)
        if (printStackTrace) printStackTrace()
        if (errorCount >= MAX_ERROR_COUNT) throw GradleException("Verik compilation failed")
    }

    override fun warning(message: String, location: MessageLocation?) {
        print("w: ")
        printMessage(message, location)
        if (printStackTrace) printStackTrace()
    }

    override fun info(message: String, location: MessageLocation?) {
        if (verbose) {
            print("i: ")
            printMessage(message, location)
        }
    }

    private fun printMessage(message: String, location: MessageLocation?) {
        if (location != null) {
            print("${location.path}: (${location.line}, ${location.column}): ")
        }
        println(message)
    }

    private fun printStackTrace() {
        Thread.currentThread().stackTrace.filter {
            it.className.startsWith("io.verik") && !it.className.contains("GradleMessageCollector")
        }.forEach { println("at $it") }
    }
}