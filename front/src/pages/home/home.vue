<template>
  <div class="home-content">
    <div class="banner-section">
      <el-carousel trigger="click" height="600px" arrow="always" :interval="5000">
        <el-carousel-item v-for="(item, index) in swiperList" :key="index">
          <div class="banner-image"
               :style="{ backgroundImage: 'url(' + (item.img.startsWith('http') ? item.img : baseUrl + item.img) + ')' }">
            <div class="banner-mask">
              <div class="banner-text">
                <h1>{{ item.title }}</h1>
                <p>{{ item.desc }}</p>
              </div>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>

      <div class="search-box-float">
        <div class="search-inner">
          <el-input v-model="searchKeyword" placeholder="请输入想要查找的车型或品牌..." class="search-input">
            <i slot="prefix" class="el-icon-search"></i>
          </el-input>
          <el-button type="primary" @click="handleSearch">搜索好车</el-button>
        </div>
      </div>
    </div>

    <div class="section-container">
      <div class="section-header">
        <h2>热门推荐</h2>
        <span class="subtitle">精选优质好车，伴您舒适出行</span>
        <span class="more-btn" @click="$router.push('/index/qichexinxi')">查看更多 <i class="el-icon-arrow-right"></i></span>
      </div>

      <div class="car-grid">
        <div class="car-card" v-for="(item, index) in carList" :key="index" @click="toDetail(item.id)">
          <div class="card-img-wrapper">
            <img :src="item.cheliangzhaopian ? baseUrl + item.cheliangzhaopian.split(',')[0] : require('@/assets/logo.png')" alt="车辆图片">
            <div class="card-tag">{{ item.zhuangtai }}</div>
          </div>
          <div class="card-body">
            <h3 class="car-title">{{ item.cheliangpinpai }} {{ item.cheliangxinghao }}</h3>
            <div class="car-tags">
              <span>{{ item.huandangfangshi }}</span>
              <span class="divider">|</span>
              <span>{{ item.zuoweishu }}座</span>
            </div>
            <div class="car-price">
              <span class="currency">¥</span>
              <span class="amount">{{ item.qichejiage }}</span>
              <span class="unit">/天</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="section-container bg-gray">
      <div class="section-header">
        <h2>最新资讯</h2>
        <span class="subtitle">行业动态与用车指南</span>
      </div>
      <div class="news-grid">
        <div class="news-item" v-for="(item, index) in newsList" :key="index" @click="$router.push('/index/newsDetail?id='+item.id)">
          <div class="news-date">
            <span class="day">{{ item.addtime ? item.addtime.substring(8,10) : '01' }}</span>
            <span class="month">{{ item.addtime ? item.addtime.substring(5,7) : '01' }}月</span>
          </div>
          <div class="news-content">
            <h4>{{ item.title }}</h4>
            <p>{{ item.introduction }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      baseUrl: this.$config.baseUrl, // 确保 config.js 中 baseUrl 配置正确
      searchKeyword: '',
      // 默认静态数据，防止后台没数据时页面空白
      swiperList: [
        {
          img: 'https://img.zcool.cn/community/01fca55cd3886ea801214168c7e997.jpg@1280w_1l_2o_100sh.jpg',
          title: '尊享品质出行',
          desc: '豪华座驾，尽在掌握'
        },
        {
          img: 'https://img.zcool.cn/community/0196e05d5b6826a8012187f4a2050f.jpg@1280w_1l_2o_100sh.jpg',
          title: '自驾游首选',
          desc: '宽敞空间，承载全家欢乐'
        }
      ],
      carList: [],
      newsList: []
    }
  },
  mounted() {
    this.getSwiperList(); // 获取后台配置的轮播图
    this.getCarList();
    this.getNewsList();
  },
  methods: {
    // 获取轮播图逻辑
    getSwiperList() {
      this.$http.get('config/list', {
        params: {
          page: 1,
          limit: 5
        }
      }).then(res => {
        if (res.data.code === 0 && res.data.data.list.length > 0) {
          let list = res.data.data.list;
          let newSwiper = [];
          list.forEach(item => {
            // 这里判断 value 是否存在且不为空，通常 config 表存的是 value
            if (item.name.indexOf('picture') >= 0 && item.value) {
              newSwiper.push({
                img: item.value, // 后台取出的通常是文件名，template里会拼接 baseUrl
                title: '优质服务',
                desc: '安全 · 便捷 · 舒适'
              });
            }
          });
          // 如果后台有有效图片，才覆盖默认数据
          if (newSwiper.length > 0) {
            this.swiperList = newSwiper;
          }
        }
      });
    },
    getCarList() {
      this.$http.get('qichexinxi/list', {
        params: {
          page: 1,
          limit: 8,
          // zhuangtai: '可租'
        }
      }).then(res => {
        if (res.data.code === 0) {
          this.carList = res.data.data.list;
        }
      });
    },
    getNewsList() {
      this.$http.get('news/list', {
        params: { page: 1, limit: 3 }
      }).then(res => {
        if (res.data.code === 0) {
          this.newsList = res.data.data.list;
        }
      });
    },
    handleSearch() {
      this.$router.push({ path: '/index/qichexinxi', query: { cheliangxinghao: this.searchKeyword } });
    },
    toDetail(id) {
      this.$router.push(`/index/qichexinxiDetail?id=${id}`);
    }
  }
}
</script>

<style lang="scss" scoped>
.home-content {
  background: #fff;
}

