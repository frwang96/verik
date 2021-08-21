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

import io.verik.compiler.common.ProjectPass
import io.verik.compiler.main.ProjectContext

object ProjectPostTransformer : ProjectPass {

    override fun pass(projectContext: ProjectContext) {
        LoopExpressionTransformer.accept(projectContext)
        InlineIfExpressionTransformer.accept(projectContext)
        FunctionReferenceTransformer.accept(projectContext)
        FunctionSpecialTransformer.accept(projectContext)
        UnaryExpressionTransformer.accept(projectContext)
        BinaryExpressionTransformer.accept(projectContext)
        PackageNameTransformer.accept(projectContext)
        PackageReferenceInsertionTransformer.accept(projectContext)
        ReferenceAndCallExpressionTransformer.accept(projectContext)
        BlockExpressionTransformer.accept(projectContext)
        ParenthesisInsertionTransformer.accept(projectContext)
    }
}