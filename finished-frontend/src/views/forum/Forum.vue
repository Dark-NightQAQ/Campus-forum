<script setup>

</script>

<template>
  <router-view v-slot="{ Component }">
    <transition name="el-fade-in" mode="out-in">
      <keep-alive include="TopicList">
        <component :is="Component" style="height: 100%" />
      </keep-alive>
    </transition>
  </router-view>
</template>

<style scoped>

</style>