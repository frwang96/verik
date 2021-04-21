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

import os
import platform
import subprocess

import sys

script_dir = os.path.dirname(os.path.realpath(__file__))
system_type = platform.system()
if system_type in ["Darwin", "Linux"]:
    is_unix = sys.stdout.isatty()
elif system_type == "Windows":
    is_unix = False
else:
    raise OSError("operating system not supported")


def main():
    print()
    print_header("install", "core")
    gradle(os.path.join(script_dir, "core"), "install")
    print_header("install", "compiler")
    gradle(os.path.join(script_dir, "compiler"), "install")

    example_root_dir = os.path.join(script_dir, "examples")
    example_dirs = [os.path.join(example_root_dir, d)
                    for d in os.listdir(example_root_dir)
                    if os.path.isdir(os.path.join(example_root_dir, d))]
    for example_dir in example_dirs:
        print_header("verik", os.path.relpath(example_dir, script_dir))
        gradle(example_dir, "verik")


def print_header(task, name):
    if is_unix:
        print(u"\u001B[32m\u001B[1m", end="")  # ANSI green bold
        print("%s %s" % (task.upper(), name), end="")
        print(u"\u001B[0m\n", end="")  # ANSI reset
    else:
        print("%s %s" % (task.upper(), name))


def gradle(path, task):
    if is_unix:
        gradle_path = os.path.join(path, "gradlew")
    else:
        gradle_path = os.path.join(path, "gradlew.bat")
    subprocess.run([gradle_path, "-p", path, task], check=True)
    print()


if __name__ == "__main__":
    main()
