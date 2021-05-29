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
import shutil
import signal
import subprocess
from datetime import datetime

import time

run_exit = False   # signal to end run


def shutdown_handler(signum, frame):
    global run_exit
    run_exit = True


signal.signal(signal.SIGTERM, shutdown_handler)
signal.signal(signal.SIGINT, shutdown_handler)


build_dir = os.path.join(os.getcwd(), "build")
vkbuild_dir = ""
vkrun_dir = os.path.join(build_dir, "vkrun", datetime.now().strftime("%y%m%d_%H%M%S"))


def main():
    if not os.path.exists(build_dir):
        raise FileNotFoundError("build directory not found")

    set_vkbuild_dir()

    try:
        run_xsim()
    except:
        open(os.path.join(vkrun_dir, "FAIL"), "w").close()
    else:
        open(os.path.join(vkrun_dir, "PASS"), "w").close()


def set_vkbuild_dir():
    global vkbuild_dir
    passing_dirs = []
    for timestamp in os.listdir(os.path.join(build_dir, "vkbuild")):
        path = os.path.join(build_dir, "vkbuild", timestamp)
        if os.path.isdir(path) and os.path.exists(os.path.join(path, "PASS")):
            passing_dirs.append(path)
    passing_dirs = sorted(passing_dirs)
    if not passing_dirs:
        raise ValueError("no passing build directories found")
    vkbuild_dir = passing_dirs[-1]


def run_xsim():
    if os.path.exists(vkrun_dir):
        shutil.rmtree(vkrun_dir)
    os.makedirs(vkrun_dir, exist_ok=True)
    os.chdir(vkrun_dir)

    os.makedirs(os.path.join(vkrun_dir, "xsim.dir"), exist_ok=True)
    xsim_vkbuild = os.path.join(vkbuild_dir, "xsim.dir", "sim")
    xsim_vkrun = os.path.join(vkrun_dir, "xsim.dir", "sim")
    devnull = open(os.devnull, "w")
    if platform.system() == "Windows":
        subprocess.run(["mklink", "/D", xsim_vkrun, xsim_vkbuild], shell=True, check=True, stdout=devnull)
    else:
        subprocess.run(["ln", "-s", xsim_vkbuild, xsim_vkrun], check=True)
    process = subprocess.Popen([shutil.which("xsim"), "-R", "sim"], stdout=devnull)
    while process.poll() is None:
        time.sleep(0.1)
        if run_exit:
            process.kill()
            raise RuntimeError("exit signal asserted")


if __name__ == "__main__":
    main()
