<template>
  <p>
    <a-space>
      <train-select-view v-model:value="params.trainCode" width="200px"></train-select-view>
      <a-button type="primary" @click="handleQuery()">查询</a-button>
      <a-button type="primary" @click="onAdd">新增</a-button>
    </a-space>
  </p>
  <a-table :dataSource="trainStations"
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
    </template>
  </a-table>
  <a-modal v-model:open="visible" title="火车车站列表" @ok="handleOk"
           ok-text="确认" cancel-text="取消">
    <a-form :model="trainStation" :label-col="{span: 4}" :wrapper-col="{ span: 20 }">
      <a-form-item label="车次编号">
        <train-select-view v-model:value="trainStation.trainCode"></train-select-view>
      </a-form-item>
      <a-form-item label="站序">
        <a-input v-model:value="trainStation.index"/>
      </a-form-item>
      <a-form-item label="站名">
        <station-select-view v-model:value="trainStation.name"></station-select-view>
      </a-form-item>
      <a-form-item label="站名拼音">
        <a-input v-model:value="trainStation.namePinyin" disabled/>
      </a-form-item>
      <a-form-item label="进站时间">
        <a-time-picker v-model:value="trainStation.entryTime" valueFormat="HH:mm:ss" placeholder="请选择时间"/>
      </a-form-item>
      <a-form-item label="出站时间">
        <a-time-picker v-model:value="trainStation.exitTime" valueFormat="HH:mm:ss" placeholder="请选择时间"/>
      </a-form-item>
      <a-form-item label="停留时间">
        <a-time-picker v-model:value="trainStation.stopTime" valueFormat="HH:mm:ss" placeholder="请选择时间" disabled/>
      </a-form-item>
      <a-form-item label="里程（公里）">
        <a-input v-model:value="trainStation.kilometers"/>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script>
import {defineComponent, ref, onMounted, watch} from 'vue';
import {notification} from "ant-design-vue";
import axios from "axios";
import {pinyin} from "pinyin-pro";
import TrainSelectView from "@/components/train-select.vue";
import StationSelectView from "@/components/station-select.vue";
import dayjs from "dayjs";

export default defineComponent({
  name: "train-station-view",
  components: {StationSelectView, TrainSelectView},
  setup() {
    const visible = ref(false);
    let trainStation = ref({
      id: undefined,
      trainCode: undefined,
      index: undefined,
      name: undefined,
      namePinyin: undefined,
      entryTime: undefined,
      exitTime: undefined,
      stopTime: undefined,
      kilometers: undefined,
      createTime: undefined,
      updateTime: undefined,
    });
    const trainStations = ref([]);
    // 分页的三个属性名是固定的
    const pagination = ref({
      total: 0,
      current: 1,
      pageSize: 10,
    });
    let loading = ref(false);
    let params = ref({
      trainCode: null
    });
    const columns = [
      {
        title: '车次编号',
        dataIndex: 'trainCode',
        key: 'trainCode',
      },
      {
        title: '站序',
        dataIndex: 'index',
        key: 'index',
      },
      {
        title: '车站名',
        dataIndex: 'name',
        key: 'name',
      },
      {
        title: '车站名拼音',
        dataIndex: 'namePinyin',
        key: 'namePinyin',
      },
      {
        title: '进站时间',
        dataIndex: 'entryTime',
        key: 'entryTime',
      },
      {
        title: '出站时间',
        dataIndex: 'exitTime',
        key: 'exitTime',
      },
      {
        title: '停留时间',
        dataIndex: 'stopTime',
        key: 'stopTime',
      },
      {
        title: '里程（公里）',
        dataIndex: 'kilometers',
        key: 'kilometers',
      },
      {
        title: '操作',
        dataIndex: 'action'
      }
    ];
    watch(() => trainStation.value.name, () => {
      if (Tool.isNotEmpty(trainStation.value.name)) {
        trainStation.value.namePinyin = pinyin(trainStation.value.name, {toneType: 'none'}).replace(" ", "");
      } else {
        trainStation.value.namePinyin = "";
      }
    }, {immediate: true});

    // 自动计算停站时长
    watch (() => trainStation.value.entryTime, () => {
      let diff = dayjs(trainStation.value.exitTime, "HH:mm:ss").diff(dayjs(trainStation.value.entryTime, "HH:mm:ss"), "second");
      trainStation.value.stopTime = dayjs("00:00:00", "HH:mm:ss").second(diff).format("HH:mm:ss");
    }, {immediate: true});

    // 自动计算停站时长
    watch (() => trainStation.value.exitTime, () => {
      let diff = dayjs(trainStation.value.exitTime, "HH:mm:ss").diff(dayjs(trainStation.value.entryTime, "HH:mm:ss"), "second");
      trainStation.value.stopTime = dayjs("00:00:00", "HH:mm:ss").second(diff).format("HH:mm:ss");
    }, {immediate: true});

    const onAdd = () => {
      trainStation.value = {};
      visible.value = true;
    };

    const onEdit = (record) => {
      trainStation.value = window.Tool.copy(record);
      visible.value = true;
    };

    const onDelete = (record) => {
      axios.delete("/business/admin/train-station/delete/" + record.id).then((response) => {
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
      axios.post("/business/admin/train-station/save", trainStation.value).then((response) => {
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
          pageSize: pagination.value.pageSize,
        };
      }
      loading.value = true;
      axios.get("/business/admin/train-station/query-list", {
        params: {
          page: param.page,
          pageSize: param.pageSize,
          trainCode: params.value.trainCode
        }
      }).then((response) => {
        loading.value = false;
        let data = response.data;
        if (data.success) {
          trainStations.value = data.content.rows;
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

    onMounted(() => {
      handleQuery({
        page: 1,
        pageSize: pagination.value.pageSize
      });
    });

    return {
      trainStation,
      visible,
      trainStations,
      pagination,
      columns,
      loading,
      params,
      handleTableChange,
      handleQuery,
      onAdd,
      handleOk,
      onEdit,
      onDelete
    };
  },
});
</script>
