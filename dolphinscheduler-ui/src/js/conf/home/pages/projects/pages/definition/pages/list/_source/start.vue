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
<template>
  <div class="start-process-model">
    <div class="clearfix list">
      <div class="text">
        {{$t('Process Name')}}
      </div>
      <div style="line-height: 32px;">{{workflowName}}</div>
    </div>
    <div class="clearfix list">
      <div class="text">
        {{$t('Failure Strategy')}}
      </div>
      <div class="cont">
        <el-radio-group v-model="failureStrategy" style="margin-top: 7px;" size="small">
          <el-radio :label="'CONTINUE'">{{$t('Continue')}}</el-radio>
          <el-radio :label="'END'">{{$t('End')}}</el-radio>
        </el-radio-group>
      </div>
    </div>
    <div class="clearfix list" v-if="sourceType === 'contextmenu'" style="margin-top: -8px;">
      <div class="text">
        {{$t('Node execution')}}
      </div>
      <div class="cont" style="padding-top: 6px;">
        <el-radio-group v-model="taskDependType" size="small">
          <el-radio :label="'TASK_POST'">{{$t('Backward execution')}}</el-radio>
          <el-radio :label="'TASK_PRE'">{{$t('Forward execution')}}</el-radio>
          <el-radio :label="'TASK_ONLY'">{{$t('Execute only the current node')}}</el-radio>
        </el-radio-group>
      </div>
    </div>
    <div class="clearfix list">
      <div class="text">
        {{$t('Notification strategy')}}
      </div>
      <div class="cont">
        <el-select style="width: 200px;" v-model="warningType" size="small">
          <el-option
                  v-for="city in warningTypeList"
                  :key="city.id"
                  :value="city.id"
                  :label="city.code">
          </el-option>
        </el-select>
      </div>
    </div>
    <div class="clearfix list">
      <div class="text">
        {{$t('Process priority')}}
      </div>
      <div class="cont">
        <m-priority v-model="processInstancePriority"></m-priority>
      </div>
    </div>
    <div class="clearfix list">
      <div class="text">
        {{$t('Worker group')}}
      </div>
      <div class="cont">
        <m-worker-groups v-model="workerGroup"></m-worker-groups>
      </div>
    </div>
    <div class="clearfix list">
      <div class="text">
        {{$t('Notification group')}}
      </div>
      <div class="cont">
        <el-select
           style="width: 200px;"
           size="small"
           v-model="warningGroupId"
          :disabled="!notifyGroupList.length">
          <el-input slot="trigger" slot-scope="{ selectedModel }" readonly :placeholder="$t('Please select a notification group')" size="small" :value="selectedModel ? selectedModel.label : ''" style="width: 200px;" @on-click-icon.stop="warningGroupId = ''">
            <em slot="suffix" class="el-icon-error" style="font-size: 15px;cursor: pointer;" v-show="warningGroupId"></em>
            <em slot="suffix" class="el-icon-bottom" style="font-size: 12px;" v-show="!warningGroupId"></em>
          </el-input>
          <el-option
                  v-for="city in notifyGroupList"
                  :key="city.id"
                  :value="city.id"
                  :label="city.code">
          </el-option>
        </el-select>
      </div>
    </div>
    <div class="clearfix list">
      <div class="text">
        {{$t('Recipient')}}
      </div>
      <div class="cont" style="width: 688px;">
        <m-email v-model="receivers" :repeat-data="receiversCc"></m-email>
      </div>
    </div>
    <div class="clearfix list">
      <div class="text">
        {{$t('Cc')}}
      </div>
      <div class="cont" style="width: 688px;">
        <m-email v-model="receiversCc" :repeat-data="receivers"></m-email>
      </div>
    </div>
    <div class="clearfix list">
      <div class="text">
        {{$t('Complement Data')}}
      </div>
      <div class="cont">
        <div style="padding-top: 6px;">
          <el-checkbox v-model="execType" size="small">{{$t('Whether it is a complement process?')}}</el-checkbox>
        </div>
      </div>
    </div>
    <template v-if="execType">
      <div class="clearfix list" style="margin:-6px 0 16px 0">
        <div class="text">
          {{$t('Mode of execution')}}
        </div>
        <div class="cont">
          <el-radio-group v-model="runMode" style="margin-top: 7px;">
            <el-radio :label="'RUN_MODE_SERIAL'">{{$t('Serial execution')}}</el-radio>
            <el-radio :label="'RUN_MODE_PARALLEL'">{{$t('Parallel execution')}}</el-radio>
          </el-radio-group>
        </div>
      </div>
      <div class="clearfix list">
        <div class="text">
          {{$t('Schedule date')}}
        </div>
        <div class="cont">
          <el-date-picker
            style="width: 360px"
            v-model="scheduleTime"
            size="small"
            @change="_datepicker"
            type="datetimerange"
            range-separator="-"
            :start-placeholder="$t('startDate')"
            :end-placeholder="$t('endDate')"
            value-format="yyyy-MM-dd HH:mm:ss">
          </el-date-picker>
        </div>
      </div>
    </template>
    <div class="submit">
      <el-button type="text" size="small" @click="close()"> {{$t('Cancel')}} </el-button>
      <el-button type="primary" size="small" round :loading="spinnerLoading" @click="ok()">{{spinnerLoading ? 'Loading...' : $t('Start')}} </el-button>
    </div>
  </div>
