/*
 * Copyright (c) 2020 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

tasks.register("test") {
    group = "verification"
    dependsOn(gradle.includedBuild("verik-compiler").task(":test"))
}

tasks.register("clean") {
    group = "build"
    dependsOn(gradle.includedBuild("verik-core").task(":clean"))
    dependsOn(gradle.includedBuild("verik-compiler").task(":clean"))
    dependsOn(gradle.includedBuild("verik-examples").task(":cache:clean"))
    dependsOn(gradle.includedBuild("verik-examples").task(":count:clean"))
    dependsOn(gradle.includedBuild("verik-examples").task(":lock:clean"))
}
