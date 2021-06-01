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

package io.verik.compiler.ast.common

import io.verik.compiler.main.messageCollector
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.name.FqName

enum class FunctionAnnotationType {
    COM,
    SEQ,
    RUN,
    TASK;

    override fun toString(): String {
        return super.toString().toLowerCase()
    }

    companion object {

        operator fun invoke(fqName: FqName?, element: PsiElement): FunctionAnnotationType? {
            return if (fqName != null) {
                when (fqName.toString()) {
                    "io.verik.core.com" -> COM
                    "io.verik.core.seq" -> SEQ
                    "io.verik.core.run" -> RUN
                    "io.verik.core.task" -> TASK
                    else -> {
                        messageCollector.error("Annotation not recognized: ${fqName.shortName()}", element)
                        null
                    }
                }
            } else null
        }
    }
}