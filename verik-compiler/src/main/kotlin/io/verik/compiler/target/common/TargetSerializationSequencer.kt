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

package io.verik.compiler.target.common

import io.verik.compiler.main.ProjectContext
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties

object TargetSerializationSequencer {

    fun getEntries(projectContext: ProjectContext): List<TargetSerializationEntry> {
        val entries = ArrayList<TargetSerializationEntry>()
        entries.add(
            Target.C_ArrayList,
            Target.ArrayList.F_new,
            Target.ArrayList.F_add,
            Target.ArrayList.F_get,
            Target.ArrayList.F_set,
            Target.ArrayList.F_size
        )
        if (projectContext.config.debug)
            checkEntries(entries)
        return entries
    }

    private fun checkEntries(entries: List<TargetSerializationEntry>) {
        val entriesTargetSet = HashSet<CompositeTarget>()
        entries.forEach { entry ->
            if (entry.targetClassDeclaration in entriesTargetSet)
                throw IllegalArgumentException("Repeated target declaration: ${entry.targetClassDeclaration.name}")
            entriesTargetSet.add(entry.targetClassDeclaration)
            entry.compositeTargets.forEach {
                if (it in entriesTargetSet)
                    throw IllegalArgumentException("Repeated target declaration: ${it.name}")
                entriesTargetSet.add(it)
            }
        }

        val targetSet = HashSet<CompositeTarget>()
        addTargetDeclarations(targetSet, Target::class)

        val missingTargetSet = targetSet.subtract(entriesTargetSet)
        if (missingTargetSet.isNotEmpty()) {
            throw IllegalArgumentException("Missing target declaration: ${missingTargetSet.first().name}")
        }
    }

    private fun addTargetDeclarations(targetSet: HashSet<CompositeTarget>, kClass: KClass<*>) {
        val kClassInstance = kClass.objectInstance!!
        kClass.declaredMemberProperties.forEach {
            @Suppress("UNCHECKED_CAST")
            val targetDeclaration = (it as KProperty1<Any, *>).get(kClassInstance) as TargetDeclaration
            if (targetDeclaration is CompositeTarget)
                targetSet.add(targetDeclaration)
        }
        kClass.nestedClasses.forEach { addTargetDeclarations(targetSet, it) }
    }
}
