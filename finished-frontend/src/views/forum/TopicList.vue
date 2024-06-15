<script setup>

import LightCard from "@/components/LightCard.vue";
import {Calendar, CollectionTag, Edit, EditPen, Guide} from "@element-plus/icons-vue";
import Weather from "@/components/Weather.vue";
import {computed, reactive, ref} from "vue";
import {get} from "@/net/api.js";
import {ElMessage} from "element-plus";
import TopicEditor from "@/components/TopicEditor.vue";

const today = computed(() => {
  const date = new Date();
  return `${date.getFullYear()} 年 ${date.getMonth()+1} 月 ${date.getDate()} 日`
})

const editor = ref();

const weather = reactive({
  location:{},
  now: {},
  hourly: {},
  success: false
})

navigator.geolocation.getCurrentPosition((position) => {
  const longitude = position.coords.longitude;
  const latitude = position.coords.latitude;
  get(`/api/forum/weather?longitude=${longitude}&latitude=${latitude}`, (data) => {
    Object.assign(weather, data)
    weather.success = true;
  })
}, (err) => {
  console.info(err)
  ElMessage.warning("位置信息获取超时")
  get("api/forum/weather?longitude=116.40529&latitude=39.90499", data => {
    Object.assign(weather, data)
    weather.success = true;
  })
}, {
  timeout: 3000,
  enableHighAccuracy: true
})
</script>

<template>
  <div style="display: flex;margin: 20px auto;gap: 20px;max-width: 900px">
    <div style="flex: 1">
      <light-card>
        <div class="create-topic" @click="editor = true">
          <el-icon><EditPen/></el-icon>点击发表帖子...
        </div>
      </light-card>
      <light-card style="margin-top: 10px;height: 40px"></light-card>
      <div style="margin-top: 10px;display: flex;flex-direction: column;gap: 10px">
        <light-card style="height: 100px" v-for="item in 10">

        </light-card>
      </div>
    </div>
    <div style="width: 280px">
      <div style="position: sticky;top: 20px">
        <light-card>
          <div style="font-weight: bold">
            <el-icon><CollectionTag/></el-icon>
            论坛公告
          </div>
          <el-divider style="margin: 10px 0"/>
          <div style="font-size: 14px;color: grey;margin: 10px">
            可惜你不玩原神可惜你不玩原神可惜你不玩原神可惜你不玩原神可惜你不玩原神可惜你不玩原神可惜你不玩原神可惜你不玩原神可惜你不玩原神可惜你不玩原神
          </div>
        </light-card>
        <light-card style="margin-top: 10px">
          <div style="font-weight: bold">
            <el-icon><Calendar/></el-icon>
            天气信息
          </div>
          <el-divider style="margin: 10px 0"/>
          <Weather :data="weather"></Weather>
        </light-card>
        <light-card style="margin-top: 10px">
          <div class="info-text">
            <div>当前日期</div>
            <div>{{today}}</div>
          </div>
          <div class="info-text">
            <div>当前IP地址</div>
            <div>127.0.0.1</div>
          </div>
        </light-card>
        <div style="font-size: 14px;margin-top: 10px;color: grey">
          <el-icon><Guide/></el-icon>
          友情链接
          <el-divider style="margin: 10px 0"/>
        </div>
        <div style="display: grid;grid-template-columns: repeat(2, 1fr);grid-gap: 10px;margin-top: 10px;">
          <div class="friend-link">
            <el-image src="https://element-plus.org/images/sponsors/mele-banner.png"/>
          </div>
          <div class="friend-link">
            <el-image src="https://element-plus.org/images/sponsors/vform-banner.png"/>
          </div>
          <div class="friend-link">
            <el-image src="https://element-plus.org/images/sponsors/jnpfsoft.jpg"/>
          </div>
        </div>
      </div>
    </div>
    <TopicEditor :show="editor" @close="editor = false"/>
  </div>
</template>

<style lang="less" scoped>
.info-text {
  display: flex;
  justify-content: space-between;
  color: grey;
  font-size: 14px;
}
.friend-link {
  border-radius: 5px;
  overflow: hidden;
}
.create-topic {
  background-color: #efefef;
  border-radius: 5px;
  height: 40px;
  color: grey;
  font-size: 14px;
  line-height: 40px;
  padding: 0 10px;

  &:hover {
    cursor: pointer;
  }
}

.dark .create-topic {
  background-color: #212225;
}
</style>