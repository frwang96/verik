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
from vkrun.stub import StubList, StubEntry


pattern_seed = re.compile("[0-9a-f]{8}")


def parse(stubs_file):
    stubs = StubList("", "")
    stubs.stubs.append(StubEntry("base", "base", "", "", 0))
    with open(stubs_file, "r") as file:
        pos = 0
        for line in file.readlines():
            if pos == 0:
                full_name = line.strip()
            elif pos == 1:
                fields = line.strip()
            elif pos == 2:
                fields_enc = line.strip()
            elif pos == 3:
                count = int(line.strip())
                insert(stubs, full_name, fields, fields_enc, count)
            pos = (pos + 1) % 4
    return stubs


def insert(stubs, full_name, fields, fields_enc, count):
    parts = full_name.split("/")
    walk_full_name = ""
    walk_stubs = stubs
    for part in parts[:-1]:
        if walk_full_name == "":
            walk_full_name = part
        else:
            walk_full_name = walk_full_name + "/" + part
        walk_stub_next = walk_stubs.get_stub(part)
        if walk_stub_next is None:
            walk_stub_next = StubList(part, walk_full_name)
            walk_stubs.stubs.append(walk_stub_next)
        walk_stubs = walk_stub_next
    walk_stubs.stubs.append(StubEntry(parts[-1], full_name, fields, fields_enc, count))


def include(stubs, full_name):
    if full_name == "all":
        for stub in stubs.stubs:
            if stub.name != "base":
                if isinstance(stub, StubList):
                    stub.include = True
                    stub.include_all = True
                else:
                    stub.include = True
    else:
        parts = full_name.split("/")
        walk_stub = stubs
        for part in parts[:-1]:
            if isinstance(walk_stub, StubEntry):
                raise ValueError("unable to match stub %s expected list but got entry" % full_name)
            walk_stub = walk_stub.get_stub(part)
            if walk_stub is None:
                raise ValueError("unable to match stub %s could not find name %s" % (full_name, part))
            walk_stub.include = True
        if isinstance(walk_stub, StubList):
            walk_stub = walk_stub.get_stub(parts[-1])
            if walk_stub is None:
                raise ValueError("unable to match stub %s could not find name %s" % (full_name, parts[-1]))
            walk_stub.include = True
            walk_stub.include_all = True
        else:
            if pattern_seed.fullmatch(parts[-1]):
                walk_stub.rseeds.append(parts[-1])
            else:
                raise ValueError("unable to match stub %s could not find name %s" % (full_name, parts[-1]))
