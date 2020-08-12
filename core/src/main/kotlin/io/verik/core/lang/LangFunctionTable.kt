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

package io.verik.core.lang

import io.verik.core.symbol.Symbol
import java.util.concurrent.ConcurrentHashMap

class LangFunctionTable {

    private val signatures = ConcurrentHashMap<String, ArrayList<LangFunctionSignature>>()

    fun add(signature: LangFunctionSignature) {
        val signatureCandidates = signatures[signature.identifier]
        if (signatureCandidates != null) {
            signatureCandidates.add(signature)
        } else {
            signatures[signature.identifier] = ArrayList(listOf(signature))
        }
    }

    fun match(identifier: String, argTypes: List<Symbol>): LangFunctionTableMatch {
        val signatureCandidates = signatures[identifier]
        return if (signatureCandidates != null) {
            val matches = signatureCandidates
                    .filter { it.argTypes == argTypes }
            when (matches.size) {
                0 -> LangFunctionTableMatchNone
                1 -> LangFunctionTableMatchSingle(matches[0].symbol, matches[0].type)
                else -> LangFunctionTableMatchMultiple
            }
        } else LangFunctionTableMatchNone
    }
}