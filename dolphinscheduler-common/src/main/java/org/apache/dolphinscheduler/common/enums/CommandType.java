/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dolphinscheduler.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * command types
 */
public enum CommandType {

    /**
     * command types
     * TODO 对应的执行状态在ExecutionStatus中找
     * 0 start a new process
     * 1 start a new process from current nodes
     * 2 recover tolerance fault process
     * 3 recover suspended process
     * 4 start process from failure task nodes
     * 5 complement data
     * 6 start a new process from scheduler
     * 7 repeat running a process
     * 8 pause a process
     * 9 stop a process
     * 10 recover waiting thread
     */
    START_PROCESS(0, "start a new process"),
    START_CURRENT_TASK_PROCESS(1, "start a new process from current nodes"),
    // TODO 恢复失败的流程(容错)
    RECOVER_TOLERANCE_FAULT_PROCESS(2, "recover tolerance fault process"),
    // TODO 恢复暂停的流程
    RECOVER_SUSPENDED_PROCESS(3, "recover suspended process"),
    // TODO 启动失败任务
    START_FAILURE_TASK_PROCESS(4, "start process from failure task nodes"),
    // TODO 补数据
    COMPLEMENT_DATA(5, "complement data"),
    SCHEDULER(6, "start a new process from scheduler"),
    // TODO 重跑
    REPEAT_RUNNING(7, "repeat running a process"),
    // TODO 暂停
    PAUSE(8, "pause a process"),
    STOP(9, "stop a process"),
    // TODO 恢复等待的线程
    RECOVER_WAITTING_THREAD(10, "recover waiting thread");

    CommandType(int code, String descp){
        this.code = code;
        this.descp = descp;
    }

    @EnumValue
    private final int code;
    private final String descp;

    public int getCode() {
        return code;
    }

    public String getDescp() {
        return descp;
    }

    public static CommandType of(Integer status){
        for(CommandType cmdType : values()){
            if(cmdType.getCode() == status){
                return cmdType;
            }
        }
        throw new IllegalArgumentException("invalid status : " + status);
    }
}
