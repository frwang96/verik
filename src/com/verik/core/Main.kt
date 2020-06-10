package com.verik.core

import com.verik.core.antlr.KotlinLexer
import com.verik.core.antlr.KotlinParser
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.tree.TerminalNode
import org.antlr.v4.runtime.tree.Tree
import java.nio.file.Path
import java.util.*

// Copyright (c) 2020 Francis Wang

fun main() {
    val file = Path.of("src/com/verik/core/Main.kt")
    val lexer = KotlinLexer(CharStreams.fromPath(file))
    val parser = KotlinParser(CommonTokenStream(lexer))
    val tree = parser.kotlinFile()

    printTree(tree, parser, lexer)
}

fun countTree(tree: Tree): Pair<Int, Int> {
    var rulesTotal = 0
    var terminalsTotal = 0
    when (tree) {
        is RuleContext -> rulesTotal++
        is TerminalNode -> terminalsTotal++
    }
    for (i in 0 until tree.childCount) {
        val (rules, terminals) = countTree(tree.getChild(i))
        rulesTotal += rules
        terminalsTotal += terminals
    }
    return Pair(rulesTotal, terminalsTotal)
}

fun printTree(tree: Tree, parser: Parser, lexer: Lexer) {
    fun printTreeRecursive(tree: Tree, parserNames: List<String>, lexerNames: List<String>, stem: Stack<Boolean>) {
        if (stem.isNotEmpty()) {
            for (s in stem.dropLast(1)) {
                print(if (s) "│  " else "   ")
            }
            print((if (stem.peek()) "├─ " else "└─ "))
        }
        val text = when (tree) {
            is RuleContext -> parserNames[tree.ruleIndex]
            is TerminalNode -> {
                if (tree.symbol.type >= 0) {
                    val name = lexerNames[tree.symbol.type]
                    val text = tree.text.replace("\n", "\\n")
                    "$name $text"
                } else "EOF"
            }
            else -> ""
        }
        print(text + "\n")
        for (i in 0 until tree.childCount) {
            stem.push(i != tree.childCount - 1)
            printTreeRecursive(tree.getChild(i), parserNames, lexerNames, stem)
            stem.pop()
        }
    }
    printTreeRecursive(tree, parser.ruleNames.toList(), lexer.ruleNames.toList(), Stack())
    val (rules, terminals) = countTree(tree)
    println("\n$rules rules and $terminals terminals")
}
