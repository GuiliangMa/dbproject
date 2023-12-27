<template>
  <div>
    <div style="margin-bottom:10px">
      <el-input
          class="ml5"
          style="width: 200px"
          prefix-icon="el-icon-reading"
          placeholder="请输入课程编号"
          v-model="inputCid"
          clearable
      />

      <el-input
          class="ml5"
          style="width: 200px"
          prefix-icon="el-icon-reading"
          placeholder="请输入课程名称"
          v-model="inputCname"
          clearable
      />

      <el-input
          class="ml5"
          style="width: 200px"
          prefix-icon="el-icon-user"
          placeholder="请输入学生学号"
          v-model="inputSid"
          clearable
      />
      <el-input
          class="ml5"
          style="width: 200px"
          prefix-icon="el-icon-user-solid"
          placeholder="请输入教师姓名"
          v-model="inputName"
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

      <el-button type="success" slot="reference" @click="dialogVisible=true">新增学生</el-button>

      <el-popconfirm title="确定批量删除这些信息吗？" class="ml5 mr5"
                     @onConfirm="handleDelBatch">
        <el-button type="danger" slot="reference">批量删除 <i class="el-icon-remove-outline"></i></el-button>
      </el-popconfirm>

      <el-upload
          action="http://localhost:9090/teachercourse/importStudentFile"
          :show-file-list="false"
          accept=".xlsx,.xls"
          style="display: inline-block"
          :on-success="handleExcelImportSuccess">
        <el-button type="success">导入学生授课<i class="el-icon-bottom"></i></el-button>
      </el-upload>
    </div>

    <el-table :data="tableData"
              border
              stripe
              :header-cell-style="{background:'#lightgray',color:'black'}"
              @selection-change="handleSelectionChange">

      <el-table-column type="expand">
        <template slot-scope="props">
          <el-form label-position="left" class="demo-table-expand" :label-position="'right'">
            <el-form-item label="课程开设时间:" style="margin-bottom: 0" label-width="150px">
              <div style="font-weight: bolder;margin-bottom: 0">
                {{props.row.startDate}} ~ {{props.row.endDate}}
              </div>
            </el-form-item>
            <el-form-item label="课程上课时间:" style="margin-bottom: 0" label-width="150px">
              <div style="margin-bottom: 0;font-weight: bolder">
                {{props.row.strWeekday}}  {{props.row.beginTime}} ~ {{props.row.endTime}}
              </div>
            </el-form-item>
            <el-form-item label="课程频率:" style="margin-bottom: 0" label-width="150px">
              <div style="font-weight: bolder">
                {{props.row.times}} 周1次
              </div>
            </el-form-item>
          </el-form>
        </template>
      </el-table-column>
      <el-table-column type="selection" width="50"/>
      <el-table-column prop="roomName" label="教室" width="100px" sortable/>
      <el-table-column prop="sid" label="学生学号" width="150px" sortable/>
      <el-table-column prop="name" label="教师名称" width="150px"/>
      <el-table-column prop="cid" label="课程号" width="150px"/>
      <el-table-column prop="cname" label="课程名称" width="150px"/>
      <el-table-column label="操作" width="220 px">
        <template slot-scope="scope">
          <el-popconfirm title="确定这条信息吗？" class="ml5 mr5"
                         @onConfirm="handleDelete(scope.row)">
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

    <div>
      <el-dialog
          title="用户信息"
          :visible.sync="dialogVisible"
          width="500px">

        <el-form :model="form"
                 :label-position="'right'">

          <el-form-item label="学生学号" label-width="120px">
            <el-input v-model="form.sid" autocomplete="off" style="width: 250px" clearable></el-input>
          </el-form-item>
          <el-form-item label="学生姓名" label-width="120px">
            <el-input v-model="form.studentName" autocomplete="off" style="width: 250px" clearable></el-input>
          </el-form-item>
          <el-form-item label="课程号" label-width="120px">
            <el-input v-model="form.cid" autocomplete="off" style="width: 250px"></el-input>
          </el-form-item>

        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button class="ml5" @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="save">确 定</el-button>
        </div>
      </el-dialog>
    </div>

  </div>
</template>


<script>
export default {
  name: "StudentCourse",
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

      inputRoomName:'',
      inputName:'',
      inputCname:'',
      inputState:'',
      inputCid:'',
      inputSid:'',

      form: {},

      multipleSelection: [],

      visible: false,

      stateOptions:[{
        value: '已使用',
        label: '已使用'
      }, {
        value: '在使用',
        label: '在使用'
      },{
        value: '待使用',
        label: '待使用'
      }],
      dialogVisible:false
    }
  },

  created() {
    this.load()
  },

  methods: {

    // 刷新与重置
    load() {
      this.request.get("http://localhost:9090/studentcourse/page?", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name:this.inputName,
          cname:this.inputCname,
          cid:this.inputCid,
          sid:this.inputSid
        }
      }).then(res => {
        // console.log(res)
        if(res.code=='200'){
          this.tableData=res.data.data
          this.total = res.data.total
        }
        else this.$message.error(res.msg)
        // console.log(this.tableData)
      })
      this.form={}
    },
    reset() {
      this.inputRoomName=''
      this.inputName=''
      this.inputCname=''
      this.inputState=''
      this.inputCid=''
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
    handleDelete(row) {
      this.request.post("http://localhost:9090/studentcourse/delete",row).then(res => {
        if (res.code== '200') {
          this.$message.success("删除成功")
          this.load()
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    handleDelBatch() {
      let users = this.multipleSelection
      this.request.post("http://localhost:9090/studentcourse/delbatch/", users).then(res => {
        console.log(1)
        if (res != 0) {
          this.$message.success("删除成功" + res.data + "条信息")
          this.load()
        } else {
          this.$message.error("删除失败")
        }
      })
    },
    handleAllExport() {
      window.open("http://localhost:9090/teachercourse/export")
    },
    handleAddStudent(row){
      this.form=row
      this.dialogVisible=true
    },
    save(){
      this.request.post("http://localhost:9090/teachercourse/handleAdd", this.form).then(res => {
        console.log(res)
        if (res.code === '200') {
          this.$message.success("保存成功")
          this.dialogVisible = false
          this.load()
        } else {
          this.$message.error(res.msg)
        }
      })
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