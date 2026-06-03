// src/assets/js/enums.js
/* eslint-disable no-undef */ // 临时关闭 "未定义变量" 校验（因使用全局变量模式）

// 1. 乘客类型枚举（保留你的嵌套结构）
const PASSENGER_TYPE = {
    ADULT: { code: "1", desc: "成人" },
    CHILD: { code: "2", desc: "儿童" },
    STUDENT: { code: "3", desc: "学生" }
};

// 2. 列车类型枚举（保留价格系数）
const TRAIN_TYPE = {
    G: { code: "G", desc: "高铁", priceRate: "1.2" },
    D: { code: "D", desc: "动车", priceRate: "1" },
    K: { code: "K", desc: "快速", priceRate: "0.8" }
};

// 3. 座位类型枚举（保留价格系数）
const SEAT_TYPE = {
    FIRST_CLASS: { code: "1", desc: "一等座", price: "0.4" },
    SECOND_CLASS: { code: "2", desc: "二等座", price: "0.3" },
    SOFT_SLEEPER: { code: "3", desc: "软卧", price: "0.6" },
    HARD_SLEEPER: { code: "4", desc: "硬卧", price: "0.5" }
};

// 4. 座位列枚举（保留座位类型关联）
const SEAT_COL = {
    FIRST_CLASS_A: { code: "A", desc: "A", type: "1" },
    FIRST_CLASS_C: { code: "C", desc: "C", type: "1" },
    FIRST_CLASS_D: { code: "D", desc: "D", type: "1" },
    FIRST_CLASS_F: { code: "F", desc: "F", type: "1" },
    SECOND_CLASS_A: { code: "A", desc: "A", type: "2" },
    SECOND_CLASS_B: { code: "B", desc: "B", type: "2" },
    SECOND_CLASS_C: { code: "C", desc: "C", type: "2" },
    SECOND_CLASS_D: { code: "D", desc: "D", type: "2" },
    SECOND_CLASS_F: { code: "F", desc: "F", type: "2" }
};

// 5. 枚举数组（供下拉框渲染）
const PASSENGER_TYPE_ARRAY = [
    { code: "1", desc: "成人" },
    { code: "2", desc: "儿童" },
    { code: "3", desc: "学生" }
];

const TRAIN_TYPE_ARRAY = [
    { code: "G", desc: "高铁", priceRate: "1.2" },
    { code: "D", desc: "动车", priceRate: "1" },
    { code: "K", desc: "快速", priceRate: "0.8" }
];

const SEAT_TYPE_ARRAY = [
    { code: "1", desc: "一等座", price: "0.4" },
    { code: "2", desc: "二等座", price: "0.3" },
    { code: "3", desc: "软卧", price: "0.6" },
    { code: "4", desc: "硬卧", price: "0.5" }
];

const SEAT_COL_ARRAY = [
    { code: "A", desc: "A", type: "1" },
    { code: "C", desc: "C", type: "1" },
    { code: "D", desc: "D", type: "1" },
    { code: "F", desc: "F", type: "1" },
    { code: "A", desc: "A", type: "2" },
    { code: "B", desc: "B", type: "2" },
    { code: "C", desc: "C", type: "2" },
    { code: "D", desc: "D", type: "2" },
    { code: "F", desc: "F", type: "2" }
];

// 6. 导出枚举（供其他组件导入使用，两种方式可选）
// 方式 1：默认导出（推荐，组件导入后可直接访问所有枚举）
export default {
    PASSENGER_TYPE,
    TRAIN_TYPE,
    SEAT_TYPE,
    SEAT_COL,
    PASSENGER_TYPE_ARRAY,
    TRAIN_TYPE_ARRAY,
    SEAT_TYPE_ARRAY,
    SEAT_COL_ARRAY
};

// 方式 2：按需导出（组件可只导入需要的枚举，减小打包体积）
// export {
//   PASSENGER_TYPE,
//   TRAIN_TYPE,
//   SEAT_TYPE,
//   SEAT_COL,
//   PASSENGER_TYPE_ARRAY,
//   TRAIN_TYPE_ARRAY,
//   SEAT_TYPE_ARRAY,
//   SEAT_COL_ARRAY
// };