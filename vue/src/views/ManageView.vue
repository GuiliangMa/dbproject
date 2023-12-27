<template>
  <div>
    <el-container style="height: 100vh; border: 1px solid #eee">

      <Aside :isCollapse="isCollapse" :sideWidth="sideWidth"/>

      <el-container>
        <el-header style="border-bottom: 1px solid #ccc;padding: 0px">
          <Header :collapseBtnClass="collapseBtnClass" :collapse="collapse"/>
        </el-header>

        <el-main>
          <!--     当前界面的子路由会在router-view中展示     -->
          <router-view/>

        </el-main>

      </el-container>
    </el-container>
  </div>
</template>

<style>
.el-header {
  background-color: #B3C0D1;
  color: #333;
  line-height: 60px;
}

.el-aside {
  color: #333;
}
</style>

<script>

import Aside from "@/components/Aside.vue";
import Header from "@/components/Header.vue";

export default {
  name: 'HomeView',

  components:{
    Aside,
    Header
  },

  data() {
    return {
      collapseBtnClass: 'el-icon-s-fold',
      isCollapse: false,
      sideWidth: 200,
    }
  },

  methods: {
    // 侧边栏展开与收缩
    collapse() {
      this.isCollapse = !this.isCollapse
      this.collapseBtnClass = this.isCollapse === false ? 'el-icon-s-fold' : 'el-icon-s-unfold'
      this.sideWidth = this.isCollapse === false ? 200 : 64
    }
  }
}
</script>

<style>
</style>