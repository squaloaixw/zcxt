<template>
  <div class="page-container">
    <div class="filter-container">
      <div class="category-tabs">
        <div
            class="tab-item"
            :class="{ active: curFenlei == '全部' }"
            @click="getList(1, '全部')"
        >全部车型</div>
        <div
            class="tab-item"
            v-for="(item, index) in fenlei"
            :key="index"
            :class="{ active: curFenlei == item }"
            @click="getList(1, item)"
        >{{ item }}</div>
      </div>

      <div class="search-bar">
        <el-form :inline="true" :model="formSearch" class="search-form">
          <el-form-item label="车牌号">
            <el-input v-model="formSearch.chepaihao" placeholder="请输入车牌号" size="small"></el-input>
          </el-form-item>
          <el-form-item label="品牌">
            <el-input v-model="formSearch.cheliangpinpai" placeholder="请输入车辆品牌" size="small"></el-input>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="formSearch.zhuangtai" placeholder="车辆状态" size="small" clearable>
              <el-option v-for="item in zhuangtaiOptions" :key="item" :label="item" :value="item"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="small" icon="el-icon-search" @click="getList(1, curFenlei)">查询</el-button>
            <el-button size="small" icon="el-icon-refresh" @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <div class="list-content">
      <div class="car-grid" v-if="dataList.length > 0">
        <div class="car-card" v-for="(item, index) in dataList" :key="index" @click="toDetail(item)">
          <div class="image-wrapper">
            <img :src="baseUrl + (item.cheliangzhaopian ? item.cheliangzhaopian.split(',')[0] : '')" alt="Vehicle Image">
            <div class="status-badge" :class="item.zhuangtai === '已出租' ? 'rented' : 'available'">
              {{ item.zhuangtai }}
            </div>
          </div>
          <div class="card-info">
            <h3 class="title">{{ item.cheliangpinpai }} {{ item.cheliangxinghao }}</h3>
            <div class="tags">
              <el-tag size="mini" type="info">{{ item.qicheleibie }}</el-tag>
              <el-tag size="mini" type="info" effect="plain">{{ item.huandangfangshi }}</el-tag>
            </div>
            <div class="price-row">
              <div class="price">
                <small>¥</small>
                <span>{{ item.qichejiage }}</span>
                <small>/天</small>
              </div>
              <el-button type="text" class="detail-btn">查看详情 <i class="el-icon-right"></i></el-button>
            </div>
          </div>
        </div>
      </div>
      <div class="empty-state" v-else>
        <i class="el-icon-truck"></i>
        <p>暂无相关车辆信息</p>
      </div>

      <el-pagination
          background
          layout="prev, pager, next"
          :total="total"
          :page-size="pageSize"
          @current-change="curChange"
          class="pagination"
      ></el-pagination>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      baseUrl: this.$config.baseUrl,
      formSearch: {
        chepaihao: '',
        qicheleibie: '',
        cheliangpinpai: '',
        zhuangtai: ''
      },
      fenlei: [],
      dataList: [],
      total: 0,
      pageSize: 12,
      curFenlei: '全部',
      zhuangtaiOptions: ['未出租', '已出租']
    };
  },
  created() {
    // 接收首页传来的搜索参数
    if (this.$route.query.cheliangxinghao) {
      this.formSearch.cheliangpinpai = this.$route.query.cheliangxinghao; // 简单映射到品牌或型号搜索
    }
    this.getFenlei();
    this.getList(1, '全部');
  },
  methods: {
    getFenlei() {
      this.$http.get('option/qicheleibie/qicheleibie').then(res => {
        if (res.data.code == 0) this.fenlei = res.data.data;
      });
    },
    getList(page, fenlei) {
      this.curFenlei = fenlei;
      let params = {
        page,
        limit: this.pageSize,
        chepaihao: this.formSearch.chepaihao ? '%' + this.formSearch.chepaihao + '%' : undefined,
        cheliangpinpai: this.formSearch.cheliangpinpai ? '%' + this.formSearch.cheliangpinpai + '%' : undefined,
        zhuangtai: this.formSearch.zhuangtai ? this.formSearch.zhuangtai : undefined,
      };
      if (fenlei !== '全部') {
        params.qicheleibie = fenlei;
      }
      if (this.formSearch.qicheleibie) {
        params.qicheleibie = this.formSearch.qicheleibie;
      }

      this.$http.get('qichexinxi/list', { params }).then(res => {
        if (res.data.code == 0) {
          this.dataList = res.data.data.list;
          this.total = res.data.data.total;
        }
      });
    },
    resetSearch() {
      this.formSearch = {
        chepaihao: '',
        qicheleibie: '',
        cheliangpinpai: '',
        zhuangtai: ''
      };
      this.getList(1, '全部');
    },
    curChange(page) {
      this.getList(page, this.curFenlei);
    },
    toDetail(item) {
      this.$router.push({ path: '/index/qichexinxiDetail', query: { id: item.id } });
    }
  }
};
</script>

<style lang="scss" scoped>
.page-container {
  width: 1200px;
  margin: 0 auto;
  padding: 20px 0;
}

.breadcrumb-container {
  margin-bottom: 20px;
  font-size: 14px;
}

.filter-container {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.05);
  margin-bottom: 20px;
  padding: 20px;

  .category-tabs {
    display: flex;
    flex-wrap: wrap;
    border-bottom: 1px solid #f0f0f0;
    padding-bottom: 15px;
    margin-bottom: 15px;

    .tab-item {
      padding: 6px 16px;
      margin-right: 10px;
      margin-bottom: 10px;
      cursor: pointer;
      border-radius: 20px;
      font-size: 14px;
      color: #666;
      transition: all 0.3s;
      background: #f5f7fa;

      &:hover, &.active {
        background: #00c292;
        color: #fff;
      }
    }
  }

  .search-form {
    display: flex;
    flex-wrap: wrap;
    .el-form-item {
      margin-bottom: 0;
      margin-right: 20px;
    }
  }
}

.list-content {
  min-height: 500px;

  .car-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
  }

  .car-card {
    background: #fff;
    border-radius: 8px;
    overflow: hidden;
    cursor: pointer;
    transition: all 0.3s;
    border: 1px solid #eef0f4;

    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 10px 20px rgba(0,0,0,0.08);
    }

    .image-wrapper {
      height: 180px;
      position: relative;
      overflow: hidden;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }

      .status-badge {
        position: absolute;
        top: 10px;
        right: 10px;
        padding: 2px 8px;
        border-radius: 4px;
        font-size: 12px;
        color: #fff;

        &.available { background: #00c292; }
        &.rented { background: #909399; }
      }
    }

    .card-info {
      padding: 16px;

      .title {
        font-size: 16px;
        color: #333;
        margin-bottom: 8px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }

      .tags {
        margin-bottom: 12px;
        .el-tag { margin-right: 5px; }
      }

      .price-row {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .price {
          color: #ff5000;
          font-weight: bold;
          span { font-size: 20px; }
          small { font-size: 12px; }
        }

        .detail-btn {
          color: #00c292;
          padding: 0;
        }
      }
    }
  }

  .empty-state {
    text-align: center;
    padding: 100px 0;
    color: #999;
    i {
      font-size: 48px;
      margin-bottom: 20px;
      color: #e0e0e0;
    }
  }

  .pagination {
    margin-top: 30px;
    text-align: center;
    /deep/ .el-pagination.is-background .el-pager li:not(.disabled).active {
      background-color: #00c292;
    }
  }
}
</style>
