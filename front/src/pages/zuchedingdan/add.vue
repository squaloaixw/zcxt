<template>
  <div class="booking-container">
    <div class="form-card">
      <div class="form-header">
        <span><i class="el-icon-s-order"></i> 确认订单信息</span>
        <el-button size="small" @click="back()">返回列表</el-button>
      </div>

      <el-form
          class="booking-form"
          ref="ruleForm"
          :model="ruleForm"
          :rules="rules"
          label-width="100px"
          label-position="right"
      >
        <el-form-item label="订单编号" prop="dingdanbianhao" v-show="false">
          <el-input v-model="ruleForm.dingdanbianhao" readonly></el-input>
        </el-form-item>

        <div class="section-title">车辆信息</div>
        <div class="section-content">
          <el-row :gutter="30">
            <el-col :span="8">
              <el-form-item label="车辆品牌" prop="cheliangpinpai">
                <el-input v-model="ruleForm.cheliangpinpai" placeholder="自动获取" readonly class="readonly-input"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="车牌号" prop="chepaihao">
                <el-input v-model="ruleForm.chepaihao" placeholder="自动获取" readonly class="readonly-input"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="汽车类别" prop="qicheleibie">
                <el-input v-model="ruleForm.qicheleibie" placeholder="自动获取" readonly class="readonly-input"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="租赁单价" prop="jiage">
                <el-input v-model="ruleForm.jiage" placeholder="自动获取" readonly class="readonly-input">
                  <template slot="append">元/天</template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="管理账号" prop="guanlizhanghao">
                <el-input v-model="ruleForm.guanlizhanghao" placeholder="自动获取" readonly class="readonly-input"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="管理姓名" prop="guanlixingming">
                <el-input v-model="ruleForm.guanlixingming" placeholder="自动获取" readonly class="readonly-input"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <div class="section-title">租赁详情</div>
        <div class="section-content highlight-bg">
          <el-row :gutter="30">
            <el-col :span="12">
              <el-form-item label="出租日期" prop="chuzuriqi">
                <el-date-picker
                    format="yyyy-MM-dd"
                    value-format="yyyy-MM-dd"
                    v-model="ruleForm.chuzuriqi"
                    type="date"
                    placeholder="请选择开始日期"
                    :picker-options="pickerOptions"
                    @change="calculateDuration"
                    style="width: 100%;">
                </el-date-picker>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="还车日期">
                <el-date-picker
                    format="yyyy-MM-dd"
                    value-format="yyyy-MM-dd"
                    v-model="jiezhiriqi"
                    type="date"
                    placeholder="请选择结束日期"
                    :picker-options="pickerOptions"
                    @change="calculateDuration"
                    style="width: 100%;">
                </el-date-picker>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="30" style="margin-top: 10px;">
            <el-col :span="12">
              <el-form-item label="租期 (天)" prop="zuqi">
                <el-input v-model="ruleForm.zuqi" readonly placeholder="自动计算" class="readonly-input"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="预计总价">
                <el-input :value="zongjia" readonly class="total-price-input">
                  <template slot="append">元</template>
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <div class="section-title">租户信息</div>
        <div class="section-content">
          <el-row :gutter="30">
            <el-col :span="6">
              <el-form-item label="账号" prop="zhanghao">
                <el-input v-model="ruleForm.zhanghao" readonly class="readonly-input"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="姓名" prop="xingming">
                <el-input v-model="ruleForm.xingming" readonly class="readonly-input"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="手机号码" prop="shouji">
                <el-input v-model="ruleForm.shouji" readonly class="readonly-input"></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="身份证" prop="shenfenzheng">
                <el-input v-model="ruleForm.shenfenzheng" readonly class="readonly-input"></el-input>
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <div class="section-title">备注说明</div>
        <div class="section-content">
          <el-form-item label="备注" prop="beizhu" label-width="60px">
            <el-input
                type="textarea"
                :rows="3"
                placeholder="如有特殊需求，请在此处填写..."
                v-model="ruleForm.beizhu">
            </el-input>
          </el-form-item>
        </div>

        <div class="form-footer">
          <el-button type="primary" class="submit-btn" @click="onSubmit">提交订单</el-button>
          <el-button class="cancel-btn" @click="back()">取消</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      baseUrl: '',
      userTableName: localStorage.getItem('sessionTable'),

      jiezhiriqi: '', // 仅前端用，用于计算租期

      pickerOptions: {
        disabledDate(time) {
          return time.getTime() < Date.now() - 8.64e7;
        }
      },

      ruleForm: {
        dingdanbianhao: this.getUUID(),
        chepaihao: '',
        cheliangpinpai: '',
        qicheleibie: '',
        jiage: 0,
        zuqi: 0,
        zongjia: 0,
        chuzuriqi: '',
        beizhu: '',
        zhanghao: '',
        xingming: '',
        shouji: '',
        shenfenzheng: '',
        guanlizhanghao: '',
        guanlixingming: '',
        ispay: '未支付',
      },
      rules: {
        zuqi: [
          { required: true, message: '请选择有效的租赁日期', trigger: 'change' },
          { validator: (rule, value, callback) => {
              if (!value || value <= 0) {
                callback(new Error('租期必须大于0天'));
              } else {
                callback();
              }
            }, trigger: 'change' }
        ],
        chuzuriqi: [{ required: true, message: '请选择出租日期', trigger: 'change' }],
        shouji: [{ validator: this.$validate.isMobile, trigger: 'blur' }],
      },
    };
  },
  computed: {
    zongjia() {
      // 强制转换为数字进行计算，避免 NaN
      const price = parseFloat(this.ruleForm.jiage) || 0;
      const days = parseInt(this.ruleForm.zuqi) || 0;
      return (price * days).toFixed(2);
    },
  },
  created() {
    this.init();
    this.baseUrl = this.$config.baseUrl;
    this.ruleForm.chuzuriqi = this.getCurDate();
  },
  methods: {
    getMakeZero(s) {
      return s < 10 ? '0' + s : s;
    },
    getCurDate() {
      const date = new Date();
      const year = date.getFullYear();
      const month = this.getMakeZero(date.getMonth() + 1);
      const day = this.getMakeZero(date.getDate());
      return `${year}-${month}-${day}`;
    },

    // 计算租期
    calculateDuration() {
      if (this.ruleForm.chuzuriqi && this.jiezhiriqi) {
        const start = new Date(this.ruleForm.chuzuriqi).getTime();
        const end = new Date(this.jiezhiriqi).getTime();

        if (end <= start) {
          this.$message.warning("还车日期必须晚于出租日期");
          this.ruleForm.zuqi = 0;
          this.jiezhiriqi = '';
          return;
        }

        // 计算天数差
        const diff = end - start;
        const days = Math.floor(diff / (24 * 3600 * 1000));
        this.ruleForm.zuqi = days;
      }
    },

    init(type) {
      // 1. 获取当前登录用户信息
      if(this.userTableName) {
        this.$http.get(`${this.userTableName}/session`, { emulateJSON: true }).then(res => {
          if (res.data.code == 0) {
            var json = res.data.data;
            if (json.zhanghao || json.zhanghao === 0) this.ruleForm.zhanghao = json.zhanghao;
            if (json.xingming || json.xingming === 0) this.ruleForm.xingming = json.xingming;
            if (json.shouji || json.shouji === 0) this.ruleForm.shouji = json.shouji;
            if (json.shenfenzheng || json.shenfenzheng === 0) this.ruleForm.shenfenzheng = json.shenfenzheng;
          }
        });
      }

      // 2. 获取车辆信息
      if (this.$route.query.id) {
        this.$http.get(`qichexinxi/info/${this.$route.query.id}`).then(res => {
          if (res.data.code == 0) {
            const car = res.data.data;
            this.ruleForm.chepaihao = car.chepaihao;
            this.ruleForm.cheliangpinpai = car.cheliangpinpai;
            this.ruleForm.qicheleibie = car.qicheleibie;
            // 兼容 logic: 优先取 qichejiage，如果没有则取 jiage
            this.ruleForm.jiage = car.qichejiage || car.jiage || 0;
            this.ruleForm.guanlizhanghao = car.guanlizhanghao;
            this.ruleForm.guanlixingming = car.guanlixingming;
          }
        });
      }
    },

    onSubmit() {
      // 提交前将计算好的总价赋值给表单
      this.ruleForm.zongjia = this.zongjia;

      if(this.ruleForm.zuqi <= 0) {
        this.$message.error('请选择正确的还车日期');
        return;
      }

      this.$refs["ruleForm"].validate(valid => {
        if (valid) {
          this.$http.post('zuchedingdan/add', this.ruleForm).then(res => {
            if (res.data.code == 0) {
              this.$message({
                message: '订单提交成功！',
                type: 'success',
                duration: 1500,
                onClose: () => {
                  this.$router.push('/index/zuchedingdan');
                }
              });
            } else {
              this.$message.error(res.data.msg);
            }
          });
        }
      });
    },
    getUUID() {
      return new Date().getTime();
    },
    back() {
      this.$router.go(-1);
    },
  }
};
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.booking-container {
  width: 1200px;
  margin: 30px auto;
}

