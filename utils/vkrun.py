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
import shutil
import signal
import subprocess
from concurrent.futures import ThreadPoolExecutor
from datetime import datetime
from threading import Lock

import math
import sys
import time

from vkrun import parse
from vkrun.entry import SeedGenerator, get_base_name, ExpandedEntry

isatty = sys.stdout.isatty()
log_stream = None
run_exit = False
run_count = 0
print_lock = Lock()


# noinspection PyUnusedLocal
def shutdown_handler(signum, frame):
    global run_exit
    run_exit = True


signal.signal(signal.SIGTERM, shutdown_handler)
signal.signal(signal.SIGINT, shutdown_handler)


def main():
    global log_stream
    parser = argparse.ArgumentParser()
    parser.add_argument("-s", metavar="SIM", help="the simulator to target", required=True)
    parser.add_argument("-b", metavar="BUILD", help="the input build directory", default="builds")
    parser.add_argument("-t", metavar="TIMESTAMP", help="the build timestamp", default="")
    parser.add_argument("-i", metavar="INPUT", help="the input rconf file", default="")
    parser.add_argument("-o", metavar="OUTPUT", help="the output simulation directory", default="runs")
    parser.add_argument("-r", metavar="RANDSEED", help="the random number generator seed", type=int, default=0)
    parser.add_argument("-l", metavar="LOAD", help="the load factor", type=float, default=1)
    parser.add_argument("-k", metavar="KILL", help="the kill timeout", type=int, default=0)
    parser.add_argument("-d", help="perform a dry run", action="store_true", default=False)
    parser.add_argument("include", metavar="INCLUDE", help="entries to include", nargs="+")
    args = parser.parse_args()

    time_start = time.time()
    timestamp = datetime.now().strftime("%y%m%d_%H%M%S")

    if args.s not in ["xsim"]:
        raise ValueError("unsupported simulator %s" % args.s)

    if args.t == "":
        args.t = get_last_build(args.b)

    build_dir = os.path.abspath(os.path.join(args.b, args.t))
    output_dir = os.path.abspath(os.path.join(args.o, timestamp))

    # log standard output
    if not args.d:
        os.makedirs(output_dir, exist_ok=True)
        log_file = os.path.join(output_dir, "vkrun.log")
        log_stream = open(log_file, "w")

    if "none" in args.include:
        if len(args.include) != 1:
            raise ValueError("none cannot be used with other entries")
        launch_none(args, timestamp, build_dir, output_dir)
    else:
        launch_rconf(args, timestamp, build_dir, output_dir)

    time_end = time.time()
    elapsed = math.ceil(time_end - time_start)
    print_log()
    print_log("run complete in %s" % get_elapsed_string(elapsed))
    print_log()
    if not args.d:
        log_stream.close()


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


def launch_none(args, timestamp, build_dir, output_dir):
    print_log()
    print_log("VKRUN")
    print_log("usage:  vkrun %s" % (" ".join(sys.argv[1:])))
    print_log("build:  %s/%s" % (os.path.abspath(args.b), args.t))
    print_log("run:    %s/%s" % (os.path.abspath(args.o), timestamp))
    print_log("sim:    %s" % args.s)
    print_log("kill:   %s" % args.k)
    print_log("base:   none")
    print_log("count:  1")
    print_log()

    if args.d:
        print_log("%s none" % get_label(0, 1))
    else:
        run(build_dir, output_dir, "none", args.s, args.k, 1, ExpandedEntry("none", None, None, None))


def launch_rconf(args, timestamp, build_dir, output_dir):
    executor_workers = os.cpu_count()
    if args.s == "xsim":
        executor_workers = 1

    if args.l < 0:
        raise ValueError("load factor must be larger than 0")
    elif args.l > 100:
        raise ValueError("load factor must be smaller than 100")

    if args.i == "":
        args.i = os.path.join(args.b, args.t, "verik/rconf.txt")
    rconf_file = os.path.abspath(args.i)

    # copy rconf file
    if not args.d:
        shutil.copyfile(rconf_file, os.path.join(output_dir, "rconf.txt"))

    # parse rconf entries
    entries = parse.parse(rconf_file)
    for name in args.include:
        parse.include(entries, name)
    seed_gen = SeedGenerator(args.r)
    expanded_entries = []
    for entry in entries:
        expanded_entries.extend(entry.get_expanded_entries(seed_gen, args.l))
    base_name = get_base_name(expanded_entries)

    print_log()
    print_log("VKRUN")
    print_log("usage:  vkrun %s" % (" ".join(sys.argv[1:])))
    print_log("build:  %s/%s" % (os.path.abspath(args.b), args.t))
    print_log("rconf:  %s" % os.path.abspath(args.i))
    print_log("run:    %s/%s" % (os.path.abspath(args.o), timestamp))
    print_log("sim:    %s" % args.s)
    print_log("seed:   %s" % args.r)
    print_log("load:   %d%%" % (int(args.l * 100)))
    print_log("kill:   %s" % args.k)
    print_log("base:   %s" % (base_name if base_name != "" else "all"))
    print_log("count:  %s" % len(expanded_entries))
    print_log()

    if args.d:
        for count, entry in enumerate(expanded_entries):
            print_log("%s %s" % (get_label(count, len(expanded_entries)), entry.name))
    else:
        with ThreadPoolExecutor(max_workers=executor_workers) as executor:
            for expanded_entry in expanded_entries:
                executor.submit(run, build_dir, output_dir, base_name, args.s, args.k, len(expanded_entries), expanded_entry)
            executor.shutdown(wait=True)


