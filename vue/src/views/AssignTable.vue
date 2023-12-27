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
          prefix-icon="el-icon-s-data"
          placeholder="请输入用户名"
          v-model="inputUsername"
          clearable
      />
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

      <el-table-column type="expand">
        <template slot-scope="props">
          <el-form label-position="left" class="demo-table-expand" :label-position="'right'">
            <el-form-item label="需求时间:" style="margin-bottom: 0" label-width="150px">
              <div style="font-weight: bolder;margin-bottom: 0">
                {{props.row.startTime}} ~ {{props.row.endTime}}
              </div>
            </el-form-item>
            <el-form-item label="空调:" style="margin-bottom: 0" label-width="150px">
              <div v-if="props.row.ac==='1'" style="font-weight: bolder;margin-bottom: 0">
                <span>有</span>
              </div>
              <div v-else style="font-weight: bolder;margin-bottom: 0">
                <span>无</span>
              </div>
            </el-form-item>
            <el-form-item label="多媒体:" style="margin-bottom: 0" label-width="150px">
              <div v-if="props.row.multiMedia==='1'" style="font-weight: bolder;margin-bottom: 0">
                <span>有</span>
              </div>
              <div v-else style="font-weight: bolder;margin-bottom: 0">
                <span>无</span>
              </div>
            </el-form-item>
            <el-form-item label="所需容量:" style="margin-bottom: 0" label-width="150px">
              <div style="font-weight: bolder;margin-bottom: 0">
                {{props.row.capacity}}
              </div>
            </el-form-item>
            <el-form-item label="教室类型:" style="margin-bottom: 0" label-width="150px">
              <div style="font-weight: bolder;margin-bottom: 0">
                {{props.row.type}}
              </div>
            </el-form-item>
          </el-form>
        </template>
      </el-table-column>
      <el-table-column type="selection" width="50"/>
      <el-table-column prop="asgid" label="请求序号" width="100px" sortable/>
      <el-table-column prop="username" label="请求用户" width="150px"/>

      <el-table-column prop="remark" label="请求事由" />
      <el-table-column prop="state" label="当前状态" width="150px">
        <template slot-scope="scope">
          <div v-if="scope.row.state=='未分配'"  style="color: #FC4668 ;font-weight: bold">
            {{scope.row.state}}
          </div>
          <div v-if="scope.row.state=='已分配'"  style="color: #42b983;font-weight: bold">
            {{scope.row.state}}
          </div>
          <div v-if="scope.row.state=='已过期'"  style="color: lightsalmon;font-weight: bold">
            <b> {{scope.row.state}} </b>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="roomName" label="分配教室" width="150"/>
      <el-table-column label="操作" width="220 px">
        <template slot-scope="scope">
          <el-popconfirm title="确定这条信息吗？" class="ml5 mr5"
                         @onConfirm="handleDelete(scope.row.asgid)">
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
  name: "AssignTable",
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
      inputUsername:'',
      inputStatus:'',
      inputStartTime:'',
      inputEndTime:'',
      inputState:'',
      inputProps:'',

      multipleSelection: [],

      visible: false,

      stateOptions:[{
        value: '已过期',
        label: '已过期'
      }, {
        value: '已分配',
        label: '已分配'
      },{
        value: '未分配',
        label: '未分配'
      }],
    }
  },

  created() {
    this.load()
  },

  methods: {

    // 刷新与重置
    load() {
      this.request.get("http://localhost:9090/asign/page?", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          roomName: this.inputRoomName,
          username:this.inputUsername,
          startTime:this.inputStartTime,
          endTime:this.inputEndTime,
          state:this.inputState
        }
      }).then(res => {
        // console.log(res)
        if(res.code=='200'){
          console.log(res)
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
    handleDelete(asgid) {
      this.request.delete("http://localhost:9090/asign/" + asgid).then(res => {
        if (res.code==='200') {
          this.$message.success("删除成功")
          this.load()
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    handleDelBatch() {
      let users = this.multipleSelection.map(v => v.uid)
      this.request.post("http://localhost:9090/asign/delbatch/", users).then(res => {
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