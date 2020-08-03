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
    parser.add_argument("-o", metavar="OUPTUT", help="the output build directory", default="builds")
    parser.add_argument("task", metavar="TASK", help="clean build",
                        choices=["clean", "build"], nargs="*", default="build")
    args = parser.parse_args()

    if "clean" in args.task:
        if os.path.exists(args.o):
            shutil.rmtree(args.o)

    if "build" in args.task:
        timestamp = datetime.now().strftime("%y%m%d_%H%M%S")
        input_dir = os.path.abspath(args.i)
        output_dir = os.path.abspath(os.path.join(args.o, timestamp))

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
        shutil.copytree(input_dir, "verik")

        try:
            # build simulation
            if args.s == "xsim":
                build_xsim(top, sources)
            else:
                raise ValueError("unsupported simulator %s" % args.s)
        except:
            with open("FAIL", "w") as f:
                pass
            raise
        with open("PASS", "w") as f:
            pass


def build_xsim(top, sources):
    for source in sources:
        path = os.path.join("verik/out", source)
        subprocess.run(["xvlog", "-sv", path], check=True)
    subprocess.run(["xelab", "-debug", "typical", top, "-s", "sim"], check=True)


if __name__ == "__main__":
    main()
