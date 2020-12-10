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

@file:Suppress("unused")

package verik.rconf

import verik.base.*
import verik.data.*

class _rconf_list internal constructor(val name: _string) {
    internal val lists = ArrayList<_rconf_list>()
    internal val entries = ArrayList<_rconf_entry>()

    fun add(entry: _rconf_entry) {
        entries.add(entry)
    }

    fun add(list: _rconf_list) {
        lists.add(list)
    }
}

fun rconf_list(name: _string): _rconf_list {
    return _rconf_list(name)
}

class _rconf_entry internal constructor(
        val name: _string,
        val value: _any,
        val count: _int
) {

    override fun equals(other: _any?): Boolean {
        return (other is _rconf_entry)
                && other.name == name
                && other.value == value
                && other.count == count
    }

    override fun hashCode(): _int {
        var result = name.hashCode()
        result = 31 * result + value.hashCode()
        result = 31 * result + count
        return result
    }
}

fun rconf_entry(name: _string, value: _any, count: _int): _rconf_entry {
    return _rconf_entry(name, value, count)
}

fun rconf_generate(list: _rconf_list, reference: _any) {
    _rconf_generator.generate(list, reference)
}