/* Banner 样式 */
.banner-section {
  position: relative;

  .banner-image {
    width: 100%;
    height: 100%;
    background-size: cover;
    background-position: center;
    position: relative;
  }

  .banner-mask {
    position: absolute;
    top: 0; left: 0; width: 100%; height: 100%;
    background: rgba(0,0,0,0.3); /* 半透明黑遮罩，确保文字可见 */
    display: flex;
    align-items: center;
    justify-content: center;
    text-align: center;
    color: #fff;

    h1 {
      font-size: 48px;
      margin-bottom: 20px;
      letter-spacing: 2px;
      text-shadow: 0 2px 10px rgba(0,0,0,0.5);
    }
    p {
      font-size: 24px;
      opacity: 0.95;
      text-shadow: 0 2px 5px rgba(0,0,0,0.5);
    }
  }
}

/* 悬浮搜索条 */
.search-box-float {
  position: absolute;
  bottom: 40px; /* 调整位置，避免太靠下 */
  left: 50%;
  transform: translateX(-50%);
  z-index: 100;
  width: 800px;
  background: rgba(255, 255, 255, 0.95);
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.15);
  backdrop-filter: blur(10px);

  .search-inner {
    display: flex;
    gap: 15px;

    .search-input {
      flex: 1;
      /deep/ .el-input__inner {
        height: 50px;
        border: 1px solid #eee;
        background: #f9f9f9;
        font-size: 16px;
        padding-left: 40px;

        &:focus {
          border-color: #00c292;
          background: #fff;
        }
      }
      /deep/ .el-input__prefix {
        left: 10px;
        i { font-size: 20px; line-height: 50px; }
      }
    }

    .el-button {
      width: 140px;
      font-size: 16px;
      background-color: #00c292;
      border-color: #00c292;
      font-weight: bold;
      transition: all 0.3s;

      &:hover {
        background-color: #0ba360;
        border-color: #0ba360;
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0, 194, 146, 0.4);
      }
    }
  }
}

/* 通用区块 */
.section-container {
  width: 1200px;
  margin: 0 auto;
  padding: 80px 0;

  &.bg-gray {
    background: #f9f9f9;
    width: 100%;

    .section-header, .news-grid {
      width: 1200px;
      margin: 0 auto;
    }
  }

  .section-header {
    text-align: center;
    margin-bottom: 50px;
    position: relative;

    h2 {
      font-size: 32px;
      color: #333;
      margin-bottom: 10px;
      font-weight: 700;
    }
    .subtitle {
      color: #999;
      font-size: 16px;
    }
    .more-btn {
      position: absolute;
      right: 0;
      top: 10px;
      color: #666;
      cursor: pointer;
      transition: color 0.3s;
      font-size: 14px;
      &:hover { color: #00c292; }
    }
  }
}

/* 汽车网格卡片 */
.car-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;

  .car-card {
    background: #fff;
    border-radius: 12px;
    overflow: hidden;
    cursor: pointer;
    transition: all 0.3s ease;
    border: 1px solid #f0f0f0;

    &:hover {
      transform: translateY(-8px);
      box-shadow: 0 12px 32px rgba(0,0,0,0.08);

      .card-img-wrapper img {
        transform: scale(1.08);
      }
    }

    .card-img-wrapper {
      height: 220px;
      overflow: hidden;
      position: relative;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        transition: transform 0.5s ease;
      }

      .card-tag {
        position: absolute;
        top: 12px;
        left: 12px;
        background: rgba(0,0,0,0.6);
        backdrop-filter: blur(4px);
        color: #fff;
        padding: 4px 12px;
        font-size: 12px;
        border-radius: 20px;
      }
    }

    .card-body {
      padding: 20px;

      .car-title {
        font-size: 18px;
        color: #333;
        margin-bottom: 12px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        font-weight: 600;
      }

      .car-tags {
        color: #999;
        font-size: 13px;
        margin-bottom: 16px;
        display: flex;
        align-items: center;
        .divider { margin: 0 8px; color: #eee; }
      }

      .car-price {
        color: #ff5000;
        font-weight: 700;
        display: flex;
        align-items: baseline;

        .currency { font-size: 14px; margin-right: 2px; }
        .amount { font-size: 26px; }
        .unit { font-size: 12px; color: #999; margin-left: 2px; font-weight: 400; }
      }
    }
  }
}

/* 资讯列表 */
.news-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 30px;

  .news-item {
    background: #fff;
    padding: 30px;
    display: flex;
    align-items: flex-start;
    border-radius: 12px;
    cursor: pointer;
    transition: all 0.3s;
    border: 1px solid #eee;

    &:hover {
      box-shadow: 0 10px 30px rgba(0,0,0,0.06);
      transform: translateY(-5px);
      border-color: #fff;
      .news-content h4 { color: #00c292; }
    }

    .news-date {
      background: #e6f9f4;
      color: #00c292;
      padding: 12px 10px;
      text-align: center;
      border-radius: 8px;
      margin-right: 20px;
      min-width: 60px;

      .day { display: block; font-size: 22px; font-weight: bold; line-height: 1.2; }
      .month { font-size: 12px; display: block; }
    }

    .news-content {
      flex: 1;
      h4 {
        font-size: 16px;
        color: #333;
        margin-bottom: 12px;
        transition: color 0.3s;
        line-height: 1.4;
        display: -webkit-box; -webkit-line-clamp: 1; -webkit-box-orient: vertical; overflow: hidden;
      }
      p {
        font-size: 13px;
        color: #888;
        line-height: 1.6;
        display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden;
      }
    }
  }
}

/* 移动端适配 */
@media (max-width: 1250px) {
  .section-container, .section-container.bg-gray .section-header, .section-container.bg-gray .news-grid {
    width: 95%;
  }
  .car-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .news-grid {
    grid-template-columns: 1fr;
  }
  .search-box-float {
    width: 90%;
  }
}
</style>
