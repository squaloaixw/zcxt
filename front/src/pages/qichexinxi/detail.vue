<template>
  <div class="detail-page-container">
    <div class="breadcrumb-box">
      <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item :to="{ path: '/index/home' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/index/qichexinxi' }">汽车信息</el-breadcrumb-item>
        <el-breadcrumb-item>详情</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="main-content-box">
      <el-row :gutter="30">
        <el-col :span="12">
          <div class="gallery-wrapper">
            <el-carousel trigger="click" indicator-position="outside" height="450px">
              <el-carousel-item v-for="(item, index) in swiperList" :key="index">
                <div class="carousel-img-box">
                  <img :src="getImageUrl(item)" alt="车辆照片" class="carousel-img">
                </div>
              </el-carousel-item>
            </el-carousel>
          </div>
        </el-col>

        <el-col :span="12">
          <div class="info-wrapper">
            <div class="info-header">
              <h1 class="title">{{ detail.cheliangpinpai }} {{ detail.cheliangxinghao }}</h1>
              <el-button class="consult-btn" type="text" icon="el-icon-chat-line-square" @click="toComment">用户评论</el-button>
            </div>

            <div class="price-status-block">
              <div class="price-box">
                <span class="currency">¥</span>
                <span class="price-num">{{ detail.qichejiage }}</span>
                <span class="unit">/天</span>
              </div>
              <div class="status-box">
                <el-tag :type="detail.zhuangtai === '未出租' ? 'success' : 'danger'" effect="dark" size="medium">
                  {{ detail.zhuangtai || '未知状态' }}
                </el-tag>
              </div>
            </div>

            <div class="specs-grid">
              <div class="spec-item">
                <i class="el-icon-s-help"></i>
                <span class="label">类别:</span>
                <span class="value">{{ detail.qicheleibie }}</span>
              </div>
              <div class="spec-item">
                <i class="el-icon-user"></i>
                <span class="label">座位:</span>
                <span class="value">{{ detail.zuoweishu }}座</span>
              </div>
              <div class="spec-item">
                <i class="el-icon-s-operation"></i>
                <span class="label">档位:</span>
                <span class="value">{{ detail.huandangfangshi }}</span>
              </div>
              <div class="spec-item">
                <i class="el-icon-truck"></i>
                <span class="label">车牌:</span>
                <span class="value">{{ detail.chepaihao }}</span>
              </div>
            </div>

            <el-divider></el-divider>

            <div class="brief-introduction">
              <p>{{ detail.qichejianjie || '暂无主要简介...' }}</p>
            </div>

            <div class="action-block">
              <el-button
                  type="primary"
                  size="large"
                  class="rent-btn"
                  :disabled="detail.zhuangtai !== '未出租'"
                  @click="onTheNode(detail.id)"
              >
                {{ detail.zhuangtai === '未出租' ? '立即预定' : '已出租' }}
              </el-button>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <div class="main-content-box map-section" v-if="detail.longitude && detail.latitude">
      <div class="section-header">
        <span class="section-title">车辆位置</span>
        <span class="section-subtitle"><i class="el-icon-location"></i> {{ detail.address || detail.quchedidian }}</span>
      </div>
      <div id="user-map-container" style="width: 100%; height: 400px; border-radius: 4px; overflow: hidden;"></div>
    </div>

    <div class="tabs-content-box">
      <el-tabs type="border-card" v-model="activeTab">
        <el-tab-pane label="车辆详情" name="details">
          <div class="rich-text-content" v-html="detail.qichexiangqing"></div>
        </el-tab-pane>

        <el-tab-pane label="用户评论" name="comments" lazy>
          <div class="comments-section">
            <div class="comment-input-box">
              <el-input
                  type="textarea"
                  :rows="3"
                  placeholder="分享您的用车体验..."
                  v-model="form.content"
                  maxlength="200"
                  show-word-limit
              ></el-input>
              <div class="submit-btn-box">
                <el-button type="primary" size="medium" @click="submitForm(0)">发表评论</el-button>
              </div>
            </div>

            <el-divider content-position="left">最新评论</el-divider>

            <div class="comment-list">
              <div v-for="(item, index) in comments" :key="index" class="comment-item">
                <div class="user-avatar">
                  <el-avatar :size="40" :src="item.avatarurl ? baseUrl + item.avatarurl : ''" icon="el-icon-user-solid"></el-avatar>
                </div>
                <div class="comment-body">
                  <div class="comment-header">
                    <span class="username">{{ item.nickname || '匿名用户' }}</span>
                    <span class="time">{{ item.addtime }}</span>

                    <el-button v-if="isLogin" type="text" size="mini" class="action-btn" @click="handleReply(item)">回复</el-button>
                    <el-button v-if="isLogin && item.userid == currentUserId" type="text" size="mini" class="action-btn delete-btn" @click="handleDelete(item.id)">删除</el-button>
                  </div>

                  <p class="comment-text">
                    <span v-if="item.parentid && item.parentid != 0" style="color: #409EFF;">@回复: </span>
                    {{ item.content }}
                  </p>

                  <div class="reply-input-box" v-if="replyId === item.id">
                    <el-input
                        size="small"
                        v-model="replyContent"
                        :placeholder="'回复 @' + item.nickname"
                        class="reply-input"
                    ></el-input>
                    <el-button type="primary" size="small" @click="submitReply(item.id)">发送</el-button>
                    <el-button size="small" @click="replyId = 0">取消</el-button>
                  </div>
                </div>
              </div>
              <el-empty v-if="comments.length === 0" description="暂无评论，快来抢沙发吧！"></el-empty>
            </div>

            <div class="pagination-box" v-if="comments.length > 0">
              <el-pagination
                  background
                  layout="prev, pager, next"
                  :page-size="pageSize"
                  :total="total"
                  @current-change="curChange"
              ></el-pagination>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      swiperList: [],
      baseUrl: this.$config.baseUrl,
      detail: {},
      activeTab: 'details',
      map: null,

      // 评论相关
      form: { content: '', userid: localStorage.getItem('userid'), refid: this.$route.query.id },
      comments: [],
      total: 0,
      pageSize: 10,

      // 新增状态
      isLogin: !!localStorage.getItem('Token'),
      currentUserId: localStorage.getItem('userid'), // 用于判断删除权限
      replyId: 0, // 当前正在回复哪条评论的ID
      replyContent: '' // 回复的内容
    };
  },
  created() {
    this.init();
    this.getComments(1);
  },
  methods: {
    init() {
      this.$http.get(`qichexinxi/detail/${this.$route.query.id}`, {}).then(res => {
        if (res.data.code == 0) {
          this.detail = res.data.data;
          if (res.data.data.cheliangzhaopian) {
            this.swiperList = res.data.data.cheliangzhaopian.split(',');
          }

          // 初始化地图
          this.$nextTick(() => {
            this.initMap();
          });
        }
      });
    },

    // 初始化百度地图
    initMap() {
      // 检查是否有地图API和坐标信息
      if (typeof BMap === 'undefined' || !this.detail.longitude || !this.detail.latitude) return;

      this.map = new BMap.Map("user-map-container");
      let point = new BMap.Point(this.detail.longitude, this.detail.latitude);

      this.map.centerAndZoom(point, 16);
      this.map.enableScrollWheelZoom(true);

      // 添加标记
      let marker = new BMap.Marker(point);
      this.map.addOverlay(marker);

      // 添加文字标签
      let labelText = this.detail.address || this.detail.quchedidian || "车辆位置";
      let label = new BMap.Label(labelText, { offset: new BMap.Size(20, -10) });
      label.setStyle({
        color: "#333",
        border: "1px solid #ccc",
        padding: "5px",
        borderRadius: "4px",
        fontSize: "12px",
        background: "#fff"
      });
      marker.setLabel(label);
    },

    getImageUrl(path) {
      if (!path) return require('@/assets/logo.png');
      return path.startsWith('http') ? path : this.baseUrl + path;
    },
    onTheNode(id) {
      if (localStorage.getItem('Token')) {
        this.$router.push({ path: '/index/zuchedingdanAdd', query: { id: id } });
      } else {
        this.$message.warning('请先登录后再预定');
        this.$router.push('/login');
      }
    },
    toComment() {
      // 1. 切换 Tab 激活项为 'comments' (对应 el-tab-pane 的 name)
      this.activeTab = 'comments';

      // 2. 等待 DOM 更新后，平滑滚动到评论区
      this.$nextTick(() => {
        const commentSection = document.querySelector('.tabs-content-box');
        if (commentSection) {
          commentSection.scrollIntoView({ behavior: 'smooth' });
        }
      });
    },
    getComments(page) {
      this.$http.get(`discussqichexinxi/list`, {
        params: {
          page,
          limit: this.pageSize,
          refid: this.$route.query.id,
          sort: 'addtime', // 确保后端支持排序，或者前端排
          order: 'desc'
        }
      }).then(res => {
        if (res.data.code == 0) {
          this.comments = res.data.data.list;
          this.total = res.data.data.total;
        }
      });
    },

    curChange(page) {
      this.getComments(page);
    },

    // 提交主评论
    submitForm() {
      this.doSubmit(this.form.content, 0);
    },

    // 点击回复按钮
    handleReply(item) {
      this.replyId = item.id;
      this.replyContent = '';
    },

    // 提交回复
    submitReply(parentId) {
      if(!this.replyContent) {
        this.$message.error('回复内容不能为空');
        return;
      }
      this.doSubmit(this.replyContent, parentId);
    },

    // 统一提交逻辑
    doSubmit(content, parentId) {
      if (!localStorage.getItem('Token')) {
        this.$message.warning('请先登录');
        this.$router.push('/login');
        return;
      }

      // 重新获取ID防止过期
      const userid = localStorage.getItem('userid');

      const payload = {
        refid: this.$route.query.id,
        userid: userid,
        content: content,
        parentid: parentId, // 0为主评论，其他为回复ID
      };

      this.$http.post(`discussqichexinxi/add`, payload).then(res => {
        if (res.data.code == 0) {
          this.$message.success('发表成功');
          this.form.content = '';
          this.replyContent = '';
          this.replyId = 0;
          this.getComments(1);
        } else {
          this.$message.error(res.data.msg);
        }
      });
    },

    // 删除评论
    handleDelete(id) {
      this.$confirm('确认删除这条评论吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http.post(`discussqichexinxi/delete`, [id]).then(res => {
          if (res.data.code == 0) {
            this.$message.success('删除成功');
            this.getComments(1);
          } else {
            this.$message.error(res.data.msg);
          }
        });
      }).catch(() => {});
    }
  },
};
</script>

