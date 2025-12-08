<template>
  <div class="center-container">
    <div class="sidebar-wrapper">
      <div class="user-profile-card">
        <div class="avatar-box">
          <img :src="sessionForm.touxiang ? baseUrl + sessionForm.touxiang : require('@/assets/touxiang.png')" alt="Avatar">
        </div>
        <div class="user-name">{{ sessionForm.xingming || sessionForm.guanlixingming || sessionForm.zhanghao }}</div>
        <div class="user-role">{{ userTableName == 'yonghu' ? '普通用户' : '管理员' }}</div>
      </div>

      <div class="side-menu">
        <div class="menu-item active">个人资料</div>
        <div class="menu-item" @click="$router.push('/index/myForumList')">我的发布</div>
        <div class="menu-item" @click="toStoreup">我的收藏</div>
        <div class="menu-item danger" @click="logout">退出登录</div>
      </div>
    </div>

    <div class="content-wrapper">
      <div class="panel-header">
        <h3>个人资料设置</h3>
      </div>

      <el-form ref="sessionForm" :model="sessionForm" :rules="rules" label-width="100px" class="user-form">
        <template v-if="userTableName=='yonghu'">
          <el-form-item label="账号" prop="zhanghao">
            <el-input v-model="sessionForm.zhanghao" readonly disabled></el-input>
          </el-form-item>
          <el-form-item label="姓名" prop="xingming">
            <el-input v-model="sessionForm.xingming"></el-input>
          </el-form-item>
          <el-form-item label="性别">
            <el-radio-group v-model="sessionForm.xingbie">
              <el-radio label="男">男</el-radio>
              <el-radio label="女">女</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="手机号" prop="shouji">
            <el-input v-model="sessionForm.shouji"></el-input>
          </el-form-item>
          <el-form-item label="身份证" prop="shenfenzheng">
            <el-input v-model="sessionForm.shenfenzheng"></el-input>
          </el-form-item>
        </template>

        <template v-if="userTableName=='putongguanliyuan'">
          <el-form-item label="管理账号" prop="guanlizhanghao">
            <el-input v-model="sessionForm.guanlizhanghao" readonly disabled></el-input>
          </el-form-item>
          <el-form-item label="姓名" prop="guanlixingming">
            <el-input v-model="sessionForm.guanlixingming"></el-input>
          </el-form-item>
          <el-form-item label="联系电话" prop="lianxidianhua">
            <el-input v-model="sessionForm.lianxidianhua"></el-input>
          </el-form-item>
        </template>

        <el-form-item label="头像上传">
          <el-upload
              class="avatar-uploader"
              :action="uploadUrl"
              :headers="headers"
              :show-file-list="false"
              :on-success="handleAvatarSuccess">
            <img v-if="sessionForm.touxiang" :src="baseUrl + sessionForm.touxiang" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="onSubmit('sessionForm')">保存修改</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-dialog title="账户充值" :visible.sync="dialogFormVisibleMoney" width="500px">
      <el-form :model="chongzhiForm">
        <el-form-item label="充值金额">
          <el-input type="number" v-model="chongzhiForm.money" placeholder="请输入金额"></el-input>
        </el-form-item>
        <el-form-item label="支付方式">
          <el-radio-group v-model="chongzhiForm.radio">
            <el-radio label="微信支付">微信</el-radio>
            <el-radio label="支付宝支付">支付宝</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisibleMoney = false">取 消</el-button>
        <el-button type="primary" @click="chongzhi">确 认</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import config from '@/config/config'

