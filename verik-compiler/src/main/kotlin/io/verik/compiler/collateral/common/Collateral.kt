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

package io.verik.compiler.collateral.common

import io.verik.compiler.collateral.declaration.CollateralSystem

object Collateral {

    object System {

        val F_display = CollateralSystem.F_display
        val F_write = CollateralSystem.F_write
        val F_sformatf = CollateralSystem.F_sformatf
        val F_random = CollateralSystem.F_random
        val F_urandom = CollateralSystem.F_urandom
        val F_urandom_range = CollateralSystem.F_urandom_range
        val F_unsigned = CollateralSystem.F_unsigned
        val F_signed = CollateralSystem.F_signed
        val F_time = CollateralSystem.F_time
        val F_fatal = CollateralSystem.F_fatal
        val F_finish = CollateralSystem.F_finish
        val F_new = CollateralSystem.F_new
        val F_wait = CollateralSystem.F_wait
        val F_name = CollateralSystem.F_name
        val F_rsort = CollateralSystem.F_rsort
    }
}
