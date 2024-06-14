<script setup>

import Card from "@/components/Card.vue";
import {Message, Notebook, Refresh, Select, User} from "@element-plus/icons-vue";
import {useCounterStore} from "@/stores/counter.js";
import {computed, reactive, ref} from "vue";
import {get, getToken, post} from "@/net/api.js";
import {ElMessage} from "element-plus";
import axios from "axios";

const store = useCounterStore()

const baseFormRef = ref();
const emailFormRef = ref();

const desc = ref("")

const loading = reactive({
  form: true,
  base: false,

})

const baseForm = reactive({
  username: "",
  gender: 1,
  phone: "",
  qq: "",
  desc: ""
})

const emailForm = reactive({
  email: "",
  code: ""
})

const validateUsername = (rule, value, callback) => {
  if (value === '') {
    callback(new Error("用户名不能为空"))
  } else if (!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)) {
    callback(new Error("用户名不能包含特殊字符, 只能是中文/英文"))
  } else {
    callback()
  }
}

const rules = {
  username: [{validator: validateUsername, trigger: ["blur", "change"]}, {
    min: 2,
    max: 30,
    message: "用户名的长度必须在2-30个字符之间",
    trigger: ["blur", "change"]
  }],
  email: [{required: true, message: "邮件地址不能为空", trigger: ["blur", "change"]}, {
    type: "email",
    message: "请输入合法的电子的邮件地址",
    trigger: ["blur", "change"]
  }],
  code: [{required: true, message: "验证码不能为空", trigger: ["blur", "change"]}, {
    min: 6,
    max: 6,
    message: "验证码至少六位",
    trigger: ["blur", "change"]
  }]
}

const saveDetails = () => {
  baseFormRef.value.validate(isValid => {
    if (isValid) {
      loading.base = true;
      post("/api/user/save-details", baseForm, () => {
        ElMessage.success({message: "用户信息更新成功", plain: true})
        store.user.username = baseForm.username
        desc.value = baseForm.desc
        loading.base = false;
      }, (message) => {
        ElMessage.warning({message: message, plain: true})
        loading.base = false;
      })
    }
  })
}

get("/api/user/details", data => {
  baseForm.username = store.user.username;
  baseForm.gender = data.gender;
  baseForm.phone = data.phone;
  baseForm.qq = data.qq;
  baseForm.desc = desc.value = data.desc;
  emailForm.email = store.user.email
  loading.form = false;
})

const codeTime = ref(0);

function sendEmailCode() {
  if (/^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/.test(emailForm.email)) {
    codeTime.value = 60;
    get(`/api/auth/ask-code?email=${emailForm.email}&type=modify`, () => {
      ElMessage.success({message: `验证码已发送至${emailForm.email}, 请及时查看`, plain: true})
      const handle = setInterval(() => {
        codeTime.value--;
        if (codeTime.value === 0) clearInterval(handle);
      }, 1000)
    }, (message) => {
      codeTime.value = 0;
      ElMessage.warning({message: message, plain: true})
    })
  } else {
    ElMessage.warning({message: "电子邮件格式不正确", plain: true})
  }

}

const modifyEmail = () => {
  emailFormRef.value.validate(value => {
    if (value) {
      post("/api/user/modify-email", emailForm, (data) => {
        ElMessage.success({message: "邮件修改成功", plain: true})
        store.user.email = emailForm.email;
      })
    }
  })
}

function beforeAvatarUpload(rawFile) {
  if (rawFile.type !== "image/jpeg" && rawFile.type !== 'image/png') {
    ElMessage.warning({message: "图片只能是JPG/PNG格式", plain: true})
  } else if (rawFile.size / 1024 > 1024) {
    ElMessage.warning({message: "图片大小不能大于1M", plain: true})
  }
  return true;
}

