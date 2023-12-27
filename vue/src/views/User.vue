<template>
  <div>
    <div style="margin-bottom:10px">
      <el-input
          style="width: 200px"
          prefix-icon="el-icon-user"
          placeholder="请输入用户名"
          v-model="inputUsername"
          clearable
      />
      <el-input
          class="ml5"
          style="width: 200px"
          prefix-icon="el-icon-message"
          placeholder="请输入邮箱"
          v-model="inputEmail"
          clearable
      />

      <el-input
          class="ml5"
          style="width: 200px"
          prefix-icon="el-icon-phone"
          placeholder="请输入手机号码"
          v-model="inputPhone"
          clearable
      />

      <el-button class="ml5" type="primary" @click="search">
        <i class="el-icon-search"/>
        <b>搜索</b>
      </el-button>

      <el-button class="ml5" type="warning" @click="reset">
        <i class="el-icon-circle-close"></i>
        <b>重置</b>
      </el-button>
    </div>

    <div style="margin-bottom: 10px">
      <el-button type="primary" @click="handleAdd">新增<i class="el-icon-circle-plus-outline"></i></el-button>

      <el-popconfirm title="确定批量删除这些信息吗？" class="ml5 mr5"
                     @onConfirm="handleDelBatch">
        <el-button type="danger" slot="reference">批量删除 <i class="el-icon-remove-outline"></i></el-button>
      </el-popconfirm>

      <el-upload
          action="http://localhost:9090/user/import"
          :show-file-list="false"
          accept=".xlsx,.xls"
          style="display: inline-block"
          :on-success="handleExcelImportSuccess">
        <el-button type="primary">导入<i class="el-icon-bottom"></i></el-button>
      </el-upload>

      <el-button type="primary" @click="handleAllExport" class="ml5">全部导出<i class="el-icon-top"></i></el-button>
    </div>

    <el-table :data="tableData"
              border
              stripe
              :header-cell-style="{background:'#lightgray',color:'black'}"
              @selection-change="handleSelectionChange">

      <el-table-column type="selection" width="50"/>

      <el-table-column prop="username" label="用户名" width="140" sortable/>
      <el-table-column prop="status" label="权限" width="120"/>
      <el-table-column prop="nickname" label="昵称" width="120"/>
      <el-table-column prop="email" label="邮箱"/>
      <el-table-column prop="phone" label="电话"/>


      <el-table-column label="操作" width="200 px">
        <template slot-scope="scope">
          <el-button type="success" @click="handleEdit(scope.row)">编辑 <i class="el-icon-edit"></i></el-button>
          <el-popconfirm title="确定这条信息吗？" class="ml5 mr5"
                         @onConfirm="handleDelete(scope.row.username)">
            <el-button type="danger" slot="reference">删除 <i class="el-icon-remove-outline"></i></el-button>
          </el-popconfirm>

        </template>
      </el-table-column>
    </el-table>

    <!--          分页-->
    <div style="padding: 10px 0">
      <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-sizes="[2, 5, 8, 10, 15, 20]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total">
      </el-pagination>
    </div>

    <!--          新增/修改-->
    <el-dialog
        title="用户信息"
        :visible.sync="dialogVisible"
        width="500px">

      <el-form :model="form"
               :label-position="'right'">

        <el-form-item label="用户名" label-width="120px">
          <el-input v-model="form.username" autocomplete="off" style="width: 250px" clearable></el-input>
        </el-form-item>

        <el-form-item label="权限" label-width="120px">
          <el-select v-model="form.status" placeholder="请选择" style="width: 250px">
            <el-option
                v-for="item in options"
                :key="item.value"
                :label="item.label"
                :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="昵称" label-width="120px">
          <el-input v-model="form.nickname" autocomplete="off" style="width: 250px" clearable></el-input>
        </el-form-item>
        <el-form-item label="邮箱" label-width="120px">
          <el-input v-model="form.email" autocomplete="off" style="width: 250px" clearable></el-input>
        </el-form-item>
        <el-form-item label="电话" label-width="120px">
          <el-input v-model="form.phone" autocomplete="off" style="width: 250px" clearable></el-input>
        </el-form-item>
        <!--        <el-form-item label="地址" label-width="120px">-->
        <!--          <el-input v-model="form.address"-->
        <!--                    style="width: 250px" type="textarea"-->

        <!--                    clearable></el-input>-->
        <!--        </el-form-item>-->


        <!--              <el-form-item label="活动区域" label-width="120px">-->
        <!--                <el-select v-model="form.region" placeholder="请选择活动区域" style="width: 250px">-->
        <!--                  <el-option label="区域一" value="shanghai"></el-option>-->
        <!--                  <el-option label="区域二" value="beijing"></el-option>-->
        <!--                </el-select>-->
        <!--              </el-form-item>-->

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>


