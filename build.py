#!/usr/bin/python3

import argparse
import os
import subprocess
import sys


isatty = sys.stdout.isatty()
exclude_examples = ["print", "lock"]


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("task", metavar="TASK", help="clean build",
                        choices=["clean", "build"], nargs="*", default="build")
    args = parser.parse_args()
    print()

    root = os.path.dirname(os.path.realpath(__file__))

    if "clean" in args.task:
        # clean examples and mockups
        verik_dir = os.path.join(root, "core/build/libs")
        if os.path.exists(verik_dir):
            verik_path = os.path.join(verik_dir, os.listdir(verik_dir)[0])
            for path, dirs, files in os.walk(os.path.join(root, "examples")):
                if "gradlew" in files:
                    print_header("clean", os.path.relpath(path, root))
                    verik(path, verik_path, ["clean"])
            for path, dirs, files in os.walk(os.path.join(root, "mockups")):
                if "gradlew" in files:
                    print_header("clean", os.path.relpath(path, root))
                    verik(path, verik_path, ["clean"])
        else:
            for path, dirs, files in os.walk(os.path.join(root, "examples")):
                if "gradlew" in files:
                    print_header("clean", os.path.relpath(path, root))
                    gradle(path, ["clean"])
            for path, dirs, files in os.walk(os.path.join(root, "mockups")):
                if "gradlew" in files:
                    print_header("clean", os.path.relpath(path, root))
                    gradle(path, ["clean"])

        # clean verik and core
        print_header("clean", "verik")
        gradle(os.path.join(root, "verik"), ["clean"])
        print_header("clean", "core")
        gradle(os.path.join(root, "core"), ["clean"])

    if "build" in args.task:
        # build verik and core
        print_header("build", "verik")
        gradle(os.path.join(root, "verik"), ["build"])
        print_header("build", "core")
        gradle(os.path.join(root, "core"), ["build"])

        # build examples and mockups
        verik_dir = os.path.join(root, "core/build/libs")
        verik_path = os.path.join(verik_dir, os.listdir(verik_dir)[0])
        for path, dirs, files in os.walk(os.path.join(root, "examples")):
            if "gradlew" in files:
                print_header("build", os.path.relpath(path, root))
                if os.path.basename(path) in exclude_examples:
                    verik(path, verik_path, ["headers", "gradle"])
                else:
                    verik(path, verik_path, ["all"])
        for path, dirs, files in os.walk(os.path.join(root, "mockups")):
            if "gradlew" in files:
                print_header("build", os.path.relpath(path, root))
                verik(path, verik_path, ["headers", "gradle"])


def print_header(task, name):
    if isatty:
        print(u"\u001B[32m\u001B[1m", end="")  # ANSI green bold
        print("%s %s" % (task.upper(), name), end="")
        print(u"\u001B[0m\n", end="")  # ANSI reset
    else:
        print("%s %s" % (task.upper(), name))


def gradle(path, tasks):
    gradlew = os.path.join(path, "gradlew")
    subprocess.run([gradlew, "-p", path, *tasks], check=True)
    print()


def verik(path, verik_path, tasks):
    vkproject = os.path.join(path, "vkproject.yaml")
    subprocess.run(["java", "-jar", verik_path, "-c", vkproject, *tasks], check=True)


if __name__ == "__main__":
    main()
