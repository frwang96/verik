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
import shutil
import signal
import subprocess
from datetime import datetime

import math
import sys
import time

run_exit = False   # signal to end run
log_stream = None  # stream to log standard output


def shutdown_handler(signum, frame):
    global run_exit
    run_exit = True


signal.signal(signal.SIGTERM, shutdown_handler)
signal.signal(signal.SIGINT, shutdown_handler)


build_dir = os.path.join(os.getcwd(), "build")
vkbuild_dir = ""
vkrun_dir = os.path.join(build_dir, "vkrun", datetime.now().strftime("%y%m%d_%H%M%S"))


def main():
    time_start = time.time()
    if not os.path.exists(build_dir):
        raise FileNotFoundError("build directory not found")

    parser = argparse.ArgumentParser()
    parser.add_argument("-t", metavar="TIMESTAMP", help="the build timestamp", default="")
    parser.add_argument("-k", metavar="KILL", help="the kill timeout", type=int, default=0)
    parser.add_argument("-d", help="perform a dry run", action="store_true", default=False)
    args = parser.parse_args()

    set_vkbuild_dir(args.t)

    if not args.d:
        os.makedirs(vkrun_dir, exist_ok=True)
        global log_stream
        log_stream = open(os.path.join(vkrun_dir, "vkrun.log"), "w")

    print_log()
    print_log("VKRUN")
    print_log("usage:  vkrun %s" % (" ".join(sys.argv[1:])))
    print_log("build:  %s" % os.path.relpath(vkbuild_dir, build_dir))
    print_log("run:    %s" % os.path.relpath(vkrun_dir, build_dir))
    print_log("kill:   %s" % args.k)
    print_log()
    run(args.k)

    time_end = time.time()
    elapsed = math.ceil(time_end - time_start)
    print_log()
    print_log("run complete in %s" % get_elapsed_string(elapsed))
    print_log()


def set_vkbuild_dir(timestamp):
    global vkbuild_dir
    if timestamp != "":
        vkbuild_dir = os.path.join(build_dir, "vkbuild", timestamp)
        if not os.path.exists(vkbuild_dir):
            raise FileNotFoundError("build directory with timestamp %s not found" % timestamp)
    else:
        passing_dirs = []
        for timestamp in os.listdir(os.path.join(build_dir, "vkbuild")):
            path = os.path.join(build_dir, "vkbuild", timestamp)
            if os.path.isdir(path) and os.path.exists(os.path.join(path, "PASS")):
                passing_dirs.append(path)
        passing_dirs = sorted(passing_dirs)
        if not passing_dirs:
            raise ValueError("no passing build directories found")
        vkbuild_dir = passing_dirs[-1]


def run(kill):
    time_start = time.time()
    os.chdir(vkrun_dir)
    if run_exit:
        result = False
    else:
        try:
            run_xsim(kill)
        except:
            result = False
        else:
            result = True
    if result:
        open(os.path.join(vkrun_dir, "PASS"), "w").close()
    else:
        open(os.path.join(vkrun_dir, "FAIL"), "w").close()
    time_end = time.time()
    elapsed = int(math.ceil(time_end - time_start))
    log_result(result, elapsed)


def run_xsim(kill):
    time_start = time.time()
    os.makedirs(os.path.join(vkrun_dir, "xsim.dir"), exist_ok=True)
    xsim_build = os.path.join(vkbuild_dir, "xsim.dir", "sim")
    xsim_entry = os.path.join(vkrun_dir, "xsim.dir", "sim")
    devnull = open(os.devnull, "w")
    if platform.system() == "Windows":
        subprocess.run(["mklink", "/D", xsim_entry, xsim_build], shell=True, check=True, stdout=devnull)
    else:
        subprocess.run(["ln", "-s", xsim_build, xsim_entry], check=True)
    process = subprocess.Popen([shutil.which("xsim"), "-R", "sim"], stdout=devnull)
    while process.poll() is None:
        time.sleep(1)
        time_current = time.time()
        if 0 < kill < time_current - time_start:
            process.kill()
            raise RuntimeError("kill timeout of %d exceeded" % kill)
        if run_exit:
            process.kill()
            raise RuntimeError("exit signal asserted")


def log_result(result, elapsed):
    label = get_label(result, elapsed)
    if result:
        color_string = u"\u001B[32m\u001B[1m"  # ANSI green bold
        color_string += label
        color_string += u"\u001B[0m"  # ANSI reset
    else:
        color_string = u"\u001B[31m\u001B[1m"  # ANSI red bold
        color_string += label
        color_string += u"\u001B[0m"  # ANSI reset
    plain_string = label
    print_log(color_string, plain_string)


def get_label(result, elapsed):
    if result:
        return "[PASS %s]" % get_elapsed_string(elapsed)
    else:
        return "[FAIL %s]" % get_elapsed_string(elapsed)


def get_elapsed_string(elapsed):
    seconds = elapsed % 60
    elapsed /= 60
    minutes = elapsed % 60
    elapsed /= 60
    hours = elapsed
    return "%02d:%02d:%02d" % (hours, minutes, seconds)


def print_log(color_string=None, plain_string=None):
    if color_string is None:
        color_string = ""
    if plain_string is None:
        plain_string = color_string
    if platform.system() in ["Darwin", "Linux"] and sys.stdout.isatty():
        print(color_string)
    else:
        print(plain_string)
    if log_stream is not None:
        # noinspection PyUnresolvedReferences
        log_stream.write(plain_string)
        # noinspection PyUnresolvedReferences
        log_stream.write("\n")


if __name__ == "__main__":
    main()
