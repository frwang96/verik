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

package io.verik.compiler.transform.mid

import io.verik.compiler.common.ProjectPass
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.m

object ProjectMidTransformer : ProjectPass {

    override fun pass(projectContext: ProjectContext) {
        m.log("MidTransform: Reduce string template expressions")
        StringTemplateExpressionReducer.pass(projectContext)
        m.flush()

        m.log("MidTransform: Transform bit constants")
        BitConstantTransformer.pass(projectContext)
        m.flush()

        m.log("MidTransform: Transform assignments")
        AssignmentTransformer.pass(projectContext)
        m.flush()

        m.log("MidTransform: Transform loop expressions")
        LoopExpressionTransformer.pass(projectContext)
        m.flush()
    }
}