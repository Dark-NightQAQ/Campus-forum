<script setup>

import {Key, Lock, Message, User} from "@element-plus/icons-vue";
import router from "@/router/index.js";
import {reactive, ref, watch} from "vue";
import {ElMessage} from "element-plus";
import {askCodeForType, doPost} from "@/net/api.js";

const form = reactive({
  email: "",
  username: "",
  password: "",
  repeat_password: "",
  code: ""
})

const formRef = ref()

const codeTime = ref(0)

let codeTimeDown;

const validatePass = (rule, value, callback) => {
  if (value === "") {
    callback(new Error("重复密码不能为空"))
  } else if (value !== form.password) {
    callback(new Error("两次密码不一致"))
  } else {
    callback()
  }
}

const validateEmail = (rule, value, callback) => {
  if (value === "") {
    callback(new Error("邮箱不能为空"))
  } else if (!/^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/.test(value)) {
    callback(new Error("邮箱格式不正确"))
  } else {
    callback()
  }
}

const validateUsername = (rule, value, callback) => {
  if (value === '') {
    callback(new Error("用户名不能为空"))
  } else if (!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)) {
    callback(new Error("用户名不能包含特殊字符, 只能是中文/英文"))
  } else {
    callback()
  }
}

const rules = reactive({
  email: [{required: true, validator: validateEmail, trigger: 'blur'}],
  username: [{required: true, validator: validateUsername, trigger: 'blur'}],
  password: [{required: true, message: "密码不能为空", trigger: 'blur'}, {min : 6, message: "密码长度不能小于6"}],
  repeat_password: [{required: true, validator: validatePass, trigger: 'blur'}],
  code: [{required: true, message: "验证码不能为空", trigger: 'blur'}]
})

const askCode = () => {
  if (/^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/.test(form.email)) {
    askCodeForType(form.email, "register", (data) => {
      ElMessage.success({message: "验证码发送成功, 请及时查看", plain: true})
      codeTime.value = 60;
      codeTimeDown = setInterval(() => {codeTime.value--}, 1000)
    })
  } else {
    ElMessage.warning("电子邮件格式不正确")
  }
}

watch(codeTime, () => {
  if (codeTime.value === 0) {
    clearInterval(codeTimeDown)
  }
})

const register = (formRef) => {
  if (!formRef) return;
  formRef.validate(valid => {
    if (valid) {
      doPost("/api/auth/register", {
        email: form.email,
        username: form.username,
        password: form.repeat_password,
        code: form.code
      }, {"Content-Type" : "application/x-www-form-urlencoded"}, () => {
        ElMessage.success({message: "注册成功!", plain: true})
        router.push("/")
      });
    }
  })
}

</script>

<template>
  <div style="margin-top: 160px">
    <h2 style="text-align: center">注册</h2>
    <div style="margin-top: 50px">
      <el-form :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="email" style="margin: 0 20px" ><el-input v-model="form.email" placeholder="邮箱" :prefix-icon="Message"/></el-form-item>
        <el-form-item prop="username" style="margin: 25px 20px" ><el-input v-model="form.username" placeholder="用户名" :prefix-icon="User"/></el-form-item>
        <el-form-item prop="password" style="margin: 25px 20px" ><el-input v-model="form.password" type="password" show-password="show-password" placeholder="密码" :prefix-icon="Lock"/></el-form-item>
        <el-form-item prop="repeat_password" style="margin: 25px 20px" ><el-input v-model="form.repeat_password" type="password" show-password="show-password" placeholder="重复密码" :prefix-icon="Lock"/></el-form-item>
        <el-row style="margin: 10px 20px">
          <el-col :span="14">
            <el-form-item prop="code"><el-input v-model="form.code" maxlength="6" placeholder="验证码" :prefix-icon="Key"/></el-form-item>
          </el-col>
          <el-col :span="8" :offset="1">
            <el-button type="success" @click="askCode" :disabled="codeTime !== 0 && /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/.test(form.email)">{{ codeTime > 0 ? `请稍等${codeTime}秒重试` : "发送验证码" }}</el-button>
          </el-col>
        </el-row>
        <el-form-item><el-button style="margin: 0 80px;width: 100%" type="warning" plain @click="register(formRef)">注册</el-button></el-form-item>
        <el-form-item>
          <div style="margin: 0 auto">
            <div @click="router.push('/')">已有账号?
              <el-link style="margin-bottom: 3px">立即登录</el-link>
            </div>
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<style scoped>

</style>