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

package io.verik.compiler.ast.property

import io.verik.compiler.ast.element.common.EExpression

sealed class StringEntry {

    abstract fun copy(): StringEntry
}

class LiteralStringEntry(val text: String) : StringEntry() {

    override fun copy(): StringEntry {
        return LiteralStringEntry(text)
    }
}

class ExpressionStringEntry(var expression: EExpression) : StringEntry() {

    override fun copy(): StringEntry {
        val copyExpression = expression.copy()
        return ExpressionStringEntry(copyExpression)
    }
}
