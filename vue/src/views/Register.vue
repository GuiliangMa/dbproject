<template>
  <div class="wrapper">
    <div style="margin: 150px auto;background-color: #fff;width: 350px;height: 400px;padding: 20px;border-radius: 10px">
      <div style="margin: 20px 0;text-align: center;font-size: 24px">
        <b>注册</b>
      </div>
      <el-form :model="user" :rules="rules" ref="user">
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
        <el-form-item prop="checkPassword">
          <el-input size="medium"
                    style="margin: 5px 0"
                    prefix-icon="el-icon-lock"
                    show-password
                    clearable
                    placeholder="请再次确定密码"
                    v-model="user.checkPassword">
          </el-input>
        </el-form-item>
        <el-form-item prop="status">
          <el-select v-model="user.status" placeholder="请选择你的身份" style="width:310px;height: 36px">
            <el-option
                prefix="el-icon-key"
                v-for="item in options"
                :key="item.value"
                :label="item.label"
                :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>

      <div style="margin: 20px 0;text-align: center">
        <el-button type="primary" size="small" autocomplete="off" @click="register">注册</el-button>
        <el-button type="danger" size="small" autocomplete="off" style="margin-left: 20px" @click="backLogin">返回登录</el-button>
      </div>

    </div>
  </div>
</template>
<script>
import {Loading} from "element-ui";
import {delay} from "@/assets/lib/utils";

export default {
  name: "Register",
  data() {
    var validatePass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入密码'));
      } else {
        if (this.user.checkPassword !== '') {
          this.$refs.user.validateField('checkPassword');
        }
        callback();
      }
    };
    var validatePass2 = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'));
      } else if (value !== this.user.password) {
        callback(new Error('两次输入密码不一致!'));
      } else {
        callback();
      }
    };


    return {
      user:{
        username:'',
        password:'',
        status:'',
        nickname:'用户',
        checkPassword:'',
      },
      rules:{
        username: [
          {required: true, message: '!用户名不能为空', trigger: 'blur'}
        ],
        password: [
          {required: true, message: '!密码不能为空', trigger: 'blur'},
          {min: 5, max: 20, message: '!密码长度应为5至20个字符', trigger: 'blur'},
          { validator: validatePass, trigger: 'blur' }
        ],
        checkPassword: [
          { validator: validatePass2, trigger: 'blur' }
        ],
        status: [
          {required: true, message: '!身份不能为空', trigger: 'blur'}
        ],
      },
      options: [{
        value: '学生',
        label: '学生'
      }, {
        value: '教师',
        label: '教师'
      }],
    }
  },
  methods:{
    async register() {
      this.$refs['user'].validate((valid) => {
        if (valid){
          this.request.post("http://localhost:9090/user/register", this.user).then(async res => {

            await console.log(res)

            let loadingInstance = Loading.service({
              text: "请稍等片刻",
              background: 'rgba(0,0,0,.5)'
            })
            await delay(200) // 同步设置

            loadingInstance.close();
            if (res.code==="200") {

              sessionStorage.setItem("registerUser",JSON.stringify(this.user))

              this.$router.push('/login')
              this.$message.success("注册成功")

            } else {
              this.$message.error(res.msg)
              this.user.password = ''
              this.checkPassword=''
              this.user.status=''
            }
          })
        }
      })
    },

    backLogin(){
      this.$router.push("/login");
    }
  }
}
</script>

<style scoped>
.wrapper {
  height: 100vh;
  background-image: linear-gradient(to bottom right, #42b983, #FFFF00);
  overflow: hidden;
}
</style>