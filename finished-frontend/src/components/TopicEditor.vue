<script setup>
import {Check, Document} from "@element-plus/icons-vue";
import {reactive} from "vue";
import {Quill, QuillEditor} from "@vueup/vue-quill";
import ImageResize from "quill-image-resize-vue";
import { ImageExtend, QuillWatch } from "quill-image-super-solution-module";
import "@vueup/vue-quill/dist/vue-quill.snow.css"
import axios from "axios";
import {ElMessage} from "element-plus";
import {get, getToken} from "@/net/api.js";

defineProps({
  show: Boolean
})

const emit = defineEmits(['close'])

const editor = reactive({
  type: null,
  title: "",
  text: "",
  loading: false,
  types: []
})

get("/api/forum/types", data => {
  editor.types = data
})

Quill.register('modules/imageResize', ImageResize)
Quill.register('modules/ImageExtend', ImageExtend)

const editorOption = {
  modules: {
    toolbar: {
      container: [
        "bold", "italic", "underline", "strike","clean",
        {color: []}, {'background': []},
        {size: ["small", false, "large", "huge"]},
        { header: [1, 2, 3, 4, 5, 6, false] },
        {list: "ordered"}, {list: "bullet"}, {align: []},
        "blockquote", "code-block", "link", "image",
        { indent: '-1' }, { indent: '+1' }
      ],
      handlers: {
        'image': function () {
          QuillWatch.emit(this.quill.id)
        }
      }
    },
    imageResize: {
      modules: [ 'Resize', 'DisplaySize' ]
    },
    ImageExtend: {
      action:  axios.defaults.baseURL + '/api/image/cache',
      name: 'file',
      size: 5,
      loading: true,
      accept: 'image/png, image/jpeg',
      response: (resp) => {
        if(resp.data) {
          return axios.defaults.baseURL + '/images' + resp.data
        } else {
          return null
        }
      },
      methods: 'POST',
      headers: xhr => {
        xhr.setRequestHeader('Authorization', `Bearer ${getToken()}`);
      },
      start: () => editor.loading = true,
      success: (data) => {
        ElMessage.success({message: '图片上传成功!', plain: true})
        editor.loading = false
      },
      error: () => {
        ElMessage.warning({message:'图片上传失败，请联系管理员!', plain: true})
        editor.loading = false
      }
    }
  }
}
</script>

<template>
  <div>
    <el-drawer :model-value="show" direction="btt" :size="650" :close-on-click-modal="false" @close="emit('close')">
      <template #header>
        <div>
          <div style="font-weight: bold">发表新的帖子</div>
          <div style="font-size: 13px">发表内容之前，请遵守相关法律法规，不要出现骂人等爆粗口不文明行为</div>
        </div>
      </template>
      <div style="display: flex;gap: 10px">
        <div style="width: 200px">
          <el-select placeholder="请选择文章类型..."  v-model="editor.type" :disable="editor.types.length === 0">
            <el-option v-for="item in editor.types" :value="item.id" :label="item.name"/>
          </el-select>
        </div>
        <div style="flex: 1">
          <el-input placeholder="请输入文章标题" :prefix-icon="Document" v-model="editor.title" />
        </div>
      </div>
      <div style="margin-top: 15px;height: 460px;overflow: hidden;border-radius: 5px"
           v-loading="editor.loading"
           element-loading-text="正在上传图片">
        <quill-editor
            placeholder="今天想分享点什么呢?"
            style="height: calc(100% - 45px)"
            content-type="delta"
            v-model:content="editor.text"
            :options="editorOption"
        />
      </div>
      <div style="display: flex;justify-content: space-between;margin-top: 10px">
        <div style="color: grey;font-size: 13px">当前字数 {{666}} 最大支持(20000字)</div>
        <div>
          <el-button type="success" :icon="Check" plain>立即发布</el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<style scoped>
:deep(.el-drawer) {
  width: 800px;
  margin: auto;
  border-radius: 10px 10px 0 0;
}

:deep(.el-drawer__header) {
  margin: 0;
}

:deep(.ql-toolbar) {
  border-radius: 5px 5px 0 0;
  border-color: var(--el-border-color);
}

:deep(.ql-container) {
  border-radius: 0 0 5px 5px;
  border-color: var(--el-border-color);
}

:deep(.ql-editor) {
  font-size: 14px;
}

:deep(.ql-editor.ql-blank::before) {
  color: var(--el-text-color-placeholder);
  font-style: normal;
}
</style>