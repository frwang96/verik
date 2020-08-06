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

package io.verik.stubs

internal class StubExpander {

    companion object {

        fun expand(stubs: List<Stub>): List<StubEntry> {
            for (stub in stubs) {
                if (stub.name in listOf("all", "none") || stub.name.matches(Regex("SEED_[0-9a-f]{8}"))) {
                    throw IllegalArgumentException("stub name ${stub.name} is reserved")
                }
            }

            val stubEntries = ArrayList<StubEntry>()
            expandRecursive(stubs, "", stubEntries)
            return stubEntries
        }

        private fun expandRecursive(stubs: List<Stub>, base: String, stubEntries: ArrayList<StubEntry>) {
            for (stub in stubs) {
                validateName(stub.name)
                val name = if (base == "") stub.name else "$base/${stub.name}"
                when (stub) {
                    is StubEntry -> {
                        stubEntries.add(StubEntry(name, stub.instance, stub.count))
                    }
                    is StubList -> {
                        expandRecursive(stub.stubs, name, stubEntries)
                    }
                }
            }
        }

        private fun validateName(name: String) {
            if (name == "") throw IllegalArgumentException("stub name cannot be empty")
            if (name.any { it.isWhitespace() }) {
                throw IllegalArgumentException("stub name \"$name\" cannot contain whitespace")
            }
            if (!name.all { it.isLetterOrDigit() || it in listOf('-', '_') }) {
                throw IllegalArgumentException("illegal characters in stub name \"$name\"")
            }
        }
    }
}