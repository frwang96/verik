/*
 * Copyright (c) 2021 Francis Wang
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

import verik.base.*

class Cache: Module() {

    @inout val rx_bp = t_MemRxBusPort()
    @inout val tx_bp = t_MemTxBusPort()

    @com fun bypass() {
        tx_bp.rst = rx_bp.rst
        tx_bp.req_op = rx_bp.req_op
        tx_bp.req_addr = rx_bp.req_addr
        tx_bp.req_data = rx_bp.req_data
        rx_bp.rsp_vld = tx_bp.rsp_vld
        rx_bp.rsp_data = tx_bp.rsp_data
    }
}