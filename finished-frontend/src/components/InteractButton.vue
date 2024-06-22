<script setup>
defineProps({
  name: String,
  color: String,
  checkName: String,
  check: Boolean
})

const emit = defineEmits(['check'])
</script>

<template>
  <div class="interact-button">
    <span class="icon" :style="{'color': check ? color : 'unset'}" @click="emit('check')">
      <slot/>
    </span>
    <span class="name" :style="{'color':color}">
      {{check ? checkName : name}}
    </span>
  </div>
</template>

<style scoped>
.interact-button {
  display: inline-block;
  height: 20px;

  .name {
    font-size: 13px;
    margin-left: 5px;
    opacity: 0.8;
  }

  .icon {
    vertical-align: middle;
    transition: .3s;

    &:hover {
      cursor: pointer;
      font-size: 18px
    }
  }
}

</style>