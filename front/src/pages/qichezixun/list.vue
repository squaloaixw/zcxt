<template>
  <div class="news-list-container">
    <div class="page-banner">
      <div class="banner-inner">
        <h1>汽车资讯</h1>
        <p>掌握一手行业动态，尽享驾趣生活</p>
      </div>
    </div>

    <div class="search-bar-wrapper">
      <div class="search-box">
        <el-input v-model="formSearch.biaoti" placeholder="搜索感兴趣的资讯标题..." class="search-input" clearable @clear="getList(1)">
          <i slot="prefix" class="el-icon-search"></i>
        </el-input>
        <el-button type="primary" @click="getList(1)">搜索</el-button>
      </div>
    </div>

    <div class="content-wrapper">
      <div class="news-list" v-if="dataList.length > 0">
        <div class="news-card" v-for="(item, index) in dataList" :key="index" @click="toDetail(item.id)">
          <div class="img-box">
            <img :src="item.fengmian ? baseUrl + item.fengmian.split(',')[0] : require('@/assets/logo.png')" alt="封面">
          </div>
          <div class="text-box">
            <h3 class="news-title">{{ item.biaoti }}</h3>
            <div class="news-meta">
              <span class="date"><i class="el-icon-time"></i> {{ item.addtime ? item.addtime.split(' ')[0] : '' }}</span>
              <span class="source" v-if="item.laiyuan"><i class="el-icon-collection-tag"></i> {{ item.laiyuan }}</span>
              <span class="clicks"><i class="el-icon-view"></i> {{ item.clicknum || 0 }}</span>
            </div>
            <div class="news-intro">
              {{ stripHtml(item.neirong).substring(0, 120) }}...
            </div>
            <div class="read-more">
              <span>阅读全文 <i class="el-icon-arrow-right"></i></span>
            </div>
          </div>
        </div>
      </div>

      <div class="empty-state" v-else>
        <i class="el-icon-document-remove"></i>
        <p>暂无相关资讯</p>
      </div>

      <div class="pagination-box">
        <el-pagination
            background
            layout="prev, pager, next"
            :total="total"
            :page-size="pageSize"
            @current-change="curChange"
        ></el-pagination>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      baseUrl: this.$config.baseUrl,
      formSearch: {
        biaoti: ''
      },
      dataList: [],
      total: 0,
      pageSize: 8,
    };
  },
  created() {
    this.getList(1);
  },
  methods: {
    getList(page) {
      let params = {
        page,
        limit: this.pageSize,
        biaoti: this.formSearch.biaoti ? '%' + this.formSearch.biaoti + '%' : undefined,
        sort: 'addtime',
        order: 'desc'
      };
      this.$http.get('qichezixun/list', { params }).then(res => {
        if (res.data.code == 0) {
          this.dataList = res.data.data.list;
          this.total = res.data.data.total;
        }
      });
    },
    curChange(page) {
      this.getList(page);
    },
    toDetail(id) {
      this.$router.push({ path: '/index/qichezixunDetail', query: { id } });
    },
    // 简单的去除HTML标签工具函数
    stripHtml(html) {
      if (!html) return '';
      return html.replace(/<[^>]+>/g, "");
    }
  }
};
</script>

<style lang="scss" scoped>
.news-list-container {
  background: #f7f8fa;
  min-height: 100vh;
}

.page-banner {
  height: 240px;
  background: linear-gradient(135deg, #00c292 0%, #0b417c 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  text-align: center;

  .banner-inner {
    h1 { font-size: 36px; margin-bottom: 10px; letter-spacing: 2px; }
    p { font-size: 16px; opacity: 0.8; font-weight: 300; }
  }
}

.search-bar-wrapper {
  width: 1200px;
  margin: -30px auto 30px;
  position: relative;
  z-index: 10;

  .search-box {
    background: #fff;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0,0,0,0.08);
    display: flex;
    gap: 15px;

    .search-input {
      /deep/ .el-input__inner { border: 1px solid #eee; height: 45px; }
    }

    .el-button {
      width: 120px;
      font-size: 16px;
      background-color: #00c292;
      border-color: #00c292;
      &:hover { background-color: #0ba360; border-color: #0ba360; }
    }
  }
}

.content-wrapper {
  width: 1200px;
  margin: 0 auto;
  padding-bottom: 50px;
}

.news-list {
  display: flex;
  flex-direction: column;
  gap: 20px;

  .news-card {
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    display: flex;
    transition: all 0.3s;
    cursor: pointer;
    box-shadow: 0 1px 3px rgba(0,0,0,0.05);

    &:hover {
      transform: translateY(-3px);
      box-shadow: 0 8px 20px rgba(0,0,0,0.08);
      .news-title { color: #00c292; }
      .img-box img { transform: scale(1.05); }
    }

    .img-box {
      width: 280px;
      height: 180px;
      overflow: hidden;
      border-radius: 6px;
      margin-right: 25px;
      flex-shrink: 0;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        transition: transform 0.5s ease;
      }
    }

    .text-box {
      flex: 1;
      display: flex;
      flex-direction: column;

      .news-title {
        font-size: 20px;
        color: #333;
        margin-bottom: 12px;
        transition: color 0.3s;
      }

      .news-meta {
        font-size: 13px;
        color: #999;
        margin-bottom: 15px;
        display: flex;
        gap: 20px;

        span { display: flex; align-items: center; gap: 5px; }
      }

      .news-intro {
        font-size: 14px;
        color: #666;
        line-height: 1.6;
        flex: 1;
        margin-bottom: 15px;
        text-align: justify;
      }

      .read-more {
        text-align: right;
        font-size: 14px;
        color: #00c292;
        font-weight: 500;
      }
    }
  }
}

.empty-state {
  text-align: center;
  padding: 80px 0;
  color: #ccc;
  i { font-size: 48px; margin-bottom: 10px; }
}

.pagination-box {
  margin-top: 40px;
  text-align: center;

  /deep/ .el-pagination.is-background .el-pager li:not(.disabled).active {
    background-color: #00c292;
  }
}

@media (max-width: 1250px) {
  .search-bar-wrapper, .content-wrapper { width: 95%; }
  .news-card { flex-direction: column; }
  .news-card .img-box { width: 100%; height: 200px; margin-right: 0; margin-bottom: 15px; }
}
</style>
