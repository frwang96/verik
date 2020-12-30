/*
 * Copyright (c) 2020 Francis Wang
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

/**
 * (UNIMPLEMENTED) Runtime configuration list. It may contain other runtime configuration lists or entries.
 */
class _rconf_list: _instance {

    /**
     * Name of the list.
     */
    val name: _string

    /**
     * (UNIMPLEMENTED) Returns the type of [_rconf_list]
     */
    constructor(): super() {
        this.name = ""
    }

    internal constructor(name: _string): super() {
        this.name = name
    }

    internal val lists = ArrayList<_rconf_list>()
    internal val entries = ArrayList<_rconf_entry>()

    /**
     * (UNIMPLEMENTED) Add runtime configuration entry.
     */
    fun add(entry: _rconf_entry) {
        entries.add(entry)
    }

    /**
     * (UNIMPLEMENTED) Add runtime configuration list.
     */
    fun add(list: _rconf_list) {
        lists.add(list)
    }
}

/**
 * (UNIMPLEMENTED) Constructs a new [_rconf_list].
 */
fun rconf_list(name: _string): _rconf_list {
    return _rconf_list(name)
}

/**
 * (UNIMPLEMENTED) Runtime configuration entry.
 */
class _rconf_entry: _instance {

    /**
     * Name of entry.
     */
    val name: _string

    /**
     * Data for runtime configuration.
     */
    val data: _data

    /**
     * Number of random seeds.
     */
    val count: _int

    /**
     * (UNIMPLEMENTED) Returns the type of [_rconf_entry].
     */
    constructor(): super() {
        this.name = ""
        this.data = 0
        this.count = 0
    }

    internal constructor(name: _string, value: _data, count: _int): super() {
        this.name = name
        this.data = value
        this.count = count
    }

    override fun equals(other: Any?): Boolean {
        return (other is _rconf_entry)
                && other.name == name
                && other.data == data
                && other.count == count
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + data.hashCode()
        result = 31 * result + count
        return result
    }
}

/**
 * (UNIMPLEMENTED) Constructs a new [_rconf_entry].
 */
fun rconf_entry(name: _string, data: _data, count: _int): _rconf_entry {
    return _rconf_entry(name, data, count)
}

/**
 * (UNIMPLEMENTED) Generate runtime configuration.
 */
fun rconf_generate(list: _rconf_list, type: _data) {
    _rconf_generator.generate(list, type)
}