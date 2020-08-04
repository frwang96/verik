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

import io.verik.common.data.*

sealed class Stub(open val name: String)

data class StubList(override val name: String, val stubs: List<Stub>): Stub(name)

data class StubEntry(override val name: String, val config: _data, val count: Int): Stub(name) {

    constructor(name: String, config: _data): this(name, config, 0)
}

fun writeStubs(args: Array<String>, type: _data, stubs: List<Stub>) {
    StubWriter.writeStubs(args, type, stubs)
}