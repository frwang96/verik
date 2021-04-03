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
 * (UNIMPLEMENTED) Runtime configuration list. It may contain other runtime configuration [lists][RconfList] or
 * [entries][RconfEntry].
 */
class RconfList: Class {

    /**
     * Name of the list.
     */
    val name: String

    constructor(): super() {
        this.name = ""
    }

    internal constructor(name: String): super() {
        this.name = name
    }

    internal val lists = ArrayList<RconfList>()
    internal val entries = ArrayList<RconfEntry>()

    /**
     * (UNIMPLEMENTED) Add a runtime configuration [entry][RconfEntry].
     * @param entry the entry to add
     */
    fun add(entry: RconfEntry) {
        entries.add(entry)
    }

    /**
     * (UNIMPLEMENTED) Add runtime configuration [list][RconfList].
     * @param list the list to add
     */
    fun add(list: RconfList) {
        lists.add(list)
    }
}

/**
 * (UNIMPLEMENTED) Returns a new [RconfList].
 * @param name the name of the list
 */
@Suppress("FunctionName")
fun i_RconfList(name: String): RconfList {
    return RconfList(name)
}

/**
 * (UNIMPLEMENTED) Runtime configuration entry.
 */
class RconfEntry: Class {

    /**
     * Name of the entry.
     */
    val name: String

    /**
     * Data for the runtime configuration.
     */
    val data: Data

    /**
     * The number of random seeds.
     */
    val count: Int

    constructor(): super() {
        this.name = ""
        this.data = 0
        this.count = 0
    }

    internal constructor(name: String, value: Data, count: Int): super() {
        this.name = name
        this.data = value
        this.count = count
    }

    override fun equals(other: Any?): Boolean {
        return (other is RconfEntry)
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
 * (UNIMPLEMENTED) Returns a new [RconfEntry].
 * @param name the name of the entry
 * @param data the data for the runtime configuration
 * @param count the number of random seeds
 */
@Suppress("FunctionName")
fun i_RconfEntry(name: String, data: Data, count: Int): RconfEntry {
    return RconfEntry(name, data, count)
}

/**
 * (UNIMPLEMENTED) Generate the runtime configuration.
 * @param list the list to generate
 * @param type the type of the runtime configuration
 */
fun rconf_generate(list: RconfList, type: Data) {
    RconfGenerator.generate(list, type)
}