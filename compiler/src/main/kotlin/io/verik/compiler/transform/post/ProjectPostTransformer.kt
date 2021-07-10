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

package io.verik.compiler.transform.post

import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.m

object ProjectPostTransformer {

    fun transform(projectContext: ProjectContext) {
        m.log("Post-transform: Reduce binary expressions")
        BinaryExpressionReducer.transform(projectContext)
        m.flush()

        m.log("Post-transform: Reduce string template expressions")
        StringTemplateExpressionReducer.transform(projectContext)
        m.flush()

        m.log("Post-transform: Transform assignments")
        AssignmentTransformer.transform(projectContext)
        m.flush()

        m.log("Post-transform: Transform function references")
        FunctionReferenceTransformer.transform(projectContext)
        m.flush()

        m.log("Post-transform: Transform loop expressions")
        LoopExpressionTransformer.transform(projectContext)
        m.flush()

        m.log("Post-transform: Transform binary expressions")
        BinaryExpressionTransformer.transform(projectContext)
        m.flush()

        m.log("Post-transform: Transform package names")
        PackageNameTransformer.transform(projectContext)
        m.flush()

        m.log("Post-transform: Decorate block expressions")
        BlockExpressionDecorator.transform(projectContext)
        m.flush()
    }
}