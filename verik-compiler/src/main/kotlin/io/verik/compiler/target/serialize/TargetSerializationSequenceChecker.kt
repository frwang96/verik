/*
 * SPDX-License-Identifier: Apache-2.0
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
