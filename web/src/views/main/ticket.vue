<template>
  <p>
    <a-space>
      <a-date-picker v-model:value="params.date" valueFormat="YYYY-MM-DD" :disabled-date="disabledDate" placeholder="请选择日期"></a-date-picker>
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
        <a-space>
          <a-button type="primary" @click="toOrder(record)" :disabled="isExpire(record)">{{ isExpire(record) ? "过期" : "预订" }}</a-button>
          <router-link :to="{
            path: 'seat',
            query: {
              date: record.date,
              trainCode: record.trainCode,
              departure: record.departure,
              departureIndex: record.departureIndex,
              destination: record.destination,
              arrivalIndex: record.arrivalIndex
            }
          }">
            <a-button type="primary">座位销售图</a-button>
          </router-link>
          <a-button type="primary" @click="showStation(record)">途径车站</a-button>
        </a-space>
      </template>
      <template v-else-if="column.dataIndex === 'station'">
        {{ record.departure }}<br/>
        {{ record.destination }}<br/>
      </template>
      <template v-else-if="column.dataIndex === 'time'">
        {{ record.departureTime }}<br/>
        {{ record.arrivalTime }}
      </template>
      <template v-else-if="column.dataIndex === 'duration'">
        {{ calDuration(record.departureTime, record.arrivalTime) }}<br/>
        <div v-if="record.departureTime.replaceAll(':', '') >= record.arrivalTime.replaceAll(':', '')">
          次日到达
        </div>
        <div v-else>
          当日到达
        </div>
      </template>
      <template v-else-if="column.dataIndex === 'firstClass'">
        <div v-if="record.firstClass >= 0">
          {{ record.firstClass }}<br/>
          {{ record.firstClassPrice }}RMB
        </div>
        <div v-else>
          --
        </div>
      </template>
      <template v-else-if="column.dataIndex === 'secondClass'">
        <div v-if="record.secondClass >= 0">
          {{ record.secondClass }}<br/>
          {{ record.secondClassPrice }}RMB
        </div>
        <div v-else>
          --
        </div>
      </template>
      <template v-else-if="column.dataIndex === 'softSleeper'">
        <div v-if="record.softSleeper >= 0">
          {{ record.softSleeper }}<br/>
          {{ record.softSleeperPrice }}RMB
        </div>
        <div v-else>
          --
        </div>
      </template>
      <template v-else-if="column.dataIndex === 'hardSleeper'">
        <div v-if="record.hardSleeper >= 0">
          {{ record.hardSleeper }}<br/>
          {{ record.hardSleeperPrice }}RMB
        </div>
        <div v-else>
          --
        </div>
      </template>
    </template>
  </a-table>
  <!-- 途经车站 -->
  <a-modal style="top: 30px" v-model:visible="visible" :title="null" :footer="null" :closable="false">
    <a-table :data-source="stations" :pagination="false">
      <a-table-column key="index" title="站序" data-index="index"/>
      <a-table-column key="name" title="站名" data-index="name"/>
      <a-table-column key="entryTime" title="进站时间" data-index="entryTime">
        <template #default="{ record }">
          {{ record.index === 1 ? '-' : record.exitTime }}
        </template>
      </a-table-column>
      <a-table-column key="exitTime" title="出站时间" data-index="exitTime">
        <template #default="{ record }">
          {{ record.index === stations.length ? '-' : record.exitTime }}
        </template>
      </a-table-column>
      <a-table-column key="stopTime" title="停站时长" data-index="stopTime">
        <template #default="{ record }">
          {{ record.index === 1 || record.index === stations.length  ? '-' : record.stopTime }}
        </template>
      </a-table-column>
    </a-table>
  </a-modal>
</template>

<script>
import {defineComponent, ref, onMounted} from 'vue';
import {notification} from "ant-design-vue";
import axios from "axios";
import StationSelectView from "@/components/station-select.vue";
import dayjs from "dayjs";
import router from "@/router";

export default defineComponent({
  name: "ticket-view",
  components: {StationSelectView},
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
      date: null,
      departure: null,
      destination: null
    });
    const columns = [
      {
        title: '车次编号',
        dataIndex: 'trainCode',
        key: 'trainCode',
      },
      {
        title: '车站',
        dataIndex: 'station',
        key: 'station',
      },
      {
        title: '时间',
        dataIndex: 'time',
        key: 'time',
      },
      {
        title: '历时',
        dataIndex: 'duration',
        key: 'duration'
      },
      {
        title: '一等座',
        dataIndex: 'firstClass',
        key: 'firstClass',
      },
      {
        title: '二等座',
        dataIndex: 'secondClass',
        key: 'secondClass',
      },
      {
        title: '软卧',
        dataIndex: 'softSleeper',
        key: 'softSleeper',
      },
      {
        title: '硬卧',
        dataIndex: 'hardSleeper',
        key: 'hardSleeper',
      },
      {
        title: '操作',
        dataIndex: 'action'
      }
    ];

    const handleQuery = (param) => {
      if (Tool.isEmpty(params.value.date)) {
        notification.error({description: "请输入日期"});
        return;
      }
      if (Tool.isEmpty(params.value.departure)) {
        notification.error({description: "请输入出发地"});
        return;
      }
      if (Tool.isEmpty(params.value.destination)) {
        notification.error({description: "请输入目的地"});
        return;
      }
      if (!param) {
        param = {
          page: 1,
          pageSize: pagination.value.pageSize
        };
      }
      // 保存查询参数
      SessionStorage.set(SESSION_TICKET_PARAMS, params.value);
      loading.value = true;
      axios.get("/business/daily-train-ticket/query-list", {
        params: {
          page: param.page,
          pageSize: param.pageSize,
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

    const toOrder = (record) => {
      dailyTrainTicket.value = Tool.copy(record);
      SessionStorage.set(SESSION_ORDER, dailyTrainTicket.value);
      router.push("/order")
    };

    // ---------------------- 途经车站 ----------------------
    const stations = ref([]);
    const showStation = record => {
      visible.value = true;
      axios.get("/business/daily-train-station/query-by-train-code", {
        params: {
          date: record.date,
          trainCode: record.trainCode
        }
      }).then((response) => {
        let data = response.data;
        if (data.success) {
          stations.value = data.content;
        } else {
          notification.error({description: data.message});
        }
      });
    };

    // 不能选择今天以前及两周以后的日期
    const disabledDate = current => {
      return current && (current <= dayjs().add(-1, 'day') || current > dayjs().add(14, 'day'));
    };

    // 判断是否过期
    const isExpire = (record) => {
      // 标准时间：2000/01/01 00:00:00
      let departureDateTimeString = record.date.replace(/-/g, "/") + " " + record.departureTime;
      let departureDateTime = new Date(departureDateTimeString);

      //当前时间
      let now = new Date();

      console.log(departureDateTime)
      return now.valueOf() >= departureDateTime.valueOf();
    };

    onMounted(() => {
      params.value = SessionStorage.get(SESSION_TICKET_PARAMS) || {};
      if (Tool.isNotEmpty(params.value)) {
        handleQuery({
          page: 1,
          pageSize: pagination.value.pageSize
        });
      }
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
      calDuration,
      toOrder,
      showStation,
      stations,
      disabledDate,
      isExpire
    };
  },
});
</script>
