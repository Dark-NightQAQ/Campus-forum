import {ref, computed, reactive} from 'vue'
import { defineStore } from 'pinia'
import axios from "axios";

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