function uploadSuccess(response) {
  ElMessage.success({message: response.message, plain: true})
  store.user.avatar = response.data;
}
const getAvatar = computed(() => {
  return store.user.avatar.length > 0 ? `${axios.defaults.baseURL}/images${store.user.avatar}` : "https://www.vexipui.com/qmhc.jpg";
});
</script>

<template>
  <div style="display: flex;max-width: 960px;margin: auto">
    <div class="setting-left">
      <card :icon="User" title="账号信息设置" desc="在这里编辑你的个人信息" v-loading="loading.form">
        <el-form :model="baseForm" :rules="rules" label-position="top" ref="baseFormRef"
                 style="margin: 0 10px 10px 10px">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="baseForm.username" maxlength="30"/>
          </el-form-item>
          <el-form-item label="性别">
            <el-radio-group v-model="baseForm.gender">
              <el-radio :value="0" label="男"/>
              <el-radio :value="1" label="女"/>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="baseForm.phone" maxlength="11"/>
          </el-form-item>
          <el-form-item label="QQ号" prop="qq">
            <el-input v-model="baseForm.qq" maxlength="13"/>
          </el-form-item>
          <el-form-item label="个人简介" prop="desc">
            <el-input type="textarea" v-model="baseForm.desc" :rows="6" maxlength="200"/>
          </el-form-item>
          <el-form-item>
            <el-button type="success" :icon="Select" :loading="loading.base" @click="saveDetails">保存用户信息
            </el-button>
          </el-form-item>
        </el-form>
      </card>

      <card style="margin-top: 10px" :icon="Message" title="电子邮件设置" desc="你可以在这里修改您的绑定的电子邮件">
        <el-form :rules="rules" :model="emailForm" label-position="top" ref="emailFormRef"
                 style="margin: 0 10px 10px 10px">
          <el-form-item label="电子邮件" prop="email">
            <el-input v-model="emailForm.email"/>
          </el-form-item>
          <el-form-item prop="code">
            <el-row style="width: 100%" :gutter="10">
              <el-col :span="18">
                <el-input placeholder="请获取验证码" v-model="emailForm.code"/>
              </el-col>
              <el-col :span="6">
                <el-button type="success" @click="sendEmailCode" :disabled="codeTime > 0" plain style="width: 100%">
                  {{ codeTime > 0 ? `请稍后${codeTime}秒后重试` : "获取验证码" }}
                </el-button>
              </el-col>
            </el-row>
          </el-form-item>
          <el-form-item>
            <el-button type="success" :icon="Refresh" @click="modifyEmail">更新电子邮件</el-button>
          </el-form-item>
        </el-form>
      </card>
    </div>
    <div class="setting-right">
      <div style="position: sticky;top: 20px">
        <card>
          <div style="text-align: center;padding: 5px 15px 0 15px">
            <el-avatar :size="70" :src="getAvatar"/>
            <div style="margin: 5px 0">
              <el-upload
                  :action="axios.defaults.baseURL + '/api/image/avatar'"
                  :show-file-list="false"
                  :before-upload="beforeAvatarUpload"
                  :on-success="uploadSuccess"
                  :headers="{'Authorization': `Bearer ${getToken()}`}">
                <el-button>修改头像</el-button>
              </el-upload>
            </div>
            <div style="font-weight: bold">
              你好, {{ store.user.username }}
            </div>
            <el-divider style="margin: 10px 0"/>
            <div style="font-size: 14px;color: gray;padding: 3px">
              {{ desc || "这个用户很懒，没有填写个人简介~" }}
            </div>
          </div>
        </card>
        <card style="margin-top: 10px;font-size: 14px;">
          <div>账号注册时间：{{ new Date(store.user.registerTime).toLocaleString() }}</div>
          <div style="color: gray">欢迎加入论坛</div>
        </card>
      </div>
    </div>
  </div>
</template>

<style scoped>
.setting-left {
  flex: 1;
  margin: 20px;
}

.setting-right {
  width: 300px;
  margin: 20px 30px 20px 0;
}
</style>