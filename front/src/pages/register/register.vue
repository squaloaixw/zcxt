<template>
  <div class="register-container">
    <div class="register-mask"></div>

    <div class="register-box">
      <div class="register-header">
        <h2>创建新账号</h2>
        <p>加入我们，体验极致租车服务</p>
      </div>

      <el-form ref="registerForm" :model="registerForm" :rules="rules" class="register-form" label-position="top">
        <div class="form-grid">
          <div class="form-col">
            <el-form-item v-if="tableName=='yonghu'" label="账号" prop="zhanghao">
              <el-input v-model="registerForm.zhanghao" placeholder="请输入账号"></el-input>
            </el-form-item>
            <el-form-item v-if="tableName=='putongguanliyuan'" label="管理账号" prop="guanlizhanghao">
              <el-input v-model="registerForm.guanlizhanghao" placeholder="请输入管理账号"></el-input>
            </el-form-item>

            <el-form-item label="密码" prop="mima">
              <el-input v-model="registerForm.mima" type="password" placeholder="请输入密码"></el-input>
            </el-form-item>

            <el-form-item label="确认密码" prop="mima2">
              <el-input v-model="registerForm.mima2" type="password" placeholder="请再次输入密码"></el-input>
            </el-form-item>

            <el-form-item v-if="tableName=='yonghu'" label="姓名" prop="xingming">
              <el-input v-model="registerForm.xingming" placeholder="请输入姓名"></el-input>
            </el-form-item>
            <el-form-item v-if="tableName=='putongguanliyuan'" label="管理姓名" prop="guanlixingming">
              <el-input v-model="registerForm.guanlixingming" placeholder="请输入管理姓名"></el-input>
            </el-form-item>
          </div>

          <div class="form-col">
            <el-form-item label="性别" prop="xingbie">
              <el-select v-model="registerForm.xingbie" placeholder="请选择性别" style="width:100%">
                <el-option v-for="(item,index) in xingbieOptions" :key="index" :label="item" :value="item"></el-option>
              </el-select>
            </el-form-item>

            <el-form-item label="年龄" prop="nianling">
              <el-input v-model="registerForm.nianling" placeholder="请输入年龄"></el-input>
            </el-form-item>

            <el-form-item v-if="tableName=='yonghu'" label="手机号" prop="shouji">
              <el-input v-model="registerForm.shouji" placeholder="请输入手机号"></el-input>
            </el-form-item>
            <el-form-item v-if="tableName=='putongguanliyuan'" label="联系电话" prop="lianxidianhua">
              <el-input v-model="registerForm.lianxidianhua" placeholder="请输入联系电话"></el-input>
            </el-form-item>

            <el-form-item v-if="tableName=='yonghu'" label="身份证" prop="shenfenzheng">
              <el-input v-model="registerForm.shenfenzheng" placeholder="请输入身份证"></el-input>
            </el-form-item>

            <el-form-item label="头像" prop="touxiang">
              <file-upload
                  tip="点击上传头像"
                  action="file/upload"
                  :limit="1"
                  :multiple="true"
                  :fileUrls="registerForm.touxiang?registerForm.touxiang:''"
                  @change="handleAvatarSuccess"
              ></file-upload>
            </el-form-item>
          </div>
        </div>

        <div class="btn-group">
          <el-button type="primary" class="submit-btn" @click="submitForm('registerForm')">立即注册</el-button>
          <div class="login-link">
            已有账号？ <span @click="$router.push('/login')">去登录</span>
          </div>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      pageFlag: '',
      tableName: '',
      registerForm: {
        xingbie: '',
        touxiang: '' // 初始化头像字段，避免校验时报错
      },
      rules: {},
      xingbieOptions: ['男', '女'],
    };
  },
  created() {
    this.pageFlag = this.$route.query.pageFlag;
    this.tableName = this.$route.query.role;

    // 默认注册用户角色，防止url参数缺失
    if (!this.tableName) {
      this.tableName = 'yonghu';
    }

    this.initRules();
  },
  methods: {
    initRules() {
      // 基础公共规则
      const baseRules = {
        mima: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        mima2: [{ required: true, message: '请确认密码', trigger: 'blur' }],
        xingbie: [{ required: true, message: '请选择性别', trigger: 'change' }],
        nianling: [
          { required: true, message: '请输入年龄', trigger: 'blur' },
          { validator: this.$validate.isIntNumer, trigger: 'blur' }
        ],
        touxiang: [{ required: true, message: '请上传头像', trigger: 'change' }]
      };

      // 根据角色合并特定规则
      if (this.tableName === 'yonghu') {
        this.rules = {
          ...baseRules,
          zhanghao: [{ required: true, message: '请输入账号', trigger: 'blur' }],
          xingming: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
          shouji: [
            { required: true, message: '请输入手机号', trigger: 'blur' },
            { validator: this.$validate.isMobile, trigger: 'blur' }
          ],
          shenfenzheng: [
            { required: true, message: '请输入身份证', trigger: 'blur' },
            { validator: this.$validate.isIdCard, trigger: 'blur' }
          ]
        };
      } else if (this.tableName === 'putongguanliyuan') {
        this.rules = {
          ...baseRules,
          guanlizhanghao: [{ required: true, message: '请输入管理账号', trigger: 'blur' }],
          guanlixingming: [{ required: true, message: '请输入管理姓名', trigger: 'blur' }],
          lianxidianhua: [
            { required: true, message: '请输入联系电话', trigger: 'blur' },
            { validator: this.$validate.isMobile, trigger: 'blur' }
          ]
        };
      }
    },
    handleAvatarSuccess(fileUrls) {
      // 获取上传后的地址
      this.registerForm.touxiang = fileUrls.replace(new RegExp(this.$config.baseUrl, "g"), "");
      // 手动触发一次头像字段的校验，消除错误提示
      if(this.registerForm.touxiang) {
        this.$refs.registerForm.clearValidate(['touxiang']);
      }
    },
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          // 再次确认密码一致性
          if (this.registerForm.mima !== this.registerForm.mima2) {
            this.$message.error('两次密码输入不一致');
            return;
          }

          this.$http.post(`${this.tableName}/register`, this.registerForm).then(res => {
            if (res.data.code === 0) {
              this.$message.success('注册成功');
              this.$router.push('/login');
            } else {
              this.$message.error(res.data.msg);
            }
          });
        } else {
          // 校验不通过，页面会自动显示红色错误提示
          console.log('error submit!!');
          return false;
        }
      });
    }
  }
};
</script>

