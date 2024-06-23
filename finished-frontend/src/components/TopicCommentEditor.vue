<script setup>
import {Delta, QuillEditor} from "@vueup/vue-quill";
import "@vueup/vue-quill/dist/vue-quill.snow.css"
import {ref} from "vue";
import {post} from "@/net/api.js";
import {ElMessage} from "element-plus";

const props = defineProps({
  show: Boolean,
  tid: String,
  quote: Object
})

const content = ref()

const emit = defineEmits(['close', "comment"])

const init = () => content.value = new Delta()

function submitComment() {
  if (deltaToText(content.value).length > 2000) {
    ElMessage.warning({message:"评论字数已超出最大限制, 请缩减内容！", plain: true})
    return
  }
  post("/api/forum/add-comment", {
    tid: props.tid,
    quote: props.quote ? props.quote.id : -1,
    content: JSON.stringify(content.value)
  }, () => {
    ElMessage.success({message: "发表评论成功！", plain: true})
    emit("comment")
  })
}

function deltaToSimpleText(delta) {
    let str = deltaToText(JSON.parse(delta));
    if (str.length > 35) str = str.substring(0,35) + "..."
    return str;
}

function deltaToText(delta) {
  if (!delta?.ops) return "";
  let str = "";
  for (let op of delta.ops) {
    str += op.insert
  }
  return str.replace(/\s/g, "");
}
</script>

<template>
    <div>
        <el-drawer @open="init"
                   :model-value="show"
                   direction="btt"
                   @close="emit('close')"
                   :size="270"
                   :close-on-click-modal="false"
                   :title="quote ? `发表对评论: ${deltaToSimpleText(quote.content)} 的评论` : '发表评论'" >
            <div>
                <div>
                  <quill-editor style="height: 120px" v-model:content="content" placeholder="请发表友善的评论, 不要攻击他人哦！"/>
                </div>
                <div style="margin-top: 10px;display: flex;">
                  <div style="flex: 1;font-size: 13px;color: grey">
                      字数统计： {{deltaToText(content).length}} (最大支持2000字)
                  </div>
                  <el-button type="success" plain @click="submitComment">发表评论</el-button>
                </div>
            </div>
        </el-drawer>
    </div>
</template>

<style lang="less" scoped>
:deep(.el-drawer) {
  width: 800px;
  margin: 20px auto;
  border-radius: 10px 10px 10px 10px;
}

:deep(.el-drawer__header) {
  margin: 0;
}

:deep(.el-drawer__body) {
  padding: 10px;
}

:deep(.ql-toolbar) {
  border-radius: 5px 5px 0 0;
  border-color: var(--el-border-color) !important;
}

:deep(.ql-container) {
  border-radius: 0 0 5px 5px;
  border-color: var(--el-border-color) !important;
}

:deep(.ql-editor) {
  font-size: 14px;
}

:deep(.ql-editor.ql-blank::before) {
  color: var(--el-text-color-placeholder) !important;
  font-style: normal !important;
}
</style>