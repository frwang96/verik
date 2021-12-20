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

package io.verik.compiler.target.serialize

import io.verik.compiler.target.common.CompositeTarget
import io.verik.compiler.target.common.Target
import io.verik.compiler.target.common.TargetDeclaration
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties

object TargetSerializationSequenceChecker {

    fun checkSequence(sequence: TargetSerializationSequence) {
        val entriesTargetSet = HashSet<CompositeTarget>()
        sequence.entries
            .flatMap { it.getCompositeTargets() }
            .forEach {
                if (it in entriesTargetSet)
                    throw IllegalArgumentException("Repeated target declaration: ${it.name}")
                entriesTargetSet.add(it)
            }

        val compositeTargetSet = HashSet<CompositeTarget>()
        addTargetDeclarations(compositeTargetSet, Target::class)

        val missingTargetSet = compositeTargetSet.subtract(entriesTargetSet)
        if (missingTargetSet.isNotEmpty()) {
            throw IllegalArgumentException("Missing target declaration: ${missingTargetSet.first().name}")
        }
    }

    private fun addTargetDeclarations(compositeTargetSet: HashSet<CompositeTarget>, kClass: KClass<*>) {
        val kClassInstance = kClass.objectInstance!!
        kClass.declaredMemberProperties.forEach {
            @Suppress("UNCHECKED_CAST")
            val targetDeclaration = (it as KProperty1<Any, *>).get(kClassInstance) as TargetDeclaration
            if (targetDeclaration is CompositeTarget)
                compositeTargetSet.add(targetDeclaration)
        }
        kClass.nestedClasses.forEach { addTargetDeclarations(compositeTargetSet, it) }
    }
}
