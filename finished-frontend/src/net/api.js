import axios from "axios";
import {ElMessage} from "element-plus";
import router from "@/router/index.js";

const defaultUrl = "http://localhost:8080"

const tokenAndExpire = "tokenAndExpire"

const defaultFailure = (message, code, url) => {
    ElMessage.warning(message);
}

const defaultError = (message) => {
    ElMessage.error(message)
}

const getToken = () => {
    const token = sessionStorage.getItem(tokenAndExpire) || localStorage.getItem(tokenAndExpire);
    if (!token) return null;
    const tokenObj = JSON.parse(token);
    if (tokenObj.expire < new Date()) {
        deleteToken();
        ElMessage.warning("登录状态过期, 请重新登录");
        return null;
    }
    return tokenObj.token;
}

const deleteToken = () => {
    localStorage.removeItem(tokenAndExpire);
    sessionStorage.removeItem(tokenAndExpire);
}

const storeToken = (token, remember, expire) => {
    console.log(`${token}+${expire}`)
    const auth = {token: token, expire: expire};
    if (remember) {
        localStorage.setItem(tokenAndExpire, JSON.stringify(auth));
    } else {
        sessionStorage.setItem(tokenAndExpire, JSON.stringify(auth));
    }
}

const doGet = (url, header, success, failure = defaultFailure, error = defaultError) => {
    axios.get(defaultUrl + url, { headers: header }).then((res) => {
        if (res.data.code === 200) {
            success(res.data.data)
        } else {
            failure(res.data.message, res.data.code, url);
        }
    }).catch(err => {
        error(err)
    })
}

const doPost = (url, data, header, success, failure = defaultFailure, error = defaultError) => {
    axios.post(defaultUrl + url, data, { headers: header}).then((res) => {
        if (res.data.code === 200) {
            success(res.data.data)
        } else {
            failure(res.data.message, res.data.code, url)
        }
    }).catch(err => {
        console.log(err)
        error(err)
    })
}

const login = (username, password, remember) => {
    doPost("/api/auth/login", {
        username: username,
        password: password
    }, {
        "Content-Type" : "application/x-www-form-urlencoded"
    }, (data) => {
        storeToken(data.token, remember, data.expireTime);
        ElMessage.success(`欢迎用户${data.username}登录成功`)
        router.push("/index")
    })
}

const logout = () => {
    doGet("/api/auth/logout", {
        "Authorization": `Bearer ${getToken()}`
    }, (data) => {
        deleteToken()
        router.push("/")
    })
}

const askCodeForType = (email, type, success) => {
    doGet(`/api/auth/ask-code?email=${email}&type=${type}`, {}, success)
}

const isLogin = () => {
    return getToken()
}

export {doGet, doPost, login, logout, askCodeForType, isLogin}