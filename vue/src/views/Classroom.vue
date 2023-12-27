<template>
  <div>
    <div style="margin-bottom:10px">
      <el-input
          style="width: 180px"
          prefix-icon="el-icon-s-data"
          placeholder="请输入教室名称"
          v-model="inputClassroomName"
          clearable
      />
      <el-input
          class="ml5"
          style="width: 180px"
          prefix-icon="el-icon-user-solid"
          placeholder="请输入最大容量"
          v-model="inputCapacity"
          clearable
      />
      <el-select v-model="inputMultiMedia" class="ml5" style="width: 130px" placeholder="是否有多媒体" clearable>
        <el-option
            v-for="item in multiMediaOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value">
        </el-option>
      </el-select>

      <el-select v-model="inputAC" class="ml5" style="width: 130px" placeholder="是否有空调" clearable>
        <el-option
            v-for="item in acOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value">
        </el-option>
      </el-select>

      <el-select v-model="inputType" class="ml5" style="width: 100px" placeholder="类型" clearable>
        <el-option
            v-for="item in typeOptions"
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
          action="http://localhost:9090/classroom/import"
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
      <el-table-column prop="rid" label="教室编号" width="100px" sortable/>
      <el-table-column prop="roomName" label="教室名称" width="120"/>
      <el-table-column prop="capacity" label="容量" width="70"/>
      <el-table-column prop="multiMedia" label="多媒体" width="70"/>
      <el-table-column prop="ac" label="空调" width="70"/>
      <el-table-column prop="type" label="类型" width="70"/>
      <el-table-column prop="available" label="可用性" width="70"/>
      <el-table-column prop="part" label="分区" width="80"/>
      <el-table-column prop="location" label="位置"/>


      <el-table-column label="操作" width="200 px">
        <template slot-scope="scope">
          <el-button type="success" @click="handleEdit(scope.row)">编辑 <i class="el-icon-edit"></i></el-button>
          <el-popconfirm title="确定这条信息吗？" class="ml5 mr5"
                         @onConfirm="handleDelete(scope.row.rid)">
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
        title="新增教室信息"
        :visible.sync="dialogVisible"
        width="500px">

      <el-form :model="form"
               :label-position="'right'">
        <el-form-item label="教室名称" label-width="120px">
          <el-input v-model="form.roomName" autocomplete="off" style="width: 250px" clearable></el-input>
        </el-form-item>

        <el-form-item label="最大容量" label-width="120px">
          <el-input v-model="form.capacity" autocomplete="off" style="width: 250px" clearable></el-input>
        </el-form-item>

        <el-form-item label="多媒体" label-width="120px">
          <el-select v-model="form.multiMedia" class="ml5" style="width: 245px" placeholder="是否有多媒体" clearable>
            <el-option
                v-for="item in multiMediaOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="空调" label-width="120px">
          <el-select v-model="form.ac" class="ml5" style="width: 245px" placeholder="是否有空调" clearable>
            <el-option
                v-for="item in acOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="类型" label-width="120px">
          <el-select v-model="form.type" class="ml5" style="width: 245px" placeholder="教室类型" clearable>
            <el-option
                v-for="item in typeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="分区" label-width="120px">
          <el-input v-model="form.part" autocomplete="off" style="width: 250px" clearable></el-input>
        </el-form-item>

        <el-form-item label="位置" label-width="120px">
          <el-input v-model="form.location"
                    style="width: 250px" type="textarea"
                    clearable></el-input>
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog
        title="编辑教室信息"
        :visible.sync="editDialogVisible"
        width="500px">

      <el-form :model="form"
               :label-position="'right'">
        <el-form-item label="教室名称" label-width="120px">
          <el-input v-model="form.roomName" autocomplete="off" style="width: 250px" clearable></el-input>
        </el-form-item>

        <el-form-item label="最大容量" label-width="120px">
          <el-input v-model="form.capacity" autocomplete="off" style="width: 250px" clearable></el-input>
        </el-form-item>

        <el-form-item label="多媒体" label-width="120px">
          <el-select v-model="form.multiMedia" class="ml5" style="width: 245px" placeholder="是否有多媒体" clearable>
            <el-option
                v-for="item in multiMediaOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="空调" label-width="120px">
          <el-select v-model="form.ac" class="ml5" style="width: 245px" placeholder="是否有空调" clearable>
            <el-option
                v-for="item in acOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="类型" label-width="120px">
          <el-select v-model="form.type" class="ml5" style="width: 245px" placeholder="教室类型" clearable>
            <el-option
                v-for="item in typeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="分区" label-width="120px">
          <el-input v-model="form.part" autocomplete="off" style="width: 250px" clearable></el-input>
        </el-form-item>

        <el-form-item label="位置" label-width="120px">
          <el-input v-model="form.location"
                    style="width: 250px" type="textarea"
                    clearable></el-input>
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
  name: "Classroom",
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

      inputClassroomName: '',
      inputCapacity: '',
      inputMultiMedia: '',
      inputAC: '',
      inputType: '',

      dialogVisible: false,
      editDialogVisible: false,
      form: {
        rid: '',
        roomName: '',
        capacity: '',
        multiMedia: '',
        ac: '',
        available: '',
        type:'',
        part: '',
        location: ''
      },

      multipleSelection: [],

      visible: false,

      multiMediaOptions: [
          {
        value: '1',
        label: '有多媒体'
      }, {
        value: '0',
        label: '无多媒体'
      }],

      acOptions: [
          {
        value: '1',
        label: '有空调'
      }, {
        value: '0',
        label: '无空调'
      }],

      typeOptions: [
          {
        value: '普通',
        label: '普通'
      }, {
        value: '阶梯',
        label: '阶梯'
      }, {
        value: '录播',
        label: '录播'
      }],
    }
  },

  created() {
    this.load()
  },

  methods: {

    // 刷新与重置
    load() {
      var Capacity=0
      if(this.inputCapacity!=='')
        Capacity = this.inputCapacity

      var MultiMedia = 2
      if(this.inputMultiMedia!=='')
        MultiMedia = this.inputMultiMedia

      var aC = 2
      if(this.inputAC!=='')
        aC = this.inputAC

      this.request.get("http://localhost:9090/classroom/page?", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          roomName: this.inputClassroomName,
          capacity: Capacity,
          multiMedia:MultiMedia,
          AC: aC,
          type: this.inputType
        }
      }).then(res => {
        // console.log(res)
        this.tableData=res.data
        this.total = res.total
        // console.log(this.tableData)
      })
    },
    reset() {
      this.inputClassroomName = ''
      this.inputCapacity = ''
      this.inputMultiMedia = ''
      this.inputAC = ''
      this.inputType = ''
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
      this.request.post("http://localhost:9090/classroom/save", this.form).then(res => {
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
      this.request.post("http://localhost:9090/classroom/update", this.form).then(res => {
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
      console.log(this.form)
      this.form.status = '学生'
      this.editDialogVisible = true
    },
    handleDelete(rid) {
      console.log(rid)
      this.request.delete("http://localhost:9090/classroom/" + rid).then(res => {
        if (res != 0) {
          this.$message.success("删除成功")
          this.load()
        } else {
          this.$message.error("删除失败")
        }
      })
    },
    handleDelBatch() {
      let users = this.multipleSelection.map(v => v.rid)
      this.request.post("http://localhost:9090/classroom/delbatch/", users).then(res => {
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
      window.open("http://localhost:9090/classroom/export")
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