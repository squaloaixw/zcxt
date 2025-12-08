<template>
  <div class="main-container">
    <div class="top-header">
      <div class="header-content">
        <div class="logo" @click="toPage('/index/home')">
          <img src="@/assets/logo.png" alt="Logo" v-if="false" />
          <span class="brand-text">出行 · 租车系统</span>
        </div>

        <div class="nav-menu">
          <div class="nav-item" :class="{ active: activeIndex === 'home' }" @click="toPage('/index/home')">首页</div>
          <div class="nav-item" :class="{ active: activeIndex === 'qichexinxi' }" @click="toPage('/index/qichexinxi')">车辆租赁</div>
          <div class="nav-item" :class="{ active: activeIndex === 'qichezixun' }" @click="toPage('/index/qichezixun')">汽车资讯</div>
          <div class="nav-item" :class="{ active: activeIndex === 'forum' }" @click="toPage('/index/forum')">交流论坛</div>
          <div class="nav-item" :class="{ active: activeIndex === 'news' }" @click="toPage('/index/news')">公告信息</div>
        </div>

        <div class="user-action">
          <div v-if="!Token" class="auth-btns">
            <el-button type="text" @click="toPage('/login')">登录</el-button>
            <el-button type="primary" size="small" round @click="toPage('/register')">注册</el-button>
          </div>
          <div v-else class="user-info">
            <el-dropdown @command="handleCommand" trigger="click">
              <span class="el-dropdown-link">
                <i class="el-icon-user-solid"></i> {{ username }}<i class="el-icon-caret-bottom el-icon--right"></i>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="center">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </div>
      </div>
    </div>

    <div class="router-view-container">
      <router-view></router-view>
    </div>

    <div class="footer">
      <div class="footer-content">
        <div class="footer-section">
          <h3>联系我们</h3>
          <p>电话：400-888-8888</p>
          <p>邮箱：service@carrental.com</p>
          <p>地址：XX省XX市XX区科技园</p>
        </div>
        <div class="footer-section">
          <h3>快速链接</h3>
          <p @click="toPage('/index/qichexinxi')">租车服务</p>
          <p @click="toPage('/index/messages')">留言反馈</p>
        </div>
        <div class="footer-section">
          <h3>关于我们</h3>
          <p>致力于提供最优质、便捷、安全的汽车租赁服务。</p>
        </div>
      </div>
      <div class="copyright">
        Copyright © 2025 汽车租赁管理系统 All Rights Reserved.
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      activeIndex: 'home',
      Token: '',
      username: ''
    }
  },
  watch: {
    $route(to) {
      this.setActiveMenu(to.path);
    }
  },
  mounted() {
    this.Token = localStorage.getItem('Token');
    this.username = localStorage.getItem('adminName');
    this.setActiveMenu(this.$route.path);
  },
  methods: {
    toPage(path) {
      if (!path) return;
      // 检查当前路由是否已经是目标路由，避免重复跳转报错
      if (this.$route.path !== path) {
        this.$router.push(path);
      }
    },
    setActiveMenu(path) {
      if (path.includes('home')) this.activeIndex = 'home';
      else if (path.includes('qichexinxi')) this.activeIndex = 'qichexinxi';
      else if (path.includes('qichezixun')) this.activeIndex = 'qichezixun';
      else if (path.includes('forum')) this.activeIndex = 'forum';
      else if (path.includes('news')) this.activeIndex = 'news';
      else this.activeIndex = '';
    },
    handleCommand(command) {
      if (command === 'logout') {
        this.logout();
      } else if (command === 'center') {
        this.$router.push('/index/center');
      }
    },
    logout() {
      localStorage.clear();
      this.$router.push('/login');
      this.$message.success('退出成功');
      // 稍微延迟刷新页面，确保状态清除干净
      setTimeout(() => {
        location.reload();
      }, 500);
    }
  }
}
</script>

<style lang="scss" scoped>
.main-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f7f8fa;
}

/* 顶部导航 */
.top-header {
  position: sticky;
  top: 0;
  z-index: 1000;
  background: #ffffff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  height: 70px;

  .header-content {
    width: 1200px;
    margin: 0 auto;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: space-between;

    .logo {
      display: flex;
      align-items: center;
      cursor: pointer;
      img {
        height: 40px;
        margin-right: 10px;
      }
      .brand-text {
        font-size: 22px;
        font-weight: bold;
        color: #00c292;
        letter-spacing: 1px;
      }
    }

    .nav-menu {
      display: flex;
      height: 100%;

      .nav-item {
        padding: 0 20px;
        height: 100%;
        display: flex;
        align-items: center;
        font-size: 16px;
        color: #333;
        cursor: pointer;
        position: relative;
        font-weight: 500;
        transition: all 0.3s;

        &:hover, &.active {
          color: #00c292;
        }

        &.active::after {
          content: '';
          position: absolute;
          bottom: 0;
          left: 50%;
          transform: translateX(-50%);
          width: 30px;
          height: 3px;
          background: #00c292;
          border-radius: 3px 3px 0 0;
        }
      }
    }

    .user-action {
      .auth-btns {
        display: flex;
        align-items: center;
      }
      .user-info {
        cursor: pointer;
        .el-dropdown-link {
          color: #333;
          font-weight: 500;
          &:hover {
            color: #00c292;
          }
        }
      }
    }
  }
}

/* 主体区域 */
.router-view-container {
  flex: 1;
  width: 100%;
  margin: 0 auto;
}

/* 页脚 */
.footer {
  background: #242f42;
  color: rgba(255, 255, 255, 0.7);
  padding: 50px 0 20px;
  margin-top: 40px;

  .footer-content {
    width: 1200px;
    margin: 0 auto;
    display: flex;
    justify-content: space-between;
    padding-bottom: 30px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);

    .footer-section {
      h3 {
        color: #fff;
        font-size: 16px;
        margin-bottom: 20px;
      }
      p {
        margin-bottom: 10px;
        font-size: 14px;
        cursor: pointer;
        transition: color 0.3s;
        &:hover {
          color: #00c292;
        }
      }
    }
  }

  .copyright {
    text-align: center;
    padding-top: 20px;
    font-size: 13px;
  }
}

@media (max-width: 1250px) {
  .top-header .header-content, .footer .footer-content {
    width: 95%;
  }
}
</style>
