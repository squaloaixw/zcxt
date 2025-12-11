<template>
  <div class="news-page">
    <div class="page-header">
      <div class="search-box">
        <el-input
            v-model="title"
            placeholder="搜索公告标题..."
            class="search-input"
            @keyup.enter.native="getNewsList(1)">
          <el-button slot="append" icon="el-icon-search" @click="getNewsList(1)">查询</el-button>
        </el-input>
      </div>
    </div>

    <div class="news-container">
      <div class="news-list">
        <div
            class="news-card"
            v-for="item in newsList"
            :key="item.id"
            @click="toNewsDetail(item)"
        >
          <div class="news-img-box">
            <el-image
                :src="baseUrl + item.picture"
                fit="cover"
                class="news-img"
            >
              <div slot="error" class="image-slot">
                <i class="el-icon-picture-outline"></i>
              </div>
            </el-image>
          </div>

          <div class="news-content">
            <div class="news-title" :title="item.title">{{item.title}}</div>
            <div class="news-desc">{{item.introduction}}</div>
            <div class="news-footer">
              <span class="news-tag">公告</span>
              <span class="read-more">查看详情 <i class="el-icon-right"></i></span>
            </div>
          </div>
        </div>

        <el-empty v-if="newsList.length === 0" description="暂无公告信息"></el-empty>
      </div>

      <div class="pagination-wrapper">
        <el-pagination
            background
            @current-change="curChange"
            @prev-click="prevClick"
            @next-click="nextClick"
            :current-page="1"
            :page-sizes="pageSizes"
            :page-size="pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total">
        </el-pagination>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      baseUrl: this.$config.baseUrl,
      breadcrumbItem: [
        { name: '公告信息' }
      ],
      newsList: [],
      total: 1,
      pageSize: 10,
      pageSizes: [10, 20, 30, 50],
      totalPage: 1,
      title: '',
      formSearch: {} // 为了兼容之前的引用，虽然这里直接用了title
    }
  },
  created() {
    this.getNewsList(1);
  },
  methods: {
    getNewsList(page) {
      let params = {page, limit: this.pageSize};
      let searchWhere = {};
      if(this.title != '') searchWhere.title = '%' + this.title + '%';
      this.$http.get('news/list', {params: Object.assign(params, searchWhere)}).then(res => {
        if (res.data.code == 0) {
          this.newsList = res.data.data.list;
          this.total = res.data.data.total;
          this.pageSize = res.data.data.pageSize;
          this.pageSizes = [this.pageSize, this.pageSize*2, this.pageSize*3, this.pageSize*5];
          this.totalPage = res.data.data.totalPage;
        }
      });
    },
    curChange(page) {
      this.getNewsList(page);
    },
    prevClick(page) {
      this.getNewsList(page);
    },
    nextClick(page) {
      this.getNewsList(page);
    },
    // --- 修复重点：修改了跳转传参方式 ---
    toNewsDetail(item) {
      // 之前是 query: { detailObj: JSON.stringify(item) }
      // 现在改为传递 id，与 news-detail.vue 的接收方式匹配
      this.$router.push({path: '/index/newsDetail', query: { id: item.id }});
    }
  }
}
</script>

<style lang="scss" scoped>
.news-page {
  background-color: #f4f6f9;
  min-height: 100vh;
  padding-bottom: 40px;
}

.page-header {
  background: #fff;
  padding: 20px 0;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.05);
  margin-bottom: 30px;

  .breadcrumb-box {
    width: 1200px;
    margin: 0 auto 20px;
  }

  .search-box {
    width: 1200px;
    margin: 0 auto;
    display: flex;
    justify-content: center;

    .search-input {
      width: 500px;
    }
  }
}

.news-container {
  width: 1200px;
  margin: 0 auto;
}

.news-list {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
}

.news-card {
  width: 49%; /* 两列布局 */
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 20px;
  display: flex;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.05);
  height: 180px;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 10px 20px rgba(0,0,0,0.1);

    .news-title {
      color: #409EFF;
    }

    .read-more {
      color: #409EFF;
    }
  }

  .news-img-box {
    width: 240px;
    height: 100%;
    overflow: hidden;
    flex-shrink: 0;

    .news-img {
      width: 100%;
      height: 100%;
      transition: transform 0.5s;
    }
  }

  &:hover .news-img {
    transform: scale(1.05);
  }

  .news-content {
    padding: 20px;
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    overflow: hidden;

    .news-title {
      font-size: 18px;
      font-weight: bold;
      color: #333;
      margin-bottom: 10px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      transition: color 0.3s;
    }

    .news-desc {
      font-size: 14px;
      color: #666;
      line-height: 1.6;
      height: 66px; /* 控制显示行数 */
      overflow: hidden;
      display: -webkit-box;
      -webkit-line-clamp: 3;
      -webkit-box-orient: vertical;
      margin-bottom: 10px;
    }

    .news-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .news-tag {
        background: #f0f9eb;
        color: #67c23a;
        padding: 2px 8px;
        border-radius: 4px;
        font-size: 12px;
        border: 1px solid #e1f3d8;
      }

      .read-more {
        font-size: 13px;
        color: #909399;
        display: flex;
        align-items: center;
        transition: color 0.3s;

        i {
          margin-left: 4px;
        }
      }
    }
  }
}

.pagination-wrapper {
  margin-top: 30px;
  text-align: center;
  padding-bottom: 20px;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
  font-size: 30px;
}
</style>
