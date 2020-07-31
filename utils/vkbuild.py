#!/usr/bin/python3

import argparse
import shutil
import os
import subprocess


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("-s", help="the simulator to target", required=True)
    parser.add_argument("-i", help="the input file list", default="sources.txt")
    parser.add_argument("-o", help="the output build directory", default="build")
    args = parser.parse_args()

    path_input = os.path.abspath(args.i)
    path_output = os.path.abspath(args.o)

    # parse input file
    with open(path_input) as file:
        lines = file.readlines()
        top = lines[0].strip()
        sources = [it.strip() for it in lines[1:]]

    # clean build directory
    if os.path.exists(path_output):
        shutil.rmtree(path_output)
    os.makedirs(path_output, exist_ok=True)
    os.chdir(path_output)

    # build simulation
    if args.s == "xsim":
        build_xsim(path_input, top, sources)
    else:
        raise ValueError("unsupported simulator %s" % args.s)


def build_xsim(path_input, top, sources):
    for source in sources:
        path = os.path.join(os.path.dirname(path_input), source)
        subprocess.run(["xvlog", "-sv", path], check=True)
    subprocess.run(["xelab", "-debug", "typical", top, "-s", "sim"], check=True)


if __name__ == "__main__":
    main()
