package com.verik.core

import java.io.File

// Copyright (c) 2020 Francis Wang

enum class Construct {
    MODULE, INTF, PORT, ENUM, STRUCT, CLASS
}

fun main(args: Array<String>) {
    val regexPackage = Regex("^package *([\\w.]+)")
    val regexConstruct = Regex("class +(\\w+)[\\s\\S]*?([\\w]+) *(\\(.*\\)|) *\\{")

    for (root in args) {
        var packageName: String? = null
        val constructs = ArrayList<Pair<Construct, String>>()
        File(root).listFiles { _, name -> name.endsWith(".kt") && name != "header.kt" } ?.forEach { it ->
            val lines = it.readText()
            val result = regexPackage.find(lines)?.groups?.get(1)
            if (result != null) {
                packageName = result.value
            }
            regexConstruct.findAll(lines).forEach {
                val name = it.groups[1]?.value
                val construct = when (it.groups[2]?.value) {
                    "_module", "_circuit" -> Construct.MODULE
                    "_intf" -> Construct.INTF
                    "_port" -> Construct.PORT
                    "_enum" -> Construct.ENUM
                    "_struct" -> Construct.STRUCT
                    else -> Construct.CLASS
                }
                if (name != null && construct != null) {
                    constructs.add(construct to name)
                }
            }
        }

        if (packageName != null) {
            val writer = File(root, "header.kt").bufferedWriter()
            writer.write("@file:Suppress(\"UNUSED_PARAMETER\", \"unused\", \"UnusedImport\")\n\n")
            writer.write("package $packageName\n\n")
            writer.write("import com.verik.common._bits\n\n")
            writer.write("// generated by verik\n\n")
            var lastIntf: String? = null
            for (i in 0 until constructs.size) {
                val (construct, name) = constructs[i]
                val fullName = if (construct == Construct.PORT) {
                    if (lastIntf != null) {
                        "$lastIntf.$name"
                    } else null
                } else name
                if (construct == Construct.INTF) {
                    lastIntf = name
                }

                if (fullName != null) {
                    when (construct) {
                        Construct.MODULE -> {
                            writer.write("infix fun $fullName.con(block: ($fullName) -> Unit) = this\n")
                        }
                        Construct.INTF -> {
                            writer.write("infix fun $fullName.con(block: ($fullName) -> Unit) = this\n")
                            writer.write("infix fun $fullName.con(x: $fullName?) {}\n")
                        }
                        Construct.PORT -> {
                            writer.write("infix fun $fullName.con(x: $fullName?) {}\n")
                        }
                        Construct.ENUM -> {
                            writer.write("infix fun $fullName.con(x: $fullName?) {}\n")
                            writer.write("infix fun $fullName.set(x: $fullName?) = this\n")
                            writer.write("infix fun $fullName.put(x: $fullName?) {}\n")
                            writer.write("fun _bits.unpack(x: $fullName) = x\n")
                            writer.write("fun $fullName() = $fullName.values()[0]\n")
                        }
                        Construct.STRUCT -> {
                            writer.write("infix fun $fullName.con(x: $fullName?) {}\n")
                            writer.write("infix fun $fullName.set(x: $fullName?) = this\n")
                            writer.write("infix fun $fullName.put(x: $fullName?) {}\n")
                            writer.write("fun _bits.unpack(x: $fullName) = x\n")
                        }
                        Construct.CLASS -> {
                            writer.write("infix fun $fullName.set(x: $fullName?) = this\n")
                            writer.write("fun $fullName.randomize(block: ($fullName) -> Unit) {}\n")
                        }
                    }
                }
            }
            writer.close()
        }
    }
}
