<template>
  <div>
    <el-container style=":height: 100vh-60px">
      <el-aside style="width: 300px">
        <el-form>
          <el-form-item label="开课时间" label-width="80px" style="margin-bottom: 30px">
            <div>
              <el-date-picker
                  class="ml5"
                  v-model="inputStartDate"
                  style="width: 180px"
                  type="date"
                  placeholder="选择开课日期"
                  :picker-options="pickerStartOptions">

              </el-date-picker>
            </div>
            <div>
              <el-date-picker
                  class="ml5"
                  v-model="inputEndDate"
                  style="width: 180px;margin-top: 5px"
                  type="date"
                  placeholder="选择结课日期"
                  :picker-options="pickEndOptions"
              >
              </el-date-picker>
            </div>
          </el-form-item>
          <el-form-item label="工作日" label-width="80px"  style="margin-bottom: 30px">
            <el-select v-model="inputWeekday" placeholder="请选择工作日" class="ml5" style="width: 180px;" clearable>
              <el-option
                  v-for="item in weekdayOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="上课时间" label-width="80px"  style="margin-bottom: 30px">
            <el-time-select
                class="ml5"
                style="width: 180px"
                v-model="inputBeginTime"
                :picker-options="{start: '08:00',step: '00:30',end: '22:00'}"
                placeholder="开始时间">
            </el-time-select>

            <el-time-select
                class="ml5"
                style="width: 180px"
                v-model="inputEndTime"
                :picker-options="{start: '08:00',step: '00:30',end: '22:00',minTime:inputBeginTime}"
                placeholder="结束时间">
            </el-time-select>
          </el-form-item>
          <el-form-item label="课程频率" label-width="80px"  style="margin-bottom: 30px">
            <el-select v-model="inputTimes" placeholder="" class="ml5" style="width: 80px;" clearable>
              <el-option

                  v-for="item in timesOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
              </el-option>
            </el-select>
            <span class="ml5">周 1 次</span>
          </el-form-item>
        </el-form>

        <el-button style="margin-left: 80px" type="primary" @click="search">
          <i class="el-icon-search"/>
          <b>搜索</b>
        </el-button>

        <el-button class="ml5" type="warning" @click="reset">
          <i class="el-icon-circle-close"></i>
          <b>重置</b>
        </el-button>
      </el-aside>

      <el-main>
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

          <el-form :inline="true" :model="form"
                   :label-position="'right'" style="margin-top: 0">
            <el-form-item label="教室" label-width="120px" style="margin-bottom: 0">
              <div style="width: 250px"><span>{{ this.form.roomName }}</span></div>
            </el-form-item>
            <el-form-item label="容量" label-width="120px" style="margin-bottom: 0">
              <div style="width: 80px"><span>{{ this.form.capacity }}</span></div>
            </el-form-item>

            <el-form-item label="多媒体" label-width="80px" style="margin-bottom: 0">
              <div style="width: 80px"><span>{{ this.form.multiMedia }}</span></div>
            </el-form-item>

            <el-form-item label="空调" label-width="120px" style="margin-bottom: 0">
              <div style="width: 80px"><span>{{ this.form.ac }}</span></div>
            </el-form-item>

            <el-form-item label="类型" label-width="80px" style="margin-bottom: 0">
              <div style="width: 80px"><span>{{ this.form.type }}</span></div>
            </el-form-item>
          </el-form>

          <el-form style="margin-top: 20px">
            <el-form-item label="课程号" label-width="80px" style="margin-bottom: 5px">
              <el-input
                  class="ml5"
                  style="width: 305px"
                  prefix-icon="el-icon-user-solid"
                  placeholder="请输入课程编号"
                  v-model="form.cid"
                  clearable
              />
            </el-form-item>
            <el-form-item label="开课时间" label-width="80px" style="margin-bottom: 5px">
              <div>
                <el-date-picker
                    class="ml5"
                    v-model="inputStartDate"
                    style="width: 140px"
                    type="date"
                    disabled="true"
                    placeholder="选择开课日期">
                </el-date-picker>
                <span> 至 </span>
                <el-date-picker
                    class="ml5"
                    v-model="inputEndDate"
                    style="width: 140px;margin-top: 5px"
                    type="date"
                    disabled="true"
                    placeholder="选择结课日期"
                >
                </el-date-picker>
              </div>
            </el-form-item>
            <el-form-item label="工作日" label-width="80px" style="margin-bottom: 5px">
              <el-select v-model="inputWeekday" placeholder="请选择工作日" class="ml5" style="width: 180px;" disabled="true">
                <el-option
                    v-for="item in weekdayOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="上课时间" label-width="80px" style="margin-bottom: 5px">
              <el-time-select
                  class="ml5"
                  style="width: 140px"
                  placeholder="起始时间"
                  disabled="true"
                  v-model="inputBeginTime">
              </el-time-select>
              <span> 至 </span>
              <el-time-select
                  class="ml5"
                  style="width: 140px"
                  placeholder="结束时间"
                  disabled="true"
                  v-model="inputEndTime"
                  :picker-options="{
                  minTime:inputBeginTime
                }">
              </el-time-select>
            </el-form-item style="margin-bottom: 5px">
            <el-form-item label="课程频率" label-width="80px">
              <el-select v-model="inputTimes" placeholder="" class="ml5" style="width: 80px;" disabled="true">
                <el-option

                    v-for="item in timesOptions"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value">
                </el-option>
              </el-select>
              <span class="ml5">周 1 次</span>
            </el-form-item>
          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button @click="dialogVisible = false">取 消</el-button>
            <el-button type="primary" @click="application">确 定</el-button>
          </div>
        </el-dialog>
      </el-main>
    </el-container>
  </div>
