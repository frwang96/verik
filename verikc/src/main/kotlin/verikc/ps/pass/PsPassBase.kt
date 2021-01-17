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

package verikc.ps.pass

import verikc.ps.ast.*

abstract class PsPassBase {

    open fun pass(compilationUnit: PsCompilationUnit) {
        for (pkg in compilationUnit.pkgs) {
            for (file in pkg.files) {
                file.modules.forEach { passModule(it) }
                file.busses.forEach { passBus(it) }
                file.primaryProperties.forEach { passPrimaryProperty(it) }
                file.enums.forEach { passEnum(it) }
                file.clses.forEach { passCls(it) }
            }
        }
    }

    protected open fun passModule(module: PsModule) {}

    protected open fun passBus(bus: PsBus) {}

    protected open fun passPrimaryProperty(primaryProperty: PsPrimaryProperty) {}

    protected open fun passEnum(enum: PsEnum) {}

    protected open fun passCls(cls: PsCls) {}
}
