<template>
  <p>
    <a-space>
      <train-select-view v-model:value="params.trainCode" width="200px"></train-select-view>
      <a-date-picker v-model:value="params.date" valueFormat="YYYY-MM-DD" placeholder="请选择日期"></a-date-picker>
      <station-select-view v-model:value="params.departure" width="200px"></station-select-view>
      <station-select-view v-model:value="params.destination" width="200px"></station-select-view>
      <a-button type="primary" @click="handleQuery()">查询</a-button>
    </a-space>
  </p>
  <a-table :dataSource="dailyTrainTickets"
           :columns="columns"
           :pagination="pagination"
           @change="handleTableChange"
           :loading="loading">
    <template #bodyCell="{ column, record }">
      <template v-if="column.dataIndex === 'action'">
      </template>
      <template v-else-if="column.dataIndex === 'station'">
        {{record.departure}}<br/>
        {{record.destination}}<br/>
      </template>
      <template v-else-if="column.dataIndex === 'time'">
        {{record.departureTime}}<br/>
        {{record.arrivalTime}}
      </template>
      <template v-else-if="column.dataIndex === 'duration'">
        {{calDuration(record.departureTime, record.arrivalTime)}}<br/>
        <div v-if="record.departureTime.replaceAll(':', '') >= record.arrivalTime.replaceAll(':', '')">
          次日到达
        </div>
        <div v-else>
          当日到达
        </div>
      </template>
      <template v-else-if="column.dataIndex === 'firstClass'">
        <div v-if="record.firstClass >= 0">
          {{record.firstClass}}<br/>
          {{record.firstClassPrice}}RMB
        </div>
        <div v-else>
          --
        </div>
      </template>
      <template v-else-if="column.dataIndex === 'secondClass'">
        <div v-if="record.secondClass >= 0">
          {{record.secondClass}}<br/>
          {{record.secondClassPrice}}RMB
        </div>
        <div v-else>
          --
        </div>
      </template>
      <template v-else-if="column.dataIndex === 'softSleeper'">
        <div v-if="record.softSleeper >= 0">
          {{record.softSleeper}}<br/>
          {{record.softSleeperPrice}}RMB
        </div>
        <div v-else>
          --
        </div>
      </template>
      <template v-else-if="column.dataIndex === 'hardSleeper'">
        <div v-if="record.hardSleeper >= 0">
          {{record.hardSleeper}}<br/>
          {{record.hardSleeperPrice}}RMB
        </div>
        <div v-else>
          --
        </div>
      </template>
    </template>
  </a-table>
</template>

<script>
import {defineComponent, ref, onMounted} from 'vue';
import {notification} from "ant-design-vue";
import axios from "axios";
import TrainSelectView from "@/components/train-select.vue";
import StationSelectView from "@/components/station-select.vue";
import dayjs from "dayjs";

