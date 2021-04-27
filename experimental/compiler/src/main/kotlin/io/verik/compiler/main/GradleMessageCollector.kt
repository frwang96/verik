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

class GradleMessageCollector(val verbose: Boolean): MessageCollector {

    private var hasErrors = false

    override fun hasErrors() = hasErrors

    override fun error(message: String, location: MessageLocation?) {
        print("e: ")
        printMessage(message, location)
        hasErrors = true
    }

    override fun warning(message: String, location: MessageLocation?) {
        print("w: ")
        printMessage(message, location)
    }

    override fun info(message: String, location: MessageLocation?) {
        if (!verbose) {
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
}