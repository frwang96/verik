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

/**
 * (UNIMPLEMENTED) Runtime configuration list. It may contain other runtime configuration lists or entries.
 */
class _rconf_list: _class {

    /**
     * Name of the runtime configuration list.
     */
    val name: _string

    /**
     * (UNIMPLEMENTED) Type constructor for [_rconf_list]
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
     * (UNIMPLEMENTED) Constructs a new [_rconf_list].
     */
    fun init(name: _string): _rconf_list {
        return _rconf_list(name)
    }

    /**
     * (UNIMPLEMENTED) Add runtime configuration [entry].
     */
    fun add(entry: _rconf_entry) {
        entries.add(entry)
    }

    /**
     * (UNIMPLEMENTED) Add runtime configuration [list].
     */
    fun add(list: _rconf_list) {
        lists.add(list)
    }
}

fun rconf_list(name: _string): _rconf_list {
    return _rconf_list().init(name)
}

/**
 * (UNIMPLEMENTED) Runtime configuration entry.
 */
class _rconf_entry: _class {

    /**
     * Name of the runtime configuration entry.
     */
    val name: _string

    /**
     * Value to set the runtime configuration.
     */
    val value: _any

    /**
     * Number of random seeds to generate.
     */
    val count: _int

    /**
     * (UNIMPLEMENTED) Type constructor for [_rconf_entry].
     */
    constructor(): super() {
        this.name = ""
        this.value = 0
        this.count = 0
    }

    internal constructor(name: _string, value: _any, count: _int): super() {
        this.name = name
        this.value = value
        this.count = count
    }

    /**
     * (UNIMPLEMENTED) Constructs a new [_rconf_entry].
     */
    fun init(name: _string, value: _any, count: _int): _rconf_entry {
        return _rconf_entry(name, value, count)
    }

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

/**
 * (UNIMPLEMENTED) Generate runtime configuration with runtime configuration [list] and [reference] type.
 */
fun rconf_generate(list: _rconf_list, reference: _any) {
    _rconf_generator.generate(list, reference)
}