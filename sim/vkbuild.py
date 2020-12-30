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
import shutil
import subprocess
from datetime import datetime


timestamp = datetime.now().strftime("%y%m%d_%H%M%S")
verik_dir = os.path.join(os.getcwd(), "build", "verik")
vkbuild_dir = os.path.join(os.getcwd(), "build", "vkbuild", timestamp)


def main():
    # parse order file
    if os.path.exists(os.path.join(verik_dir, "order.txt")):
        with open(os.path.join(verik_dir, "order.txt")) as file:
            lines = file.readlines()
            top = lines[0].strip()
            sources = [it.strip() for it in lines[1:]]
    else:
        raise FileNotFoundError("order file not found")

    # clean build directory
    if os.path.exists(vkbuild_dir):
        shutil.rmtree(vkbuild_dir)
    os.makedirs(vkbuild_dir, exist_ok=True)
    os.chdir(vkbuild_dir)

    # copy source files
    os.makedirs("verik", exist_ok=True)
    shutil.copyfile(os.path.join(verik_dir, "vkproject.yaml"), "verik/vkproject.yaml")
    shutil.copyfile(os.path.join(verik_dir, "order.txt"), "verik/order.txt")
    shutil.copytree(os.path.join(verik_dir, "src"), "verik/src")
    shutil.copytree(os.path.join(verik_dir, "out"), "verik/out")
    if os.path.exists(os.path.join(verik_dir, "rconf.txt")):
        shutil.copyfile(os.path.join(verik_dir, "rconf.txt"), "verik/rconf.txt")

    try:
        build_xsim(top, sources)
    except:
        open("FAIL", "w").close()
    else:
        open("PASS", "w").close()


def build_xsim(top, sources):
    for source in sources:
        path = os.path.join("verik", "out", source)
        subprocess.run([shutil.which("xvlog"), "-sv", path], check=True)
    subprocess.run([shutil.which("xelab"), "-debug", "typical", top, "-s", "sim"], check=True)


if __name__ == "__main__":
    main()
