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
          style="width: 200px;"
          type="datetime"
          placeholder="选择起用时间">
      </el-date-picker>
      <el-date-picker
          class="ml5"
          v-model="inputEndTime"
          style="width: 200px;"
          type="datetime"
          placeholder="选择终止时间">
      </el-date-picker>
      <el-select v-model="inputState" placeholder="请选择当前状态" class="ml5" style="width: 200px;" clearable>
        <el-option
            v-for="item in stateOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value">
        </el-option>
      </el-select>
      <el-select v-model="inputProps" placeholder="请选择使用类型" class="ml5" style="width: 200px;" clearable>
        <el-option
            v-for="item in propsOptions"
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

      <el-table-column type="selection" width="40"/>
<!--      <el-table-column prop="uid" label="编号" width="70px" sortable/>-->
      <el-table-column prop="roomName" label="教室名称" width="100"/>
      <el-table-column prop="name" label="姓名" width="100px"/>
      <el-table-column prop="status" label="身份" width="70"/>
      <el-table-column prop="startTime" label="启用时间" width="150" sortable/>
      <el-table-column prop="endTime" label="停用时间" width="150" sortable/>
      <el-table-column prop="state" label="当前状态" width="100px">
        <template slot-scope="scope">
          <div v-if="scope.row.state=='已使用'"  style="color: #3F5EF8;font-weight: bold">
            {{scope.row.state}}
          </div>
          <div v-if="scope.row.state=='在使用'"  style="color: #42b983;font-weight: bold">
            {{scope.row.state}}
          </div>
          <div v-if="scope.row.state=='待使用'"  style="color: lightsalmon;font-weight: bold">
            <b> {{scope.row.state}} </b>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="props" label="使用类型" width="100px"/>
      <el-table-column prop="remark" label="备注"/>

      <el-table-column label="操作" width="120 px">
        <template slot-scope="scope">
          <el-popconfirm title="确定这条信息吗？" class="ml5 mr5"
                         @onConfirm="handleDelete(scope.row.uid)">
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

  </div>
</template>


<script>
export default {
  name: "MyUsage",
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
      inputStatus:'',
      inputStartTime:'',
      inputEndTime:'',
      inputState:'',
      inputProps:'',

      form: {
        uid: '',
        roomName: '',
        username: '',
        name: '',
        status: '',
        startTime: '',
        endTime:'',
        remark: '',
        state:''
      },

      multipleSelection: [],

      visible: false,

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
      propsOptions:[{
        value: '课用',
        label: '课用'
      }, {
        value: '临时',
        label: '临时'
      }],
    }
  },

  created() {
    this.load()
  },

  methods: {

    // 刷新与重置
    load() {
      this.request.get("http://localhost:9090/usage/page?", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          roomName: this.inputRoomName,
          name:this.inputName,
          status:this.inputStatus,
          startTime:this.inputStartTime,
          endTime:this.inputEndTime,
          state:this.inputState,
          props:this.inputProps
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

    // 选择项列对应函数
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },

    // CRUD
    handleDelete(uid) {
      this.request.delete("http://localhost:9090/usage/" + uid).then(res => {
        if (res != 0) {
          this.$message.success("删除成功")
          this.load()
        } else {
          this.$message.error("删除失败")
        }
      })
    },
    handleDelBatch() {
      let users = this.multipleSelection.map(v => v.uid)
      this.request.post("http://localhost:9090/usage/delbatch/", users).then(res => {
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
      window.open("http://localhost:9090/usage/export")
    }
  }
}
</script>

<style scoped>
</style>