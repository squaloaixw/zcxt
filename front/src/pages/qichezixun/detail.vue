<template>
  <div class="detail-container">
    <div class="breadcrumb-box">
      <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item :to="{ path: '/index' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>汽车资讯</el-breadcrumb-item>
        <el-breadcrumb-item>详情</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="main-content" v-if="detail.id">
      <div class="top-section">
        <div class="carousel-box">
          <el-carousel trigger="click" height="400px" indicator-position="outside">
            <el-carousel-item v-for="(item, index) in detailBanner" :key="index">
              <div class="image-wrapper">
                <el-image
                    style="width: 100%; height: 100%"
                    :src="item.substr(0,4)=='http' ? item : baseUrl + item"
                    fit="cover"
                    class="carousel-image">
                </el-image>
              </div>
            </el-carousel-item>
          </el-carousel>
        </div>

        <div class="info-box">
          <h1 class="title">{{ detail.biaoti }}</h1>

          <div class="meta-row">
            <span class="meta-item"><i class="el-icon-time"></i> {{ detail.fabushijian }}</span>
            <span class="meta-item"><i class="el-icon-user"></i> {{ detail.faburen }}</span>
            <span class="meta-item"><i class="el-icon-view"></i> {{ detail.clicknum }}次浏览</span>
          </div>

          <div class="intro-box">
            <div class="label">简介：</div>
            <div class="content">{{ detail.jianjie }}</div>
          </div>

          <div class="actions">
            <div class="store-btn" @click="storeup(1)" v-if="!isStoreup">
              <i class="el-icon-star-off"></i> 收藏资讯
            </div>
            <div class="store-btn active" @click="storeup(-1)" v-else>
              <i class="el-icon-star-on"></i> 已收藏
            </div>
          </div>
        </div>
      </div>

      <div class="content-section">
        <el-tabs v-model="activeName" type="border-card">
          <el-tab-pane label="详细内容" name="first">
            <div class="rich-text" v-html="detail.neirong"></div>
          </el-tab-pane>
        </el-tabs>
      </div>

      <div class="hot-section">
        <div class="section-header">
          <span>热门推荐</span>
        </div>
        <div class="hot-list">
          <div
              class="hot-item"
              v-for="item in hotList"
              :key="item.id"
              @click="toDetail(item)"
          >
            <div class="img-box">
              <el-image
                  v-if="item.fengmian"
                  style="width: 100%; height: 100%"
                  :src="baseUrl + item.fengmian.split(',')[0]"
                  fit="cover">
              </el-image>
            </div>
            <div class="info">
              <div class="hot-title">{{ item.biaoti }}</div>
              <div class="hot-meta">{{ item.faburen }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-else class="loading-state">
      <i class="el-icon-loading"></i> 正在加载详情...
    </div>
  </div>
</template>

<script>
import CountDown from '@/components/CountDown';

export default {
  //数据集合
  data() {
    return {
      tablename: 'qichezixun',
      baseUrl: '',
      breadcrumbItem: [
        {
          name: '详情信息'
        }
      ],
      title: '',
      detailBanner: [],
      endTime: 0,
      hotList: [],
      detail: {},
      activeName: 'first',
      total: 1,
      pageSize: 5,
      pageSizes: [10, 20, 30, 50],
      totalPage: 1,
      rules: {
        content: [
          { required: true, message: '请输入内容', trigger: 'blur' }
        ]
      },
      storeupParams: {
        name: '',
        picture: '',
        refid: 0,
        tablename: 'qichezixun',
        userid: localStorage.getItem('userid')
      },
      isStoreup: false,
      storeupInfo: {},
      buynumber: 1,
    }
  },
  created() {
    this.init();
  },
  //方法集合
  methods: {
    init() {
      this.baseUrl = this.$config.baseUrl;

      // --- 核心修复：兼容多种参数传递方式 ---
      // 1. 如果 URL 中直接包含 id 参数 (如 ?id=81)
      if(this.$route.query.id) {
        this.detail.id = this.$route.query.id;
      }
      // 2. 如果 URL 包含 detailObj 对象字符串
      if (this.$route.query.detailObj) {
        this.detail = JSON.parse(this.$route.query.detailObj);
      }
      // 3. 如果从收藏页跳转过来
      if (this.$route.query.storeupObj) {
        this.detail.id = JSON.parse(this.$route.query.storeupObj).refid;
      }

      // --- 确保获取到 ID 后再发起请求，避免 undefined 错误 ---
      if (this.detail.id) {
        this.$http.get(this.tablename + '/detail/' + this.detail.id, {}).then(res => {
          if (res.data.code == 0) {
            this.detail = res.data.data;
            this.title = this.detail.biaoti;
            this.detailBanner = this.detail.fengmian ? this.detail.fengmian.split(",") : [];
            this.$forceUpdate();
          }
        });
        // 获取收藏状态
        this.getStoreupStatus();
      } else {
        console.warn("未获取到有效的 detail ID，无法请求数据");
      }

      // 获取热门列表（不需要 ID）
      this.getHotList();
    },
    toDetail(item) {
      // 统一跳转逻辑，传递 detailObj
      this.$router.push({ path: '/index/qichezixunDetail', query: { detailObj: JSON.stringify(item) } });
      // 强制重新加载数据，因为是同路由跳转
      this.detail = item;
      this.init();
      document.body.scrollTop = 0;
      document.documentElement.scrollTop = 0;
    },
    getHotList() {
      let autoSortUrl = "qichezixun/autoSort";
      this.$http.get(autoSortUrl, {
        params: {
          page: 1,
          limit: 4,
        }
      }).then(res => {
        if (res.data.code == 0) {
          this.hotList = res.data.data.list;
        }
      })
    },
    storeup(type) {
      // 1. 校验数据完整性
      if (!this.detail.id) {
        this.$message.error('数据加载异常，无法操作');
        return;
      }

      // 2. 核心修复：检查登录状态
      // 必须实时获取 userid，不能依赖 data() 中初始化的值（因为可能是 null）
      let userid = localStorage.getItem('userid');
      if (!userid) {
        this.$message.error('请先登录');
        // 可选：自动跳转到登录页
        // this.$router.push('/login');
        return;
      }

      // 3. 准备请求参数
      // 确保所有字段都有值，防止后端报空指针错误
      this.storeupParams.userid = userid;
      this.storeupParams.name = this.title;
      // 安全获取图片，如果没有则传空字符串
      this.storeupParams.picture = (this.detailBanner && this.detailBanner.length > 0) ? this.detailBanner[0] : '';
      this.storeupParams.refid = this.detail.id;
      this.storeupParams.type = type; // type=1 表示收藏

      // 4. 执行收藏逻辑
      if (type == 1 && !this.isStoreup) {
        this.$http.post('storeup/add', this.storeupParams).then(res => {
          if (res.data.code == 0) {
            this.isStoreup = true;
            // 收藏成功后，重新获取一下收藏信息，以便获取 storeupInfo.id 用于取消收藏
            this.getStoreupStatus();
            this.$message({
              type: 'success',
              message: '收藏成功!',
              duration: 1500,
            });
          }
        });
      }

      // 5. 执行取消收藏逻辑
      if (type == -1 && this.isStoreup) {
        if (!this.storeupInfo || !this.storeupInfo.id) {
          this.$message.error('取消失败，无法获取收藏记录ID');
          return;
        }
        let delIds = new Array();
        delIds.push(this.storeupInfo.id);
        this.$http.post('storeup/delete', delIds).then(res => {
          if (res.data.code == 0) {
            this.isStoreup = false;
            this.storeupInfo = {}; // 清空信息
            this.$message({
              type: 'success',
              message: '取消成功!',
              duration: 1500,
            });
          }
        });
      }
    },
    getStoreupStatus() {
      if (localStorage.getItem("Token")) {
        this.$http.get('storeup/list', { params: { page: 1, limit: 1, type: 1, refid: this.detail.id, tablename: 'qichezixun', userid: localStorage.getItem('userid') } }).then(res => {
          if (res.data.code == 0 && res.data.data.list.length > 0) {
            this.isStoreup = true;
            this.storeupInfo = res.data.data.list[0];
          }
        });
      }
    },
    download(file) {
      if (!file) {
        this.$message({
          type: 'error',
          message: '文件不存在',
          duration: 1500,
        });
        return;
      }
      window.open(this.baseUrl + file)
    },
  },
  components: {
    CountDown
  }
}
</script>

<style lang="scss" scoped>
.detail-container {
  width: 100%;
  background-color: #f5f7fa;
  min-height: 100vh;
  padding-bottom: 40px;
}

.loading-state {
  padding: 50px;
  text-align: center;
  color: #999;
  font-size: 16px;
}

.breadcrumb-box {
  width: 1200px;
  margin: 0 auto;
  padding: 20px 0;
}

.main-content {
  width: 1200px;
  margin: 0 auto;
}

/* 顶部区域 */
.top-section {
  display: flex;
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  margin-bottom: 20px;
  gap: 30px;
}

.carousel-box {
  width: 50%;
  flex-shrink: 0;
  border-radius: 8px;
  overflow: hidden;

  /* 修复 el-carousel 内部可能的圆角溢出 */
  /deep/ .el-carousel__item {
    border-radius: 8px;
  }
}

.image-wrapper {
  width: 100%;
  height: 100%;
}

.info-box {
  flex: 1;
  display: flex;
  flex-direction: column;

  .title {
    font-size: 24px;
    color: #333;
    margin: 0 0 15px 0;
    line-height: 1.4;
    font-weight: 600;
  }

  .meta-row {
    display: flex;
    gap: 20px;
    color: #999;
    font-size: 14px;
    margin-bottom: 20px;

    .meta-item {
      display: flex;
      align-items: center;
      gap: 5px;
    }
  }

  .intro-box {
    background: #f8f9fa;
    padding: 15px;
    border-radius: 6px;
    margin-bottom: 20px;
    border-left: 4px solid #409EFF;

    .label {
      font-weight: bold;
      margin-bottom: 8px;
      color: #666;
    }

    .content {
      color: #666;
      line-height: 1.6;
      font-size: 14px;
      max-height: 150px;
      overflow-y: auto;
    }
  }

  .actions {
    margin-top: auto;
    display: flex;
    gap: 15px;

    .store-btn {
      display: inline-flex;
      align-items: center;
      gap: 6px;
      padding: 10px 25px;
      border-radius: 20px;
      cursor: pointer;
      font-size: 14px;
      transition: all 0.3s;
      border: 1px solid #dcdfe6;
      color: #606266;
      background: #fff;

      &:hover {
        border-color: #409EFF;
        color: #409EFF;
      }

      &.active {
        background: #fff;
        border-color: #ffba00;
        color: #ffba00;
      }

      i {
        font-size: 16px;
      }
    }
  }
}

/* 内容区域 */
.content-section {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  margin-bottom: 20px;
  overflow: hidden;

  /* 使用 /deep/ 替代 ::v-deep 以获得更好兼容性 */
  /deep/ .el-tabs--border-card {
    border: none;
    box-shadow: none;
  }

  /deep/ .el-tabs__header {
    background-color: #f5f7fa;
    border-bottom: 1px solid #e4e7ed;
  }

  /deep/ .el-tabs__item.is-active {
    color: #409EFF;
    font-weight: bold;
    border-top: 2px solid #409EFF; /* 增加选中态的顶部高亮 */
  }

  .rich-text {
    padding: 20px;
    min-height: 300px;
    line-height: 1.8;
    color: #333;

    /deep/ img {
      max-width: 100%;
      height: auto;
      display: block;
      margin: 10px auto;
    }
  }
}

/* 热门推荐 */
.hot-section {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  padding: 20px;

  .section-header {
    border-left: 4px solid #409EFF;
    padding-left: 10px;
    font-size: 18px;
    font-weight: bold;
    margin-bottom: 20px;
    color: #333;
  }

  .hot-list {
    display: flex;
    justify-content: space-between;
    gap: 20px;
  }

  .hot-item {
    flex: 1;
    cursor: pointer;
    transition: transform 0.3s;
    background: #fff;

    &:hover {
      transform: translateY(-5px);

      .hot-title {
        color: #409EFF;
      }
    }

    .img-box {
      width: 100%;
      height: 160px;
      border-radius: 6px;
      overflow: hidden;
      margin-bottom: 10px;
      background: #eee;
    }

    .info {
      .hot-title {
        font-size: 15px;
        color: #333;
        margin-bottom: 6px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        font-weight: 500;
      }

      .hot-meta {
        font-size: 12px;
        color: #999;
      }
    }
  }
}
</style>
