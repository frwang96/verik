/*
 * Copyright (c) 2021 Francis Wang
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

import verik.base.*
import verik.data.*

class _test: _module() {

    @busport val arb_bp = _arb_test_bp()

    @run fun test() {
        wait(arb_bp.cp)
        arb_bp.cp.request = ubit(0x01)
        println("@${time()}: Drove req")
        repeat(2) { wait(arb_bp.cp) }
        if (arb_bp.cp.grant == ubit(0x01)) {
            println("@${time()}: Success")
        } else {
            println("@${time()}: Error")
        }
    }
}