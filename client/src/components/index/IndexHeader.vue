<template>
  <div class="navbar">
    <div class="header-left">
      <span class="project-title">{{this.$project.projectName}}</span>
    </div>

    <div class="header-right">
      <div class="user-info">
        <el-avatar icon="el-icon-user-solid" size="small" class="user-avatar"></el-avatar>
        <span class="user-role">{{this.$storage.get('role')}}</span>
        <span class="user-name">{{this.$storage.get('adminName')}}</span>
      </div>

      <div class="action-items">
        <el-tooltip content="退出到前台" placement="bottom" v-if="this.$storage.get('role')!='管理员'">
          <div class="action-item" @click="onIndexTap">
            <i class="el-icon-s-home"></i>
          </div>
        </el-tooltip>

        <el-tooltip content="退出登录" placement="bottom">
          <div class="action-item logout-btn" @click="onLogout">
            <i class="el-icon-switch-button"></i>
            <span>退出</span>
          </div>
        </el-tooltip>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      user: {},
    };
  },
  mounted() {
    let sessionTable = this.$storage.get("sessionTable")
    this.$http({
      url: sessionTable + '/session',
      method: "get"
    }).then(({ data }) => {
      if (data && data.code === 0) {
        this.user = data.data;
        this.$storage.set('userid',data.data.id);
      } else {
        this.$message.error(data.msg);
      }
    });
  },
  methods: {
    onLogout() {
      this.$storage.clear()
      this.$router.replace({ name: "login" });
    },
    onIndexTap(){
      window.location.href = `http://localhost:8081/`
    },
  }
};
</script>

<style lang="scss" scoped>
.navbar {
  height: 60px;
  overflow: hidden;
  position: relative;
  /* 修改点：改为深色背景，提升质感 */
  background: #242f42;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  color: #fff; /* 修改点：文字统一改为白色 */
}

.header-left {
  display: flex;
  align-items: center;

  .project-title {
    font-size: 20px;
    font-weight: 600;
    color: #fff; /* 标题白色 */
    letter-spacing: 1px;
  }
}

.header-right {
  display: flex;
  align-items: center;
  height: 100%;

  .user-info {
    display: flex;
    align-items: center;
    margin-right: 20px;
    padding-right: 20px;
    border-right: 1px solid rgba(255,255,255,0.15); /* 修改点：分割线改为半透明白 */

    .user-avatar {
      background: #fff; /* 修改点：头像背景改为白色 */
      color: #242f42; /* 图标颜色改为深色 */
      margin-right: 8px;
    }

    .user-role {
      font-size: 12px;
      background: rgba(255,255,255,0.1); /* 修改点：角色标签改为半透明背景 */
      padding: 2px 6px;
      border-radius: 4px;
      color: #fff;
      margin-right: 8px;
    }

    .user-name {
      font-size: 14px;
      color: #fff;
      font-weight: 500;
    }
  }

  .action-items {
    display: flex;
    align-items: center;

    .action-item {
      display: flex;
      align-items: center;
      padding: 0 12px;
      height: 60px;
      cursor: pointer;
      color: #fff; /* 修改点：图标改为白色 */
      transition: background .3s;

      &:hover {
        background: rgba(0,0,0,0.2); /* 修改点：悬停效果为加深背景 */
      }

      i {
        font-size: 18px;
      }
    }

    .logout-btn {
      span {
        margin-left: 5px;
        font-size: 14px;
      }
    }
  }
}
</style>
