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
