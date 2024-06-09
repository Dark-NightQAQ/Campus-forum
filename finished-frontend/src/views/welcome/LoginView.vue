<script setup>

import {Lock, User} from "@element-plus/icons-vue";
import {reactive, ref} from "vue";
import router from "@/router/index.js";
import {ElMessage} from "element-plus";
import {login} from "@/net/api.js"

const form = reactive({
  username: "",
  password: "",
  remember: false
})

const formRef = ref()

const rules = reactive({
  username: [{required: true, message: "用户名/邮箱不能为空", trigger: 'blur'}],
  password: [{required: true, message: "密码不能为空", trigger: "blur"}]
})

const loginValid = (formRef) => {
  if (!formRef) return;
  formRef.validate((valid) => {
    if (valid) {
      login(form.username, form.password, form.remember)
    } else {
      ElMessage.warning("err")
    }
  })
}
</script>

<template>
  <div style="margin-top: 160px">
    <h2 style="text-align: center">登录</h2>
    <div style="margin-top: 50px">
      <el-form :rules="rules" :model="form" ref="formRef">
        <el-form-item prop="username" style="margin: 25px 20px" >
          <el-input v-model="form.username" placeholder="用户名" maxlength="30" :prefix-icon="User"/>
        </el-form-item>
        <el-form-item style="margin: 25px 20px" prop="password">
        <el-input v-model="form.password"  type="password" placeholder="密码" show-password="show-password" :prefix-icon="Lock"/>
        </el-form-item>
      </el-form>
      <el-row style="margin: 0 20px" justify="space-between">
        <el-col :span="6">
          <el-form-item>
            <el-checkbox v-model="form.remember">记住我</el-checkbox>
          </el-form-item>
        </el-col>
        <el-col :span="5">
          <el-link style="margin-top: 6px" @click="router.push('/reset')">忘记密码?</el-link>
        </el-col>
      </el-row>
      <div style="margin-top: 10px">
        <el-form-item><el-button style="width: 100%; margin: 0 50px;" type="success" plain @click="loginValid(formRef)">登录</el-button></el-form-item>
        <el-form-item>
          <el-divider style="margin: 15px 10px">
            没有账号
          </el-divider>
        </el-form-item>
        <el-form-item><el-button style="width: 100%; margin: 0 50px;" type="warning" plain @click="router.push('/register')">注册</el-button></el-form-item>
      </div>
    </div>
  </div>
</template>

<style scoped>

</style>