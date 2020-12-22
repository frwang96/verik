#!/usr/bin/python3

import argparse
import os
import subprocess
import sys


isatty = sys.stdout.isatty()
examples_excluded = []
examples_unchecked = []


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("task", metavar="TASK", help="clean build",
                        choices=["clean", "build"], nargs="*", default="build")
    args = parser.parse_args()
    print()

    root = os.path.dirname(os.path.realpath(__file__))

    if "clean" in args.task:
        # clean examples and mockups
        verikc_dir = os.path.join(root, "verikc/build/libs")
        if os.path.exists(verikc_dir):
            verikc_path = os.path.join(verikc_dir, os.listdir(verikc_dir)[0])
            for path, dirs, files in os.walk(os.path.join(root, "examples")):
                if "gradlew" in files:
                    print_header("clean", os.path.relpath(path, root))
                    verikc(path, verikc_path, ["clean"])
            for path, dirs, files in os.walk(os.path.join(root, "mockups")):
                if "gradlew" in files:
                    print_header("clean", os.path.relpath(path, root))
                    verikc(path, verikc_path, ["clean"])
        else:
            for path, dirs, files in os.walk(os.path.join(root, "examples")):
                if "gradlew" in files:
                    print_header("clean", os.path.relpath(path, root))
                    gradle(path, ["clean"])
            for path, dirs, files in os.walk(os.path.join(root, "mockups")):
                if "gradlew" in files:
                    print_header("clean", os.path.relpath(path, root))
                    gradle(path, ["clean"])

        # clean verik and verikc
        print_header("clean", "verik")
        gradle(os.path.join(root, "verik"), ["clean"])
        print_header("clean", "verikc")
        gradle(os.path.join(root, "verikc"), ["clean"])

    if "build" in args.task:
        # build verik and verikc
        print_header("build", "verik")
        gradle(os.path.join(root, "verik"), ["build"])
        print_header("build", "verikc")
        gradle(os.path.join(root, "verikc"), ["build"])

        # build examples and mockups
        verikc_dir = os.path.join(root, "verikc/build/libs")
        verikc_path = os.path.join(verikc_dir, os.listdir(verikc_dir)[0])
        for path, dirs, files in os.walk(os.path.join(root, "examples")):
            if "gradlew" in files:
                print_header("build", os.path.relpath(path, root))
                if os.path.basename(path) in examples_excluded:
                    verikc(path, verikc_path, ["headers", "gradle"])
                else:
                    verikc(path, verikc_path, ["all"])
                    if os.path.basename(path) not in examples_unchecked:
                        print_header("consistency", os.path.relpath(path, root))
                        print()
                        consistency(path)
        for path, dirs, files in os.walk(os.path.join(root, "mockups")):
            if "gradlew" in files:
                print_header("build", os.path.relpath(path, root))
                verikc(path, verikc_path, ["headers", "gradle"])


def print_header(task, name):
    if isatty:
        print(u"\u001B[32m\u001B[1m", end="")  # ANSI green bold
        print("%s %s" % (task.upper(), name), end="")
        print(u"\u001B[0m\n", end="")  # ANSI reset
    else:
        print("%s %s" % (task.upper(), name))


def print_error(message):
    if isatty:
        print(u"\u001B[31m\u001B[1m", end="")  # ANSI red bold
        print("ERROR: " + message, end="")
        print(u"\u001B[0m\n", end="")  # ANSI reset
    else:
        print("ERROR: " + message)


def gradle(path, tasks):
    gradlew = os.path.join(path, "gradlew")
    subprocess.run([gradlew, "-p", path, *tasks], check=True)
    print()


def verikc(path, verikc_path, tasks):
    vkproject = os.path.join(path, "vkproject.yaml")
    subprocess.run(["java", "-jar", verikc_path, "-c", vkproject, *tasks], check=True)


def consistency(path):
    cons_path = os.path.join(path, "cons")
    out_path = os.path.join(path, "build/verik/out")
    file_name_set = set()
    for path, dirs, files in os.walk(cons_path):
        for file in files:
            file_name_set.add(os.path.relpath(os.path.join(path, file), cons_path))
    for path, dirs, files in os.walk(out_path):
        for file in files:
            file_name_set.add(os.path.relpath(os.path.join(path, file), out_path))
    for file_name in sorted(list(file_name_set)):
        cons_file_path = os.path.join(cons_path, file_name)
        out_file_path = os.path.join(out_path, file_name)
        if not os.path.exists(cons_file_path):
            print_error("(%s) could not find cons file" % file_name)
            print()
            exit(0)
        if not os.path.exists(out_file_path):
            print_error("(%s) could not find out file " % file_name)
            print()
            exit(0)
        consistency_file(cons_file_path, out_file_path, file_name)


def consistency_file(cons_file_path, out_file_path, file_name):
    with open(cons_file_path) as file:
        cons_lines = file.readlines()
    with open(out_file_path) as file:
        out_lines = file.readlines()[10:]
    lines = max(len(cons_lines), len(out_lines))
    for i in range(lines):
        cons_line = "" if i >= len(cons_lines) else cons_lines[i].strip()
        out_line = "" if i >= len(out_lines) else out_lines[i][14:].strip()
        if cons_line != out_line:
            print_error("(%s:%d)" % (file_name, i + 1))
            print("    expected : " + cons_line)
            print("    actual   : " + out_line)
            print()
            exit(0)


if __name__ == "__main__":
    main()
