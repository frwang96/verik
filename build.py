#!/usr/bin/python3

#  Copyright (c) 2020 Francis Wang
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#      https://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.

import argparse
import os
import platform
import subprocess

import sys

examples_excluded = ["fizzbuzz"]
examples_unchecked = []


script_dir = os.path.dirname(os.path.realpath(__file__))
system_type = platform.system()
if system_type in ["Darwin", "Linux"]:
    gradlew_file = "gradlew"
    ansi_formatting = sys.stdout.isatty()
elif system_type == "Windows":
    gradlew_file = "gradlew.bat"
    ansi_formatting = False
else:
    raise OSError("operating system not supported")


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("task", metavar="TASK", help="clean build",
                        choices=["clean", "build"], nargs="*", default="build")
    args = parser.parse_args()
    print()

    example_dirs = [os.path.join(script_dir, "examples", d)
                    for d in os.listdir(os.path.join(script_dir, "examples"))
                    if os.path.isdir(os.path.join(script_dir, "examples", d))]
    mockup_dirs = [os.path.join(script_dir, "mockups", d)
                   for d in os.listdir(os.path.join(script_dir, "mockups"))
                   if os.path.isdir(os.path.join(script_dir, "mockups", d))]

    if "clean" in args.task:
        # clean examples and mockups
        verikc_dir = os.path.join(script_dir, "verikc/build/libs")
        if os.path.exists(verikc_dir):
            verikc_path = os.path.join(verikc_dir, os.listdir(verikc_dir)[0])
            for example_dir in example_dirs:
                print_header("clean", os.path.relpath(example_dir, script_dir))
                verikc(example_dir, verikc_path, ["clean"])
            for mockup_dir in mockup_dirs:
                print_header("clean", os.path.relpath(mockup_dir, script_dir))
                verikc(mockup_dir, verikc_path, ["clean"])
        else:
            for example_dir in example_dirs:
                print_header("clean", os.path.relpath(example_dir, script_dir))
                gradle(example_dir, ["clean"])
            for mockup_dir in mockup_dirs:
                print_header("clean", os.path.relpath(mockup_dir, script_dir))
                gradle(mockup_dir, ["clean"])

        # clean verik and verikc
        print_header("clean", "verik")
        gradle(os.path.join(script_dir, "verik"), ["clean"])
        print_header("clean", "verikc")
        gradle(os.path.join(script_dir, "verikc"), ["clean"])

    if "build" in args.task:
        # build verik and verikc
        print_header("build", "verik")
        gradle(os.path.join(script_dir, "verik"), ["build"])
        print_header("build", "verikc")
        gradle(os.path.join(script_dir, "verikc"), ["build"])

        # build examples and mockups
        verikc_dir = os.path.join(script_dir, "verikc/build/libs")
        verikc_path = os.path.join(verikc_dir, os.listdir(verikc_dir)[0])
        for example_dir in example_dirs:
            print_header("build", os.path.relpath(example_dir, script_dir))
            if os.path.basename(example_dir) not in examples_excluded:
                verikc(example_dir, verikc_path, ["all"])
                if os.path.basename(example_dir) not in examples_unchecked:
                    compare(example_dir)
            else:
                verikc(example_dir, verikc_path, ["headers", "gradle"])
        for mockup_dir in mockup_dirs:
            print_header("build", os.path.relpath(mockup_dir, script_dir))
            verikc(mockup_dir, verikc_path, ["headers", "gradle"])


def print_header(task, name):
    if ansi_formatting:
        print(u"\u001B[32m\u001B[1m", end="")  # ANSI green bold
        print("%s %s" % (task.upper(), name), end="")
        print(u"\u001B[0m\n", end="")  # ANSI reset
    else:
        print("%s %s" % (task.upper(), name))


def print_error(message):
    if ansi_formatting:
        print(u"\u001B[31m\u001B[1m", end="")  # ANSI red bold
        print("ERROR: " + message, end="")
        print(u"\u001B[0m\n", end="")  # ANSI reset
    else:
        print("ERROR: " + message)


def gradle(path, tasks):
    gradlew = os.path.join(path, gradlew_file)
    subprocess.run([gradlew, "-p", path, *tasks, "--console=plain"], check=True)
    print()


def verikc(path, verikc_path, tasks):
    vkproject = os.path.join(path, "vkproject.yaml")
    subprocess.run(["java", "-jar", verikc_path, "-c", vkproject, *tasks], check=True)


def compare(path):
    comp_path = os.path.join(path, "comp")
    out_path = os.path.join(path, "build/verik/out")
    file_name_set = set()
    for path, dirs, files in os.walk(comp_path):
        for file in files:
            file_name_set.add(os.path.relpath(os.path.join(path, file), comp_path))
    for path, dirs, files in os.walk(out_path):
        for file in files:
            file_name_set.add(os.path.relpath(os.path.join(path, file), out_path))
    for file_name in sorted(list(file_name_set)):
        comp_file_path = os.path.join(comp_path, file_name)
        out_file_path = os.path.join(out_path, file_name)
        if not os.path.exists(comp_file_path):
            print_error("(%s) could not find comp file" % file_name)
            print()
            exit(0)
        if not os.path.exists(out_file_path):
            print_error("(%s) could not find out file " % file_name)
            print()
            exit(0)
        compare_file(comp_file_path, out_file_path, file_name)


def compare_file(comp_file_path, out_file_path, file_name):
    with open(comp_file_path) as file:
        comp_lines = file.readlines()
    with open(out_file_path) as file:
        out_lines = file.readlines()[10:]
    lines = max(len(comp_lines), len(out_lines))
    for i in range(lines):
        comp_line = "" if i >= len(comp_lines) else comp_lines[i].strip()
        out_line = "" if i >= len(out_lines) else out_lines[i][14:].strip()
        if comp_line != out_line:
            print_error("(%s:%d)" % (file_name, i + 1))
            print("    expected : " + comp_line)
            print("    actual   : " + out_line)
            print()
            exit(0)


if __name__ == "__main__":
    main()
