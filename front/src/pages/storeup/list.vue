<template>
  <div class="my-storeup-container">
    <div class="page-header">
      <div class="header-left">
        <span class="title">我的收藏</span>
        <el-button type="text" icon="el-icon-back" @click="backClick" class="back-link">返回个人中心</el-button>
      </div>

      <div class="header-right">
        <el-input
            v-model="formSearch.name"
            placeholder="搜索收藏名称..."
            size="medium"
            class="search-input"
            clearable
            @keyup.enter.native="getStoreupList(1)">
          <el-button slot="append" icon="el-icon-search" @click="getStoreupList(1)"></el-button>
        </el-input>
      </div>
    </div>

    <div class="storeup-grid">
      <el-row :gutter="25">
        <el-col :span="6" v-for="(item, index) in storeupList" :key="index">
          <div class="storeup-card" @click="toDetail(item)">
            <div class="card-img-box">
              <el-image
                  :src="item.picture ? (item.picture.startsWith('http') ? item.picture : baseUrl + item.picture) : ''"
                  fit="cover"
                  class="card-img"
              >
                <div slot="error" class="image-slot">
                  <i class="el-icon-picture-outline"></i>
                </div>
              </el-image>
              <div class="hover-overlay">
                <span>查看详情</span>
              </div>
            </div>
            <div class="card-info">
              <div class="card-title" :title="item.name">{{ item.name }}</div>
              <div class="card-desc">{{ item.remark || '暂无备注' }}</div>
            </div>
          </div>
        </el-col>
      </el-row>

      <el-empty v-if="storeupList.length === 0" description="暂无收藏记录"></el-empty>
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
import config from '@/config/config'
export default {
  data() {
    return {
      baseUrl: config.baseUrl,
      formSearch: {
        name: ''
      },
      storeupType: 1, // 默认为1
      storeupList: [],
      total: 1,
      pageSize: 8,
      pageSizes: [8, 16, 24, 32],
      totalPage: 1
    }
  },
  created() {
    // 防止 localStorage 中没有 storeupType 导致查询失败
    this.storeupType = localStorage.getItem('storeupType') ? localStorage.getItem('storeupType') : 1;
    this.getStoreupList(1);
  },
  methods: {
    backClick() {
      this.$router.push('/index/center')
    },
    getStoreupList(page) {
      let params = {
        page,
        limit: this.pageSize,
        type: this.storeupType,
        userid: localStorage.getItem('userid'),
        sort: "addtime",
        order: "desc"
      };
      let searchWhere = {};
      if (this.formSearch.name != '') searchWhere.name = '%' + this.formSearch.name + '%';

      this.$http.get('storeup/list', {params: Object.assign(params, searchWhere)}).then(res => {
        if (res.data.code == 0) {
          this.storeupList = res.data.data.list;
          this.total = res.data.data.total;
          this.pageSize = res.data.data.pageSize;
          this.pageSizes = [this.pageSize, this.pageSize*2, this.pageSize*3, this.pageSize*5];
          this.totalPage = res.data.data.totalPage;
        }
      });
    },
    curChange(page) {
      this.getStoreupList(page);
    },
    prevClick(page) {
      this.getStoreupList(page);
    },
    nextClick(page) {
      this.getStoreupList(page);
    },

    // 【核心修复部分】
    toDetail(item) {
      // 1. item.tablename 是 'qichexinxi'
      // 2. item.refid 是该汽车的真实 ID
      // 3. 将 refid 赋值给 id 参数传递给详情页
      this.$router.push({
        path: `/index/${item.tablename}Detail`,
        query: { id: item.refid }
      });
    }
  }
}
</script>

<style lang="scss" scoped>
.my-storeup-container {
  width: 1200px;
  margin: 20px auto;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.05);
  padding: 30px;
  min-height: 600px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;

  .header-left {
    display: flex;
    align-items: baseline;
    .title {
      font-size: 24px;
      font-weight: bold;
      color: #333;
      margin-right: 20px;
    }
    .back-link {
      color: #909399;
      font-size: 14px;
      &:hover { color: #409EFF; }
    }
  }

  .header-right {
    .search-input {
      width: 300px;
    }
  }
}

.storeup-grid {
  min-height: 400px;
}

.storeup-card {
  background: #fff;
  border-radius: 8px;
  border: 1px solid #ebeef5;
  overflow: hidden;
  margin-bottom: 25px;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 16px rgba(0,0,0,0.1);
    border-color: #409EFF;

    .card-img {
      transform: scale(1.05);
    }

    .hover-overlay {
      opacity: 1;
    }
  }

  .card-img-box {
    width: 100%;
    height: 180px;
    overflow: hidden;
    position: relative;
    background: #f5f7fa;

    .card-img {
      width: 100%;
      height: 100%;
      transition: transform 0.5s;
    }

    .hover-overlay {
      position: absolute;
      top: 0; left: 0; width: 100%; height: 100%;
      background: rgba(0,0,0,0.3);
      display: flex;
      justify-content: center;
      align-items: center;
      opacity: 0;
      transition: opacity 0.3s;

      span {
        color: #fff;
        border: 1px solid #fff;
        padding: 5px 15px;
        border-radius: 20px;
        font-size: 13px;
        background: rgba(0,0,0,0.2);
      }
    }
  }

  .card-info {
    padding: 15px;

    .card-title {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 8px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .card-desc {
      font-size: 12px;
      color: #909399;
      height: 20px;
      overflow: hidden;
    }
  }
}

.pagination-box {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  color: #909399;
  font-size: 24px;
}
</style>
