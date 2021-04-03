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

examples_excluded = []


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
        print_header("publish", "verik")
        gradle(os.path.join(script_dir, "verik"), ["publishToMavenLocal"])
        print_header("build", "verikc")
        gradle(os.path.join(script_dir, "verikc"), ["build"])

        # build examples and mockups
        verikc_dir = os.path.join(script_dir, "verikc/build/libs")
        verikc_path = os.path.join(verikc_dir, os.listdir(verikc_dir)[0])
        for example_dir in example_dirs:
            print_header("build", os.path.relpath(example_dir, script_dir))
            if os.path.basename(example_dir) not in examples_excluded:
                verikc(example_dir, verikc_path, ["all"])
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


if __name__ == "__main__":
    main()
