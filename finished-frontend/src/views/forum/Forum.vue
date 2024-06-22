<script setup>
import {get} from "@/net/api.js";
import {useCounterStore} from "@/stores/counter.js";

const store = useCounterStore()

get("/api/forum/types", data => {
  const array = []
  array.push({name:"全部", id: 0, color: 'linear-gradient(45deg, white, red, orange, gold, green, blue)'})
  data.forEach(item => {array.push(item)})
  store.forum.types = array
})
</script>

<template>
  <router-view v-slot="{ Component }">
    <transition name="el-fade-in-linear">
      <keep-alive include="TopicList">
        <component :is="Component" style="height: 100%"/>
      </keep-alive>
    </transition>
  </router-view>
</template>
