<script setup>
import {Check, Document} from "@element-plus/icons-vue";
import {computed, reactive, ref} from "vue";
import {Quill, QuillEditor} from "@vueup/vue-quill";
import ImageResize from "quill-image-resize-vue";
import { ImageExtend, QuillWatch } from "quill-image-super-solution-module";
import "@vueup/vue-quill/dist/vue-quill.snow.css"
import axios from "axios";
import {ElMessage} from "element-plus";
import {get, getToken, post} from "@/net/api.js";
import ColorDot from "@/components/ColorDot.vue";
import {useCounterStore} from "@/stores/counter.js";

defineProps({
  show: Boolean
})

const store = useCounterStore();

const emit = defineEmits(['close', 'success'])

const editor = reactive({
  type: null,
  title: "",
  text: "",
  loading: false,
})

function deltaToText(delta) {
  if (!delta.ops) return "";
  let str = "";
  for (let op of delta.ops) {
    str += op.insert
  }
  return str.replace(/\s/g, "");
}

const contentLength = computed(() => {
  return deltaToText(editor.text)
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

const submitTopic = () => {
  const text = deltaToText(editor.text);
  if (text.length > 20000) {
    ElMessage.warning({message:"字数超出限制", plain:true})
    return
  }
  if (!editor.text) {
    ElMessage.warning({message:"请填写标题!", plain:true})
    return
  }
  if (!editor.type) {
    ElMessage.warning({message:"请选择一个合适的文章类型!", plain:true})
    return;
  }
  post("/api/forum/create-topic", {
    type: editor.type.id,
    title: editor.title,
    content: editor.text
  }, () => {
    ElMessage.success({message:"文章发表成功", plain:true})
    emit('success')
  })
}

const refEditor = ref();

function initEditor() {
  refEditor.value.setContents('', 'user')
  editor.title = "";
  editor.type = null
}
</script>

<template>
  <div>
    <el-drawer @open="initEditor" :model-value="show" direction="btt" :size="650" :close-on-click-modal="false" @close="emit('close')">
      <template #header>
        <div>
          <div style="font-weight: bold">发表新的帖子</div>
          <div style="font-size: 13px">发表内容之前，请遵守相关法律法规，不要出现骂人等爆粗口不文明行为</div>
        </div>
      </template>
      <div style="display: flex;gap: 10px">
        <div style="width: 200px">
          <el-select placeholder="请选择文章类型..." value-key="id" v-model="editor.type" :disable="store.forum.types.length === 0">
            <el-option v-for="item in store.forum.types.filter(type => type.id > 0)" :value="item" :label="item.name">
              <div>
                <ColorDot :color="item.color"/>
                <span style="margin-left: 10px">{{item.name}}</span>
              </div>
            </el-option>
          </el-select>
        </div>
        <div style="flex: 1">
          <el-input placeholder="请输入文章标题" :prefix-icon="Document" v-model="editor.title" maxlength="38" />
        </div>
      </div>
      <div style="margin-top: 10px;font-size: 13px;color: grey">
        <ColorDot :color="editor.type ? editor.type.color : '#ccc'"/>
        <span style="margin-left: 5px">{{ editor.type ? editor.type.desc : "请在上方选择一个文章类型" }}</span>
      </div>
      <div style="margin-top: 10px;height: 440px;overflow: hidden;border-radius: 5px"
           v-loading="editor.loading"
           element-loading-text="正在上传图片">
        <quill-editor
            ref="refEditor"
            placeholder="今天想分享点什么呢?"
            style="height: calc(100% - 45px)"
            content-type="delta"
            v-model:content="editor.text"
            :options="editorOption"
        />
      </div>
      <div style="display: flex;justify-content: space-between;margin-top: 10px">
        <div style="color: grey;font-size: 13px">当前字数 {{ contentLength.length }} 最大支持(20000字) 注:上传图片时, 请手动拖拽设置图片大小, 最大不要超过600px宽度</div>
        <div>
          <el-button type="success" :icon="Check" plain @click="submitTopic">立即发布</el-button>
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