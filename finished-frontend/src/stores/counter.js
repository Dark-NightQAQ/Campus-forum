import {reactive} from 'vue'
import { defineStore } from 'pinia'
import axios from "axios";

export const useCounterStore = defineStore('counter', () => {
  const user = reactive({
    id: -1,
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
  const getAvatar = (avatar) => {
    return avatar ? `${axios.defaults.baseURL}/images${avatar}` : "https://www.vexipui.com/qmhc.jpg";
  };

  return { user, forum, findTypeById, getAvatar}
})
