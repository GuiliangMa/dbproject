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
    </div>
    <div style="margin-bottom:10px">
      <el-input
          class="ml5"
          style="width: 200px"
          prefix-icon="el-icon-house"
          placeholder="请输入教室名称"
          v-model="inputRoomName"
          clearable
      />
      <el-select v-model="inputState" placeholder="请选择当前使用状态" class="ml5" style="width: 200px;" clearable>
        <el-option
            v-for="item in stateOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value">
        </el-option>
      </el-select>
      <el-button class="ml5" type="primary" @click="search">
        <i class="el-icon-search"/>
        <b>搜索</b>
      </el-button>

      <el-button class="ml5" type="warning" @click="reset">
        <i class="el-icon-circle-close"></i>
        <b>重置</b>
      </el-button>
      <el-popconfirm title="确定批量删除这些信息吗？" class="ml5 mr5"
                     @onConfirm="handleDelBatch">
        <el-button type="danger" slot="reference">批量删除 <i class="el-icon-remove-outline"></i></el-button>
      </el-popconfirm>

      <el-button type="primary" @click="handleAllExport" class="ml5">全部导出<i class="el-icon-top"></i></el-button>
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
      <el-table-column prop="tcid" label="授课序号" width="100px" sortable/>
      <el-table-column prop="roomName" label="教室名称" width="150"/>
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

      <el-table-column label="操作" width="220 px">
        <template slot-scope="scope">
          <el-button type="success" slot="reference" @click="handleAddStudent(scope.row)">新增学生</el-button>
          <el-popconfirm title="确定这条信息吗？" class="ml5 mr5"
                         @onConfirm="handleDelete(scope.row.tcid)">
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
          <el-form-item label="授课序号" label-width="120px">
            <el-input v-model="form.tcid" autocomplete="off" style="width: 250px" disabled="true"></el-input>
          </el-form-item>
          <el-form-item label="课程号" label-width="120px">
            <el-input v-model="form.cid" autocomplete="off" style="width: 250px" disabled="true"></el-input>
          </el-form-item>
          <el-form-item label="课程名称" label-width="120px">
            <el-input v-model="form.cname" autocomplete="off" style="width: 250px" disabled="true"></el-input>
          </el-form-item>

        </el-form>
        <div slot="footer" class="dialog-footer">
<!--          <el-upload-->
<!--              action="http://localhost:9090/teachercourse/importStudentFile"-->
<!--              :show-file-list="false"-->
<!--              accept=".xlsx,.xls"-->
<!--              style="display: inline-block"-->
<!--              :on-success="handleExcelImportSuccess">-->
<!--            <el-button type="success">导入<i class="el-icon-bottom"></i></el-button>-->
<!--          </el-upload>-->
          <el-button class="ml5" @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="save">确 定</el-button>
        </div>
      </el-dialog>
    </div>

  </div>
</template>


<script>
export default {
  name: "TeacherCourse",
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
      dialogVisible:false,

    }
  },

  created() {
    this.load()
  },

  methods: {

    // 刷新与重置
    load() {
      this.request.get("http://localhost:9090/teachercourse/page?", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          roomName: this.inputRoomName,
          cname:this.inputCname,
          mode:this.inputState,
          cid:this.inputCid
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
    handleDelete(tcid) {
      this.request.delete("http://localhost:9090/teachercourse/" + tcid).then(res => {
        if (res.code === '200') {
          this.$message.success("删除成功")
          this.load()
        } else {
          this.$message.error("删除失败")
        }
      })
    },
    handleDelBatch() {
      let users = this.multipleSelection.map(v => v.tcid)
      this.request.post("http://localhost:9090/teachercourse/delbatch/", users).then(res => {
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