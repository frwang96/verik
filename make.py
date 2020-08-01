#!/usr/bin/python3

import os
import subprocess


def main():
    root = os.path.dirname(os.path.realpath(__file__))
    gradle(os.path.join(root, "common"))
    gradle(os.path.join(root, "core"))

    verik_dir = os.path.join(root, "core/build/libs")
    verik_path = os.path.join(verik_dir, os.listdir(verik_dir)[0])

    for path, dirs, files in os.walk(os.path.join(root, "mockups")):
        if "gradlew" in files:
            gradle(path)
    for path, dirs, files in os.walk(os.path.join(root, "examples")):
        if "gradlew" in files:
            gradle(path)
            verik(path, verik_path)


def gradle(path):
    gradlew = os.path.join(path, "gradlew")
    subprocess.run([gradlew, "-p", path, "clean", "build"], check=True)


def verik(path, verik_path):
    vkprojconf = os.path.join(path, "vkprojconf.yaml")
    subprocess.run(["java", "-jar", verik_path, "all", vkprojconf], check=True)


if __name__ == "__main__":
    main()
