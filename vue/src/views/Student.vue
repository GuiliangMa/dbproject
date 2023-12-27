<template>
  <div>
    <div style="margin-bottom:10px">
      <el-input
          style="width: 200px"
          prefix-icon="el-icon-s-data"
          placeholder="请输入学号"
          v-model="inputSid"
          clearable
      />
      <el-input
          class="ml5"
          style="width: 200px"
          prefix-icon="el-icon-user-solid"
          placeholder="请输入姓名"
          v-model="inputName"
          clearable
      />

      <el-input
          class="ml5"
          style="width: 200px"
          prefix-icon="el-icon-user"
          placeholder="请输入用户名"
          v-model="inputUsername"
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
      <el-table-column prop="sid" label="学号" width="120px" sortable/>
      <el-table-column prop="name" label="姓名" width="120"/>
      <el-table-column prop="username" label="用户名" width="140"/>
      <el-table-column prop="gender" label="性别" width="120"/>
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
        title="新增学生信息"
        :visible.sync="dialogVisible"
        width="500px">

      <el-form :model="form"
               :label-position="'right'">

        <el-form-item label="学号" label-width="120px">
          <el-input v-model="form.sid" autocomplete="off" style="width: 250px" clearable></el-input>
        </el-form-item>

        <el-form-item label="姓名" label-width="120px">
          <el-input v-model="form.name" autocomplete="off" style="width: 250px" clearable></el-input>
        </el-form-item>

        <el-form-item label="性别" label-width="120px">
<!--          <el-input v-model="form.gender" autocomplete="off" style="width: 250px" clearable></el-input>-->
          <el-radio v-model="form.gender" label="男">男</el-radio>
          <el-radio v-model="form.gender" label="女">女</el-radio>
        </el-form-item>

        <el-form-item label="权限" label-width="120px">
          <el-input v-model="form.status" disabled style="width: 250px"></el-input>
        </el-form-item>

        <el-form-item label="邮箱" label-width="120px">
          <el-input v-model="form.email" autocomplete="off" style="width: 250px" clearable></el-input>
        </el-form-item>
        <el-form-item label="电话" label-width="120px">
          <el-input v-model="form.phone" autocomplete="off" style="width: 250px" clearable></el-input>
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog
        title="编辑学生信息"
        :visible.sync="editDialogVisible"
        width="500px">

      <el-form :model="form"
               :label-position="'right'">

        <el-form-item label="学号"  label-width="120px">
          <el-input v-model="form.sid" disabled autocomplete="off" style="width: 250px" clearable></el-input>
        </el-form-item>

        <el-form-item label="姓名" label-width="120px">
          <el-input v-model="form.name" autocomplete="off" style="width: 250px" clearable></el-input>
        </el-form-item>

        <el-form-item label="性别" label-width="120px">
          <!--          <el-input v-model="form.gender" autocomplete="off" style="width: 250px" clearable></el-input>-->
          <el-radio v-model="form.gender" label="男">男</el-radio>
          <el-radio v-model="form.gender" label="女">女</el-radio>
        </el-form-item>

        <el-form-item label="用户名" label-width="120px">
          <el-input v-model="form.username" disabled style="width: 250px"></el-input>
        </el-form-item>

        <el-form-item label="权限" label-width="120px">
          <el-input v-model="form.status" disabled style="width: 250px"></el-input>
        </el-form-item>

        <el-form-item label="邮箱" label-width="120px">
          <el-input v-model="form.email" autocomplete="off" style="width: 250px" clearable></el-input>
        </el-form-item>
        <el-form-item label="电话" label-width="120px">
          <el-input v-model="form.phone" autocomplete="off" style="width: 250px" clearable></el-input>
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="edit">确 定</el-button>
      </div>
    </el-dialog>

  </div>
</template>


<script>
export default {
  name: "User",
  data() {
    return {
      tableData: [],
      collapseBtnClass: 'el-icon-s-fold',
      isCollapse: false,
      sideWidth: 200,
      headerBg: 'headerBg',

      total: 0,
      pageNum: 1,
      pageSize: 8,

      inputSid: '',
      inputName: '',
      inputUsername: '',

      dialogVisible: false,
      editDialogVisible:false,
      form: {
        sid:'',
        name:'',
        username:'',
        status:'',
        gender:'',
        email:'',
        phone:''
      },

      multipleSelection: [],

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
      this.request.get("http://localhost:9090/student/page?", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          sid:this.inputSid,
          name:this.inputName,
          username:this.inputUsername
        }
      }).then(res => {
        console.log(res)
        this.tableData = res.data
        this.total = res.total
      })
    },
    reset() {
      this.inputUsername = ''
      this.inputName = ''
      this.inputSid = ''
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
      this.form.status='学生'
      this.dialogVisible = true
    },
    save() {
      console.log(this.form)
      this.request.post("http://localhost:9090/student/save", this.form).then(res => {
        console.log(res)
        if (res.code=='200') {
          this.$message.success("保存成功")
          this.dialogVisible = false
          this.load()
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    edit(){
      console.log(this.form)
      this.request.post("http://localhost:9090/student/update", this.form).then(res => {
        console.log(res)
        if (res.code=='200') {
          this.$message.success("保存成功")
          this.editDialogVisible = false
          this.load()
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    handleEdit(row) {
      // console.log(row)
      this.form = Object.assign({}, row)//深拷贝,v-mode 具有双向绑定
      console.log(this.form)
      this.form.status='学生'
      this.editDialogVisible = true
    },
    handleDelete(username) {
      console.log(username)
      this.request.delete("http://localhost:9090/student/" + username).then(res => {
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