<script>
export default {
  name: "User",
  data() {
    return {
      msg: 'hello world',
      tableData: [],
      collapseBtnClass: 'el-icon-s-fold',
      isCollapse: false,
      sideWidth: 200,
      headerBg: 'headerBg',

      total: 0,
      pageNum: 1,
      pageSize: 8,

      inputUsername: '',
      inputEmail: '',
      inputPhone: '',

      dialogVisible: false,
      form: {
        username: '',
        nickname: '',
        status: '',
        email: '',
        phone: ''
      },

      multipleSelection: [],

      options: [
        {
          value: '管理员',
          label: '管理员'
        },
        {
          value: '教师',
          label: '教师'
        },
        {
          value: '学生',
          label: '学生'
        }],

      visible: false
    }
  },

  created() {
    this.load()
  },

  methods: {

    // 刷新与重置
    load() {
      // fetch("http://localhost:9090/user/page?" +
      //     "pageNum="+this.pageNum+
      //     "&pageSize="+this.pageSize+
      //     "&username="+this.inputUsername+
      //     "&email="+this.inputEmail+
      //     "&phone="+this.inputPhone)
      //     .then(res => res.json()).then(res => {
      //   console.log(res)
      //   this.tableData = res.data
      //   this.total = res.total
      // })
      this.request.get("http://localhost:9090/user/page?", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          username: this.inputUsername,
          email: this.inputEmail,
          phone: this.inputPhone
        }
      }).then(res => {
        console.log(res)
        this.tableData = res.data
        this.total = res.total
      })
    },
    reset() {
      this.inputUsername = ''
      this.inputEmail = ''
      this.inputPhone = ''
      this.load()
    },
    search() {
      this.load()
    },
    // 换页函数（自带必要）
    handleSizeChange(pageSize) {
      this.pageSize = pageSize
      this.load()
    },
    handleCurrentChange(pageNum) {
      this.pageNum = pageNum
      this.load()
    },

    // 选择项列对应函数
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },

    // CRUD
    handleAdd() {
      this.form = {}
      this.dialogVisible = true
    },
    save() {
      let num = 0
      if (this.form.id == null) num = 1
      this.request.post("http://localhost:9090/user", this.form).then(res => {
        console.log(res)
        if (res != 0) {
          this.$message.success("保存成功")
          this.dialogVisible = false
          if (num == 1) this.reset()
          else this.load()
        } else {
          this.$message.error("保存失败")
        }
      })
    },
    handleEdit(row) {
      // console.log(row)
      this.form = Object.assign({}, row)//深拷贝,v-mode 具有双向绑定
      console.log(this.form)
      this.dialogVisible = true
    },
    handleDelete(username) {
      console.log(username)
      this.request.delete("http://localhost:9090/user/" + username).then(res => {
        if (res != 0) {
          this.$message.success("删除成功")
          this.load()
        } else {
          this.$message.error("删除失败")
        }
      })
    },
    handleDelBatch() {
      let users = this.multipleSelection.map(v => v.username)
      this.request.post("http://localhost:9090/user/delbatch/", users).then(res => {
        console.log(1)
        if (res != 0) {
          this.$message.success("删除成功" + res + "条信息")
          this.load()
        } else {
          this.$message.error("删除失败")
        }
      })
    },
    handleAllExport() {
      window.open("http://localhost:9090/user/export")

      // this.request.post("http://localhost:9090/user/export", {
      //   params: {
      //     username: this.inputUsername,
      //     email: this.inputEmail,
      //     phone: this.inputPhone
      //   }
      // })
    },
    handleExcelImportSuccess() {
      this.$message.success("文件导入成功")
      this.reset()
    }
  }
}
</script>

<style scoped>
</style>