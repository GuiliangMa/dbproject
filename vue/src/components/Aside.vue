<script>
export default {
  name: "Aside",

  props: {
    msg: String,
    isCollapse: Boolean,
    sideWidth:Number
  },
  data(){
    return{
      user:sessionStorage.getItem("user")?JSON.parse(sessionStorage.getItem("user")):{username:'',nickname:'游客'}
    }
  },
  methods:{
    returnRouter(index){
      console.log(this.$route)
    }
  }
}
</script>

<template>
  <el-aside :width="sideWidth+'px'"
            style="background-color: rgb(238, 241, 246);height: 100%;box-shadow: 2px 0 6px rgb(0 21 41/35%)">
    <el-menu :default-openeds="['3','4']"
             style="height: 100%; overflow-x: hidden"
             background-color="rgb(48, 65, 86)"
             text-color="#fff"
             active-text-color="#ffd04b"
             :collapse-transition="false"
             :collapse="isCollapse"
             router>
      <div style="height:60px;line-height: 60px;text-align: center">
        <i class="el-icon-s-home" style="margin: 5px;color:white"></i>
        <!--            <img src="../assets/sdu.png" alt="" style="width: 20px; position: relative; top: 50px; margin-right: 5px">-->
        <b style="color:white" v-show="!isCollapse">教室管理系统</b>
      </div>

      <el-menu-item index="/home">
        <i class="el-icon-house"></i>
        <span>主页</span>
      </el-menu-item>

      <el-submenu index="2" v-if="user.status==='管理员'">
        <template slot="title">
          <i class="el-icon-menu"></i>
          <span slot="title">教室管理</span>
        </template>
          <el-menu-item index="/classroom">教室基本信息</el-menu-item>
          <el-menu-item index="/apply">教室申请信息</el-menu-item>
          <el-menu-item index="/usagetable">教室使用信息</el-menu-item>
          <el-menu-item index="/assign"> 教室请用信息</el-menu-item>
      </el-submenu>

      <el-submenu index="3">
        <template slot="title">
          <i class="el-icon-house"></i>
          <span slot="title">我的教室</span>
        </template>
          <el-menu-item index="/free">空闲教室</el-menu-item>
        <el-menu-item index="/myasign">我的请求</el-menu-item>
          <el-menu-item index="/myapply">我申请的</el-menu-item>
          <el-menu-item index="/myusage">我使用的</el-menu-item>
      </el-submenu>

      <el-submenu index="4">
        <template slot="title">
          <i class="el-icon-reading"></i>
          <span slot="title">我的课程</span>
        </template>
        <el-menu-item v-if="user.status==='管理员'" index="/teachercourse">开课信息</el-menu-item>
        <el-menu-item v-if="user.status==='管理员'" index="/teachercoursetrial">课程教室审核</el-menu-item>
        <el-menu-item v-if="user.status!=='学生'" index="/teachercourseapply">申请课程教室</el-menu-item>
        <el-menu-item v-if="user.status!=='学生'" index="/myteachercourse">我的授课</el-menu-item>
<!--        <el-menu-item v-if="user.status!=='学生'" index="/mycoursetrial">我申请的授课</el-menu-item>-->
        <el-menu-item v-if="user.status!=='教师'" index="/mycourse">我的课程</el-menu-item>
      </el-submenu>

      <el-submenu index="5" v-if="user.status==='管理员'">
        <template slot="title">
          <i class="el-icon-s-custom"></i>
          <span slot="title">基础管理</span>
        </template>
        <el-menu-item index="/user">用户信息</el-menu-item>
        <el-menu-item index="/student">学生信息</el-menu-item>
        <el-menu-item index="/teacher">教师信息</el-menu-item>
        <el-menu-item index="/course">课程信息</el-menu-item>
        <el-menu-item index="/studentcourse"> 学生课程信息 </el-menu-item>
        <el-menu-item index="/teach"> 教师授课信息 </el-menu-item>
      </el-submenu>


    </el-menu>
  </el-aside>
</template>

<style scoped>

</style>