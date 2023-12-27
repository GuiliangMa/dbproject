<template>
  <div class="wrapper">
    <div style="margin: 200px auto;background-color: #fff;width: 350px;height: 300px;padding: 20px;border-radius: 10px">
      <div style="margin: 20px 0;text-align: center;font-size: 24px">
        <b>登录</b>
      </div>
      <el-form :model="user" :rules="rules" ref="userForm">
        <el-form-item prop="username">
          <el-input size="medium"
                    style="margin: 5px 0"
                    prefix-icon="el-icon-user"
                    clearable
                    placeholder="请输入账号"
                    v-model="user.username">
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input size="medium"
                    style="margin: 5px 0"
                    prefix-icon="el-icon-lock"
                    show-password
                    clearable
                    placeholder="请输入密码"
                    v-model="user.password">
          </el-input>
        </el-form-item>
      </el-form>

      <div style="margin: 20px 0;text-align: center">
        <el-button type="primary" size="small" autocomplete="off" @click="login">登录</el-button>
        <el-button type="warning" size="small" autocomplete="off" style="margin-left: 20px" @click="register">注册</el-button>
      </div>

    </div>
  </div>
</template>

<script>

import {Loading} from "element-ui";
import {delay} from "@/assets/lib/utils";
import user from "@/views/User.vue";

export default {
  name: "Login",
  data() {
    return {
      user:{
        username: '',
        password: ''
      },
      rules: {
        // 属性与prop对应
        username: [
          {required: true, message: '!用户名不能为空', trigger: 'blur'}
        ],
        password: [
          {required: true, message: '!密码不能为空', trigger: 'blur'},
          {min: 5, max: 20, message: '!密码长度应为5至20个字符', trigger: 'blur'}
        ]
      },
    }
  },

  created() {
    if(sessionStorage.getItem("registerUser")){
      console.log("true")
      this.user.username=JSON.parse(sessionStorage.getItem("registerUser")).username
      this.user.password=JSON.parse(sessionStorage.getItem("registerUser")).password
    }
    else{
      this.user.username=''
      this.user.password=''
    }
  },

  methods: {
    async login(formName) {
      this.$refs['userForm'].validate((valid) => {
        if (valid){
          this.request.post("http://localhost:9090/user/login", this.user).then(async res => {

            await console.log(res)

            let loadingInstance = Loading.service({
              text: "请稍等片刻",
              background: 'rgba(0,0,0,.5)'
            })
            await delay(200) // 同步设置

            loadingInstance.close();
            if (res.code==="200") {

              sessionStorage.setItem("user",JSON.stringify(res.data))

              this.$router.push('/home')
              this.$message.success("登录成功")
              if(sessionStorage.getItem("registerUser")){
                sessionStorage.removeItem("registerUser")
              }

            } else {
              this.$message.error(res.msg)
              this.user.password = ''
            }
          })
        }
      })
    },

    register(){
      this.$router.push('/register')
    }
  }
}
</script>

<style scoped>
.wrapper {
  height: 100vh;
  background-image: linear-gradient(to bottom right, #FC4668, #3F5EF8);
  overflow: hidden;
}
</style>