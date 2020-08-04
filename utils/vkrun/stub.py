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

class StubEntry:

    def __init__(self, name, full_name, fields, fields_enc, count):
        self.name = name
        self.full_name = full_name
        self.fields = fields
        self.fields_enc = fields_enc
        self.count = count
        self.rseeds = []
        self.include = False

    def __repr__(self):
        return "%s %s" % (self.name, self.rseeds)

    def generate_rseeds(self, rseed, load):
        for i in range(self.count):
            self.rseeds.append("%08x" % i)


class StubList:

    def __init__(self, name, full_name):
        self.name = name
        self.full_name = full_name
        self.stubs = []
        self.include = False
        self.include_all = False

    def __repr__(self):
        if self.name == "":
            return "%s" % self.stubs
        else:
            return "%s %s" % (self.name, self.stubs)

    def get_stub(self, name):
        for stub in self.stubs:
            if stub.name == name:
                return stub
        return None

    def get_included(self):
        if self.include_all:
            return self
        else:
            stubs = []
            for stub in self.stubs:
                if stub.include:
                    if isinstance(stub, StubList):
                        stubs.append(stub.get_included())
                    else:
                        stubs.append(stub)
            stub_list = StubList(self.name, self.full_name)
            stub_list.stubs = stubs
            return stub_list

    def generate_rseeds(self, rseed, load):
        for stub in self.stubs:
            stub.generate_rseeds(rseed, load)

    def count(self):
        count = 0
        for stub in self.stubs:
            if isinstance(stub, StubList):
                count += stub.count()
            else:
                if stub.rseeds:
                    count += len(stub.rseeds)
                else:
                    count += 1
        return count

    def list(self):
        for stub in self.stubs:
            if isinstance(stub, StubList):
                for sub_stub in stub.list():
                    yield sub_stub
            else:
                yield stub
