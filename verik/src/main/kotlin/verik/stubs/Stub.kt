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

package verik.stubs

import verik.base.*

class _stub_list(val name: String) {
    val lists = ArrayList<_stub_list>()
    val entries = ArrayList<_stub_entry>()

    fun add(entry: _stub_entry) {
        entries.add(entry)
    }

    fun add(list: _stub_list) {
        lists.add(list)
    }
}

fun stub_list(name: String): _stub_list {
    return _stub_list(name)
}

class _stub_entry {
    var name = ""
    var config: Any = 0
    var count = 0

    override fun equals(other: Any?): Boolean {
        return (other is _stub_entry)
                && other.name == name
                && other.config == config
                && other.count == count
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + config.hashCode()
        result = 31 * result + count
        return result
    }
}

fun stub_entry(name: String, config: Any, count: Int): _stub_entry {
    val entry = _stub_entry()
    entry.name = name
    entry.config = config
    entry. count = count
    return entry
}

fun generate_stubs(args: Array<String>, list: _stub_list, reference: _any) {
    StubWriter.writeStubs(args, list, reference)
}