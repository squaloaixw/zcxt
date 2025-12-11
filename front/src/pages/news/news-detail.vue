<template>
  <div class="news-detail-container">
    <div class="breadcrumb-box">
      <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item :to="{ path: '/index/home' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/index/news' }">公告信息</el-breadcrumb-item>
        <el-breadcrumb-item>正文</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="news-card">
      <div class="news-header">
        <h1 class="news-title">{{ detail.title }}</h1>
        <div class="news-meta">
          <span class="meta-item"><i class="el-icon-time"></i> 发布时间：{{ detail.addtime }}</span>
          <span class="meta-item" v-if="detail.clicknum"><i class="el-icon-view"></i> 阅读：{{ detail.clicknum }}</span>
          <span class="meta-item"><i class="el-icon-user"></i> 发布人：管理员</span>
        </div>
      </div>

      <el-divider></el-divider>

      <div class="news-cover" v-if="detail.picture">
        <img :src="baseUrl + detail.picture" alt="封面图">
      </div>

      <div class="news-content ql-editor" v-html="detail.content"></div>

      <div class="news-footer">
        <el-button @click="$router.go(-1)" icon="el-icon-back">返回列表</el-button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      detail: {},
      baseUrl: this.$config.baseUrl
    }
  },
  created() {
    this.detail.id = this.$route.query.id;
    this.init();
  },
  methods: {
    init() {
      this.$http.get(`news/detail/${this.detail.id}`).then(res => {
        if (res.data.code == 0) {
          this.detail = res.data.data;
          // 增加点击量(可选)
          // this.addClickNum();
        }
      });
    }
  }
}
</script>

<style lang="scss" scoped>
.news-detail-container {
  width: 1000px; /* 稍微窄一点，提升阅读体验 */
  margin: 30px auto;
  min-height: 600px;
}

.breadcrumb-box {
  margin-bottom: 20px;
}

.news-card {
  background: #fff;
  padding: 50px 80px;
  border-radius: 8px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
}

.news-header {
  text-align: center;
  margin-bottom: 30px;

  .news-title {
    font-size: 32px;
    color: #333;
    font-weight: 600;
    margin-bottom: 20px;
    line-height: 1.4;
  }

  .news-meta {
    color: #999;
    font-size: 14px;

    .meta-item {
      margin: 0 15px;
      i { margin-right: 5px; }
    }
  }
}

.news-cover {
  text-align: center;
  margin: 30px 0;
  img {
    max-width: 100%;
    max-height: 400px;
    border-radius: 4px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  }
}

.news-content {
  font-size: 16px;
  color: #444;
  line-height: 1.8;
  padding: 20px 0;
  min-height: 300px;

  /* 深度选择器，防止v-html里的图片溢出 */
  /deep/ img {
    max-width: 100%;
    height: auto;
    display: block;
    margin: 20px auto;
    border-radius: 4px;
  }

  /deep/ p {
    margin-bottom: 1.5em;
  }
}

.news-footer {
  text-align: center;
  margin-top: 50px;
  padding-top: 30px;
  border-top: 1px solid #eee;
}

/* 响应式适配 */
@media (max-width: 768px) {
  .news-detail-container {
    width: 95%;
  }
  .news-card {
    padding: 30px 20px;
  }
  .news-header .news-title {
    font-size: 24px;
  }
}
</style>