<style lang="scss" scoped>
// 变量定义
$--color-primary: #00c292;
$--color-danger: #f56c6c;
$--text-main: #303133;
$--text-regular: #606266;
$--text-secondary: #909399;
$--border-color-base: #dcdfe6;

.detail-page-container {
  width: 1200px;
  margin: 20px auto;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}

.breadcrumb-box {
  margin-bottom: 20px;
}

.main-content-box {
  background: #fff;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  margin-bottom: 30px;
}

/* 地图区域样式 */
.map-section {
  .section-header {
    margin-bottom: 15px;
    border-left: 4px solid $--color-primary;
    padding-left: 10px;
    display: flex;
    align-items: baseline;

    .section-title {
      font-size: 18px;
      font-weight: 600;
      color: $--text-main;
      margin-right: 15px;
    }
    .section-subtitle {
      font-size: 14px;
      color: $--text-secondary;
    }
  }
}

/* 左侧图集样式 */
.gallery-wrapper {
  .carousel-img-box {
    width: 100%;
    height: 450px;
    background: #f5f7fa;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 4px;
    overflow: hidden;

    .carousel-img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }

  /deep/ .el-carousel__indicators--outside {
    margin-top: 10px;
  }
}

/* 右侧信息区样式 */
.info-wrapper {
  padding-left: 20px;
  height: 450px;
  display: flex;
  flex-direction: column;

  .info-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 15px;

    .title {
      margin: 0;
      font-size: 28px;
      color: $--text-main;
      font-weight: 600;
      line-height: 1.3;
    }
    .consult-btn {
      color: $--color-primary;
      font-size: 16px;
      padding-top: 5px;
      &:hover { text-decoration: underline; }
    }
  }

  .price-status-block {
    background: #f9fafc;
    padding: 20px;
    border-radius: 6px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 25px;

    .price-box {
      color: $--color-danger;
      .currency { font-size: 20px; font-weight: bold; }
      .price-num { font-size: 36px; font-weight: bold; line-height: 1; margin: 0 4px; }
      .unit { font-size: 14px; color: $--text-secondary; }
    }
  }

  .specs-grid {
    display: flex;
    flex-wrap: wrap;
    gap: 20px 30px;
    margin-bottom: 20px;
    flex-grow: 1;

    .spec-item {
      width: calc(50% - 15px);
      display: flex;
      align-items: center;
      font-size: 15px;

      i {
        color: $--color-primary;
        font-size: 18px;
        margin-right: 10px;
      }
      .label { color: $--text-secondary; margin-right: 8px; }
      .value { color: $--text-main; font-weight: 500; }

      &.full-width {
        width: 100%;
      }
    }
  }

  .brief-introduction {
    color: $--text-regular;
    font-size: 14px;
    line-height: 1.6;
    margin-bottom: 25px;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
    overflow: hidden;
  }

  .action-block {
    .rent-btn {
      width: 100%;
      height: 50px;
      font-size: 18px;
      font-weight: bold;
      background-color: $--color-primary;
      border-color: $--color-primary;
      box-shadow: 0 4px 12px rgba($--color-primary, 0.3);
      transition: all 0.3s;

      &:hover {
        background-color: lighten($--color-primary, 5%);
        border-color: lighten($--color-primary, 5%);
        transform: translateY(-2px);
      }

      &.is-disabled {
        background-color: #a0cfff;
        border-color: #a0cfff;
        box-shadow: none;
      }
    }
  }
}

