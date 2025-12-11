<template>
  <div class="forum-detail-container">
    <div class="breadcrumb-box">
      <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item :to="{ path: '/index/home' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/index/forum' }">交流论坛</el-breadcrumb-item>
        <el-breadcrumb-item>帖子详情</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="post-main-card">
      <div class="post-header">
        <span class="tag-badge" :class="detail.isdone === '开放' ? 'open' : 'closed'">
          {{ detail.isdone === '开放' ? '进行中' : '已结帖' }}
        </span>
        <h1 class="post-title">{{ detail.title }}</h1>
      </div>

      <div class="user-info-bar">
        <el-avatar :size="50" :src="baseUrl + 'upload/touxiang.png'" icon="el-icon-user-solid"></el-avatar>
        <div class="user-text">
          <div class="name">{{ detail.username || '匿名用户' }} <span class="lz-tag">楼主</span></div>
          <div class="time">发布于 {{ detail.addtime }}</div>
        </div>
      </div>

      <div class="post-content" v-html="detail.content"></div>
    </div>

    <div class="reply-section">
      <div class="section-header">
        <span class="title">全部回复 ({{ commentList.length }})</span>
      </div>

      <div class="comment-list">
        <div v-for="(item, index) in commentList" :key="item.id" class="comment-item">
          <div class="avatar-col">
            <el-avatar :size="40" :src="baseUrl + 'upload/touxiang.png'" icon="el-icon-user-solid"></el-avatar>
          </div>
          <div class="content-col">
            <div class="info-row">
              <span class="username">{{ item.username }}</span>
              <span class="floor-num">#{{ index + 1 }}楼</span>
            </div>
            <div class="comment-text" v-html="item.content"></div>
            <div class="action-row">
              <span class="time">{{ item.addtime }}</span>
            </div>
          </div>
        </div>
        <el-empty v-if="commentList.length === 0" description="暂无回复，快来抢沙发吧"></el-empty>
      </div>

      <div class="comment-editor-box">
        <div class="editor-header">发表回复</div>
        <el-form>
          <el-input
              type="textarea"
              :rows="5"
              placeholder="请友善交流，文明发言..."
              v-model="commentForm.content">
          </el-input>
          <div class="btn-row">
            <el-button type="primary" @click="submitComment">发送回复</el-button>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      baseUrl: this.$config.baseUrl,
      detail: {},
      commentList: [],
      commentForm: {
        content: '',
        parentid: '',
        userid: localStorage.getItem('userid'),
        username: localStorage.getItem('username')
      }
    };
  },
  created() {
    this.detail.id = this.$route.query.id;
    this.init();
  },
  methods: {
    init() {
      // 1. 获取帖子详情
      this.$http.get(`forum/detail/${this.detail.id}`).then(res => {
        if (res.data.code == 0) {
          this.detail = res.data.data;
          // 2. 获取该帖子的评论 (parentid = 帖子id)
          this.getComments();
        }
      });
    },
    getComments() {
      this.$http.get(`forum/list`, {
        params: {
          parentid: this.detail.id,
          sort: 'addtime',
          order: 'asc'
        }
      }).then(res => {
        if (res.data.code == 0) {
          this.commentList = res.data.data.list;
        }
      });
    },
    submitComment() {
      if (!this.commentForm.content) {
        this.$message.error('请输入回复内容');
        return;
      }
      if (!localStorage.getItem('Token')) {
        this.$message.warning('请先登录');
        this.$router.push('/login');
        return;
      }

      this.commentForm.parentid = this.detail.id;
      // 注意：部分系统 forum表复用，title可能也需要填
      let payload = {
        ...this.commentForm,
        title: '回复：' + this.detail.title
      };

      this.$http.post('forum/add', payload).then(res => {
        if (res.data.code == 0) {
          this.$message.success('回复成功');
          this.commentForm.content = '';
          this.getComments();
        } else {
          this.$message.error(res.data.msg);
        }
      });
    }
  }
};
</script>

<style lang="scss" scoped>
$--color-primary: #00c292;

.forum-detail-container {
  width: 1200px;
  margin: 20px auto;
}

/* 楼主帖子主体样式 */
.post-main-card {
  background: #fff;
  border-radius: 8px;
  padding: 40px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.05);

  .post-header {
    display: flex;
    align-items: center;
    margin-bottom: 25px;

    .tag-badge {
      padding: 4px 10px;
      border-radius: 4px;
      font-size: 13px;
      margin-right: 15px;
      color: #fff;
      &.open { background: $--color-primary; }
      &.closed { background: #909399; }
    }

    .post-title {
      font-size: 26px;
      color: #333;
      margin: 0;
      line-height: 1.3;
    }
  }

  .user-info-bar {
    display: flex;
    align-items: center;
    padding-bottom: 20px;
    border-bottom: 1px solid #f0f0f0;
    margin-bottom: 30px;

    .user-text {
      margin-left: 15px;
      .name {
        font-size: 16px;
        font-weight: bold;
        color: #333;
        margin-bottom: 5px;
        .lz-tag {
          font-size: 12px;
          background: #e6f7ff;
          color: #1890ff;
          padding: 2px 6px;
          border-radius: 2px;
          margin-left: 8px;
          font-weight: normal;
        }
      }
      .time {
        font-size: 13px;
        color: #999;
      }
    }
  }

  .post-content {
    font-size: 16px;
    line-height: 1.8;
    color: #444;
    min-height: 200px;
    /deep/ img { max-width: 100%; }
  }
}

/* 回复区域样式 */
.reply-section {
  background: #fff;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.05);

  .section-header {
    border-bottom: 1px solid #eee;
    padding-bottom: 15px;
    margin-bottom: 20px;
    .title {
      font-size: 18px;
      font-weight: bold;
      color: #333;
      border-left: 4px solid $--color-primary;
      padding-left: 10px;
    }
  }

  .comment-item {
    display: flex;
    padding: 20px 0;
    border-bottom: 1px solid #f5f5f5;

    .avatar-col {
      width: 60px;
      text-align: center;
    }
    .content-col {
      flex: 1;
      .info-row {
        display: flex;
        justify-content: space-between;
        margin-bottom: 10px;
        .username { font-weight: bold; color: #555; }
        .floor-num { color: #ccc; font-size: 12px; }
      }
      .comment-text {
        color: #333;
        line-height: 1.6;
        margin-bottom: 10px;
      }
      .action-row {
        .time { color: #999; font-size: 12px; }
      }
    }
  }

  /* 评论输入框 */
  .comment-editor-box {
    margin-top: 40px;
    background: #f9f9f9;
    padding: 20px;
    border-radius: 8px;

    .editor-header {
      font-weight: bold;
      margin-bottom: 15px;
    }
    .btn-row {
      text-align: right;
      margin-top: 15px;
    }
  }
}
</style>
