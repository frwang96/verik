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

package verik.rconf

internal class StubExpander {

    companion object {

        fun expand(list: _rconf_list): List<_rconf_entry> {
            val entries = ArrayList<_rconf_entry>()
            validateName(list.name)
            expandRecursive(list, list.name, entries)
            return entries
        }

        private fun expandRecursive(list: _rconf_list, base: String, entries: ArrayList<_rconf_entry>) {
            for (child_list in list.lists) {
                validateName(child_list.name)
                expandRecursive(child_list, "$base/${child_list.name}", entries)
            }
            for (entry in list.entries) {
                validateName(entry.name)
                entries.add(rconf_entry("$base/${entry.name}", entry.config, entry.count))
            }
        }

        private fun validateName(name: String) {
            if (name == "") throw IllegalArgumentException("stub name cannot be empty")
            if (name in listOf("all", "none") || name.matches(Regex("SEED_[0-9a-f]{8}"))) {
                throw IllegalArgumentException("stub name \"$name\" is reserved")
            }
            if (name.any { it.isWhitespace() }) {
                throw IllegalArgumentException("stub name \"$name\" cannot contain whitespace")
            }
            if (!name.all { it.isLetterOrDigit() || it in listOf('-', '_') }) {
                throw IllegalArgumentException("illegal characters in stub name \"$name\"")
            }
        }
    }
}