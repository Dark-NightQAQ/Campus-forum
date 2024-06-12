<script setup>

import Card from "@/components/Card.vue";
import {Lock, Setting, Switch} from "@element-plus/icons-vue";
import {reactive, ref} from "vue";
import {post} from "@/net/api.js";
import {ElMessage} from "element-plus";

const form = reactive({
  password: "",
  new_password: "",
  new_password_repeat: ""
})

const validatePass = (rule, value, callback) => {
  if (value === "") {
    callback(new Error("重复密码不能为空"))
  } else if (value !== form.new_password) {
    callback(new Error("两次密码不一致"))
  } else {
    callback()
  }
}

const formRef = ref()


const rules = {
  password: [{required:true, message: "请输入原来的密码", trigger: 'blur'}],
  new_password: [{required: true, message:"请输入新密码", trigger: 'blur'}, {min: 6, message: "密码的长度必须大于6个字符", trigger: "blur"}],
  new_password_repeat: [{validator: validatePass, trigger: ["blur", "change"]}, {required: true, message:"请输入密码", trigger: 'blur'}],
}

function resetPassword() {
  formRef.value.validate(valid => {
    if (valid) {
      post("/api/user/change-password", {
        password: form.password,
        new_password: form.new_password_repeat
      }, () => {
        ElMessage.success({message: "密码修改成功", plain: true})
        formRef.value.resetFields();
      })

    }
  })
}
</script>

<template>
  <div style="margin: auto;max-width: 600px">
    <div style="margin-top: 20px">
      <card :icon="Setting" title="隐私设置" desc="在这里设置自己个人信息的展示">
        <div class="checkbox-list" >
          <el-checkbox>公开展示手机号</el-checkbox>
          <el-checkbox>公开展示电子邮件</el-checkbox>
          <el-checkbox>公开展示QQ号</el-checkbox>
          <el-checkbox>公开展示性别</el-checkbox>
        </div>
      </card>
      <card style="margin: 20px 0" :icon="Setting" title="修改密码" desc="在这里修改自己的密码">
        <el-form ref="formRef" :rules="rules" :model="form" label-width="100" style="margin: 20px">
          <el-form-item label="当前密码" prop="password">
            <el-input :prefix-icon="Lock" type="password" placeholder="当前密码" v-model="form.password" />
          </el-form-item>
          <el-form-item label="新密码" prop="new_password">
            <el-input :prefix-icon="Lock" type="password" placeholder="新密码" v-model="form.new_password"/>
          </el-form-item>
          <el-form-item label="重复新密码" prop="new_password_repeat">
            <el-input :prefix-icon="Lock" type="password" placeholder="重复输入新密码" v-model="form.new_password_repeat" />
          </el-form-item>
          <div style="text-align: center">
            <el-button :icon="Switch" type="success" @click="resetPassword">立即重置密码</el-button>
          </div>
        </el-form>
      </card>
    </div>
  </div>
</template>

<style scoped>
.checkbox-list {
  margin: 10px 0 0 10px;
  display: flex;
  flex-direction: column;
}
</style>