#  Copyright 2020 Francis Wang
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

import re
from vkrun.entry import Entry


pattern_seed = re.compile("/SEED_([0-9a-f]{8})")
pattern_len = 14


def parse(rconf_file):
    entries = []
    with open(rconf_file, "r") as file:
        pos = 0
        for line in file.readlines():
            if pos == 0:
                name = line.strip()
            elif pos == 1:
                fields = line.strip()
            elif pos == 2:
                fields_enc = line.strip()
            elif pos == 3:
                count = int(line.strip())
                entries.append(Entry(name, fields, fields_enc, count))
            pos = (pos + 1) % 4
    return entries


def include(entries, name):
    match = False
    if name == "all":
        for entry in entries:
            entry.include = True
            match = True
    else:
        seed = None
        match = pattern_seed.fullmatch(name[-pattern_len:])
        if match:
            seed = match.group(1)
            name = name[:-pattern_len]
        for entry in entries:
            if entry.match(name, seed):
                match = True
    if not match:
        raise ValueError("could not match %s" % name)
