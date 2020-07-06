package com.verik.core

import com.verik.core.kt.KtAntlrException
import com.verik.core.kt.KtParseException
import com.verik.core.kt.KtRuleParser
import com.verik.core.sv.SvAssertionException
import com.verik.core.vk.*
import java.io.File
import kotlin.system.exitProcess

// Copyright (c) 2020 Francis Wang

const val VERSION = "1.0"

fun main(args: Array<String>) {
    val confPath = when (args.size) {
        0 -> "vkprojconf.yaml"
        1 -> args[1]
        else -> exitWithError("ERROR: project configuration file expected", null)
    }

    val conf = try {
        ProjConf(confPath)
    } catch (exception: Exception) {
        exitWithError("ERROR: ${exception.message}", exception)
    }

    try {
        val confFile = File(confPath)
        confFile.copyTo(conf.buildDir.resolve("vkprojconf.yaml"))
    } catch (exception: Exception) {
        exitWithError("ERROR: ${exception.message}", exception)
    }

    try {
        val output = getOutput(conf)
        conf.dstFile.parentFile.mkdirs()
        conf.dstFile.writeText(output)
    } catch (exception: Exception) {
        exitWithError("ERROR: ${exception.message}", exception)
    }

    try {
        val tcl = TclBuilder.build(conf)
        conf.vivado.tclFile.parentFile.mkdirs()
        conf.vivado.tclFile.writeText(tcl)
    } catch (exception: Exception) {
        exitWithError("ERROR: ${exception.message}", exception)
    }
}

private fun getOutput(conf: ProjConf): String {
    val txtFile = try {
        conf.srcFile.readText()
    } catch (exception: Exception) {
        exitWithError("ERROR: ${exception.message}", exception)
    }

    val ktFile = try {
        KtRuleParser.parseKotlinFile(txtFile)
    } catch (exception: KtAntlrException) {
        exitWithError("ERROR: ${exception.linePos} ${exception.message}", exception)
    } catch (exception: KtParseException) {
        exitWithError("ERROR: ${exception.linePos} ${exception.message}", exception)
    } catch (exception: Exception) {
        exitWithError("ERROR: ${exception.message}", exception)
    }

    val vkFile = try {
        VkFile(ktFile)
    } catch (exception: VkGrammarException) {
        exitWithError("ERROR: grammar assertion failure", exception)
    } catch (exception: VkParseException) {
        exitWithError("ERROR: ${exception.linePos} ${exception.message}", exception)
    } catch (exception: Exception) {
        exitWithError("ERROR: ${exception.message}", exception)
    }

    val svFile = try {
        vkFile.extract()
    } catch (exception: VkExtractException) {
        exitWithError("ERROR: ${exception.linePos} ${exception.message}", exception)
    } catch (exception: Exception) {
        exitWithError("ERROR: ${exception.message}", exception)
    }

    return try {
        val lines = txtFile.chars().filter { it == '\n'.toInt() }.count() + 1
        val labelLength = lines.toString().length
        val builder = SourceBuilder(conf, labelLength)
        svFile.build(builder)
        builder.toString()
    } catch (exception: SvAssertionException) {
        val message = if (exception.message == "") {
            "build assertion failure"
        } else exception.message
        exitWithError("ERROR: $message", exception)
    } catch (exception: Exception) {
        exitWithError("ERROR: ${exception.message}", exception)
    }
}

private fun exitWithError(message: String, exception: java.lang.Exception?): Nothing {
    print("\u001B[31m") // ANSI red
    print(message)
    print("\u001B[0m\n") // ANSI reset
    if (exception != null) {
        println("${exception::class.simpleName}:")
        for (trace in exception.stackTrace) {
            println("\t$trace")
        }
    }
    exitProcess(1)
}