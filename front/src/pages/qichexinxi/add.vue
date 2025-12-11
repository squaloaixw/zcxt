<template>
  <div class="booking-container" :style='{"width":"1200px","padding":"20px","margin":"10px auto","position":"relative","background":"#f5f7fa"}'>
    <el-card shadow="hover" class="booking-card">
      <div slot="header" class="clearfix">
        <span class="page-title"><i class="el-icon-s-order"></i> 确认预定信息</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="$router.back()">返回上一页</el-button>
      </div>

      <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="120px" class="booking-form">

        <div class="section-title">车辆信息</div>
        <div class="car-info-box">
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="车辆名称" prop="qichemingcheng">
                <el-input v-model="ruleForm.qichemingcheng" readonly placeholder="自动获取" class="readonly-input"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="车牌号" prop="chepaihao">
                <el-input v-model="ruleForm.chepaihao" readonly placeholder="自动获取" class="readonly-input"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="日租金" prop="qichejiage">
                <el-input v-model="ruleForm.qichejiage" readonly class="readonly-input">
                  <template slot="append">元/天</template>
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <el-divider></el-divider>

        <div class="section-title">租期与费用</div>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="选择租期" required>
              <el-date-picker
                  v-model="bookingDateRange"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="取车日期"
                  end-placeholder="还车日期"
                  value-format="yyyy-MM-dd"
                  :picker-options="pickerOptions"
                  @change="handleDateChange"
                  style="width: 100%;">
              </el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="租赁天数" prop="zulintianshu">
              <el-input v-model="ruleForm.zulintianshu" readonly>
                <template slot="append">天</template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="总价格" prop="zongjiage">
              <div class="total-price-display">
                ¥ {{ ruleForm.zongjiage || 0 }}
              </div>
              <input type="hidden" v-model="ruleForm.zongjiage">
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider></el-divider>

        <div class="section-title">预定人信息</div>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="用户名" prop="yonghuming">
              <el-input v-model="ruleForm.yonghuming" readonly class="readonly-input"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="姓名" prop="xingming">
              <el-input v-model="ruleForm.xingming" readonly class="readonly-input"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="手机号" prop="shouji">
              <el-input v-model="ruleForm.shouji" readonly class="readonly-input"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="身份证" prop="shenfenzheng">
              <el-input v-model="ruleForm.shenfenzheng" readonly class="readonly-input"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <div class="form-actions">
          <el-button type="primary" size="medium" @click="onSubmit" class="submit-btn" :loading="loading">立即提交订单</el-button>
          <el-button size="medium" @click="$router.back()">取消</el-button>
        </div>

      </el-form>
    </el-card>
  </div>
</template>

