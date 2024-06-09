<script setup>

import {Key, Lock, Message} from "@element-plus/icons-vue";
import router from "@/router/index.js";
import {reactive, ref, watch} from "vue";
import {ElMessage} from "element-plus";
import {askCodeForType, doGet, doPost} from "@/net/api.js";

const form = reactive({
  email: "",
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

const rules = reactive({
  email: [{validator: validateEmail, trigger: 'blur'}],
  username: [{required: true, message: "用户名不能为空", trigger: 'blur'}],
  password: [{required: true, message: "密码不能为空", trigger: 'blur'}, {min : 6, message: "密码长度不能小于6"}],
  repeat_password: [{validator: validatePass, trigger: 'blur'}],
  code: [{required: true, message: "验证码不能为空", trigger: 'blur'}]
})

const askCode = () => {
  if (/^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/.test(form.email)) {
    askCodeForType(form.email, "reset", (data) => {
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

const reset = async (formRef) => {
  console.log(formRef)
  if (!formRef) return;
  formRef.validate(valid => {
    if (valid) {
      doPost("/api/auth/reset-password", {
        email: form.email,
        password: form.repeat_password,
        code: form.code
      }, {"Content-Type" : "application/x-www-form-urlencoded"}, () => {
        ElMessage.success({message: "密码重置成功, 请重新登录", plain: true})
        router.push("/")
      });
    }
  })
}
</script>

<template>
  <div style="margin-top: 160px">
    <h2 style="text-align: center">重置密码</h2>
    <div style="margin-top: 50px">
      <el-form ref="formRef" :model="form" :rules="rules">
        <el-form-item prop="email" style="margin: 0 20px"><el-input v-model="form.email" placeholder="邮箱" :prefix-icon="Message"/></el-form-item>
        <el-form-item prop="password" style="margin: 25px 20px"><el-input v-model="form.password" type="password" show-password="show-password" placeholder="密码" :prefix-icon="Lock"/></el-form-item>
        <el-form-item prop="repeat_password" style="margin: 25px 20px"><el-input v-model="form.repeat_password" type="password" show-password="show-password" placeholder="重复密码" :prefix-icon="Lock"/></el-form-item>
        <el-row style="margin: 0 20px">
          <el-col :span="14">
            <el-form-item prop="code"><el-input v-model="form.code" maxlength="6" placeholder="验证码" :prefix-icon="Key"/></el-form-item>
          </el-col>
          <el-col :span="8" :offset="1">
            <el-button type="success" @click="askCode" :disabled="codeTime !== 0 && /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/.test(form.email)">{{ codeTime > 0 ? `请稍等${codeTime}秒重试` : "发送验证码" }}</el-button>
          </el-col>
        </el-row>
        <el-form-item style="margin-top: 10px"><el-button style="width: 100%;margin: 0 50px;" type="success" plain @click="reset(formRef)">重置密码</el-button></el-form-item>
        <el-form-item>
          <el-divider style="margin: 15px 10px"></el-divider>
        </el-form-item>
        <el-form-item><el-button style="width: 100%; margin: 0 50px;" type="warning" plain @click="router.push('/')">返回登录</el-button></el-form-item>
      </el-form>
    </div>
  </div>
</template>

<style scoped>

</style>