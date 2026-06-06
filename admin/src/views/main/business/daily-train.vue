<template>
  <p>
    <a-space>
      <a-date-picker v-model:value="params.date" valueFormat="YYYY-MM-DD" placeholder="请选择日期"/>
      <train-select-view v-model:value="params.code" width="200px"></train-select-view>
      <a-button type="primary" @click="handleQuery()">查询</a-button>
      <a-button type="primary" @click="onAdd">新增</a-button>
      <a-button type="primary" danger @click="onClickGenerateDailyTrain">手动生成车次信息</a-button>
    </a-space>
  </p>
  <a-table :dataSource="dailyTrains"
           :columns="columns"
           :pagination="pagination"
           @change="handleTableChange"
           :loading="loading">
    <template #bodyCell="{ column, record }">
      <template v-if="column.dataIndex === 'action'">
        <a-space>
          <a-popconfirm
              title="删除后不可恢复，确认删除?"
              @confirm="onDelete(record)"
              ok-text="确认" cancel-text="取消">
            <a style="color: red">删除</a>
          </a-popconfirm>
          <a @click="onEdit(record)">编辑</a>
        </a-space>
      </template>
      <template v-else-if="column.dataIndex === 'type'">
        <span v-for="item in TRAIN_TYPE_ARRAY" :key="item.code">
          <span v-if="item.code === record.type">
            {{ item.desc }}
          </span>
        </span>
      </template>
    </template>
  </a-table>
  <a-modal v-model:open="visible" title="每日车次" @ok="handleOk"
           ok-text="确认" cancel-text="取消">
    <a-form :model="dailyTrain" :label-col="{span: 4}" :wrapper-col="{ span: 20 }">
      <a-form-item label="日期">
        <a-date-picker v-model:value="dailyTrain.date" valueFormat="YYYY-MM-DD" placeholder="请选择日期"/>
      </a-form-item>
      <a-form-item label="车次编号">
        <train-select-view v-model:value="dailyTrain.code" @change="onChangeCode"></train-select-view>
      </a-form-item>
      <a-form-item label="车次类型">
        <a-select v-model:value="dailyTrain.type">
          <a-select-option v-for="item in TRAIN_TYPE_ARRAY" :key="item.code" :value="item.code">
            {{ item.desc }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="始发站">
        <a-input v-model:value="dailyTrain.departure"/>
      </a-form-item>
      <a-form-item label="始发站拼音">
        <a-input v-model:value="dailyTrain.departurePinyin"/>
      </a-form-item>
      <a-form-item label="出发时间">
        <a-time-picker v-model:value="dailyTrain.departureTime" valueFormat="HH:mm:ss" placeholder="请选择时间"/>
      </a-form-item>
      <a-form-item label="终点站">
        <a-input v-model:value="dailyTrain.destination"/>
      </a-form-item>
      <a-form-item label="终点站拼音">
        <a-input v-model:value="dailyTrain.destinationPinyin"/>
      </a-form-item>
      <a-form-item label="到站时间">
        <a-time-picker v-model:value="dailyTrain.arrivalTime" valueFormat="HH:mm:ss" placeholder="请选择时间"/>
      </a-form-item>
    </a-form>
  </a-modal>
  <a-modal v-model:visible="generateDailyVisible" title="生成车次" @ok="handleGenerateDailyOK"
           :confirm-loading="generateDailyLoading" ok-text="确认" cancel-text="取消">
    <a-form :model="generateDaily" :label-col="{span: 4}" :wrapper-col="{ span: 20 }">
      <a-form-item label="日期">
        <a-date-picker v-model:value="generateDaily.date" placeholder="请选择日期"/>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script>
import {defineComponent, ref, onMounted} from 'vue';
import {notification} from "ant-design-vue";
import axios from "axios";
import TrainSelectView from "@/components/train-select.vue";
import dayjs from 'dayjs';

export default defineComponent({
  name: "daily-train-view",
  components: {TrainSelectView},
  setup() {
    const TRAIN_TYPE_ARRAY = window.TRAIN_TYPE_ARRAY;
    const visible = ref(false);
    let dailyTrain = ref({
      id: undefined,
      date: undefined,
      code: undefined,
      type: undefined,
      departure: undefined,
      departurePinyin: undefined,
      departureTime: undefined,
      destination: undefined,
      destinationPinyin: undefined,
      arrivalTime: undefined,
      createTime: undefined,
      updateTime: undefined,
    });
    const dailyTrains = ref([]);
    // 分页的三个属性名是固定的
    const pagination = ref({
      total: 0,
      current: 1,
      pageSize: 10,
    });
    let loading = ref(false);
    let params = ref({
      date: null,
      code: null
    });
    const generateDaily = ref({
      date: null,
    });
    const generateDailyVisible = ref(false);
    const generateDailyLoading = ref(false);
    const columns = [
      {
        title: '日期',
        dataIndex: 'date',
        key: 'date',
      },
      {
        title: '车次编号',
        dataIndex: 'code',
        key: 'code',
      },
      {
        title: '车次类型',
        dataIndex: 'type',
        key: 'type',
      },
      {
        title: '始发站',
        dataIndex: 'departure',
        key: 'departure',
      },
      {
        title: '始发站拼音',
        dataIndex: 'departurePinyin',
        key: 'departurePinyin',
      },
      {
        title: '出发时间',
        dataIndex: 'departureTime',
        key: 'departureTime',
      },
      {
        title: '终点站',
        dataIndex: 'destination',
        key: 'destination',
      },
      {
        title: '终点站拼音',
        dataIndex: 'destinationPinyin',
        key: 'destinationPinyin',
      },
      {
        title: '到站时间',
        dataIndex: 'arrivalTime',
        key: 'arrivalTime',
      },
      {
        title: '操作',
        dataIndex: 'action'
      }
    ];

    const onAdd = () => {
      dailyTrain.value = {};
      visible.value = true;
    };

    const onEdit = (record) => {
      dailyTrain.value = window.Tool.copy(record);
      visible.value = true;
    };

    const onDelete = (record) => {
      axios.delete("/business/admin/daily-train/delete/" + record.id).then((response) => {
        const data = response.data;
        if (data.success) {
          notification.success({description: "删除成功！"});
          handleQuery({
            page: pagination.value.current,
            size: pagination.value.pageSize,
          });
        } else {
          notification.error({description: data.message});
        }
      });
    };

    const handleOk = () => {
      axios.post("/business/admin/daily-train/save", dailyTrain.value).then((response) => {
        let data = response.data;
        if (data.success) {
          notification.success({description: "保存成功！"});
          visible.value = false;
          handleQuery({
            page: pagination.value.current,
            pageSize: pagination.value.pageSize
          });
        } else {
          notification.error({description: data.message});
        }
      });
    };

    const handleQuery = (param) => {
      if (!param) {
        param = {
          page: 1,
          pageSize: pagination.value.pageSize
        };
      }
      loading.value = true;
      axios.get("/business/admin/daily-train/query-list", {
        params: {
          page: param.page,
          pageSize: param.pageSize,
          code: params.value.code,
          date: params.value.date
        }
      }).then((response) => {
        loading.value = false;
        let data = response.data;
        if (data.success) {
          dailyTrains.value = data.content.rows;
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

    const onChangeCode = (train) => {
      console.log("车次下拉组件选择：", train);
      let temp = Tool.copy(train);
      delete temp.id;
      // 用assign可以合并
      dailyTrain.value = Object.assign(dailyTrain.value, temp);
    };

    const onClickGenerateDailyTrain = () => {
      generateDailyVisible.value = true;
    };

    const handleGenerateDailyOK = () => {
      let date = dayjs(generateDaily.value.date).format("YYYY-MM-DD");
      generateDailyLoading.value = true;
      axios.get("/business/admin/daily-train/generate-daily-train/" + date).then((response) => {
        generateDailyLoading.value = false;
        let data = response.data;
        if (data.success) {
          notification.success({description: "生成成功！"});
          generateDailyVisible.value = false;
          handleQuery({
            page: pagination.value.current,
            pageSize: pagination.value.pageSize
          });
        }
        else {
          notification.error({description: data.message});
        }
      });
    };

    onMounted(() => {
      handleQuery({
        page: 1,
        pageSize: pagination.value.pageSize
      });
    });

    return {
      TRAIN_TYPE_ARRAY,
      dailyTrain,
      visible,
      dailyTrains,
      pagination,
      loading,
      params,
      generateDaily,
      generateDailyVisible,
      generateDailyLoading,
      columns,
      handleTableChange,
      handleQuery,
      onAdd,
      handleOk,
      onEdit,
      onDelete,
      onChangeCode,
      onClickGenerateDailyTrain,
      handleGenerateDailyOK
    };
  },
});
</script>
