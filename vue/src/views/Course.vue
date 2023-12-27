<template>
  <div>
    <div style="margin-bottom:10px">
      <el-input
          style="width: 180px"
          prefix-icon="el-icon-s-data"
          placeholder="请输入课程号"
          v-model="inputCid"
          clearable
      />
      <el-input
          class="ml5"
          style="width: 180px"
          prefix-icon="el-icon-user-solid"
          placeholder="请输入课程名称"
          v-model="inputCname"
          clearable
      />

      <el-input
          class="ml5"
          style="width: 180px"
          prefix-icon="el-icon-s-data"
          placeholder="请输入学分"
          v-model="inputCredit"
          clearable
      />

      <el-select v-model="inputCtype" class="ml5" style="width: 100px" placeholder="类型" clearable>
        <el-option
            v-for="item in ctypeOptions"
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
    </div>

    <div style="margin-bottom: 10px">
      <el-button type="primary" @click="handleAdd">新增<i class="el-icon-circle-plus-outline"></i></el-button>

      <el-popconfirm title="确定批量删除这些信息吗？" class="ml5 mr5"
                     @onConfirm="handleDelBatch">
        <el-button type="danger" slot="reference">批量删除 <i class="el-icon-remove-outline"></i></el-button>
      </el-popconfirm>

      <el-upload
          action="http://localhost:9090/course/import"
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

      <el-table-column type="selection" width="40"/>
      <el-table-column prop="cid" label="课程编号" width="250px" sortable/>
      <el-table-column prop="cname" label="课程名称"/>
      <el-table-column prop="credit" label="课程学分" width="200"/>
      <el-table-column prop="ctype" label="课程类型" width="200px"/>


      <el-table-column label="操作" width="200 px">
        <template slot-scope="scope">
          <el-button type="success" @click="handleEdit(scope.row)">编辑 <i class="el-icon-edit"></i></el-button>
          <el-popconfirm title="确定删除该课程吗？" class="ml5 mr5"
                         @onConfirm="handleDelete(scope.row.cid)">
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
        title="新增课程信息"
        :visible.sync="dialogVisible"
        width="500px">

      <el-form :model="form"
               :label-position="'right'">
        <el-form-item label="课程编号" label-width="120px">
          <el-input v-model="form.cid" autocomplete="off" style="width: 250px" clearable></el-input>
        </el-form-item>

        <el-form-item label="课程名称" label-width="120px">
          <el-input v-model="form.cname" autocomplete="off" style="width: 250px" clearable></el-input>
        </el-form-item>

        <el-form-item label="课程学分" label-width="120px">
          <el-input v-model="form.credit" autocomplete="off" style="width: 250px" clearable></el-input>
        </el-form-item>

        <el-form-item label="课程类型" label-width="120px">
          <el-select v-model="form.ctype" class="ml5" style="width: 245px" placeholder="课程类型" clearable>
            <el-option
                v-for="item in ctypeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog
        title="编辑课程信息"
        :visible.sync="editDialogVisible"
        width="500px">

      <el-form :model="form"
               :label-position="'right'">
        <el-form-item label="课程编号" label-width="120px">
          <el-input v-model="form.cid" autocomplete="off" style="width: 250px" disabled></el-input>
        </el-form-item>

        <el-form-item label="课程名称" label-width="120px">
          <el-input v-model="form.cname" autocomplete="off" style="width: 250px" clearable></el-input>
        </el-form-item>

        <el-form-item label="课程学分" label-width="120px">
          <el-input v-model="form.credit" autocomplete="off" style="width: 250px" clearable></el-input>
        </el-form-item>

        <el-form-item label="课程类型" label-width="120px">
          <el-select v-model="form.ctype" class="ml5" style="width: 245px" placeholder="课程类型" clearable>
            <el-option
                v-for="item in ctypeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="editDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="edit">确 定</el-button>
      </div>
    </el-dialog>

  </div>
</template>


<script>
export default {
  name: "Course",
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

      inputCid: '',
      inputCname: '',
      inputCredit: '',
      inputCtype: '',

      dialogVisible: false,
      editDialogVisible: false,
      form: {
        rid: '',
        roomName: '',
        capacity: '',
        multiMedia: '',
        ac: '',
        available: '',
        type: '',
        part: '',
        location: ''
      },

      multipleSelection: [],

      visible: false,

      ctypeOptions: [
        {
          value: '必修',
          label: '必修'
        }, {
          value: '选修',
          label: '选修'
        }, {
          value: '限选',
          label: '限选'
        }, {
          value: '任选',
          label: '任选'
        }],
    }
  },

  created() {
    this.load()
  },

  methods: {

    // 刷新与重置
    load() {
      var Credit = 0
      if(this.inputCredit==='')
        Credit = 0
      else Credit = this.inputCredit
      this.request.get("http://localhost:9090/course/page?", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          cid: this.inputCid,
          cname: this.inputCname,
          credit: Credit,
          ctype: this.inputCtype
        }
      }).then(res => {
        // console.log(res)
        this.tableData = res.data.data
        this.total = res.data.total
        // console.log(this.tableData)
      })
    },
    reset() {
      this.inputCid = ''
      this.inputCname = ''
      this.inputCredit = ''
      this.inputCtype = ''
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
      console.log(this.form)
      this.request.post("http://localhost:9090/course/save", this.form).then(res => {
        console.log(res)
        if (res.code == '200') {
          this.$message.success("保存成功")
          this.dialogVisible = false
          this.load()
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    edit() {
      console.log(this.form)
      this.request.post("http://localhost:9090/course/update", this.form).then(res => {
        console.log(res)
        if (res.code == '200') {
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
      this.editDialogVisible = true
    },
    handleDelete(cid) {
      this.request.delete("http://localhost:9090/course/" + cid).then(res => {
        if (res.code=='200') {
          this.$message.success("删除成功")
          this.load()
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    handleDelBatch() {
      let users = this.multipleSelection.map(v => v.cid)
      this.request.post("http://localhost:9090/course/delbatch/", users).then(res => {
        if (res.code=='200') {
          this.$message.success("删除成功" + res.data + "条信息")
          this.load()
        } else {
          this.$message.error("删除失败")
        }
      })
    },
    handleAllExport() {
      window.open("http://localhost:9090/course/export")
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