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

package verikc.ge

import verikc.ge.ast.GeCompilationUnit
import verikc.rs.RsResolveUtil

object GeGenerifyUtil {

    fun generifyCompilationUnit(string: String): GeCompilationUnit {
        val compilationUnit = GeStageDriver.build(RsResolveUtil.resolveCompilationUnit(string))
        GeStageDriver.generify(compilationUnit)
        return compilationUnit
    }
}