/*
 * Copyright (c) 2022 Francis Wang
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

package io.verik.importer.cast.common

import org.antlr.v4.runtime.RuleContext

class SignatureBuilder private constructor(private val name: String) {

    private val builder = StringBuilder()
    private var isNewLine = true
    private var isIndented = false

    private fun buildSignature(ctx: RuleContext): String {
        val signatureVisitor = SignatureVisitor()
        ctx.accept(signatureVisitor)
        val signatureFragments = signatureVisitor.signatureFragments
        for (index in 0 until signatureFragments.size - 1) {
            buildSignatureFragments(signatureFragments[index + 1], signatureFragments[index])
        }
        return builder.toString()
    }

    private fun buildSignatureFragments(fragment: SignatureFragment, lastFragment: SignatureFragment) {
        if (isNewLine) {
            if (isIndented) {
                builder.append("    ")
            }
            isNewLine = false
        } else if (hasSpace(fragment.kind, lastFragment.kind)) {
            builder.append(" ")
        }

        when (fragment.kind) {
            SignatureFragmentKind.TEXT -> {
                builder.append(fragment.text)
            }
            SignatureFragmentKind.NULL -> {}
            SignatureFragmentKind.NAME -> {
                builder.append(name)
            }
            SignatureFragmentKind.BREAK -> {
                builder.appendLine()
                isNewLine = true
            }
            SignatureFragmentKind.INDENT_IN -> {
                isIndented = true
            }
            SignatureFragmentKind.INDENT_OUT -> {
                isIndented = false
            }
            SignatureFragmentKind.SEMICOLON -> {
                builder.append(";")
            }
            SignatureFragmentKind.COLON -> {
                builder.append(":")
            }
            SignatureFragmentKind.COLON_COLON -> {
                builder.append("::")
            }
            SignatureFragmentKind.SHARP -> {
                builder.append("#")
            }
            SignatureFragmentKind.LBRACK -> {
                builder.append("[")
            }
            SignatureFragmentKind.RBRACK -> {
                builder.append("]")
            }
            SignatureFragmentKind.COMMA -> {
                builder.append(",")
            }
            SignatureFragmentKind.COMMA_BREAK -> {
                builder.appendLine(",")
                isNewLine = true
            }
            SignatureFragmentKind.LPAREN -> {
                builder.append("(")
            }
            SignatureFragmentKind.LPAREN_BREAK -> {
                builder.appendLine("(")
                isNewLine = true
                isIndented = true
            }
            SignatureFragmentKind.RPAREN -> {
                builder.append(")")
            }
            SignatureFragmentKind.RPAREN_BREAK -> {
                builder.appendLine()
                builder.append(")")
                isIndented = false
            }
            SignatureFragmentKind.LBRACE -> {
                builder.append("{")
            }
            SignatureFragmentKind.LBRACE_BREAK -> {
                builder.appendLine("{")
                isNewLine = true
                isIndented = true
            }
            SignatureFragmentKind.RBRACE -> {
                builder.append("}")
            }
            SignatureFragmentKind.RBRACE_BREAK -> {
                builder.appendLine()
                builder.append("}")
                isIndented = false
            }
        }
    }

    private fun hasSpace(kind: SignatureFragmentKind, lastKind: SignatureFragmentKind): Boolean {
        return when {
            lastKind == SignatureFragmentKind.NULL -> false
            lastKind == SignatureFragmentKind.COLON -> false
            lastKind == SignatureFragmentKind.COLON_COLON -> false
            lastKind == SignatureFragmentKind.SHARP -> false
            lastKind == SignatureFragmentKind.LBRACK -> false
            lastKind == SignatureFragmentKind.LPAREN -> false
            kind == SignatureFragmentKind.BREAK -> false
            kind == SignatureFragmentKind.INDENT_IN -> false
            kind == SignatureFragmentKind.INDENT_OUT -> false
            kind == SignatureFragmentKind.SEMICOLON -> false
            kind == SignatureFragmentKind.COMMA -> false
            kind == SignatureFragmentKind.COMMA_BREAK -> false
            kind == SignatureFragmentKind.COLON -> false
            kind == SignatureFragmentKind.COLON_COLON -> false
            kind == SignatureFragmentKind.RBRACK -> false
            kind == SignatureFragmentKind.RPAREN -> false
            kind == SignatureFragmentKind.RPAREN_BREAK -> false
            kind == SignatureFragmentKind.RBRACE_BREAK -> false
            lastKind == SignatureFragmentKind.NAME && kind == SignatureFragmentKind.LPAREN -> false
            lastKind == SignatureFragmentKind.NAME && kind == SignatureFragmentKind.LPAREN_BREAK -> false
            lastKind == SignatureFragmentKind.RBRACK && kind == SignatureFragmentKind.LBRACK -> false
            lastKind == SignatureFragmentKind.RPAREN_BREAK && kind == SignatureFragmentKind.LPAREN_BREAK -> false
            else -> true
        }
    }

    companion object {

        fun buildSignature(ctx: RuleContext, name: String): String {
            val signatureBuilder = SignatureBuilder(name)
            return signatureBuilder.buildSignature(ctx)
        }
    }
}
