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

internal class _rconf_expander {

    companion object {

        fun expand(list: _rconf_list): List<_rconf_entry> {
            val entries = ArrayList<_rconf_entry>()
            validate_name(list.name)
            expand_recursive(list, list.name, entries)
            return entries
        }

        private fun expand_recursive(list: _rconf_list, base: String, entries: ArrayList<_rconf_entry>) {
            for (child_list in list.lists) {
                validate_name(child_list.name)
                expand_recursive(child_list, "$base/${child_list.name}", entries)
            }
            for (entry in list.entries) {
                validate_name(entry.name)
                entries.add(rconf_entry("$base/${entry.name}", entry.data, entry.count))
            }
        }

        private fun validate_name(name: String) {
            if (name == "") throw IllegalArgumentException("name cannot be empty")
            if (name in listOf("all", "none") || name.matches(Regex("SEED_[0-9a-f]{8}"))) {
                throw IllegalArgumentException("name \"$name\" is reserved")
            }
            if (name.any { it.isWhitespace() }) {
                throw IllegalArgumentException("name \"$name\" cannot contain whitespace")
            }
            if (!name.all { it.isLetterOrDigit() || it in listOf('-', '_') }) {
                throw IllegalArgumentException("illegal characters in name \"$name\"")
            }
        }
    }
}