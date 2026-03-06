<!-- 404.vue -->
<template>
  <div class="not-found-page">
    <h1>404 - 页面未找到</h1>
    <p>您访问的页面不存在或已被移除</p>
    <!-- 返回cz.vue按钮 -->
    <button class="back-to-cz" @click="goBackToCz">返回</button>
  </div>
</template>

<script setup>
import { useRouter, useRoute } from 'vue-router';

const router = useRouter();
const route = useRoute();

// 返回上一级页面
const goBackToCz = () => {
  router.back();
};
</script>

<style scoped>
.not-found-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background-color: #f5f5f5;
  text-align: center;
  padding: 20px;
}
.not-found-page h1 {
  font-size: 64px;
  font-weight: bold;
  color: #ff4d4f;
  margin-bottom: 20px;
}
.not-found-page p {
  font-size: 18px;
  color: #666;
  margin-bottom: 30px;
}
.back-to-cz {
  background-color: #ff4d4f;
  color: #fff;
  border: none;
  border-radius: 24px;
  padding: 12px 32px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}
.back-to-cz:hover {
  background-color: #ff7875;
}
</style>