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

package io.verik.core.lang.pkg

import io.verik.core.lang.LangFunctionSignature
import io.verik.core.lang.LangFunctionTable
import io.verik.core.lang.LangSymbol.FUN_BOOL_INVOKE
import io.verik.core.lang.LangSymbol.FUN_SINT_INVOKE
import io.verik.core.lang.LangSymbol.FUN_UINT_INVOKE
import io.verik.core.lang.LangSymbol.TYPE_BOOL
import io.verik.core.lang.LangSymbol.TYPE_INT
import io.verik.core.lang.LangSymbol.TYPE_SINT
import io.verik.core.lang.LangSymbol.TYPE_UINT

object LangPkgData: LangPkg {

    override fun load(functionTable: LangFunctionTable) {
        functionTable.add(LangFunctionSignature(
                FUN_BOOL_INVOKE,
                "_bool",
                listOf(),
                TYPE_BOOL
        ))

        functionTable.add(LangFunctionSignature(
                FUN_UINT_INVOKE,
                "_uint",
                listOf(TYPE_INT),
                TYPE_UINT
        ))

        functionTable.add(LangFunctionSignature(
                FUN_SINT_INVOKE,
                "_sint",
                listOf(TYPE_INT),
                TYPE_SINT
        ))
    }
}