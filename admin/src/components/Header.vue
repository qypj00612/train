<template>
  <a-layout-header class="header">
    <div class="logo">
      <router-link to="/welcome" style="color: white; font-size: 18px">甲蛙12306</router-link>
    </div>
    <div style="float: right; color: white;">
      欢迎使用admin管理控制台！
    </div>
    <a-menu
        v-model:selectedKeys="selectedKeys"
        theme="dark"
        mode="horizontal"
        :style="{ lineHeight: '64px' }"
    >
      <a-menu-item key="/welcome">
        <router-link to="/welcome">
          <coffee-outlined></coffee-outlined>&nbsp; 欢迎
        </router-link>
      </a-menu-item>
      <a-menu-item key="/about">
        <router-link to="/about">
          <user-outlined></user-outlined>&nbsp; 关于
        </router-link>
      </a-menu-item>
<!--      <a-sub-menu key="batch">-->
<!--        <template #title>-->
<!--          <span>-->
<!--            <UnorderedListOutlined></UnorderedListOutlined>批次管理-->
<!--          </span>-->
<!--        </template>-->
<!--        <a-menu-item key="/batch/job">-->
<!--          <router-link to="/batch/job">-->
<!--            <user-outlined></user-outlined>&nbsp; 任务管理-->
<!--          </router-link>-->
<!--        </a-menu-item>-->
<!--      </a-sub-menu>-->
<!--      <a-sub-menu key="base">-->
<!--        <template #title>-->
<!--          <span>-->
<!--            <UnorderedListOutlined></UnorderedListOutlined>基础数据-->
<!--          </span>-->
<!--        </template>-->
<!--        <a-menu-item key="/base/station">-->
<!--          <router-link to="/base/station">-->
<!--            <user-outlined></user-outlined>&nbsp; 车站管理-->
<!--          </router-link>-->
<!--        </a-menu-item>-->
<!--        <a-menu-item key="/base/train">-->
<!--          <router-link to="/base/train">-->
<!--            <user-outlined></user-outlined>&nbsp; 火车管理-->
<!--          </router-link>-->
<!--        </a-menu-item>-->
<!--        <a-menu-item key="/base/train-station">-->
<!--          <router-link to="/base/train-station">-->
<!--            <user-outlined></user-outlined>&nbsp; 列车车站-->
<!--          </router-link>-->
<!--        </a-menu-item>-->
<!--        <a-menu-item key="/base/train-carriage">-->
<!--          <router-link to="/base/train-carriage">-->
<!--            <user-outlined></user-outlined>&nbsp; 火车车厢-->
<!--          </router-link>-->
<!--        </a-menu-item>-->
<!--        <a-menu-item key="/base/train-seat">-->
<!--          <router-link to="/base/train-seat">-->
<!--            <user-outlined></user-outlined>&nbsp; 列车座位-->
<!--          </router-link>-->
<!--        </a-menu-item>-->
<!--      </a-sub-menu>-->
    </a-menu>
  </a-layout-header>
</template>

<script>
import {defineComponent, ref, watch} from 'vue';
import router from "@/router";

export default defineComponent({
  name: "header-view",
  setup() {
    const selectedKeys = ref([]);
    watch(() => router.currentRoute.value.path, (newValue) => {
      console.log('watch', newValue);
      selectedKeys.value = [];
      selectedKeys.value.push(newValue);
    }, {immediate: true});
    return {
      selectedKeys
    };
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
.logo {
  float: left;
  height: 31px;
  width: 150px;
  color: white;
  font-size: 20px;
}
</style>
