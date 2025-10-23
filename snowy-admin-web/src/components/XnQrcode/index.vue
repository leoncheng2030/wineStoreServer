<template>
  <div class="xn-qrcode">
    <QRCode
      ref="qrcodeRef"
      :tag="tag"
      :text="value"
      :size="size"
      :color="color"
      :bg-color="bgColor"
      :level="level"
      :margin="margin"
      :scale="scale"
      :width="width"
      :height="height"
      :image-src="imageSrc"
      :image-width="imageWidth"
      :image-height="imageHeight"
      :image-x="imageX"
      :image-y="imageY"
      :image-margin="imageMargin"
      :image-margin-color="imageMarginColor"
      :options="options"
      v-bind="$attrs"
    />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import QRCode from '@chenfengyuan/vue-qrcode'

// 定义组件名称
defineOptions({
  name: 'XnQrcode'
})

// 定义组件属性
const props = defineProps({
  // 二维码内容
  text: {
    type: [String, Number],
    default: ''
  },
  // 二维码标签类型
  tag: {
    type: String,
    default: 'canvas'
  },
  // 二维码尺寸
  size: {
    type: [String, Number],
    default: 128
  },
  // 前景色
  color: {
    type: String,
    default: '#000'
  },
  // 背景色
  bgColor: {
    type: String,
    default: '#FFF'
  },
  // 容错级别
  level: {
    type: String,
    default: 'L'
  },
  // 边距
  margin: {
    type: [String, Number],
    default: 0
  },
  // 缩放比例
  scale: {
    type: [String, Number],
    default: 4
  },
  // 宽度
  width: {
    type: [String, Number],
    default: undefined
  },
  // 高度
  height: {
    type: [String, Number],
    default: undefined
  },
  // 中间图片地址
  imageSrc: {
    type: String,
    default: undefined
  },
  // 中间图片宽度
  imageWidth: {
    type: [String, Number],
    default: undefined
  },
  // 中间图片高度
  imageHeight: {
    type: [String, Number],
    default: undefined
  },
  // 中间图片X坐标
  imageX: {
    type: [String, Number],
    default: undefined
  },
  // 中间图片Y坐标
  imageY: {
    type: [String, Number],
    default: undefined
  },
  // 中间图片边距
  imageMargin: {
    type: [String, Number],
    default: 0
  },
  // 中间图片边距颜色
  imageMarginColor: {
    type: String,
    default: '#FFF'
  },
  // 其他选项
  options: {
    type: Object,
    default: () => ({})
  }
})

// 确保文本值始终是字符串类型
const value = computed(() => {
  console.log('二维码组件接收到的文本:', props.text)
  return String(props.text || '')
})

// 定义组件引用
const qrcodeRef = ref()

// 定义组件方法
defineExpose({
  // 获取二维码数据URL
  toDataURL (options) {
    if (qrcodeRef.value) {
      return qrcodeRef.value.toDataURL(options)
    }
  },
  // 获取二维码文件
  toFile (filename, options) {
    if (qrcodeRef.value) {
      return qrcodeRef.value.toFile(filename, options)
    }
  },
  // 获取二维码数据
  toData (options) {
    if (qrcodeRef.value) {
      return qrcodeRef.value.toData(options)
    }
  }
})
</script>

<style scoped>
.xn-qrcode {
  display: inline-block;
}
</style>