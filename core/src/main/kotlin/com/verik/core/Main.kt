package com.verik.core

import com.verik.core.kt.KtRuleParser
import com.verik.core.vk.VkFile
import java.io.File
import kotlin.system.exitProcess

// Copyright (c) 2020 Francis Wang

const val VERSION = "1.0"

data class CommandArgs(
        val onlyHeaders: Boolean,
        val confPath: String
) {

    companion object {

        operator fun invoke(args: Array<String>): CommandArgs {
            var onlyHeaders = false
            var confPath = "vkprojconf.yaml"
            var confSpecified = false

            for (arg in args) {
                when (arg) {
                    "-h" -> onlyHeaders = true
                    else -> {
                        if (arg[0] == '-' || confSpecified) {
                            println("usage: verik [-h] [<projconf>]")
                            exitProcess(1)
                        } else {
                            confPath = arg
                            confSpecified = true
                        }
                    }
                }
            }

            return CommandArgs(onlyHeaders, confPath)
        }
    }
}

data class BuildOutput(val string: String, val top: String)

fun main(args: Array<String>) {
    val commandArgs = CommandArgs(args)

    StatusPrinter.info("loading project configuration file ${commandArgs.confPath}")
    val conf = try {
        ProjConf(commandArgs.confPath)
    } catch (exception: Exception) {
        StatusPrinter.error(exception.message, exception)
    }

    try {
        if (conf.buildDir.exists()) {
            conf.buildDir.deleteRecursively()
        }
        val confFile = File(commandArgs.confPath)
        confFile.copyTo(conf.buildDir.resolve("vkprojconf.yaml"))
    } catch (exception: Exception) {
        StatusPrinter.error(exception.message, exception)
    }

    StatusPrinter.info("processing source file ${conf.srcFile.relativeTo(conf.projDir)}")
    val top = try {
        val buildOutput = getBuildOutput(conf)
        conf.dstFile.parentFile.mkdirs()
        conf.dstFile.writeText(buildOutput.string)
        buildOutput.top
    } catch (exception: Exception) {
        StatusPrinter.error(exception.message, exception)
    }

    StatusPrinter.info("generating tcl file ${conf.vivado.tclFile.relativeTo(conf.projDir)}")
    try {
        val tcl = TclBuilder.build(conf, top)
        conf.vivado.tclFile.parentFile.mkdirs()
        conf.vivado.tclFile.writeText(tcl)
    } catch (exception: Exception) {
        StatusPrinter.error(exception.message, exception)
    }
}

private fun getBuildOutput(conf: ProjConf): BuildOutput {
    val txtFile = try {
        conf.srcFile.readText()
    } catch (exception: Exception) {
        StatusPrinter.error(exception.message, exception)
    }

    val ktFile = try {
        KtRuleParser.parseKotlinFile(txtFile)
    } catch (exception: Exception) {
        StatusPrinter.error(exception.message, exception)
    }

    val vkFile = try {
        VkFile(ktFile)
    } catch (exception: Exception) {
        StatusPrinter.error(exception.message, exception)
    }

    val svFile = try {
        vkFile.extract()
    } catch (exception: Exception) {
        StatusPrinter.error(exception.message, exception)
    }

    return try {
        val lines = txtFile.count{ it == '\n' } + 1
        val labelLength = lines.toString().length
        val builder = SourceBuilder(conf, labelLength)
        svFile.build(builder)
        BuildOutput(builder.toString(), vkFile.top.identifier.drop(1))
    } catch (exception: Exception) {
        StatusPrinter.error(exception.message, exception)
    }
}
