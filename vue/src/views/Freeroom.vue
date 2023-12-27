<template>
  <div>
    <div style="margin-bottom:10px">
      <el-input
          style="width: 180px"
          prefix-icon="el-icon-s-data"
          placeholder="请输入教室名称"
          class="ml5"
          v-model="inputRoomName"
          clearable
      />
      <el-input
          class="ml5"
          style="width: 180px"
          prefix-icon="el-icon-user-solid"
          placeholder="请输入需求容量"
          v-model="inputCapacity"
          clearable
      />
      <el-select v-model="inputMultiMedia" class="ml5" style="width: 130px" placeholder="多媒体" clearable>
        <el-option
            v-for="item in multiMediaOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value">
        </el-option>
      </el-select>

      <el-select v-model="inputAC" class="ml5" style="width: 130px" placeholder="空调" clearable>
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
    </div>
    <div style="margin-bottom:10px">
      <el-date-picker
          class="ml5"
          v-model="inputUseDate"
          style="width: 180px"
          type="date"
          placeholder="选择使用日期"
          :picker-options="pickerOptions">
      </el-date-picker>

      <el-time-select
          class="ml5"
          style="width: 180px"
          v-model="inputStartTime"
          :picker-options="{start: '08:00',step: '00:30',end: '22:00'}"
          placeholder="开始时间">
      </el-time-select>

      <el-time-select
          class="ml5"
          style="width: 180px"
          v-model="inputEndTime"
          :picker-options="{start: '08:00',step: '00:30',end: '22:00',minTime:inputStartTime}"
          placeholder="结束时间">
      </el-time-select>


      <el-button style="margin-left: 30px" type="primary" @click="search">
        <i class="el-icon-search"/>
        <b>搜索</b>
      </el-button>

      <el-button class="ml5" type="warning" @click="reset">
        <i class="el-icon-circle-close"></i>
        <b>重置</b>
      </el-button>

      <el-button class="ml5" type="success" @click="handleAsign">
        <i class="el-icon-circle-close"></i>
        <b>请求教室</b>
      </el-button>

    </div>
    <el-table :data="tableData"
              border
              stripe
              :header-cell-style="{background:'#lightgray',color:'black'}">

      <el-table-column prop="rid" label="教室编号" width="100px" sortable/>
      <el-table-column prop="roomName" label="教室名称" width="120"/>
      <el-table-column prop="capacity" label="容量" width="70"/>
      <el-table-column prop="multiMedia" label="多媒体" width="70"/>
      <el-table-column prop="ac" label="空调" width="70"/>
      <el-table-column prop="type" label="类型" width="70"/>
      <el-table-column prop="available" label="可用性" width="70"/>
      <el-table-column prop="part" label="分区" width="80"/>
      <el-table-column prop="location" label="位置"/>


      <el-table-column label="操作" width="120 px">
        <template slot-scope="scope">
          <el-button type="success" @click="handleApply(scope.row)">申请 <i class="el-icon-edit"></i></el-button>
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
        title="临时申请教室信息表"
        :visible.sync="dialogVisible"
        width="500px">


      <el-form :inline="true" :model="form" class="demo-form-inline"
               :label-position="'right'">
        <el-form-item v-if="user.status==='教师'" label="教工号" label-width="120px" style="margin-bottom: 0;">
          <div style="width: 80px"><span>{{this.form.id}}</span></div>
        </el-form-item>
        <el-form-item v-else-if="user.status==='学生'" label="学号" label-width="120px" style="margin-bottom: 0;">
          <div style="width: 250px"><span>{{this.form.id}}</span></div>
        </el-form-item>

        <el-form-item inline-message="true" label="身份" label-width="120px" style="margin-bottom: 5px;">
          <div style="width: 80px"><span>{{this.user.status}}</span></div>
        </el-form-item>

        <el-form-item inline-message="true" label="姓名" label-width="80px" style="margin-bottom: 5px;">
          <div style="width: 80px"><span>{{this.form.name}}</span></div>
        </el-form-item>
      </el-form>

      <el-form :inline="true" :model="form"
               :label-position="'right'">
        <el-form-item label="教室" label-width="120px" style="margin-bottom: 0">
          <div style="width: 250px"><span>{{this.form.roomName}}</span></div>
        </el-form-item>
        <el-form-item label="容量" label-width="120px" style="margin-bottom: 0">
          <div style="width: 80px"><span>{{this.form.capacity}}</span></div>
        </el-form-item>

        <el-form-item label="多媒体" label-width="80px" style="margin-bottom: 0">
          <div style="width: 80px"><span>{{this.form.multiMedia}}</span></div>
        </el-form-item>

        <el-form-item label="空调" label-width="120px" style="margin-bottom: 0">
          <div style="width: 80px"><span>{{this.form.ac}}</span></div>
        </el-form-item>

        <el-form-item label="类型" label-width="80px" style="margin-bottom: 0">
          <div style="width: 80px"><span>{{this.form.type}}</span></div>
        </el-form-item>
      </el-form>

      <el-form :model="form"
               :label-position="'right'" title="申请人信息">
        <el-form-item label="位置" label-width="120px">
          <span>{{this.form.location}}</span>
        </el-form-item>
        <el-form-item label="使用日期" label-width="120px" style="margin-bottom: 5px">
          <el-date-picker
              class="ml5"
              type="date"
              v-model="form.useDate"
              style="width: 245px"
              disabled
              placeholder="选择使用日期">
          </el-date-picker>
        </el-form-item>


        <el-form-item label="起用时间" label-width="120px" style="margin-bottom: 5px">
          <el-time-select
              class="ml5"
              v-model="form.startTime"
              style="width: 245px"
              disabled
              placeholder="选择终止时间">
          </el-time-select>
        </el-form-item>

        <el-form-item label="终止时间" label-width="120px" style="margin-bottom: 5px">
          <el-time-select
              class="ml5"
              style="width: 245px"
              v-model="form.endTime"
              disabled
              placeholder="选择终止时间">
          </el-time-select>
        </el-form-item>

        <el-form-item label="申请理由" label-width="120px">
          <el-input type="textarea" style="width: 250px" v-model="form.remark"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="application">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog
        title="请用教室信息表"
        :visible.sync="asignDialog"
        width="500px">

      <el-form :model="asignForm"
               :label-position="'right'" :rules="rules" ref="asignForm" class="demo-ruleForm">
        <el-form-item label="需求容量" label-width="120px" prop="capacity">
          <el-input
              class="ml5"
              style="width: 250px"
              prefix-icon="el-icon-user-solid"
              placeholder="请输入需求容量"
              v-model="asignForm.capacity"
              clearable
          />
        </el-form-item>

        <el-form-item label="多媒体" label-width="120px" prop="multiMedia">
          <el-select v-model="asignForm.multiMedia" class="ml5" style="width: 250px" placeholder="多媒体" clearable>
            <el-option
                v-for="item in multiMediaOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="空调" label-width="120px" prop="ac">
          <el-select v-model="asignForm.ac" class="ml5" style="width: 250px" placeholder="空调" clearable>
            <el-option
                v-for="item in acOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="类型" label-width="120px" prop="type">
          <el-select v-model="asignForm.type" class="ml5" style="width: 250px" placeholder="类型" clearable>
            <el-option
                v-for="item in typeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="使用日期" label-width="120px" prop="date">
          <el-date-picker
              class="ml5"
              v-model="asignForm.date"
              style="width: 250px"
              type="date"
              placeholder="选择使用日期"
              :picker-options="pickerOptions">
          </el-date-picker>
        </el-form-item>


        <el-form-item label="起用时间" label-width="120px" prop="begin">
          <el-time-select
              class="ml5"
              style="width: 250px"
              v-model="asignForm.begin"
              :picker-options="{start: '08:00',step: '00:30',end: '22:00'}"
              placeholder="开始时间">
          </el-time-select>
        </el-form-item>

        <el-form-item label="终止时间" label-width="120px" prop="end">
          <el-time-select
              class="ml5"
              style="width: 250px"
              v-model="asignForm.end"
              :picker-options="{start: '08:00',step: '00:30',end: '22:00',minTime:inputStartTime}"
              placeholder="结束时间">
          </el-time-select>
        </el-form-item>

        <el-form-item label="申请理由" label-width="120px" prop="remark">
          <el-input class="ml5" type="textarea" style="width: 250px" v-model="asignForm.remark"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="asignDialog = false">取 消</el-button>
        <el-button type="primary" @click="asign('asignForm')">确 定</el-button>
      </div>
    </el-dialog>

  </div>
