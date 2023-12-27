<script>

export default {
  name: "Header",
  props: {
    collapseBtnClass: String,
    collapse: Function
  },
  data(){
    return{
      user:sessionStorage.getItem("user")?JSON.parse(sessionStorage.getItem("user")):{username:'',nickname:'游客'}
    }
  },
  methods:{
    logout(){
      this.$router.push("/login")
      sessionStorage.removeItem("user")
      this.$message("退出成功")
    }
  }
}
</script>

<template>
  <div style="font-size: 12px;background: ghostwhite;display: flex;line-height: 60px">
    <div style="flex:1;font-size:18px">
      <span :class="collapseBtnClass"
                  style="cursor:pointer;padding: 15px"
                  @click="collapse"></span>
    </div>

    <el-dropdown style="text-align: right;flex:2;cursor: pointer;width: 100px;padding-right: 20px">
      <div style="inline-block">
        <span>{{ user.nickname }}</span>
        <i class="el-icon-arrow-down" style="margin-right: 15px;margin-left: 5px"></i>
      </div>
      <el-dropdown-menu slot="dropdown" style="margin: 10px;width: 70px">
        <el-dropdown-item style="font-size: 14px">主页</el-dropdown-item>
        <el-dropdown-item style="font-size: 14px">
          <span to="/login" style="text-decoration: none" @click="logout">退出</span>
        </el-dropdown-item>
      </el-dropdown-menu>

    </el-dropdown>
  </div>
</template>

<style scoped>

</style>