<template>
  <div class="menu-preview">
    <el-scrollbar wrap-class="scrollbar-wrapper">
      <el-menu
          :default-openeds="[]"
          :unique-opened="true"
          class="el-menu-vertical-demo"
          default-active="0"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#00c292"
      >

        <el-submenu index="0" @click.native="menuHandler('')">
          <template slot="title">
            <i class="el-icon-s-home" />
            <span>系统首页</span>
          </template>
        </el-submenu>

        <el-submenu index="1">
          <template slot="title">
            <i class="el-icon-user-solid" />
            <span>个人中心</span>
          </template>
          <el-menu-item index="1-1" @click="menuHandler('updatePassword')">修改密码</el-menu-item>
          <el-menu-item index="1-2" @click="menuHandler('center')">个人信息</el-menu-item>
        </el-submenu>

        <el-submenu v-for=" (menu,index) in menuList.backMenu" :key="menu.menu" :index="index+2+''">
          <template slot="title">
            <i class="el-icon-menu" :class="icons[index]" />
            <span>{{ menu.menu }}</span>
          </template>
          <el-menu-item v-for=" (child,sort) in menu.child" :key="sort" :index="(index+2)+'-'+sort" @click="menuHandler(child.tableName)">{{ child.menu }}</el-menu-item>
        </el-submenu>
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script>
import menu from '@/utils/menu'
export default {
  data() {
    return {
      menuList: [],
      dynamicMenuRoutes: [],
      role: '',
      icons: [
        'el-icon-s-cooperation', 'el-icon-s-order', 'el-icon-s-platform',
        'el-icon-s-fold', 'el-icon-s-unfold', 'el-icon-s-operation',
        'el-icon-s-promotion', 'el-icon-s-release', 'el-icon-s-ticket',
        'el-icon-s-management', 'el-icon-s-open', 'el-icon-s-shop',
        'el-icon-s-marketing'
      ],
    }
  },
  mounted() {
    const menus = menu.list()
    if(menus) {
      this.menuList = menus
    } else {
      // 兼容性逻辑，保持原样
      let params = { page: 1, limit: 1, sort: 'id' }
      this.$http({
        url: "menu/list",
        method: "get",
        params: params
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.menuList = JSON.parse(data.data.list[0].menujson);
          this.$storage.set("menus", this.menuList);
        }
      })
    }
    this.role = this.$storage.get('role')

    for(let i=0;i<this.menuList.length;i++) {
      if(this.menuList[i].roleName == this.role) {
        this.menuList = this.menuList[i];
        break;
      }
    }
  },
  created(){
    this.icons.sort(()=>{ return (0.5-Math.random()) })
  },
  methods: {
    menuHandler(name) {
      let router = this.$router
      name = '/'+name
      router.push(name)
    },
  }
}
</script>
<style lang="scss" scoped>
.menu-preview {
  height: 100%;
  .el-scrollbar {
    height: 100%;
    & /deep/ .scrollbar-wrapper {
      overflow-x: hidden;
    }

    .el-menu {
      border-right: none;
    }

    .el-menu-vertical-demo {
      /* 隐藏首页的箭头 */
      .el-submenu:first-of-type /deep/ .el-submenu__title .el-submenu__icon-arrow {
        display: none;
      }

      i {
        margin-right: 10px;
        color: inherit;
      }
    }

    /* 菜单项悬停和激活样式 */
    .el-menu-item:hover, .el-submenu__title:hover {
      background-color: #263445 !important;
    }

    .el-menu-item.is-active {
      background-color: #1f2d3d !important;
      border-left: 4px solid #00c292;
    }
  }
}
</style>