/* Tabs 样式 */
.tabs-content-box {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  overflow: hidden;

  /deep/ .el-tabs__header {
    background: #f5f7fa;
    margin-bottom: 0;
  }
  /deep/ .el-tabs__content {
    padding: 30px;
  }

  .rich-text-content {
    line-height: 1.8;
    color: $--text-regular;
    /deep/ img { max-width: 100%; height: auto; }
  }
}

/* 评论区样式 - 已整理修复 */
.comments-section {
  .comment-input-box {
    margin-bottom: 40px;
    .submit-btn-box {
      margin-top: 15px;
      text-align: right;
    }
  }

  .comment-list {
    .comment-item {
      display: flex;
      padding: 20px 0;
      border-bottom: 1px solid $--border-color-base;

      &:last-child {
        border-bottom: none;
      }

      .user-avatar {
        margin-right: 20px;
      }

      .comment-body {
        flex: 1;

        .comment-header {
          margin-bottom: 10px;
          display: flex;
          justify-content: space-between;
          align-items: center; /* 确保垂直居中 */

          .username {
            font-weight: bold;
            color: $--text-main;
          }

          .time {
            color: $--text-secondary;
            font-size: 13px;
          }

          /* 操作按钮样式 (回复/删除) */
          .action-btn {
            margin-left: 10px;
            cursor: pointer;
            &.delete-btn {
              color: $--color-danger;
            }
          }
        }

        .comment-text {
          color: $--text-regular;
          line-height: 1.6;
          font-size: 15px;
        }

        /* 回复输入框样式 */
        .reply-input-box {
          margin-top: 10px;
          background-color: #f5f7fa;
          padding: 10px;
          border-radius: 4px;
          display: flex;
          align-items: center;
          gap: 10px;

          .reply-input {
            flex: 1;
          }
        }
      }
    }
  }

  .pagination-box {
    text-align: center;
    margin-top: 30px;
  }
}
</style>
