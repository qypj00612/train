SESSION_ALL_TRAIN = "SESSION_ALL_TRAIN";

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