export default {
  data() {
    return {
      baseUrl: config.baseUrl,
      // 【修复点】：login.vue 中存储的键名为 sessionTable，这里必须一致，否则为 null 导致接口 404
      userTableName: localStorage.getItem('sessionTable'),
      sessionForm: {},
      headers: { Token: localStorage.getItem('Token') },
      uploadUrl: config.baseUrl + 'file/upload',
      rules: {
        xingming: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
        shouji: [{ validator: this.$validate.isMobile, trigger: 'blur' }],
        shenfenzheng: [{ validator: this.$validate.isIdCard, trigger: 'blur' }]
      },
      dialogFormVisibleMoney: false,
      dialogFormVisibleVip: false,
      chongzhiForm: { money: '', radio: '微信支付' }
    };
  },
  created() {
    this.getSession();
  },
  methods: {
    getSession() {
      // 如果 userTableName 为空，直接跳转回登录，避免发送错误请求
      if(!this.userTableName) {
        this.$router.push('/login');
        return;
      }
      this.$http.get(`${this.userTableName}/session`).then(res => {
        if (res.data.code == 0) {
          this.sessionForm = res.data.data;
          // 防止头像路径为空时的报错
          if(!this.sessionForm.touxiang) this.sessionForm.touxiang = '';
        }
      });
    },
    handleAvatarSuccess(res) {
      if (res.code == 0) {
        this.sessionForm.touxiang = 'upload/' + res.file;
        this.$forceUpdate();
      }
    },
    onSubmit() {
      this.$refs.sessionForm.validate(valid => {
        if (valid) {
          this.$http.post(`${this.userTableName}/update`, this.sessionForm).then(res => {
            if (res.data.code == 0) {
              this.$message.success('个人信息更新成功');
              this.getSession(); // 刷新数据
            }
          });
        }
      });
    },
    chongzhi() {
      if (!this.chongzhiForm.money || this.chongzhiForm.money <= 0) return this.$message.error('请输入有效金额');

      this.sessionForm.money = parseFloat(this.sessionForm.money) + parseFloat(this.chongzhiForm.money);
      this.$http.post(`${this.userTableName}/update`, this.sessionForm).then(res => {
        if (res.data.code == 0) {
          this.$message.success('充值成功');
          this.dialogFormVisibleMoney = false;
        }
      });
    },
    chongzhivip() {
      this.sessionForm.vip = '是';
      this.$http.post(`${this.userTableName}/update`, this.sessionForm).then(res => {
        if (res.data.code == 0) {
          this.$message.success('开通成功，您已成为尊贵VIP');
          this.dialogFormVisibleVip = false;
          localStorage.setItem('vip', '是');
        }
      });
    },
    toStoreup() {
      this.$router.push('/index/storeup');
    },
    logout() {
      localStorage.clear();
      this.$router.push('/index/home');
    }
  }
};
</script>

<style lang="scss" scoped>
.center-container {
  width: 1200px;
  margin: 20px auto;
  display: flex;
  gap: 20px;
}

/* 左侧边栏 */
.sidebar-wrapper {
  width: 280px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.05);
  padding: 30px 20px;
  height: fit-content;

  .user-profile-card {
    text-align: center;
    margin-bottom: 30px;
    border-bottom: 1px solid #f0f0f0;
    padding-bottom: 20px;

    .avatar-box {
      width: 100px;
      height: 100px;
      margin: 0 auto 15px;
      border-radius: 50%;
      overflow: hidden;
      border: 2px solid #e0e0e0;
      img { width: 100%; height: 100%; object-fit: cover; }
    }

    .user-name {
      font-size: 18px;
      font-weight: bold;
      color: #333;
      margin-bottom: 5px;
    }

    .user-role {
      font-size: 12px;
      color: #999;
      margin-bottom: 10px;
    }

    .vip-tag {
      font-size: 12px;
      padding: 2px 8px;
      border-radius: 10px;
      background: #eee;
      color: #666;
      &.active { background: #fff7e6; color: #ff9900; border: 1px solid #ffcc80; }
    }
  }

  .side-menu {
    .menu-item {
      padding: 12px 15px;
      cursor: pointer;
      border-radius: 6px;
      color: #666;
      transition: background 0.3s;
      margin-bottom: 5px;

      &:hover { background: #f5f7fa; color: #00c292; }
      &.active { background: #e6f9f4; color: #00c292; font-weight: 500; }
      &.danger { color: #f56c6c; &:hover { background: #fef0f0; } }
    }
  }
}

/* 右侧内容区 */
.content-wrapper {
  flex: 1;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.05);
  padding: 30px 40px;

  .panel-header {
    border-bottom: 1px solid #f0f0f0;
    margin-bottom: 30px;
    padding-bottom: 15px;
    h3 { margin: 0; font-size: 20px; color: #333; }
  }

  .user-form {
    max-width: 600px;
  }

  .balance-box {
    display: flex;
    align-items: center;
    .money { font-size: 20px; font-weight: bold; color: #ff5000; margin-right: 20px; }
  }

  .avatar-uploader {
    /deep/ .el-upload {
      border: 1px dashed #d9d9d9;
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      &:hover { border-color: #00c292; }
    }
    .avatar-uploader-icon {
      font-size: 28px;
      color: #8c939d;
      width: 100px;
      height: 100px;
      line-height: 100px;
      text-align: center;
    }
    .avatar {
      width: 100px;
      height: 100px;
      display: block;
      object-fit: cover;
    }
  }
}
</style>
