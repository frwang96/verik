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

import random
import math


class Entry:

    def __init__(self, name, fields, fields_enc, count):
        self.name = name
        self.fields = fields
        self.fields_enc = fields_enc
        self.count = count
        self.extra_seeds = []
        self.include = False

    def __repr__(self):
        return self.name

    def match(self, name, seed):
        if seed is None:
            if self.name == name or self.name.startswith(name + "/"):
                self.include = True
                return True
            else:
                return False
        else:
            if self.name == name:
                self.extra_seeds.append(seed)
                return True
            else:
                return False

    def get_expanded_entries(self, seed_gen, load):
        if self.count == 0:
            if self.include:
                return [ExpandedEntry(self.name, self.fields, self.fields_enc, None)]
            else:
                return []
        else:
            seeds = []
            if self.include:
                count = int(math.ceil(self.count * load))
                if count <= 0:
                    count = 1
                seeds.extend(seed_gen.get_seeds(count))
            for seed in self.extra_seeds:
                if seed not in seeds:
                    seeds.append(seed)
            return [ExpandedEntry(self.name + "/SEED_" + seed, self.fields, self.fields_enc, seed) for seed in seeds]


class ExpandedEntry:

    def __init__(self, name, fields, fields_enc, seed):
        self.name = name
        self.fields = fields
        self.fields_enc = fields_enc
        self.seed = seed

    def relative_name(self, base_name):
        if base_name == "":
            return self.name
        elif base_name == self.name:
            return ""
        elif self.name.startswith(base_name + "/"):
            return self.name[len(base_name)+1:]
        else:
            raise ValueError("base name %s does not match entry name %s" % (base_name, self.name))


class SeedGenerator:

    def __init__(self, seed):
        self.random = random.Random()
        self.random.seed(seed)
        self.seeds = []

    def get_seeds(self, count):
        while len(self.seeds) < count:
            rand = "%08x" % self.random.getrandbits(32)
            while rand in self.seeds:
                rand = "%08x" % self.random.getrandbits(32)
            self.seeds.append(rand)
        return self.seeds[:count]


def get_base_name(entries):
    base_name = entries[0].name
    for entry in entries:
        if base_name != "" and base_name != entry.name and not entry.name.startswith(base_name + "/"):
            parts = zip(entry.name.split("/"), base_name.split("/"))
            base_name = ""
            for part in parts:
                if part[0] == part[1]:
                    if base_name == "":
                        base_name = part[0]
                    else:
                        base_name = base_name + "/" + part[0]
    return base_name
