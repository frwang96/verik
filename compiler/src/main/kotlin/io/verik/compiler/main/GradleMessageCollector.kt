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

import io.verik.compiler.ast.element.common.CDeclaration
import io.verik.compiler.ast.element.common.CElement
import io.verik.plugin.Config
import org.gradle.api.GradleException
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtNamedDeclaration

class GradleMessageCollector(config: Config) : MessageCollector() {

    private val verbose = config.verbose
    private val debug = config.debug
    private val startTime = System.nanoTime()

    private val MAX_ERROR_COUNT = 20

    override fun fatal(message: String, location: SourceLocation?): Nothing {
        error(message, location)
        throw GradleException("Verik compilation failed")
    }

    override fun error(message: String, location: SourceLocation?) {
        super.error(message, location)
        print("e: ")
        printMessage(message, location)
        if (debug) printStackTrace()
        if (errorCount >= MAX_ERROR_COUNT) throw GradleException("Verik compilation failed")
    }

    override fun warning(message: String, location: SourceLocation?) {
        print("w: ")
        printMessage(message, location)
        if (debug) printStackTrace()
    }

    override fun info(message: String) {
        if (verbose) {
            print("i: ")
            if (debug)
                print("${getElapsedString()}: ")
            printMessage(message, null)
        }
    }

    override fun log(message: String) {
        if (debug) {
            print("i: ${getElapsedString()}: ")
            printMessage(message, null)
        }
    }

    override fun flush() {
        if (errorCount != 0) throw GradleException("Verik compilation failed")
    }

    private fun printMessage(message: String, location: SourceLocation?) {
        if (location != null) {
            print("${location.path}: (${location.line}, ${location.column}): ")
            val qualifiedName = getQualifiedName(location.element)
            if (qualifiedName != null)
                print("$qualifiedName: ")
        }
        println(message)
    }

    private fun printStackTrace() {
        Thread.currentThread().stackTrace.forEach {
            if (it.className.startsWith("io.verik") && !it.className.contains("MessageCollector"))
                println("at $it")
        }
    }

    private fun getElapsedString(): String {
        val elapsed = (System.nanoTime() - startTime) / 1e9
        return "%.3fs".format(elapsed)
    }

    private fun getQualifiedName(element: Any?): String? {
        val names = ArrayList<String>()
        when (element) {
            is PsiElement -> {
                var parent: PsiElement? = element
                while (parent != null) {
                    if (parent is KtNamedDeclaration)
                        names.add(parent.name!!)
                    parent = parent.parent
                }
            }
            is CElement -> {
                var parent: CElement? = element
                while (parent != null) {
                    if (parent is CDeclaration)
                        names.add(parent.name.name)
                    parent = parent.parent
                }
            }
        }
        return if (names.isNotEmpty()) {
            names.reverse()
            names.joinToString(separator = ".")
        } else null
    }
}