#!/usr/bin/python3

import argparse
from datetime import datetime
import shutil
import os
import subprocess


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("-s", metavar="SIM", help="the simulator to target", required=True)
    parser.add_argument("-i", metavar="INPUT", help="the input verik directory", default="verik")
    parser.add_argument("-b", metavar="BUILD", help="the output build directory", default="builds")
    args = parser.parse_args()

    timestamp = datetime.now().strftime("%y%m%d_%H%M%S")
    input_dir = os.path.abspath(args.i)
    output_dir = os.path.abspath(os.path.join(args.b, timestamp))

    # parse order file
    with open(os.path.join(input_dir, "order.txt")) as file:
        lines = file.readlines()
        top = lines[0].strip()
        sources = [it.strip() for it in lines[1:]]

    # clean build directory
    if os.path.exists(output_dir):
        shutil.rmtree(output_dir)
    os.makedirs(output_dir, exist_ok=True)
    os.chdir(output_dir)

    # copy source files
    os.makedirs(os.path.join(output_dir, "verik"), exist_ok=True)
    shutil.copyfile(os.path.join(input_dir, "vkproject.yaml"), "verik/vkproject.yaml")
    shutil.copyfile(os.path.join(input_dir, "order.txt"), "verik/order.txt")
    shutil.copyfile(os.path.join(input_dir, "stubs.txt"), "verik/stubs.txt")
    shutil.copytree(os.path.join(input_dir, "src"), "verik/src")
    shutil.copytree(os.path.join(input_dir, "out"), "verik/out")

    try:
        if args.s == "xsim":
            build_xsim(top, sources)
        else:
            raise ValueError("unsupported simulator %s" % args.s)
    except:
        open("FAIL", "w").close()
    open("PASS", "w").close()


def build_xsim(top, sources):
    for source in sources:
        path = os.path.join("verik/out", source)
        subprocess.run(["xvlog", "-sv", path], check=True)
    subprocess.run(["xelab", "-debug", "typical", top, "-s", "sim"], check=True)


if __name__ == "__main__":
    main()