def run(build_dir, output_dir, base_name, sim, timeout, total_count, expanded_entry):
    global run_exit
    time_start = time.time()
    result = True
    relative_name = expanded_entry.relative_name(base_name)
    sim_dir = os.path.join(output_dir, relative_name)
    os.makedirs(sim_dir, exist_ok=True)
    os.chdir(sim_dir)
    if run_exit:
        result = False
    else:
        try:
            if sim == "xsim":
                run_xsim(build_dir, sim_dir, timeout)
        except:
            result = False
    if result:
        open(os.path.join(sim_dir, "PASS"), "w").close()
    else:
        open(os.path.join(sim_dir, "FAIL"), "w").close()
    time_end = time.time()
    elapsed = int(math.ceil(time_end - time_start))
    log_result(total_count, expanded_entry.name, result, elapsed)


def run_xsim(input_dir, sim_dir, timeout):
    time_start = time.time()
    ln_target = os.path.join(input_dir, "xsim.dir/sim")
    ln_dir = os.path.join(sim_dir, "xsim.dir")
    os.makedirs(ln_dir, exist_ok=True)
    subprocess.run(["ln", "-s", ln_target, ln_dir], check=True)
    devnull = open(os.devnull, "w")
    process = subprocess.Popen(["xsim", "-R", "sim"], stdout=devnull)
    while process.poll() is None:
        time.sleep(1)
        time_current = time.time()
        if 0 < timeout < time_current - time_start:
            process.kill()
            raise RuntimeError("timeout of %d exceeded" % timeout)
        if run_exit:
            process.kill()
            raise RuntimeError("exit signal asserted")


def log_result(total_count, name, result, elapsed):
    global run_count
    with print_lock:
        if result:
            color_string = u"\u001B[32m\u001B[1m"  # ANSI green bold
            color_string += get_label(run_count, total_count, result, elapsed)
            color_string += u"\u001B[0m"  # ANSI reset
            color_string += u"\u001B[32m"  # ANSI green
            color_string += " %s" % name
            color_string += u"\u001B[0m"  # ANSI reset
        else:
            color_string = u"\u001B[31m\u001B[1m"  # ANSI red bold
            color_string += get_label(run_count, total_count, result, elapsed)
            color_string += u"\u001B[0m"  # ANSI reset
            color_string += u"\u001B[31m"  # ANSI red
            color_string += " %s" % name
            color_string += u"\u001B[0m"  # ANSI reset
        plain_string = get_label(run_count, total_count, result, elapsed)
        plain_string += " %s" % name
        print_log(color_string, plain_string)
        run_count += 1


def get_label(count, total_count, result=None, elapsed=None):
    count_string = str(count + 1).zfill(len(str(total_count)))
    if result is None:
        return "[%s/%d]" % (count_string, total_count)
    elif result:
        return "[%s/%d PASS %s]" % (count_string, total_count, get_elapsed_string(elapsed))
    else:
        return "[%s/%d FAIL %s]" % (count_string, total_count, get_elapsed_string(elapsed))


def get_elapsed_string(elapsed):
    if elapsed is None:
        return "00:00:00"
    else:
        seconds = elapsed % 60
        elapsed /= 60
        minutes = elapsed % 60
        elapsed /= 60
        hours = elapsed
        return "%02d:%02d:%02d" % (hours, minutes, seconds)


def print_log(color_string=None, plain_string=None):
    global log_stream
    if color_string is None:
        color_string = ""
    if plain_string is None:
        plain_string = color_string
    if isatty:
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