export default defineComponent({
  name: "daily-train-ticket-view",
  components: {StationSelectView, TrainSelectView},
  setup() {
    const visible = ref(false);
    let dailyTrainTicket = ref({
      id: undefined,
      date: undefined,
      trainCode: undefined,
      departure: undefined,
      departurePinyin: undefined,
      departureTime: undefined,
      departureIndex: undefined,
      destination: undefined,
      destinationPinyin: undefined,
      arrivalTime: undefined,
      arrivalIndex: undefined,
      firstClass: undefined,
      firstClassPrice: undefined,
      secondClass: undefined,
      secondClassPrice: undefined,
      softSleeper: undefined,
      softSleeperPrice: undefined,
      hardSleeper: undefined,
      hardSleeperPrice: undefined,
      createTime: undefined,
      updateTime: undefined,
    });
    const dailyTrainTickets = ref([]);
    // 分页的三个属性名是固定的
    const pagination = ref({
      total: 0,
      current: 1,
      pageSize: 10,
    });
    let loading = ref(false);
    const params = ref({
      trainCode: null,
      date: null,
      departure: null,
      destination: null
    });
    const columns = [
      {
        title: '日期',
        dataIndex: 'date',
        key: 'date',
      },
      {
        title: '车次编号',
        dataIndex: 'trainCode',
        key: 'trainCode',
      },
      {
        title: '车站',
        dataIndex: 'station',
      },
      {
        title: '时间',
        dataIndex: 'time',
      },
      {
        title: '历时',
        dataIndex: 'duration',
      },
      // {
      //   title: '出发站',
      //   dataIndex: 'departure',
      //   key: 'departure',
      // },
      // {
      //   title: '出发站拼音',
      //   dataIndex: 'departurePinyin',
      //   key: 'departurePinyin',
      // },
      // {
      //   title: '出发时间',
      //   dataIndex: 'departureTime',
      //   key: 'departureTime',
      // },
      // {
      //   title: '出发站序',
      //   dataIndex: 'departureIndex',
      //   key: 'departureIndex',
      // },
      // {
      //   title: '到达站',
      //   dataIndex: 'destination',
      //   key: 'destination',
      // },
      // {
      //   title: '到达站拼音',
      //   dataIndex: 'destinationPinyin',
      //   key: 'destinationPinyin',
      // },
      // {
      //   title: '到站时间',
      //   dataIndex: 'arrivalTime',
      //   key: 'arrivalTime',
      // },
      // {
      //   title: '到站站序',
      //   dataIndex: 'arrivalIndex',
      //   key: 'arrivalIndex',
      // },
      {
        title: '一等座余票',
        dataIndex: 'firstClass',
        key: 'firstClass',
      },
      // {
      //   title: '一等座票价',
      //   dataIndex: 'firstClassPrice',
      //   key: 'firstClassPrice',
      // },
      {
        title: '二等座余票',
        dataIndex: 'secondClass',
        key: 'secondClass',
      },
      // {
      //   title: '二等座票价',
      //   dataIndex: 'secondClassPrice',
      //   key: 'secondClassPrice',
      // },
      {
        title: '软卧余票',
        dataIndex: 'softSleeper',
        key: 'softSleeper',
      },
      // {
      //   title: '软卧票价',
      //   dataIndex: 'softSleeperPrice',
      //   key: 'softSleeperPrice',
      // },
      {
        title: '硬卧余票',
        dataIndex: 'hardSleeper',
        key: 'hardSleeper',
      },
      // {
      //   title: '硬卧票价',
      //   dataIndex: 'hardSleeperPrice',
      //   key: 'hardSleeperPrice',
      // },
    ];

    const handleQuery = (param) => {
      if (!param) {
        param = {
          page: 1,
          pageSize: pagination.value.pageSize
        };
      }
      loading.value = true;
      axios.get("/business/admin/daily-train-ticket/query-list", {
        params: {
          page: param.page,
          pageSize: param.pageSize,
          trainCode: params.value.trainCode,
          date: params.value.date,
          departure: params.value.departure,
          destination: params.value.destination
        }
      }).then((response) => {
        loading.value = false;
        let data = response.data;
        if (data.success) {
          dailyTrainTickets.value = data.content.rows;
          // 设置分页控件的值
          pagination.value.current = param.page;
          pagination.value.total = data.content.total;
        } else {
          notification.error({description: data.message});
        }
      });
    };

    const handleTableChange = (page) => {
      // console.log("看看自带的分页参数都有啥：" + JSON.stringify(page));
      pagination.value.pageSize = page.pageSize;
      handleQuery({
        page: page.current,
        pageSize: page.pageSize
      });
    };

    const calDuration = (departureTime, arrivalTime) => {
      let diff = dayjs(arrivalTime, 'HH:mm:ss').diff(dayjs(departureTime, 'HH:mm:ss'), 'seconds');
      return dayjs('00:00:00', 'HH:mm:ss').second(diff).format('HH:mm:ss');
    };

    onMounted(() => {
      handleQuery({
        page: 1,
        pageSize: pagination.value.pageSize
      });
    });

    return {
      dailyTrainTicket,
      visible,
      dailyTrainTickets,
      pagination,
      loading,
      params,
      columns,
      handleTableChange,
      handleQuery,
      calDuration
    };
  },
});
</script>