</template>


<script>
export default {
  name: "Classroom",
  data() {
    return {
      user:sessionStorage.getItem("user")?JSON.parse(sessionStorage.getItem("user")):{username:'',nickname:'游客'},
      tableData: [],
      collapseBtnClass: 'el-icon-s-fold',
      isCollapse: false,
      sideWidth: 200,
      headerBg: 'headerBg',

      pickerOptions: {
        disabledDate(time) {
          return (time.getTime() < Date.now()) || (time.getTime()> Date.now() + 3600 * 1000 * 24 * 14)
        },
      },

      total: 0,
      pageNum: 1,
      pageSize: 8,

      inputRoomName: '',
      inputCapacity: '',
      inputMultiMedia: '',
      inputAC: '',
      inputType: '',
      inputStartTime:'',
      inputEndTime:'',
      inputUseDate:'',

      form: {},

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
      dialogVisible:false,
      temp:{},
      asignForm:{
        username:'',
        capacity:'',
        multiMedia:'',
        ac:'',
        useDate:'',
        starttime:'',
        endtime:'',
        remark:''
      },
      asignDialog:false,
      rules: {
        capacity: [
          { required: true, message: '请输入容量', trigger: 'blur' },
        ],
        multiMedia: [
          { required: true, message: '请选择是否采用多媒体', trigger: 'blur' }
        ],
        ac: [
          { required: true, message: '请选择是否采用空调', trigger: 'blur' }
        ],
        type: [
          { required: true, message: '请选择教室类型', trigger: 'blur' }
        ],
        date: [
          { required: true, message: '请选择使用时间', trigger: 'blur' }
        ],
        begin: [
          { required: true, message: '请选择启用时间', trigger: 'blur' }
        ],
        end: [
          { required: true, message: '请选择停用时间', trigger: 'blur' }
        ],
        remark: [
          { required: true, message: '请填写使用信息', trigger: 'blur' }
        ]
      }
    }
  },

  created() {
    this.load()
  },

  methods: {

    // 刷新与重置
    load() {
      if(this.inputEndTime===''||this.inputStartTime===''||this.inputUseDate===''){
        this.$message.error("未正确输入日期格式")
        return;
      }
      var Capacity=0
      if(this.inputCapacity!=='')
        Capacity = this.inputCapacity

      var MultiMedia = 2
      if(this.inputMultiMedia!=='')
        MultiMedia = this.inputMultiMedia

      var aC = 2
      if(this.inputAC!=='')
        aC = this.inputAC
      this.request.get("http://localhost:9090/apply/free?", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          useDate:this.inputUseDate,
          startTime:this.inputStartTime,
          endTime:this.inputEndTime,
          roomName:this.inputRoomName,
          capacity: Capacity,
          multiMedia:MultiMedia,
          AC: aC,
          type: this.inputType
        }
      }).then(res => {
        if(res.code =='200'){
          this.tableData=res.data.data
          this.total = res.data.total
        }
        else this.$message.error(res.msg);
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
    async handleApply(row) {
      this.form = Object.assign({}, row)
      await this.request.get("http://localhost:9090/apply/get?", {
        params: {
          username: this.user.username
        }
      }).then(res => {
        Object.assign(this.form, res.data)
        Object.assign(this.form,{useDate: this.inputUseDate})
        Object.assign(this.form,{startTime:this.inputStartTime})
        Object.assign(this.form,{endTime:this.inputEndTime})
      })
      this.dialogVisible = true
    },
    application(){
      this.temp = Object.assign({},{username:this.user.username});
      Object.assign(this.temp,{rid:this.form.rid})
      Object.assign(this.temp,{date:this.form.useDate})
      Object.assign(this.temp,{begin:this.form.startTime})
      Object.assign(this.temp,{end:this.form.endTime})
      Object.assign(this.temp,{remark:this.form.remark})
      console.log(this.temp)

      this.request.post("http://localhost:9090/apply/apply", this.temp).then(res => {
        console.log(res)
        if (res.code == '200') {
          this.$message.success("保存成功")
          this.dialogVisible = false
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    handleAsign(){
      this.asignForm={}
      this.asignDialog=true
    },
    asign(formName){
        this.$refs[formName].validate((valid) => {
          if (valid) {
            Object.assign(this.asignForm,{username:this.user.username})
            console.log(this.asignForm)
            this.request.post("http://localhost:9090/asign/assign", this.asignForm).then(res => {
              console.log(res)
              if (res.code == '200') {
                this.$message.success("请求成功")
                this.asignDialog = false
              } else {
                this.$message.error(res.msg)
              }
            })
          } else {
            console.log('error submit!!');
            return false;
          }
        });

    }
  }
}
</script>

<style scoped>
</style>