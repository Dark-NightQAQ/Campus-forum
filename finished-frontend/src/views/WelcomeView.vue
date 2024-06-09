<script setup>

</script>

<template>
  <div style="width: 100vw; height: 100vh;display: flex;overflow: hidden;">
    <div class="left">
      <el-image src="http://img.netbian.com/file/2024/0606/000716MnCNm.jpg" style="width: 100%; height: 100%" :fit="'cover'"></el-image>
    </div>
    <div class="right">
      <router-view v-slot="{ Component }">
        <transition name="el-fade-in" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </div>
  </div>
</template>

<style scoped>
.left {
  flex: 1;
}
.right {
  width: 430px;
  z-index: 1;
  background: var(--el-bg-color);
}
</style>