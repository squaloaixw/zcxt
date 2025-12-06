<template>
  <div style="height: 100%;">
    <index-aside v-if="'vertical' == 'vertical'" class="index-aside-container"></index-aside>

    <el-main class="index-main-container">
      <index-header class="index-header"></index-header>

      <index-aside v-if="'vertical' == 'horizontal'" class="index-aside-horizontal"></index-aside>

      <bread-crumbs :title="title" class="bread-crumbs"></bread-crumbs>

      <router-view class="router-view"></router-view>
    </el-main>
  </div>
</template>

<script>
import IndexAside from '@/components/index/IndexAsideStatic'
import IndexHeader from '@/components/index/IndexHeader'
import menu from "@/utils/menu";

export default {
  components: {
    IndexAside,
    IndexHeader
  },
  data() {
    return {
      menuList: [],
      role: "",
      currentIndex: -2,
      itemMenu: [],
      title: '',
    };
  },
  mounted() {
    let menus = menu.list();
    this.menuList = menus;
    this.role = this.$storage.get("role");
  },
  created() {
    this.init();
  },
  methods: {
    init(){
      this.$nextTick(()=>{})
    },
    menuHandler(menu) {
      this.$router.push({
        name: menu.tableName
      });
      this.title = menu.menu;
    },
    titleChange(index, menus) {
      this.currentIndex = index
      this.itemMenu = menus;
    },
    homeChange(index) {
      this.itemMenu = [];
      this.title = ""
      this.currentIndex = index
      this.$router.push({
        name: 'home'
      });
    },
    centerChange(index) {
      this.itemMenu = [{
        "buttons": ["新增", "查看", "修改", "删除"],
        "menu": "修改密码",
        "tableName": "updatePassword"
      }, {
        "buttons": ["新增", "查看", "修改", "删除"],
        "menu": "个人信息",
        "tableName": "center"
      }];
      this.title = ""
      this.currentIndex = index
      this.$router.push({
        name: 'home'
      });
    }
  }
};
</script>

<style lang="scss" scoped>
/* 侧边栏容器 */
.index-aside-container {
  box-shadow: 2px 0 6px rgba(0, 21, 41, 0.15);
  overflow: hidden;
  top: 0;
  left: 0;
  background: #304156; /* 深色侧边栏背景 */
  bottom: 0;
  width: 210px;
  position: fixed;
  height: 100%;
  z-index: 1001;
}

/* 主体容器 */
.index-main-container {
  min-height: 100%;
  padding: 0;
  margin: 0 0 0 210px; /* 留出侧边栏宽度 */
  position: relative;
  background: #f0f2f5; /* 整体浅灰背景 */
  display: block;
}

/* 顶部导航栏样式 */
.index-header {
  position: relative;
  width: 100%;
  z-index: 1000;
}

/* 面包屑样式 */
.bread-crumbs {
  padding: 15px 20px;
  margin-bottom: 10px;
}

/* 路由视图样式 */
.router-view {
  padding: 0 20px 20px 20px;
  background: transparent;
}

a {
  text-decoration: none;
  color: #555;
}
a:hover {
  background: #00c292;
}
</style>
