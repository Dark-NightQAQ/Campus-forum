import { createRouter, createWebHistory } from 'vue-router'
import {isLogin} from "@/net/api.js";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'welcome',
      component: () => import("@/views/WelcomeView.vue"),
      children: [
        {
          path: "",
          name: "welcome-login",
          component: () => import("@/views/welcome/LoginView.vue")
        }, {
          path: "/register",
          name: "welcome-register",
          component: () => import("@/views/welcome/RegisterView.vue")
        }, {
          path: "/reset",
          name: "welcome-reset",
          component: () => import("@/views/welcome/ResetView.vue")
        }
      ]
    }, {
      path: "/index",
      name: "index",
      component: () => import("@/views/IndexView.vue"),
      children: [
        {
          path: "user-setting",
          name: "user-setting",
          component: () => import("@/views/setting/UserSetting.vue")
        },{
          path: "privacy-setting",
          name: "privacy-setting",
          component: () => import("@/views/setting/PrivacySetting.vue")
        }

      ]
    }

  ]
})

router.beforeEach((to, from, next) => {
  const loginStatus = isLogin();
  if (to.name.startsWith("welcome-") && loginStatus) {
    next("/index")
  } else if (to.name.startsWith("index") && !loginStatus) {
    next("/")
  } else {
    next();
  }
})

export default router
