<template>
  <div>
    <div style="margin-bottom:10px">
      <el-input
          class="ml5"
          style="width: 200px"
          prefix-icon="el-icon-s-data"
          placeholder="请输入教室名称"
          v-model="inputRoomName"
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
      <el-select v-model="inputStatus" placeholder="请选择身份" class="ml5" style="width: 200px;" clearable>
        <el-option
            v-for="item in statusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value">
        </el-option>
      </el-select>
    </div>
    <div style="margin-bottom:10px">
      <el-date-picker
          class="ml5"
          v-model="inputStartTime"
          type="datetime"
          style="width: 200px"
          placeholder="选择起用时间">
      </el-date-picker>
      <el-date-picker
          class="ml5"
          v-model="inputEndTime"
          style="width: 200px"
          type="datetime"
          placeholder="选择终止时间">
      </el-date-picker>
      <el-select v-model="inputMode" placeholder="请选择当前状态" class="ml5" style="width: 200px;" clearable>
        <el-option
            v-for="item in modeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value">
        </el-option>
      </el-select>
    </div>




    <div style="margin-bottom: 10px">
      <el-button class="ml5" type="primary" @click="search">
        <i class="el-icon-search"/>
        <b>搜索</b>
      </el-button>

      <el-button class="ml5" type="warning" @click="reset">
        <i class="el-icon-circle-close"></i>
        <b>重置</b>
      </el-button>

      <el-button type="primary" @click="handleAllExport" class="ml5">全部导出<i class="el-icon-top"></i></el-button>
    </div>

    <el-table :data="tableData"
              border
              stripe
              :header-cell-style="{background:'#lightgray',color:'black'}">
<!--      <el-table-column prop="apid" label="编号" width="70px" sortable/>-->
      <el-table-column type="expand">
        <template slot-scope="props">
          <el-form label-position="left" class="demo-table-expand">
            <el-form-item label="申请事由">
              <span>{{ props.row.remark }}</span>
            </el-form-item>
          </el-form>
        </template>
      </el-table-column>
      <el-table-column prop="roomName" label="申请教室" width="100"/>
      <el-table-column prop="name" label="申请人" width="200"/>
      <el-table-column prop="status" label="身份" width="100px"/>
      <el-table-column prop="startTime" label="启用时间" width="200" sortable/>
      <el-table-column prop="endTime" label="停用时间" width="200" sortable/>
      <el-table-column prop="mode" label="当前状态" width="100px">
        <template slot-scope="scope">
          <div v-if="scope.row.mode=='待批准'"  style="color: #3F5EF8;font-weight: bold">
            {{scope.row.mode}}
          </div>
          <div v-if="scope.row.mode=='已同意'"  style="color: #42b983;font-weight: bold">
            {{scope.row.mode}}
          </div>
          <div v-if="scope.row.mode=='已拒绝'"  style="color: #FC4668;font-weight: bold">
            <b> {{scope.row.mode}} </b>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="操作/备注">
        <template slot-scope="scope">
          <div v-if="scope.row.mode ==='待批准' ">
            <el-popconfirm title="确定同意该申请吗？" class="ml5 mr5"
                           @onConfirm="accept(scope.row)">
              <el-button type="success" slot="reference" >同意 <i class="el-icon-circle-check"></i></el-button>
            </el-popconfirm>
            <el-button type="danger" slot="reference" @click="handleRefuse(scope.row)">拒绝 <i class="el-icon-circle-close"></i></el-button>
          </div>
          <div v-else>
            <span>{{scope.row.msg}}</span>
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

    <el-dialog
      title="编写拒绝原因"
      :visible.sync="refuseDialogVisible"
      width="500px">
      <el-form :model="form"
               :label-position="'right'">
        <el-form-item label="申请人" label-width="120px" style="margin-bottom: 0px">
          <span>{{this.form.name}}</span>
        </el-form-item>
        <el-form-item label="申请教室" label-width="120px" style="margin-bottom: 0px">
          <span>{{this.form.roomName}}</span>
        </el-form-item>
        <el-form-item label="申请日期" label-width="120px" style="margin-bottom: 0px">
          <span>{{this.form.startTime}} ~ {{this.form.endTime}}</span>
        </el-form-item>
        <el-form-item label="申请事由" label-width="120px">
          <el-input type="textarea" style="width: 300px" v-model="form.remark" disabled></el-input>
        </el-form-item>
        <el-form-item>
          ---------------------------------------------------------------------------
        </el-form-item>
        <el-form-item label="拒绝原因" label-width="120px">
          <el-input type="textarea" style="width: 300px" v-model="form.msg"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="refuseDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="refuse">确 定 拒 绝</el-button>
      </div>
    </el-dialog>

  </div>
</template>


<script>
export default {
  name: "Apply",
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
      inputStatus:'',
      inputStartTime:'',
      inputEndTime:'',
      inputMode:'',

      visible: false,
      form:{},
      temp:{},

      statusOptions: [{
        value: '教师',
        label: '教师'
      }, {
        value: '学生',
        label: '学生'
      },{
        value: '管理员',
        label: '管理员'
      }],
      modeOptions:[{
        value: '待批准',
        label: '待批准'
      }, {
        value: '已同意',
        label: '已同意'
      },{
        value: '已拒绝',
        label: '已拒绝'
      }],
      refuseDialogVisible:false,
    }
  },

  created() {
    this.load()
  },

  methods: {

    // 刷新与重置
    load() {
      this.request.get("http://localhost:9090/apply/page?", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          roomName: this.inputRoomName,
          name:this.inputName,
          status:this.inputStatus,
          startTime:this.inputStartTime,
          endTime:this.inputEndTime,
          mode:this.inputMode
        }
      }).then(res => {
        if(res.code=='200'){
          this.tableData=res.data.data
          this.total = res.data.total
        }
        else this.$message.error(res.msg)
        // console.log("!")
        // console.log(this.tableData)
      })
    },
    reset() {
      this.inputRoomName=''
      this.inputName=''
      this.inputStatus=''
      this.inputStartTime=''
      this.inputEndTime=''
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

    accept(row){
      this.temp = Object.assign({}, row)
      Object.assign(this.temp,{admin:this.user.username})
      this.request.post("http://localhost:9090/apply/accept",this.temp).then(res=>{
        console.log("!")
        if(res.code == '200'){
          this.$message.success("同意成功")
        }
        else this.$message.error(res.msg);
        this.load();
      })
    },

    handleRefuse(row){
      this.form = row
      this.refuseDialogVisible = true
    },

    handleAllExport() {
      window.open("http://localhost:9090/usage/export")
    },

    refuse(){
      Object.assign(this.form,{admin:this.user.username})
      this.request.post("http://localhost:9090/apply/refuse",this.form).then(res=>{
        console.log("!")
        if(res.code == '200'){
          this.$message.success("拒绝成功")
          this.refuseDialogVisible=false
        }
        else this.$message.error(res.msg);
        this.load();
      })
    },
  }
}
</script>

<style scoped>
</style>