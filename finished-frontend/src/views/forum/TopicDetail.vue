<script setup>
import {get} from "@/net/api.js";
import {useRoute} from "vue-router";

const route = useRoute();

const tid = route.params.tid;
get(`/api/forum/topic?tid=${tid}`, data => {
})

</script>

<template>
  123
</template>

<style scoped>

</style>