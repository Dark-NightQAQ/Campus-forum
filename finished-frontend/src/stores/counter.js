import {reactive} from 'vue'
import { defineStore } from 'pinia'

export const useCounterStore = defineStore('counter', () => {
  const user = reactive({
    username: "",
    email: "",
    role: "",
    avatar: "",
    registerTime: null,
  })

  return { user }
})