.form-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0,0,0,0.08);
  overflow: hidden;
  padding: 30px 40px;
}

.form-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #eee;
  padding-bottom: 20px;
  margin-bottom: 30px;

  span {
    font-size: 20px;
    font-weight: 600;
    color: #333;
    display: flex;
    align-items: center;
    i {
      margin-right: 8px;
      color: #00c292; /* 主题色 */
    }
  }
}

.section-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  border-left: 4px solid #00c292;
  padding-left: 12px;
  margin: 25px 0 20px 0;
}

.section-content {
  padding: 0 10px;

  &.highlight-bg {
    background: #f0f9eb; /* 浅绿色背景 */
    padding: 25px;
    border-radius: 6px;
    border: 1px solid #e1f3d8;
  }
}

.readonly-input /deep/ .el-input__inner {
  background-color: #f5f7fa;
  color: #606266;
  cursor: not-allowed;
  border-color: #e4e7ed;
}

.total-price-input /deep/ .el-input__inner {
  color: #f56c6c;
  font-weight: bold;
  font-size: 20px; /* 稍微放大总价字体 */
}

.form-footer {
  margin-top: 40px;
  text-align: center;
  border-top: 1px solid #eee;
  padding-top: 30px;

  .submit-btn {
    width: 200px;
    height: 46px;
    font-size: 16px;
    letter-spacing: 2px;
    background-color: #00c292;
    border-color: #00c292;

    &:hover {
      background-color: #0bb388;
      border-color: #0bb388;
    }
  }

  .cancel-btn {
    width: 120px;
    height: 46px;
    margin-left: 20px;
  }
}
</style>