<style lang="scss" scoped>
.register-container {
  position: fixed;
  left: 0; top: 0; width: 100%; height: 100vh;
  // 背景图地址，可根据需要更换
  background: url('https://images.unsplash.com/photo-1492144534655-ae79c964c9d7?ixlib=rb-1.2.1&auto=format&fit=crop&w=1950&q=80') no-repeat center center;
  background-size: cover;
  display: flex;
  justify-content: center;
  align-items: center;
  overflow-y: auto;
}

.register-mask {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  background: rgba(0, 0, 0, 0.5);
}

.register-box {
  width: 800px;
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  position: relative;
  z-index: 1;
  box-shadow: 0 10px 30px rgba(0,0,0,0.2);
  margin: 50px 0; /* 防止高度不够时贴边 */

  .register-header {
    text-align: center;
    margin-bottom: 30px;
    h2 {
      font-size: 26px;
      color: #333;
      margin-bottom: 8px;
    }
    p {
      color: #999;
      font-size: 14px;
    }
  }

  .form-grid {
    display: flex;
    gap: 40px;

    .form-col {
      flex: 1;
    }
  }

  /* 输入框美化 */
  /deep/ .el-input__inner {
    height: 40px;
    border-radius: 4px;
    border: 1px solid #dcdfe6;
    &:focus {
      border-color: #00c292;
    }
  }

  /* 头像上传组件微调 */
  /deep/ .avatar-uploader .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    &:hover {
      border-color: #00c292;
    }
  }

  .btn-group {
    text-align: center;
    margin-top: 30px;

    .submit-btn {
      width: 200px;
      height: 44px;
      font-size: 16px;
      background: linear-gradient(135deg, #00c292 0%, #0ba360 100%);
      border: none;
      border-radius: 22px;
      box-shadow: 0 4px 10px rgba(0, 194, 146, 0.3);
      transition: all 0.3s;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 6px 15px rgba(0, 194, 146, 0.4);
      }
    }

    .login-link {
      margin-top: 15px;
      font-size: 14px;
      color: #666;
      span {
        color: #00c292;
        cursor: pointer;
        font-weight: 500;
        &:hover { text-decoration: underline; }
      }
    }
  }
}

/* 响应式适配 */
@media (max-width: 768px) {
  .register-box {
    width: 90%;
    .form-grid {
      flex-direction: column;
      gap: 0;
    }
  }
}
</style>
