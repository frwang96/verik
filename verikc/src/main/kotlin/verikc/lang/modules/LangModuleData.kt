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

package verikc.lang.modules

import verikc.base.ast.LineException
import verikc.base.ast.ReifiedType
import verikc.base.ast.TypeClass.INSTANCE
import verikc.base.ast.TypeClass.TYPE
import verikc.lang.LangEntryList
import verikc.lang.LangSymbol.TYPE_SBIT
import verikc.lang.LangSymbol.FUNCTION_TYPE_BOOL
import verikc.lang.LangSymbol.FUNCTION_TYPE_INT
import verikc.lang.LangSymbol.FUNCTION_TYPE_SBIT
import verikc.lang.LangSymbol.FUNCTION_TYPE_UBIT
import verikc.lang.LangSymbol.TYPE_BOOL
import verikc.lang.LangSymbol.TYPE_DATA
import verikc.lang.LangSymbol.TYPE_INSTANCE
import verikc.lang.LangSymbol.TYPE_INT
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.lang.extract.LangExtractorUtil
import verikc.lang.reify.LangReifierUtil
import verikc.sv.ast.SvExtractedType

object LangModuleData: LangModule {

    override fun load(list: LangEntryList) {
        list.addType(
                "_data",
                TYPE_INSTANCE,
                { null },
                TYPE_DATA
        )

        list.addType(
                "_bool",
                TYPE_DATA,
                { SvExtractedType("logic", "", "") },
                TYPE_BOOL
        )

        list.addFunction(
                "_bool",
                null,
                listOf(),
                listOf(),
                TYPE_BOOL,
                { ReifiedType(TYPE_BOOL, TYPE, listOf()) },
                { null },
                FUNCTION_TYPE_BOOL
        )

        list.addType(
                "_int",
                TYPE_DATA,
                { SvExtractedType("integer", "", "") },
                TYPE_INT
        )

        list.addFunction(
                "_int",
                null,
                listOf(),
                listOf(),
                TYPE_INT,
                { ReifiedType(TYPE_INT, TYPE, listOf()) },
                { null },
                FUNCTION_TYPE_INT
        )

        list.addType(
                "_ubit",
                TYPE_DATA,
                { SvExtractedType("logic", LangExtractorUtil.toPacked(it.args[0]), "" ) },
                TYPE_UBIT
        )

        list.addFunction(
                "_ubit",
                null,
                listOf(TYPE_INT),
                listOf(INSTANCE),
                TYPE_UBIT,
                {
                    val width = LangReifierUtil.toInt(it.args[0])
                    if (width == 0) throw LineException("width of ubit cannot be 0", it)
                    ReifiedType(TYPE_UBIT, TYPE, listOf(width))
                },
                { null },
                FUNCTION_TYPE_UBIT
        )

        list.addType(
                "_sbit",
                TYPE_DATA,
                { SvExtractedType( "logic signed", LangExtractorUtil.toPacked(it.args[0]), "" ) },
                TYPE_SBIT
        )

        list.addFunction(
                "_sbit",
                null,
                listOf(TYPE_INT),
                listOf(INSTANCE),
                TYPE_SBIT,
                {
                    val width = LangReifierUtil.toInt(it.args[0])
                    if (width == 0) throw LineException("width of sbit cannot be 0", it)
                    ReifiedType(TYPE_SBIT, TYPE, listOf(width))
                },
                { null },
                FUNCTION_TYPE_SBIT
        )
    }
}