</template>


<script>
export default {
  name: "Classroom",
  data() {
    return {
      user: sessionStorage.getItem("user") ? JSON.parse(sessionStorage.getItem("user")) : {
        username: '',
        nickname: '游客'
      },
      tableData: [],
      collapseBtnClass: 'el-icon-s-fold',
      isCollapse: false,
      sideWidth: 200,
      headerBg: 'headerBg',

      total: 0,
      pageNum: 1,
      pageSize: 8,

      inputTid: '',
      inputCid: '',
      inputStartDate: '',
      inputEndDate: '',
      inputWeekday: '',
      inputBeginTime: '',
      inputEndTime: '',
      inputTimes: '',

      form: {},

      multipleSelection: [],

      visible: false,

      weekdayOptions: [
        {
          value: '1',
          label: '周日'
        },
        {
          value: '2',
          label: '周一'
        },
        {
          value: '3',
          label: '周二'
        },
        {
          value: '4',
          label: '周三'
        },
        {
          value: '5',
          label: '周四'
        },
        {
          value: '6',
          label: '周五'
        },
        {
          value: '7',
          label: '周六'
        }
      ],

      timesOptions: [
        {
          value: '1',
          label: '1'
        }, {
          value: '2',
          label: '2'
        }, {
          value: '3',
          label: '3',
        }, {
          value: '4',
          label: '4',
        }],

      dialogVisible: false,
      temp: {},

      pickerStartOptions: {
        disabledDate(time) {
          return (time.getTime() < Date.now() - 86400000)
        },
      },

      // pickEndOptions: this.endOptions()
    }
  },

  created() {
    this.load()
  },

  methods: {

    endOptions(){
      const that=this
      return{
        disabledDate(time) {
        if (that.inputBeginTime==='') {
          return true
        }
        else return false
          // return (time.getTime()>new Date(that.inputBeginTime).getTime())
      }
      }
    },

    // 刷新与重置
    load() {
      let Times = 1;
      if(this.inputTimes!=='')
        Times = this.inputTimes
      this.request.get("http://localhost:9090/teachercourse/findroom?", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          startDate: this.inputStartDate,
          endDate:this.inputEndDate,
          weekday:this.inputWeekday,
          beginTime:this.inputBeginTime,
          endTime:this.inputEndTime,
          times:Times
        }
      }).then(res => {
        if(res.code =='200'){
          this.tableData=res.data.data
          this.total = res.data.total
          console.log(this.tableData)
        }
        else this.$message.error(res.msg);
        // console.log(this.tableData)
      })
    },
    reset() {
      this.inputTid = ''
      this.inputCid = ''
      this.inputStartDate = ''
      this.inputEndDate = ''
      this.inputWeekday = ''
      this.inputBeginTime = ''
      this.inputEndTime = ''
      this.inputTimes = ''
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
      console.log("1");
      this.form = Object.assign({}, row)
      // if(this.user.status==='教师'){
      //   await this.request.get("http://localhost:9090/teachercourse/gettid?", {
      //     params: {
      //       username: this.user.username
      //     }
      //   }).then(res => {
      //     console.log(res)
      //     Object.assign(this.form, res.data)
      //   })
      // }
      this.dialogVisible = true
    },
    application() {
      this.temp = Object.assign({}, {username: this.user.username});
      Object.assign(this.temp, {rid: this.form.rid})
      Object.assign(this.temp,{cid:this.form.cid})
      Object.assign(this.temp, {startDate: this.inputStartDate})
      Object.assign(this.temp, {endDate: this.inputEndDate})
      Object.assign(this.temp, {weekday: this.inputWeekday})
      Object.assign(this.temp, {beginTime: this.inputBeginTime})
      Object.assign(this.temp, {endTime: this.inputEndTime})
      Object.assign(this.temp, {times: this.inputTimes})
      console.log(this.temp)

      this.request.post("http://localhost:9090/teachercourse/apply", this.temp).then(res => {
        console.log(res)
        if (res.code == '200') {
          this.$message.success("保存成功")
          this.dialogVisible = false
        } else {
          this.$message.error(res.msg)
        }
      })

    }
  }
}
</script>

<style scoped>
</style>