</template>
<script>
  import dayjs from 'dayjs'
  import mEmail from './email.vue'
  import store from '@/conf/home/store'
  import { warningTypeList } from './util'
  import mPriority from '@/module/components/priority/priority'
  import mWorkerGroups from '@/conf/home/pages/dag/_source/formModel/_source/workerGroups'

  export default {
    name: 'start-process',
    data () {
      return {
        store,
        processDefinitionId: 0,
        failureStrategy: 'CONTINUE',
        warningTypeList: warningTypeList,
        workflowName: '',
        warningType: '',
        notifyGroupList: [],
        warningGroupId: '',
        scheduleTime: '',
        spinnerLoading: false,
        execType: false,
        taskDependType: 'TASK_POST',
        receivers: [],
        receiversCc: [],
        runMode: 'RUN_MODE_SERIAL',
        processInstancePriority: 'MEDIUM',
        workerGroup: 'default'

      }
    },
    props: {
      startData: Object,
      startNodeList: {
        type: String,
        default: ''
      },
      sourceType: String
    },
    methods: {
      _datepicker (val) {
        this.scheduleTime = val
      },
      _start () {
        this.spinnerLoading = true
        let param = {
          processDefinitionId: this.startData.id,
          scheduleTime: this.scheduleTime.length && this.scheduleTime.join(',') || '',
          failureStrategy: this.failureStrategy,
          warningType: this.warningType,
          warningGroupId: this.warningGroupId === '' ? 0 : this.warningGroupId,
          execType: this.execType ? 'COMPLEMENT_DATA' : null,
          startNodeList: this.startNodeList,
          taskDependType: this.taskDependType,
          runMode: this.runMode,
          processInstancePriority: this.processInstancePriority,
          receivers: this.receivers.join(',') || '',
          receiversCc: this.receiversCc.join(',') || '',
          workerGroup: this.workerGroup
        }
        // Executed from the specified node
        if (this.sourceType === 'contextmenu') {
          param.taskDependType = this.taskDependType
        }
        this.store.dispatch('dag/processStart', param).then(res => {
          this.$message.success(res.msg)
          this.$emit('onUpdateStart')
          setTimeout(() => {
            this.spinnerLoading = false
            this.close()
          }, 500)
        }).catch(e => {
          this.$message.error(e.msg || '')
          this.spinnerLoading = false
        })
      },
      _getNotifyGroupList () {
        return new Promise((resolve, reject) => {
          this.store.dispatch('dag/getNotifyGroupList').then(res => {
            this.notifyGroupList = res
            resolve()
          })
        })
      },
      _getReceiver () {
        this.store.dispatch('dag/getReceiver', { processDefinitionId: this.startData.id }).then(res => {
          this.receivers = res.receivers && res.receivers.split(',') || []
          this.receiversCc = res.receiversCc && res.receiversCc.split(',') || []
        })
      },
      ok () {
        this._start()
      },
      close () {
        this.$emit('closeStart')
      }
    },
    watch: {
      execType (a) {
        this.scheduleTime = a ? [dayjs().format('YYYY-MM-DD 00:00:00'), dayjs().format('YYYY-MM-DD 00:00:00')] : ''
      }
    },
    created () {
      this.warningType = this.warningTypeList[0].id
      this.workflowName = this.startData.name

      this._getReceiver()
      let stateWorkerGroupsList = this.store.state.security.workerGroupsListAll || []
      if (stateWorkerGroupsList.length) {
        this.workerGroup = stateWorkerGroupsList[0].id
      } else {
        this.store.dispatch('security/getWorkerGroupsAll').then(res => {
          this.$nextTick(() => {
            if (res.length > 0) {
              this.workerGroup = res[0].id
            }
          })
        })
      }
    },
    mounted () {
      this._getNotifyGroupList().then(() => {
        this.$nextTick(() => {
          this.warningGroupId = ''
        })
      })
      this.workflowName = this.startData.name
    },
    computed: {},
    components: { mEmail, mPriority, mWorkerGroups }
  }
</script>

<style lang="scss" rel="stylesheet/scss">
  .start-process-model {
    width: 860px;
    min-height: 300px;
    background: #fff;
    border-radius: 3px;
    .title-box {
      margin-bottom: 18px;
      span {
        padding-left: 30px;
        font-size: 16px;
        padding-top: 29px;
        display: block;
      }
    }
    .list {
      margin-bottom: 14px;
      .text {
        width: 140px;
        float: left;
        text-align: right;
        line-height: 32px;
        padding-right: 8px;
      }
      .cont {
        width: 350px;
        float: left;
        .add-email-model {
          padding: 20px;
        }

      }
    }
    .submit {
      text-align: right;
      padding-right: 30px;
      padding-top: 10px;
      padding-bottom: 30px;
    }
  }
</style>
