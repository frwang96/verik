#!/usr/bin/python3

import os
import subprocess
import sys


isatty = sys.stdout.isatty()


def main():
    root = os.path.dirname(os.path.realpath(__file__))
    print()
    print_header("common")
    gradle(os.path.join(root, "common"))
    print_header("core")
    gradle(os.path.join(root, "core"))

    verik_dir = os.path.join(root, "core/build/libs")
    verik_path = os.path.join(verik_dir, os.listdir(verik_dir)[0])

    for path, dirs, files in os.walk(os.path.join(root, "mockups")):
        if "gradlew" in files:
            print_header(os.path.relpath(path, root))
            verik(path, verik_path, ["headers", "gradle"])
    for path, dirs, files in os.walk(os.path.join(root, "examples")):
        if "gradlew" in files:
            print_header(os.path.relpath(path, root))
            verik(path, verik_path, ["all"])


def print_header(name):
    if isatty:
        print(u"\u001B[32m\u001B[1m", end="")  # ANSI green bold
        print("BUILD %s" % name, end="")
        print(u"\u001B[0m\n", end="")  # ANSI reset
    else:
        print("BUILD %s" % name)


def gradle(path):
    gradlew = os.path.join(path, "gradlew")
    subprocess.run([gradlew, "-p", path, "build"], check=True)
    print()


def verik(path, verik_path, tasks):
    vkprojconf = os.path.join(path, "vkprojconf.yaml")
    subprocess.run(["java", "-jar", verik_path, "-c", vkprojconf, *tasks], check=True)


if __name__ == "__main__":
    main()
