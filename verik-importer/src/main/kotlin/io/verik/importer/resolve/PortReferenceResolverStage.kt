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

package io.verik.importer.resolve

import io.verik.importer.ast.element.EModule
import io.verik.importer.common.NullDeclaration
import io.verik.importer.common.TreeVisitor
import io.verik.importer.main.ImporterContext
import io.verik.importer.main.ImporterStage
import io.verik.importer.message.Messages

object PortReferenceResolverStage : ImporterStage() {

    override fun process(importerContext: ImporterContext) {
        importerContext.compilationUnit.accept(PortReferenceResolverVisitor)
    }

    private object PortReferenceResolverVisitor : TreeVisitor() {

        override fun visitModule(module: EModule) {
            super.visitModule(module)
            module.portReferences.forEach { portReference ->
                if (portReference.reference == NullDeclaration) {
                    val port = module.ports.find { it.name == portReference.name }
                    if (port != null) {
                        portReference.reference = port
                    } else {
                        Messages.INTERNAL_ERROR.on(module, "Unable to resolve port reference: ${portReference.name}")
                    }
                }
            }
        }
    }
}
