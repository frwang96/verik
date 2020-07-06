package com.verik.core.kt

import com.verik.core.LinePos
import java.lang.Exception
import java.util.*

// Copyright (c) 2020 Francis Wang

sealed class KtNode(open val linePos: LinePos) {

    fun getAsRule(): KtRule {
        return if (this is KtRule) this
        else throw KtGrammarException("token node accessed as rule", linePos)
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
            is KtToken -> {
                builder.appendln("${this.type} ${this.text.replace("\n", "\\n")}")
            }
            is KtRule -> {
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

data class KtRule(
        override val linePos: LinePos,
        val type: KtRuleType,
        val children: List<KtNode>
): KtNode(linePos) {

    override fun countRuleNodes() = children.sumBy { it.countRuleNodes() } + 1

    override fun countTokenNodes() = children.sumBy { it.countTokenNodes() }

    override fun toString() = super.toString()

    fun containsType(type: KtTokenType): Boolean {
        return this.children.any { it is KtToken && it.type == type }
    }

    fun containsType(type: KtRuleType): Boolean {
        return this.children.any { it is KtRule && it.type == type }
    }

    fun getFirst(): KtNode {
        return if (this.children.isNotEmpty()) children[0]
        else throw KtGrammarException("rule node has no children", linePos)
    }

    fun getFirstAsRule(): KtRule {
        return if (this.children.isNotEmpty()) {
            val child = this.children[0]
            if (child is KtRule) child
            else throw KtGrammarException("first child is token but accessed as rule", linePos)
        } else throw KtGrammarException("rule node has no children", linePos)
    }

    fun getFirstAsTokenType(): KtTokenType {
        return if (this.children.isNotEmpty()) {
            val child = this.children[0]
            if (child is KtToken) child.type
            else throw KtGrammarException("first child is rule but accessed as token", linePos)
        } else throw KtGrammarException("rule node has no children", linePos)
    }

    fun getFirstAsTokenText(): String {
        return if (this.children.isNotEmpty()) {
            val child = this.children[0]
            if (child is KtToken) child.text
            else throw KtGrammarException("first child is rule but accessed as token", linePos)
        } else throw KtGrammarException("rule node has no children", linePos)
    }

    fun getChildrenAs(type: KtRuleType): List<KtRule> {
        return children.filter { it is KtRule && it.type == type }.map { it as KtRule }
    }

    fun getChildrenAs(type: KtTokenType): List<KtToken> {
        return children.filter { it is KtToken && it.type == type }.map { it as KtToken }
    }

    fun getChildAs(type: KtRuleType): KtRule {
        val matchingChildren = getChildrenAs(type)
        if (matchingChildren.isEmpty()) throw KtGrammarException("rule node has no children matching $type", linePos)
        if (matchingChildren.size > 1) throw KtGrammarException("rule node has multiple children matching $type", linePos)
        return matchingChildren[0]
    }

    fun getChildAs(type: KtTokenType): KtToken {
        val matchingChildren = getChildrenAs(type)
        if (matchingChildren.isEmpty()) throw KtGrammarException("rule node has no children matching $type", linePos)
        if (matchingChildren.size > 1) throw KtGrammarException("rule node has multiple children matching $type", linePos)
        return matchingChildren[0]
    }

    fun getDirectDescendantAs(type: KtRuleType, exception: Exception): KtRule {
        var child: KtRule = this
        while (true) {
            if (child.children.size != 1) throw exception
            val nextChild = child.children[0]
            if (nextChild is KtRule) {
                if (nextChild.type == type) return nextChild
                child = nextChild
            } else throw exception
        }
    }

    fun getDirectDescendantAs(type: KtTokenType, exception: Exception): KtToken {
        var child: KtRule = this
        while (true) {
            if (child.children.size != 1) throw exception
            when (val nextChild = child.children[0]) {
                is KtRule -> child = nextChild
                is KtToken -> {
                    if  (nextChild.type == type) return nextChild
                    else throw exception
                }
            }
        }
    }
}

data class KtToken(
        override val linePos: LinePos,
        val type: KtTokenType,
        val text: String
): KtNode(linePos) {

    override fun countRuleNodes() = 0

    override fun countTokenNodes() = 1

    override fun toString() = super.toString()
}
