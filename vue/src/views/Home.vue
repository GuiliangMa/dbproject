<template>
  <div>
    <el-table :data="tableData"
              border
              stripe
              :header-cell-style="{background:'#lightgray',color:'black'}">


      <el-table-column prop="msgid" label="信息编号" width="140" sortable/>
      <el-table-column prop="fromuser" label="发送者" width="120"/>
      <el-table-column prop="msg" label="信息"/>
      <el-table-column prop="sendtime" width="200" label="发送时间"/>

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
  </div>
</template>


<script>
export default {
  name: "Home",
  data() {
    return {
      tableData: [],
      collapseBtnClass: 'el-icon-s-fold',
      isCollapse: false,
      sideWidth: 200,
      headerBg: 'headerBg',

      total: 0,
      pageNum: 1,
      pageSize: 20,

      user:sessionStorage.getItem("user")?JSON.parse(sessionStorage.getItem("user")):{username:'',nickname:'游客'}
    }
  },

  created() {
    this.load()
  },

  methods: {

    load() {
      this.request.get("http://localhost:9090/message/page?", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          username: this.user.username
        }
      }).then(res => {
        console.log(res)
        this.tableData = res.data.data
        this.total = res.data.total
      })
    },
    reset() {
      this.inputUsername = ''
      this.inputEmail = ''
      this.inputPhone = ''
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
  }
}
</script>

<style scoped>
</style>