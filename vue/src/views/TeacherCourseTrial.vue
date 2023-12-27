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
          placeholder="请输入申请用户"
          v-model="inputUsername"
          clearable
      />
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
                {{props.row.times}} 周一次
              </div>
            </el-form-item>
          </el-form>
        </template>
      </el-table-column>
      <el-table-column type="selection" width="50"/>
<!--      <el-table-column prop="uid" label="编号" width="70px" sortable/>-->
      <el-table-column prop="roomName" label="教室名称" width="150"/>
      <el-table-column prop="username" label="申请用户" width="150px"/>
      <el-table-column prop="cid" label="课程号" width="150px"/>
      <el-table-column prop="cname" label="课程名称" width="150px"/>
      <el-table-column prop="state" label="当前状态" width="100px">
        <template slot-scope="scope">
          <div v-if="scope.row.state=='待批准'"  style="color: #3F5EF8;font-weight: bold">
            {{scope.row.state}}
          </div>
          <div v-if="scope.row.state=='已同意'"  style="color: #42b983;font-weight: bold">
            {{scope.row.state}}
          </div>
          <div v-if="scope.row.state=='已拒绝'"  style="color: #FC4668;font-weight: bold">
            <b> {{scope.row.state}} </b>
          </div>
        </template>
      </el-table-column>

      <el-table-column label="操作/备注">
        <template slot-scope="scope">
          <div v-if="scope.row.state ==='待批准' ">
            <el-popconfirm title="确定同意该申请吗？" class="ml5 mr5"
                           @onConfirm="accept(scope.row.tcid)">
              <el-button type="success" slot="reference" >同意 <i class="el-icon-circle-check"></i></el-button>
            </el-popconfirm>
            <el-button type="danger" slot="reference" @click="handleRefuse(scope.row)">拒绝 <i class="el-icon-circle-close"></i></el-button>
          </div>
          <div v-else>
            <span>{{scope.row.remark}}</span>
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
        <el-form-item label="拒绝原因" label-width="120px">
          <el-input type="textarea" style="width: 300px" v-model="form.remark"></el-input>
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
  name: "TeacherCourseTrial",
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
      inputCname:'',
      inputState:'',
      inputCid:'',
      inputUsername:'',

      form: {},

      multipleSelection: [],

      visible: false,

      stateOptions:[{
        value: '已同意',
        label: '已同意'
      }, {
        value: '已拒绝',
        label: '已拒绝'
      },{
        value: '待批准',
        label: '待批准'
      }],
      refuseDialogVisible:false,
      user:sessionStorage.getItem("user")?JSON.parse(sessionStorage.getItem("user")):{username:'',nickname:'游客'}
    }
  },

  created() {
    this.load()
  },

  methods: {

    // 刷新与重置
    load() {
      this.request.get("http://localhost:9090/teachercourse/trial?", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          roomName: this.inputRoomName,
          username:this.inputUsername,
          cname:this.inputCname,
          state:this.inputState,
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
    },
    reset() {
      this.inputRoomName=''
      this.inputName=''
      this.inputCname=''
      this.inputState=''
      this.inputCid=''
      this.inputUsername=''
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
    handleAllExport() {
      window.open("http://localhost:9090/teachercourse/export")
    },
    accept(Tcid){
      let temp=Object.assign({},{tcid:Tcid})
      Object.assign(temp,{username:this.user.username})
      this.request.post("http://localhost:9090/teachercourse/accept",temp).then(res=>{
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
    refuse(){
      this.request.post("http://localhost:9090/teachercourse/refuse",this.form).then(res=>{
        console.log("!")
        if(res.code == '200'){
          this.$message.success("拒绝成功")
          this.refuseDialogVisible=false
        }
        else this.$message.error(res.msg);
        this.load();
      })
    }
  }
}
</script>

<style scoped>
</style>