<template>
  <div class="forum-container">
    <div class="breadcrumb-box">
      <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item :to="{ path: '/index/home' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>交流论坛</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="forum-header">
      <div class="section-title">汽车论坛</div>
      <div class="header-decoration"></div>
    </div>

    <div class="filter-box">
      <el-form :inline="true" :model="formSearch" class="search-form">
        <el-form-item label="">
          <el-input v-model="title" placeholder="请输入帖子标题关键词" prefix-icon="el-icon-search" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getForumList(1)">查询</el-button>
          <el-button type="success" icon="el-icon-edit" @click="toForumAdd">发布新帖</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="forum-list">
      <div class="forum-item" v-for="item in forumList" :key="item.id" @click="toForumDetail(item)">
        <div class="item-main">
          <div class="item-icon">
            <i class="el-icon-chat-dot-round"></i>
          </div>
          <div class="item-content">
            <div class="item-title">{{item.title}}</div>
            <div class="item-meta">
              <span class="meta-tag"><i class="el-icon-user"></i> {{item.username}}</span>
              <span class="meta-tag"><i class="el-icon-time"></i> {{item.addtime}}</span>
            </div>
          </div>
        </div>
        <div class="item-arrow">
          <i class="el-icon-arrow-right"></i>
        </div>
      </div>

      <el-empty v-if="forumList.length === 0" description="暂无帖子"></el-empty>
    </div>

    <div class="pagination-box">
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
</template>

<script>
export default {
  data() {
    return {
      title: '',
      layouts: '',
      forumList: [],
      total: 1,
      pageSize: 10,
      pageSizes: [10, 20, 30, 50],
      totalPage: 1,
      formSearch: {}
    }
  },
  created() {
    this.getForumList(1);
  },
  methods: {
    getForumList(page) {
      let params = {page, limit: this.pageSize, isdone: '开放', sort: 'addtime', order: 'desc'};
      let searchWhere = {};
      if(this.title != '') searchWhere.title = '%' + this.title + '%';
      this.$http.get('forum/flist', {params: Object.assign(params, searchWhere)}).then(res => {
        if (res.data.code == 0) {
          this.forumList = res.data.data.list;
          this.total = res.data.data.total;
          this.pageSize = res.data.data.pageSize;
          this.pageSizes = [this.pageSize, this.pageSize*2, this.pageSize*3, this.pageSize*5];
          this.totalPage = res.data.data.totalPage;
        }
      });
    },
    curChange(page) {
      this.getForumList(page);
    },
    prevClick(page) {
      this.getForumList(page);
    },
    nextClick(page) {
      this.getForumList(page);
    },
    toForumAdd() {
      this.$router.push('/index/forumAdd');
    },
    toForumDetail(item) {
      this.$router.push({path: '/index/forumDetail', query: {id: item.id, detailObj: JSON.stringify(item)}});
    }
  }
}
</script>

<style lang="scss" scoped>
.forum-container {
  width: 1200px;
  margin: 30px auto;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  padding-bottom: 30px;
  position: relative;
}

.breadcrumb-box {
  padding: 20px 30px;
  background: #f9f9f9;
  border-bottom: 1px solid #eee;
}

.forum-header {
  position: relative;
  background: linear-gradient(135deg, #409EFF 0%, #2c82df 100%);
  padding: 40px 0;
  text-align: center;
  color: #fff;

  .section-title {
    font-size: 32px;
    font-weight: 600;
    letter-spacing: 2px;
    text-shadow: 0 2px 4px rgba(0,0,0,0.1);
  }

  .header-decoration {
    width: 60px;
    height: 4px;
    background: rgba(255,255,255,0.4);
    margin: 15px auto 0;
    border-radius: 2px;
  }
}

.filter-box {
  padding: 25px 30px;
  background: #fcfcfc;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  justify-content: flex-end;

  .search-form {
    display: flex;
    align-items: center;
  }
}

.forum-list {
  padding: 20px 30px;
  min-height: 400px;
}

.forum-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  margin-bottom: 15px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  background: #fff;
  transition: all 0.3s ease;
  cursor: pointer;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 5px 15px rgba(0,0,0,0.05);
    border-color: #409EFF;

    .item-icon {
      background: #409EFF;
      color: #fff;
    }

    .item-arrow {
      color: #409EFF;
      transform: translateX(5px);
    }

    .item-title {
      color: #409EFF;
    }
  }

  .item-main {
    display: flex;
    align-items: center;
    flex: 1;

    .item-icon {
      width: 50px;
      height: 50px;
      border-radius: 50%;
      background: #f0f2f5;
      color: #909399;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 24px;
      margin-right: 20px;
      transition: all 0.3s;
      flex-shrink: 0;
    }

    .item-content {
      .item-title {
        font-size: 18px;
        color: #303133;
        font-weight: 600;
        margin-bottom: 8px;
        transition: color 0.3s;
      }

      .item-meta {
        font-size: 13px;
        color: #909399;

        .meta-tag {
          margin-right: 20px;
          display: inline-flex;
          align-items: center;

          i {
            margin-right: 5px;
          }
        }
      }
    }
  }

  .item-arrow {
    color: #c0c4cc;
    font-size: 20px;
    transition: all 0.3s;
  }
}

.pagination-box {
  display: flex;
  justify-content: center;
  padding-top: 20px;
}
</style>
