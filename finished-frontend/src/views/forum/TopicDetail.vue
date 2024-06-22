<script setup>
import {get, post} from "@/net/api.js";
import {useRoute} from "vue-router";
import axios from "axios";
import {computed, reactive, ref} from "vue";
import {ArrowLeft, EditPen, Female, Hide, Male, Pointer, Star, Link} from "@element-plus/icons-vue";
import {QuillDeltaToHtmlConverter} from 'quill-delta-to-html';
import Card from "@/components/Card.vue";
import router from "@/router/index.js";
import TopicTag from "@/components/TopicTag.vue";
import InteractButton from "@/components/InteractButton.vue";
import {ElMessage} from "element-plus";
import {useCounterStore} from "@/stores/counter.js";
import TopicEditor from "@/components/TopicEditor.vue";

const route = useRoute();

const store = useCounterStore()

const topic = reactive({
  data: null,
  like: false,
  collect: false,
  comments: []
})

const tid = route.params.tid;
function init() {
  get(`/api/forum/topic?tid=${tid}`, data => {
    topic.data = data;
    topic.like = data.interact.like;
    topic.collect = data.interact.collect;
  })
}
init()

const content = computed(() => {
  const ops = JSON.parse(topic.data.content).ops;
  const converter = new QuillDeltaToHtmlConverter(ops, {inlineStyles: true});
  return converter.convert()
})

const edit = ref(false)

function interact(type, message) {
  get(`/api/forum/interact?tid=${tid}&type=${type}&state=${!topic[type]}`, () => {
    topic[type] = !topic[type];
    if (topic[type]) {
      ElMessage.success({message: `${message}成功`, plain: true})
    } else {
      ElMessage.success({message: `取消${message}操作成功`, plain: true})
    }
  })
}

function updateTopic(editor) {
  post("/api/forum/update-topic", {
    id: tid,
    type: editor.type.id,
    title: editor.title,
    content: editor.text
  }, () => {
    ElMessage.success({message:"帖子内容更新成功", plain:true})
    edit.value = false;
  })
}

async function copyUrl() {
  await navigator.clipboard.writeText(window.location.href);
  ElMessage.success({message: "已复制到粘贴板", plain: true})
}
</script>

<template>
  <div class="topic-page">
    <div class="topic-main" style="position: sticky;top: 0;z-index: 10">
      <card style="display: flex;width: 100%">
        <el-button :icon="ArrowLeft" type="info" size="small" plain round @click="router.push('/index')">返回列表</el-button>
        <div style="text-align: center;flex: 1;" v-if="topic.data">
          <TopicTag :type="topic.data.type"/>
          <span style="font-weight: bold;margin-left: 5px">{{topic.data.title}}</span>
        </div>
      </card>
    </div>
    <div class="topic-main" v-if="topic.data">
      <div class="topic-main-left">
        <el-avatar :src="axios.defaults.baseURL + '/images' + topic.data.user.avatar"
                   :size="60"/>
        <div>
          <div style="font-size: 18px;font-weight: bold">
            {{topic.data.user.username}}
            <span style="color: hotpink" v-if="topic.data.user.gender === 1">
              <el-icon><Female/></el-icon>
            </span>
            <span style="color: dodgerblue" v-if="topic.data.user.gender === 0">
              <el-icon><Male/></el-icon>
            </span>
            <span style="color: grey" v-if="!topic.data.user.gender">
              <el-icon><Hide/></el-icon>
            </span>
          </div>
          <div class="desc" v-if="topic.data.user.email">{{topic.data.user.email}}</div>
          <div class="desc" v-else>该用户隐藏了邮箱</div>
        </div>
        <el-divider style="margin: 10px 0"/>
        <div style="text-align: left;margin: 0 5px">
          <div class="desc" v-if="topic.data.user.phone">手机号:{{topic.data.user.phone}}</div>
          <div class="desc" v-else>该用户隐藏了手机号</div>
          <div class="desc" v-if="topic.data.user.qq">QQ号:{{topic.data.user.qq}}</div>
          <div class="desc" v-else>该用户隐藏了QQ号</div>
        </div>
        <el-divider style="margin: 10px 0"/>
        <div class="desc" style="margin: 0 5px">
          {{topic.data.user.desc}}
        </div>
      </div>
      <div class="topic-main-right">
        <div class="topic-content" v-html="content"></div>
        <el-divider/>
        <div style="color: grey;text-align: center;font-size: 13px">
          <div>发帖时间：{{new Date(topic.data.time).toLocaleString()}}</div>
        </div>
        <div style="text-align: right;margin-top: 30px">
          <interact-button name="编辑" color="dodgerblue" :check="false" @check="edit = true" v-if="store.user.id === topic.data.user.id">
            <el-icon><EditPen/></el-icon>
          </interact-button>
          <interact-button style="margin-left: 20px" name="点个赞吧" color="pink" check-name="已点赞" :check="topic.like" @check="interact('like', '点赞')">
            <el-icon><Pointer/></el-icon>
          </interact-button>
          <interact-button name="收藏本帖" style="margin-left: 20px" :check="topic.collect" color="orange" check-name="已收藏" @check="interact('collect', '收藏')">
            <el-icon><Star/></el-icon>
          </interact-button>
          <interact-button name="分享本帖" style="margin-left: 20px" :check="false" color="grey" @check="copyUrl()">
            <el-icon><Link/></el-icon>
          </interact-button>
        </div>
      </div>
    </div>
    <div>

    </div>
    <topic-editor
        :show="edit"
        submit-button="更新帖子内容"
        :default-text="topic.data.content"
        :default-title="topic.data.title"
        :default-type="topic.data.type"
        @close="edit = false;init()"
        :submit="updateTopic"
        :tid="tid"
        v-if="topic.data"/>
  </div>
</template>

<style lang="less" scoped>
.topic-page {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 10px 0;

}

.topic-main {
  display: flex;
  border-radius: 7px;
  margin: 0 auto;
  background-color: var(--el-bg-color);
  width: 800px;

  .topic-main-left {
    width: 200px;
    padding: 10px;
    text-align: center;
    border-right: solid 1px var(--el-border-color);

    .desc {
      font-size: 12px;
      color: grey;
    }
  }

  .topic-main-right {
    padding: 20px;
    width: 600px;
    display: flex;
    flex-direction: column;


    .topic-content {
      font-size: 14px;
      opacity: 0.9;
      line-height: 22px;
      flex: 1;
    }
  }
}

</style>