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

@file:Suppress("UNUSED_PARAMETER")

package verik.data

import verik.base.*

operator fun _sbit.unaryPlus(): _sbit {
    throw VerikDslException()
}

operator fun _ubit.unaryPlus(): _ubit {
    throw VerikDslException()
}

operator fun _sbit.unaryMinus(): _sbit {
    throw VerikDslException()
}

operator fun _ubit.unaryMinus(): _ubit {
    throw VerikDslException()
}

operator fun _sbit.inc(): _sbit {
    throw VerikDslException()
}

operator fun _ubit.inc(): _ubit {
    throw VerikDslException()
}

operator fun _sbit.dec(): _sbit {
    throw VerikDslException()
}

operator fun _ubit.dec(): _ubit {
    throw VerikDslException()
}

operator fun _sbit.not(): _bool {
    throw VerikDslException()
}

operator fun _ubit.not(): _bool {
    throw VerikDslException()
}

operator fun _sbit.plus(x: _sbit): _sbit {
    throw VerikDslException()
}

operator fun _sbit.plus(x: _ubit): _ubit {
    throw VerikDslException()
}

operator fun _ubit.plus(x: _sbit): _ubit {
    throw VerikDslException()
}

operator fun _ubit.plus(x: _ubit): _ubit {
    throw VerikDslException()
}

operator fun _sbit.minus(x: _sbit): _sbit {
    throw VerikDslException()
}

operator fun _sbit.minus(x: _ubit): _ubit {
    throw VerikDslException()
}

operator fun _ubit.minus(x: _sbit): _ubit {
    throw VerikDslException()
}

operator fun _ubit.minus(x: _ubit): _ubit {
    throw VerikDslException()
}

operator fun _sbit.times(x: _sbit): _sbit {
    throw VerikDslException()
}

operator fun _sbit.times(x: _ubit): _ubit {
    throw VerikDslException()
}

operator fun _ubit.times(x: _sbit): _ubit {
    throw VerikDslException()
}

operator fun _ubit.times(x: _ubit): _ubit {
    throw VerikDslException()
}

operator fun _sbit.rem(x: _sbit): _sbit {
    throw VerikDslException()
}

operator fun _sbit.rem(x: _ubit): _ubit {
    throw VerikDslException()
}

operator fun _ubit.rem(x: _sbit): _ubit {
    throw VerikDslException()
}

operator fun _ubit.rem(x: _ubit): _ubit {
    throw VerikDslException()
}

operator fun _sbit.div(x: _sbit): _sbit {
    throw VerikDslException()
}

operator fun _sbit.div(x: _ubit): _ubit {
    throw VerikDslException()
}

operator fun _ubit.div(x: _sbit): _ubit {
    throw VerikDslException()
}

operator fun _ubit.div(x: _ubit): _ubit {
    throw VerikDslException()
}

operator fun _sbit.compareTo(x: _sbit): _int {
    throw VerikDslException()
}

operator fun _ubit.compareTo(x: _ubit): _int {
    throw VerikDslException()
}