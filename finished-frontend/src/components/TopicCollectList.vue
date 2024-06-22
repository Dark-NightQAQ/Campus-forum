<script setup>
import {get} from "@/net/api.js";
import {reactive, ref} from "vue";
import LightCard from "@/components/LightCard.vue";
import router from "@/router/index.js";
import TopicTag from "@/components/TopicTag.vue";
import {ElMessage} from "element-plus";

defineProps({
  show: Boolean
})

const emit = defineEmits(['close'])

const list = ref([])

function init() {
  get("/api/forum/collects", data => {
    list.value = data
  })
}

function deleteCollect(index, tid) {
  get(`/api/forum/interact?tid=${tid}&type=collect&state=false`, () => {
    ElMessage.success({message: "取消收藏成功", plain: true})
    list.value.splice(index, 1);
  })
}
</script>

<template>
  <el-drawer :model-value="show" @close="emit('close')" @open="init" title="我收藏的帖子">
    <div class="collect-list">
      <light-card v-for="(item, index) in list" class="topic-card" @click="router.push(`/index/topic-detail/${item.id}`)">
        <topic-tag :type="item.type"/>
        <div class="title">
          <b>{{item.title}}</b>
        </div>
        <el-link type="danger" @click.stop="deleteCollect(index, item.id)">删除</el-link>
      </light-card>
    </div>
  </el-drawer>
</template>

<style scoped>
.collect-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.topic-card {
  background-color: rgba(128, 128, 128, 0.2);
  transition: 0.3s;
  display: flex;
  justify-content: space-between;

  .title {
    margin-left: 5px;
    font-size: 14px;
    flex: 1;
    white-space: normal;
    display: block;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  &:hover {
    scale: 1.02;
    cursor: pointer;
  }
}
</style>