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

package io.verik.core.al

import io.verik.core.FileLine
import io.verik.core.FileLineException
import java.util.*

sealed class AlNode(open val fileLine: FileLine) {

    fun asRule(): AlRule {
        return if (this is AlRule) this
        else throw FileLineException("token node accessed as rule", fileLine)
    }

    abstract fun countRuleNodes(): Int

    abstract fun countTokenNodes(): Int

    private fun toStringRecursive(stem: Stack<Boolean>, builder: StringBuilder) {
        if (stem.isNotEmpty()) {
            for (s in stem.dropLast(1)) {
                builder.append(if (s) "│  " else "   ")
            }
            builder.append(if (stem.last()) "├─ " else "└─ ")
        }
        when (this) {
            is AlToken -> {
                builder.appendln("${this.type} ${this.text.replace("\n", "\\n")}")
            }
            is AlRule -> {
                builder.appendln("${this.type}")
                for (child in children.dropLast(1)) {
                    stem.push(true)
                    child.toStringRecursive(stem, builder)
                    stem.pop()
                }
                if (children.isNotEmpty()) {
                    stem.push(false)
                    children.last().toStringRecursive(stem, builder)
                    stem.pop()
                }
            }
        }
    }

    override fun toString(): String {
        val builder = StringBuilder()
        toStringRecursive(Stack(), builder)
        return builder.toString()
    }
}

data class AlRule(
        override val fileLine: FileLine,
        val type: AlRuleType,
        val children: List<AlNode>
): AlNode(fileLine) {

    override fun countRuleNodes() = children.sumBy { it.countRuleNodes() } + 1

    override fun countTokenNodes() = children.sumBy { it.countTokenNodes() }

    override fun toString() = super.toString()

    fun containsType(type: AlTokenType): Boolean {
        return this.children.any { it is AlToken && it.type == type }
    }

    fun containsType(type: AlRuleType): Boolean {
        return this.children.any { it is AlRule && it.type == type }
    }

    fun first(): AlNode {
        return if (this.children.isNotEmpty()) children[0]
        else throw FileLineException("rule node has no children", fileLine)
    }

    fun firstAsRule(): AlRule {
        return if (this.children.isNotEmpty()) {
            val child = this.children[0]
            if (child is AlRule) child
            else throw FileLineException("first child is token but accessed as rule", fileLine)
        } else throw FileLineException("rule node has no children", fileLine)
    }

    fun firstAsToken(): AlToken {
        return if (this.children.isNotEmpty()) {
            val child = this.children[0]
            if (child is AlToken) child
            else throw FileLineException("first child is rule but accessed as token", fileLine)
        } else throw FileLineException("rule node has no children", fileLine)
    }

    fun firstAsTokenType(): AlTokenType {
        return firstAsToken().type
    }

    fun firstAsTokenText(): String {
        return firstAsToken().text
    }

    fun childrenAs(type: AlRuleType): List<AlRule> {
        return children.filter { it is AlRule && it.type == type }.map { it as AlRule }
    }

    fun childrenAs(type: AlTokenType): List<AlToken> {
        return children.filter { it is AlToken && it.type == type }.map { it as AlToken }
    }

    fun childAs(type: AlRuleType): AlRule {
        val matchingChildren = childrenAs(type)
        if (matchingChildren.isEmpty()) throw FileLineException("rule node has no children matching $type", fileLine)
        if (matchingChildren.size > 1) throw FileLineException("rule node has multiple children matching $type", fileLine)
        return matchingChildren[0]
    }

    fun childAs(type: AlTokenType): AlToken {
        val matchingChildren = childrenAs(type)
        if (matchingChildren.isEmpty()) throw FileLineException("rule node has no children matching $type", fileLine)
        if (matchingChildren.size > 1) throw FileLineException("rule node has multiple children matching $type", fileLine)
        return matchingChildren[0]
    }

    fun directDescendantAs(type: AlRuleType, exception: Exception): AlRule {
        var child: AlRule = this
        while (true) {
            if (child.children.size != 1) throw exception
            val nextChild = child.children[0]
            if (nextChild is AlRule) {
                if (nextChild.type == type) return nextChild
                child = nextChild
            } else throw exception
        }
    }
}

data class AlToken(
        override val fileLine: FileLine,
        val type: AlTokenType,
        val text: String
): AlNode(fileLine) {

    override fun countRuleNodes() = 0

    override fun countTokenNodes() = 1

    override fun toString() = super.toString()
}
