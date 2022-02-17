/*
 * Copyright (c) 2022 Francis Wang
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

// test.kt /////////////////////////////////////////////////////////////////////////////////////////////////////////////

package imported

import io.verik.core.*

@Task
fun t0(
    x0: Boolean
): Unit = imp()

open class c : Class() {

    @Task
    open fun t1(
        x1: Int
    ): Unit = imp()

    @Task
    open fun t2(
        x2: Ubit<`4`>,
        x3: Boolean
    ): Unit = imp()

    @Task
    open fun t3(
        x4: Int,
        x5: Int
    ): Unit = imp()
}
