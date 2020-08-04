#!/usr/bin/python3

import argparse
from datetime import datetime
import time
import math
import os
import subprocess
import sys
from vkrun import parse

isatty = sys.stdout.isatty()


class Tee:
    def __init__(self, stdout, file):
        self.stdout = stdout
        self.file = file

    def write(self, obj):
        self.stdout.write(obj)
        self.file.write(obj)

    def flush(self):
        self.stdout.flush()
        self.file.flush()

    def isatty(self):
        return self.stdout.isatty()


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("-s", metavar="SIM", help="the simulator to target", required=True)
    parser.add_argument("-i", metavar="INPUT", help="the input verik directory", default="verik")
    parser.add_argument("-b", metavar="BUILD", help="the input build directory", default="builds")
    parser.add_argument("-t", metavar="TIMESTAMP", help="the build timestamp", default="")
    parser.add_argument("-o", metavar="OUTPUT", help="the output simulation directory", default="runs")
    parser.add_argument("-r", metavar="RSEED", help="the random number generator seed", type=int, default=0)
    parser.add_argument("-l", metavar="LOAD", help="the load factor", type=float, default=1)
    parser.add_argument("-d", help="perform a dry run", action="store_true", default=False)
    parser.add_argument("stub", metavar="STUB", nargs="+")
    args = parser.parse_args()

    time_start = time.time()
    timestamp = datetime.now().strftime("%y%m%d_%H%M%S")

    if args.t == "":
        args.t = get_last_build(args.b)

    if args.l < 0:
        raise ValueError("load factor must be larger than 0")
    elif args.l > 100:
        raise ValueError("load factor must be smaller than 100")

    stubs_file = os.path.abspath(os.path.join(args.i, "stubs.txt"))
    build_dir = os.path.abspath(os.path.join(args.b, args.t))
    output_dir = os.path.abspath(os.path.join(args.o, timestamp))

    # log standard output
    sys.stdout = Tee(sys.stdout, open("vkrun.log", "w"))
    print()
    print("build: %s" % args.t)
    print("run:   %s" % timestamp)
    print()

    # generate test stubs
    stubs = parse.parse(stubs_file)
    for full_name in args.stub:
        parse.include(stubs, full_name)
    included_stubs = stubs.get_included()
    included_stubs.generate_rseeds(args.r, args.l)
    total_count = included_stubs.count()

    count = 0
    for stub in included_stubs.list():
        if stub.rseeds:
            for rseed in stub.rseeds:
                if args.d:
                    print("%s %s/%s" % (get_label(count, total_count), stub.full_name, rseed))
                else:
                    run(build_dir, output_dir, args.s, count, total_count, stub, rseed)
                count += 1
        else:
            if args.d:
                print("%s %s" % (get_label(count, total_count), stub.full_name))
            else:
                run(build_dir, output_dir, args.s, count, total_count, stub)
            count += 1

    time_end = time.time()
    print()
    print("run complete in %ds" % (math.ceil(time_end - time_start)))
    print()


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


def run(build_dir, output_dir, sim, count, total_count, stub, rseed=None):
    sim_dir = os.path.join(output_dir, stub.full_name)
    full_name = stub.full_name
    if rseed is not None:
        sim_dir = os.path.join(sim_dir, rseed)
        full_name = full_name + "/" + rseed

    try:
        if sim == "xsim":
            run_xsim(build_dir, sim_dir)
        else:
            raise ValueError("unsupported simulator %s" % sim)
    except:
        print_result(count, total_count, full_name, False)
        with open(os.path.join(sim_dir, "FAIL"), "w"):
            pass
        raise
    print_result(count, total_count, full_name, True)
    with open(os.path.join(sim_dir, "PASS"), "w"):
        pass


def run_xsim(input_dir, sim_dir):
    os.makedirs(sim_dir, exist_ok=True)
    os.chdir(sim_dir)
    ln_target = os.path.join(input_dir, "xsim.dir/sim")
    ln_dir = os.path.join(sim_dir, "xsim.dir")
    os.makedirs(ln_dir, exist_ok=True)
    subprocess.run(["ln", "-s", ln_target, ln_dir], check=True)

    devnull = open(os.devnull, "w")
    subprocess.run(["xsim", "-R", "sim"], stdout=devnull, check=True)


def print_result(count, total_count, name, result):
    count_string = str(count + 1).zfill(len(str(total_count)))
    if isatty:
        if result:
            print(u"\u001B[32m\u001B[1m", end="")  # ANSI green bold
            print(get_label(count, total_count, result), end=" ")
            print(u"\u001B[0m", end="")  # ANSI reset

            print(u"\u001B[32m", end="")  # ANSI green
            print(name, end="")
            print(u"\u001B[0m\n", end="")  # ANSI reset
        else:
            print(u"\u001B[31m\u001B[1m", end="")  # ANSI red bold
            print(get_label(count, total_count, result), end=" ")
            print(u"\u001B[0m", end="")  # ANSI reset

            print(u"\u001B[31m", end="")  # ANSI red
            print(name, end="")
            print(u"\u001B[0m\n", end="")  # ANSI reset
    else:
        print("%s %s" % (get_label(count_string, total_count, result), name))


def get_label(count, total_count, result=None):
    count_string = str(count + 1).zfill(len(str(total_count)))
    if result is None:
        return "[%s/%d]" % (count_string, total_count)
    elif result:
        return "[%s/%d PASS]" % (count_string, total_count)
    else:
        return "[%s/%d FAIL]" % (count_string, total_count)


if __name__ == "__main__":
    main()
