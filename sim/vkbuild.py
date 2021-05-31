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

import yaml

build_dir = os.path.join(os.getcwd(), "build")
verik_dir = os.path.join(build_dir, "verik")
vkbuild_dir = os.path.join(build_dir, "vkbuild", datetime.now().strftime("%y%m%d_%H%M%S"))


def main():
    if not os.path.exists(build_dir):
        raise FileNotFoundError("build directory not found")

    # parse order file
    if os.path.exists(os.path.join(verik_dir, "order.yaml")):
        with open(os.path.join(verik_dir, "order.yaml")) as file:
            order = yaml.safe_load(file)
            top = order["top"]
            sources = order["order"]
    else:
        raise FileNotFoundError("order file not found")

    # clean build directory
    if os.path.exists(vkbuild_dir):
        shutil.rmtree(vkbuild_dir)
    os.makedirs(vkbuild_dir, exist_ok=True)
    os.chdir(vkbuild_dir)

    try:
        for source in sources:
            path = os.path.join(verik_dir, source)
            subprocess.run([shutil.which("xvlog"), "-sv", path], check=True)
        subprocess.run([shutil.which("xelab"), "-debug", "typical", top, "-s", "sim"], check=True)
    except:
        open("FAIL", "w").close()
    else:
        open("PASS", "w").close()


if __name__ == "__main__":
    main()
