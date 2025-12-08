<template>
  <div class="login-wrapper">
    <div class="poster-side">
      <div class="poster-content">
        <div class="brand">
          <i class="el-icon-truck"></i> xx租车
        </div>
        <h1 class="slogan">开启您的<br>尊贵驾乘之旅</h1>
        <p class="sub-slogan">数千款车型任您选择，随心所欲，即刻出发。</p>
      </div>
      <div class="circle c1"></div>
      <div class="circle c2"></div>
    </div>

    <div class="form-side">
      <div class="form-container">
        <div class="header">
          <h2>欢迎回来</h2>
          <p>请登录您的账号</p>
        </div>

        <el-form :model="loginForm" class="login-form" @keyup.enter.native="submitForm">
          <el-form-item>
            <div class="input-group">
              <i class="el-icon-user"></i>
              <el-input
                  v-model="loginForm.username"
                  placeholder="请输入账号"
                  class="custom-input">
              </el-input>
            </div>
          </el-form-item>

          <el-form-item>
            <div class="input-group">
              <i class="el-icon-lock"></i>
              <el-input
                  v-model="loginForm.password"
                  type="password"
                  placeholder="请输入密码"
                  show-password
                  class="custom-input">
              </el-input>
            </div>
          </el-form-item>

          <input type="hidden" v-model="role">

          <el-button type="primary" class="submit-btn" :loading="loading" @click="submitForm">
            {{ loading ? '登录中...' : '立 即 登 录' }}
          </el-button>

          <div class="form-footer">
            <span>还没有账号？</span>
            <span class="link-btn" @click="$router.push('/register')">立即注册</span>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      // 默认锁定为用户角色，若需管理员登录需另行处理或恢复选择框
      role: 'yonghu',
      loading: false,
      loginForm: {
        username: '',
        password: ''
      }
    }
  },
  methods: {
    submitForm() {
      if (!this.loginForm.username || !this.loginForm.password) {
        this.$message.warning('请输入账号和密码');
        return;
      }

      this.loading = true;

      // 1. 发起登录请求
      this.$http.post(`${this.role}/login?username=${this.loginForm.username}&password=${this.loginForm.password}`).then(res => {
        if (res.data.code === 0) {
          // 2. 保存 Token 和 基础信息
          localStorage.setItem('Token', res.data.token);
          localStorage.setItem('role', this.role);
          localStorage.setItem('sessionTable', this.role);
          localStorage.setItem('adminName', this.loginForm.username);

          // 3. 请求 Session 获取用户 ID (用于个人中心和收藏功能)
          this.$http.get(`${this.role}/session`, {
            headers: { 'Token': res.data.token }
          }).then(sessionRes => {
            if (sessionRes.data.code === 0) {
              localStorage.setItem('userid', sessionRes.data.data.id);

              // 4. 【修复】使用 onClose 确保提示框显示完毕后再跳转
              this.$message({
                message: '登录成功，正在跳转...',
                type: 'success',
                duration: 1500,
                onClose: () => {
                  this.loading = false;
                  this.$router.push('/index/home');
                }
              });
            } else {
              this.loading = false;
              this.$message.error('获取用户信息失败');
            }
          }).catch(() => {
            this.loading = false;
          });
        } else {
          this.loading = false;
          this.$message.error(res.data.msg);
        }
      }).catch(() => {
        this.loading = false;
        this.$message.error('连接服务器失败，请检查网络');
      });
    }
  }
}
</script>

<style lang="scss" scoped>
.login-wrapper {
  display: flex;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  background: #fff;
}

/* 左侧海报样式 */
.poster-side {
  flex: 1.2; // 占比略大
  background: linear-gradient(135deg, #00c292 0%, #0ba360 100%);
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 0 80px;
  overflow: hidden;
  color: #fff;

  // 背景图叠加，增加质感
  &::before {
    content: '';
    position: absolute;
    top: 0; left: 0; width: 100%; height: 100%;
    background: url('https://images.unsplash.com/photo-1485291571150-772bcfc10da5?ixlib=rb-1.2.1&auto=format&fit=crop&w=1000&q=80') no-repeat center center;
    background-size: cover;
    opacity: 0.2; // 让图片隐约可见，不抢文字
    mix-blend-mode: overlay;
  }

  .poster-content {
    position: relative;
    z-index: 2;
  }

  .brand {
    font-size: 24px;
    font-weight: bold;
    margin-bottom: 40px;
    display: flex;
    align-items: center;
    i { margin-right: 10px; font-size: 30px; }
  }

  .slogan {
    font-size: 56px;
    font-weight: 800;
    line-height: 1.2;
    margin-bottom: 20px;
    letter-spacing: 2px;
  }

  .sub-slogan {
    font-size: 18px;
    opacity: 0.9;
    max-width: 400px;
  }

  // 装饰圆圈
  .circle {
    position: absolute;
    border-radius: 50%;
    background: rgba(255,255,255,0.1);
  }
  .c1 { width: 300px; height: 300px; top: -50px; right: -50px; }
  .c2 { width: 500px; height: 500px; bottom: -100px; left: -100px; }
}

/* 右侧表单样式 */
.form-side {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;

  .form-container {
    width: 400px;
    padding: 0 20px;
  }

  .header {
    margin-bottom: 40px;
    h2 {
      font-size: 32px;
      color: #333;
      margin-bottom: 10px;
    }
    p {
      color: #999;
      font-size: 16px;
    }
  }

  .input-group {
    display: flex;
    align-items: center;
    border-bottom: 2px solid #f0f0f0;
    padding: 5px 0;
    transition: border-color 0.3s;
    margin-bottom: 10px;

    &:focus-within {
      border-color: #00c292;
      i { color: #00c292; }
    }

    i {
      font-size: 20px;
      color: #c0c4cc;
      margin-right: 10px;
      transition: color 0.3s;
    }
  }

  // 覆盖 Element UI 输入框默认样式
  /deep/ .custom-input .el-input__inner {
    border: none;
    padding: 0;
    height: 40px;
    font-size: 16px;
    background: transparent !important;
    &:focus { box-shadow: none; }
  }

  .submit-btn {
    width: 100%;
    height: 50px;
    border-radius: 25px;
    font-size: 18px;
    font-weight: 600;
    margin-top: 30px;
    background: linear-gradient(90deg, #00c292 0%, #0ba360 100%);
    border: none;
    box-shadow: 0 10px 20px rgba(0, 194, 146, 0.3);
    transition: transform 0.2s, box-shadow 0.2s;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 15px 25px rgba(0, 194, 146, 0.4);
    }
    &:active {
      transform: scale(0.98);
    }
  }

  .form-footer {
    margin-top: 30px;
    text-align: center;
    color: #666;
    font-size: 14px;

    .link-btn {
      color: #00c292;
      font-weight: 600;
      cursor: pointer;
      margin-left: 5px;
      &:hover { text-decoration: underline; }
    }
  }
}

/* 响应式适配 */
@media (max-width: 900px) {
  .poster-side {
    display: none; // 小屏幕隐藏海报
  }
  .form-side {
    flex: 1;
    background: url('https://images.unsplash.com/photo-1485291571150-772bcfc10da5?ixlib=rb-1.2.1&auto=format&fit=crop&w=1000&q=80') no-repeat center center;
    background-size: cover;

    .form-container {
      background: rgba(255,255,255,0.95);
      padding: 40px;
      border-radius: 12px;
      box-shadow: 0 10px 30px rgba(0,0,0,0.1);
    }
  }
}
</style>
