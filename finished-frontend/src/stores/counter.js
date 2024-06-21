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
  const forum = reactive({
    types: []
  })
  function findTypeById(id) {
    for (let type of forum.types) {
      if (type.id === id)  {
        return type
      }
    }
  }

  return { user, forum, findTypeById }
})
