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
      <el-table-column prop="sid" label="学生学号" width="150px" sortable/>
      <el-table-column prop="roomName" label="教室名称" width="150"/>
      <el-table-column prop="name" label="教师名称" width="150px"/>
      <el-table-column prop="cid" label="课程号" width="150px"/>
      <el-table-column prop="cname" label="课程名称" width="150px"/>
      <el-table-column prop="mode" label="当前状态">
        <template slot-scope="scope">
          <div v-if="scope.row.mode=='已使用'"  style="color: #3F5EF8;font-weight: bold">
            {{scope.row.mode}}
          </div>
          <div v-if="scope.row.mode=='在使用'"  style="color: #42b983;font-weight: bold">
            {{scope.row.mode}}
          </div>
          <div v-if="scope.row.mode=='待使用'"  style="color: lightsalmon;font-weight: bold">
            <b> {{scope.row.mode}} </b>
          </div>
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

  </div>
</template>


<script>
export default {
  name: "MyCourse",
  data() {
    return {
      user:sessionStorage.getItem("user")?JSON.parse(sessionStorage.getItem("user")):{username:'',nickname:'游客'},
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
      this.request.get("http://localhost:9090/studentcourse/studentpage?", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          roomName: this.inputRoomName,
          name:this.inputName,
          cname:this.inputCname,
          mode:this.inputState,
          cid:this.inputCid,
          username:this.user.username
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