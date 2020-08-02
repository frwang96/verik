#!/usr/bin/python3

import argparse
from datetime import datetime
import shutil
import os
import subprocess
import sys


class Tee:
    def __init__(self, *files):
        self.files = files

    def write(self, obj):
        for file in self.files:
            file.write(obj)

    def flush(self):
        for file in self.files:
            file.flush()


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("-s", help="the simulator to target", required=True)
    parser.add_argument("-i", help="the input build directory", default="builds")
    parser.add_argument("-t", help="the input build timestamp", default="")
    parser.add_argument("-o", help="the output simulation directory", default="runs")
    parser.add_argument("task", choices=["run", "clean"], nargs="?", default="run")
    args = parser.parse_args()

    if args.task == "run":
        timestamp = datetime.now().strftime("%y%m%d_%H%M%S")

        if args.t == "":
            args.t = get_last_build(args.i)

        input_dir = os.path.abspath(os.path.join(args.i, args.t))
        output_dir = os.path.abspath(os.path.join(args.o, timestamp))

        # make simulation directory
        if os.path.exists(output_dir):
            shutil.rmtree(output_dir)
        os.makedirs(output_dir, exist_ok=True)
        os.chdir(output_dir)

        # log standard output
        sys.stdout = Tee(sys.stdout, open("vkrun.log", "w"))
        print("build: %s" % args.t)
        print("run:   %s" % timestamp)
        print()

        try:
            # run simulation
            if args.s == "xsim":
                run_xsim(input_dir, output_dir)
            else:
                raise ValueError("unsupported simulator %s" % args.s)
        except:
            print_result(0, 1, "base", False)
            with open("FAIL", "w") as f:
                pass
            raise
        print_result(0, 1, "base", True)
        with open("PASS", "w") as f:
            pass
    elif args.task == "clean":
        if os.path.exists(args.o):
            shutil.rmtree(args.o)


def get_last_build(build_dir):
    dirs = os.listdir(build_dir)
    passing_dirs = []
    for name in dirs:
        path = os.path.join(build_dir, name)
        if os.path.isdir(path) and os.path.exists(os.path.join(path, "PASS")):
            passing_dirs.append(name)
    passing_dirs = sorted(passing_dirs)
    if not passing_dirs:
        raise ValueError("no passing build directories found")
    return passing_dirs[-1]


def print_result(count, total_count, name, result):
    count_string = str(count + 1).zfill(len(str(total_count)))
    if result:
        print("[%s/%d PASS] %s" % (count_string, total_count, name))
    else:
        print("[%s/%d FAIL] %s" % (count_string, total_count, name))


def run_xsim(input_dir, output_dir):
    ln_target = os.path.join(input_dir, "xsim.dir/sim")
    ln_dir = os.path.join(output_dir, "xsim.dir")
    os.makedirs(ln_dir, exist_ok=True)
    subprocess.run(["ln", "-s", ln_target, ln_dir], check=True)

    devnull = open(os.devnull, "w")
    subprocess.run(["xsim", "-R", "sim"], stdout=devnull, check=True)


if __name__ == "__main__":
    main()
