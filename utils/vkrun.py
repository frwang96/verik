#!/usr/bin/python3

import argparse
from datetime import datetime
import shutil
import os
import subprocess


def main():
    timestamp = datetime.now().strftime("%y%m%d_%H%M%S")

    parser = argparse.ArgumentParser()
    parser.add_argument("-s", help="the simulator to target", required=True)
    parser.add_argument("-i", help="the input build directory", default="build")
    parser.add_argument("-o", help="the output simulation directory", default="runs/%s" % timestamp)
    args = parser.parse_args()

    path_input = os.path.abspath(args.i)
    path_output = os.path.abspath(args.o)

    # make simulation directory
    if os.path.exists(path_output):
        shutil.rmtree(path_output)
    os.makedirs(path_output, exist_ok=True)
    os.chdir(path_output)

    if args.s == "xsim":
        run_xsim(path_input, path_output)
    else:
        raise ValueError("unsupported simulator %s" % args.s)


def run_xsim(path_input, path_output):
    path_target = os.path.join(path_input, "xsim.dir/sim")
    path_dir = os.path.join(path_output, "xsim.dir")
    os.makedirs(path_dir, exist_ok=True)
    subprocess.run(["ln", "-s", path_target, path_dir], check=True)

    devnull = open(os.devnull, "w")
    subprocess.run(["xsim", "-R", "sim"], stdout=devnull, check=True)


if __name__ == "__main__":
    main()
