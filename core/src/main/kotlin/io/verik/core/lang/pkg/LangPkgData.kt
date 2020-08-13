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

import io.verik.core.lang.*
import io.verik.core.lang.LangSymbol.FUN_BOOL_TYPE
import io.verik.core.lang.LangSymbol.FUN_SINT_TYPE
import io.verik.core.lang.LangSymbol.FUN_UINT_TYPE
import io.verik.core.lang.LangSymbol.TYPE_BOOL
import io.verik.core.lang.LangSymbol.TYPE_INT
import io.verik.core.lang.LangSymbol.TYPE_SINT
import io.verik.core.lang.LangSymbol.TYPE_UINT
import io.verik.core.svx.SvxType
import io.verik.core.vkx.VkxType

object LangPkgData: LangPkg {

    override fun load(
            typeTable: LangTypeTable,
            functionTable: LangFunctionTable
    ) {
        typeTable.add(LangType(
                TYPE_BOOL,
                { SvxType("logic", "", "") },
                "_bool"
        ))

        functionTable.add(LangFunction(
                FUN_BOOL_TYPE,
                listOf(),
                TYPE_BOOL,
                { VkxType(TYPE_BOOL, listOf()) },
                { null },
                "_bool"
        ))

        typeTable.add(LangType(
                TYPE_UINT,
                { SvxType("logic", LangExtractorUtil.extractDimensionPacked(it[0]), "") },
                "_uint"
        ))

        functionTable.add(LangFunction(
                FUN_UINT_TYPE,
                listOf(TYPE_INT),
                TYPE_UINT,
                { VkxType(TYPE_UINT, listOf(LangResolverUtil.extractInt(it[0]))) },
                { null },
                "_uint"
        ))

        typeTable.add(LangType(
                TYPE_SINT,
                { SvxType("logic signed", LangExtractorUtil.extractDimensionPacked(it[0]), "") },
                "_sint"
        ))

        functionTable.add(LangFunction(
                FUN_SINT_TYPE,
                listOf(TYPE_INT),
                TYPE_SINT,
                { VkxType(TYPE_SINT, listOf(LangResolverUtil.extractInt(it[0]))) },
                { null },
                "_sint"
        ))
    }
}