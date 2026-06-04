// 所有的session key都在这里统一定义，可以避免多个功能使用同一个key
SESSION_ORDER = "SESSION_ORDER";
SESSION_TICKET_PARAMS = "SESSION_TICKET_PARAMS";

SessionStorage = {
    get: function (key) {
        var value = sessionStorage.getItem(key);
        if (value && typeof(value) !== 'undefined' && value !== "undefined") {
            return JSON.parse(value);
        }
    },
    set: function (key, data) {
        sessionStorage.setItem(key, JSON.stringify(data));
    },
    remove: function (key) {
        sessionStorage.removeItem(key);
    },
    clearAll: function () {
        sessionStorage.clear();
    }
}