/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.core

object Core {

    val C_Unit = CoreClassDeclaration("Unit")
    val C_Nothing = CoreClassDeclaration("Nothing")
    val C_Class = CoreClassDeclaration("Class")
    val C_Int = CoreClassDeclaration("Int")
    val C_Boolean = CoreClassDeclaration("Boolean")
    val C_String = CoreClassDeclaration("String")

    val C_Ubit = CoreClassDeclaration("Ubit")
    val C_Sbit = CoreClassDeclaration("Sbit")
    val C_Packed = CoreClassDeclaration("Packed")
    val C_Unpacked = CoreClassDeclaration("Unpacked")
    val C_Time = CoreClassDeclaration("Time")

    val C_Struct = CoreClassDeclaration("Struct")
    val C_Module = CoreClassDeclaration("Module")

    val C_Queue = CoreClassDeclaration("Queue")
    val C_DynamicArray = CoreClassDeclaration("DynamicArray")
    val C_AssociativeArray = CoreClassDeclaration("AssociativeArray")

    val T_ADD = CoreCardinalFunctionDeclaration("ADD")
    val T_SUB = CoreCardinalFunctionDeclaration("SUB")
}
