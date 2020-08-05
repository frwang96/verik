#!/usr/bin/python3

import argparse
from datetime import datetime
import time
import math
import os
import subprocess
import sys
import signal
from threading import Lock
from concurrent.futures import ThreadPoolExecutor
from vkrun import parse


isatty = sys.stdout.isatty()
log_stream = None
run_exit = False
run_count = 0
run_result = {}
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

    if args.s not in ["xsim"]:
        raise ValueError("unsupported simulator %s" % args.s)

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
    if not args.d:
        os.makedirs(output_dir, exist_ok=True)
        log_file = os.path.join(output_dir, "vkrun.log")
        log_stream = open(log_file, "w")

    print_log()
    print_log("build: %s" % args.t)
    print_log("run:   %s" % timestamp)
    print_log()

    # generate test stubs
    stubs = parse.parse(stubs_file)
    for full_name in args.stub:
        parse.include(stubs, full_name)
    included_stubs = stubs.get_included()
    included_stubs.generate_rseeds(args.r, args.l)
    total_count = included_stubs.count()

    if args.d:
        count = 0
        for stub in included_stubs.list():
            if stub.rseeds:
                for rseed in stub.rseeds:
                    print_log("%s %s/%s" % (get_label(count, total_count), stub.full_name, rseed))
                    count += 1
            else:
                print_log("%s %s" % (get_label(count, total_count), stub.full_name))
                count += 1
    else:
        with ThreadPoolExecutor(max_workers=os.cpu_count()) as executor:
            for stub in included_stubs.list():
                if stub.rseeds:
                    for rseed in stub.rseeds:
                        executor.submit(run, build_dir, output_dir, args.s, total_count, stub, rseed)
                else:
                    executor.submit(run, build_dir, output_dir, args.s, total_count, stub, None)
            executor.shutdown(wait=True)

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


def run(build_dir, output_dir, sim, total_count, stub, rseed):
    global run_exit
    time_start = time.time()
    result = True
    sim_dir = os.path.join(output_dir, stub.full_name)
    full_name = stub.full_name
    if rseed is not None:
        sim_dir = os.path.join(sim_dir, rseed)
        full_name = full_name + "/" + rseed
    os.makedirs(sim_dir, exist_ok=True)
    os.chdir(sim_dir)
    if run_exit:
        result = False
    else:
        try:
            if sim == "xsim":
                run_xsim(build_dir, sim_dir)
        except:
            result = False

    if result:
        open(os.path.join(sim_dir, "PASS"), "w").close()
    else:
        open(os.path.join(sim_dir, "FAIL"), "w").close()
    time_end = time.time()
    elapsed = int(math.ceil(time_end - time_start))
    log_result(total_count, full_name, result, elapsed)


def run_xsim(input_dir, sim_dir):
    ln_target = os.path.join(input_dir, "xsim.dir/sim")
    ln_dir = os.path.join(sim_dir, "xsim.dir")
    os.makedirs(ln_dir, exist_ok=True)
    subprocess.run(["ln", "-s", ln_target, ln_dir], check=True)
    devnull = open(os.devnull, "w")
    subprocess.run(["xsim", "-R", "sim"], stdout=devnull, check=True)


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
        run_result[name] = (result, elapsed)


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