<script>
export default {
  data() {
    return {
      baseUrl: this.$config.baseUrl,
      loading: false,
      // 临时存储日期范围 [start, end]
      bookingDateRange: [],
      // 限制不能选今天之前的日期
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() < Date.now() - 8.64e7;
        }
      },
      ruleForm: {
        dingdanbianhao: this.getUUID(), // 自动生成订单号
        qichemingcheng: '',
        chepaihao: '',
        qichejiage: 0,
        zulintianshu: 0,
        zongjiage: 0,
        qucheshijian: '',
        huancheshijian: '',
        yonghuming: '',
        xingming: '',
        shouji: '',
        shenfenzheng: '',
        zhuangtai: '未支付', // 默认状态
      },
      rules: {
        qichemingcheng: [{ required: true, message: '车辆信息缺失', trigger: 'blur' }],
        zulintianshu: [{ required: true, message: '请选择租期', trigger: 'change' }],
        zongjiage: [{ required: true, message: '价格计算错误', trigger: 'change' }],
        yonghuming: [{ required: true, message: '用户信息缺失', trigger: 'blur' }]
      },
      user: {} // 存储完整的用户信息
    };
  },
  created() {
    // 1. 获取当前登录用户信息
    this.getSession();

    // 2. 如果URL带了车辆ID，去获取车辆详情
    const carId = this.$route.query.id;
    if (carId) {
      this.getCarInfo(carId);
    }
  },
  methods: {
    // 获取当前登录用户
    getSession() {
      if(!localStorage.getItem('userid')) {
        this.$router.push('/login');
        return;
      }
      this.$http.get(`${localStorage.getItem('UserTableName')}/session`, {emulateJSON: true}).then(res => {
        if (res.data.code == 0) {
          this.user = res.data.data;
          // 自动填充表单
          this.ruleForm.yonghuming = this.user.yonghuming;
          this.ruleForm.xingming = this.user.xingming;
          this.ruleForm.shouji = this.user.shouji;
          this.ruleForm.shenfenzheng = this.user.shenfenzheng;
        }
      });
    },

    // 获取车辆详细信息
    getCarInfo(id) {
      this.$http.get(`qichexinxi/info/${id}`).then(res => {
        if (res.data.code == 0) {
          const car = res.data.data;
          this.ruleForm.qichemingcheng = car.qichemingcheng; // 品牌/名称
          this.ruleForm.chepaihao = car.chepaihao;
          this.ruleForm.qichejiage = car.qichejiage; // 单价
          // 可以在这里存车辆图片等其他信息用于展示
        } else {
          this.$message.error('获取车辆信息失败');
        }
      });
    },

    // 处理日期变化，自动计算天数和总价
    handleDateChange(val) {
      if (val && val.length === 2) {
        this.ruleForm.qucheshijian = val[0];
        this.ruleForm.huancheshijian = val[1];

        // 计算天数
        const start = new Date(val[0]);
        const end = new Date(val[1]);
        const diffTime = Math.abs(end - start);
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)) + 1; // 包含起止当天算+1天，或者根据你的业务逻辑调整

        this.ruleForm.zulintianshu = diffDays;

        // 计算总价
        if (this.ruleForm.qichejiage) {
          this.ruleForm.zongjiage = (parseFloat(this.ruleForm.qichejiage) * diffDays).toFixed(2);
        }
      } else {
        this.ruleForm.zulintianshu = 0;
        this.ruleForm.zongjiage = 0;
        this.ruleForm.qucheshijian = '';
        this.ruleForm.huancheshijian = '';
      }
    },

    // 提交订单
    onSubmit() {
      this.$refs['ruleForm'].validate(valid => {
        if (valid) {
          if (this.ruleForm.zongjiage <= 0) {
            this.$message.error('订单金额异常');
            return;
          }

          this.loading = true;
          this.$http.post(`zuchedingdan/add`, this.ruleForm).then(res => {
            this.loading = false;
            if (res.data.code == 0) {
              this.$message({
                message: '预定成功，请前往支付',
                type: 'success',
                duration: 1500,
                onClose: () => {
                  // 跳转到后台管理或者个人中心的订单列表，或者直接去支付页面
                  // 假设跳转到个人中心
                  this.$router.push('/index/center');
                }
              });
            } else {
              this.$message.error(res.data.msg);
            }
          });
        }
      });
    },

    // 生成随机订单号
    getUUID() {
      return new Date().getTime();
    }
  }
};
</script>

<style lang="scss" scoped>
.booking-container {
  font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", "微软雅黑", Arial, sans-serif;
}

.booking-card {
  border-radius: 8px;
  border: none;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.page-title {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #409EFF;
  margin-bottom: 20px;
  padding-left: 10px;
  border-left: 4px solid #409EFF;
  line-height: 1;
}

.readonly-input {
  // 让只读输入框看起来更像文本展示，而不是不可编辑的表单
  /deep/ .el-input__inner {
    background-color: #f5f7fa;
    color: #606266;
    cursor: default;
  }
}

.total-price-display {
  font-size: 28px;
  color: #f56c6c;
  font-weight: bold;
  font-family: Arial, sans-serif;
}

.form-actions {
  margin-top: 40px;
  text-align: center;

  .submit-btn {
    width: 200px;
    margin-right: 20px;
    font-size: 16px;
  }
}

/deep/ .el-form-item__label {
  font-weight: 500;
}
</style>
