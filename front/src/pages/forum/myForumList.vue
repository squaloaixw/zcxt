<template>
  <div class="my-publish-container">
    <div class="page-header">
      <div class="header-left">
        <span class="title">我的发布</span>
        <span class="subtitle">管理您发布的帖子内容</span>
      </div>
      <el-button
          type="text"
          icon="el-icon-back"
          class="back-btn"
          @click="backClick">返回个人中心</el-button>
    </div>

    <div class="table-container">
      <el-table
          :data="tableData"
          style="width: 100%"
          :header-cell-style="{background:'#f5f7fa',color:'#606266'}"
          class="custom-table"
          stripe>
        <el-table-column
            label="帖子标题"
            prop="title"
            min-width="200">
          <template slot-scope="scope">
            <span class="post-title">{{ scope.row.title }}</span>
          </template>
        </el-table-column>
        <el-table-column
            label="发布时间"
            prop="addtime"
            width="180"
            align="center">
          <template slot-scope="scope">
            <i class="el-icon-time" style="margin-right: 5px; color: #909399;"></i>
            {{ scope.row.addtime }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template slot-scope="scope">
            <el-button
                size="mini"
                type="primary"
                plain
                icon="el-icon-edit"
                @click="handleEdit(scope.$index, scope.row)">修改</el-button>
            <el-button
                size="mini"
                type="danger"
                plain
                icon="el-icon-delete"
                @click="handleDelete(scope.$index, scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="tableData.length === 0" description="您还没有发布过帖子"></el-empty>
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
      layouts: '',
      tableData: [],
      total: 1,
      pageSize: 10,
      pageSizes: [10, 20, 30, 50],
      totalPage: 1
    }
  },
  created() {
    this.getMyForumList(1);
  },
  methods: {
    backClick() {
      this.$router.push('/index/center')
    },
    getMyForumList(page) {
      this.$http.get('forum/list', {params: {page, limit: this.pageSize, parentid: 0, sort: 'addtime', order: 'desc'}}).then(res => {
        if (res.data.code == 0) {
          this.tableData = res.data.data.list;
          this.total = res.data.data.total;
          this.pageSize = res.data.data.pageSize;
          this.pageSizes = [this.pageSize, this.pageSize*2, this.pageSize*3, this.pageSize*5];
          this.totalPage = res.data.data.totalPage;
        }
      });
    },
    curChange(page) {
      this.getMyForumList(page);
    },
    prevClick(page) {
      this.getMyForumList(page);
    },
    nextClick(page) {
      this.getMyForumList(page);
    },
    handleEdit(index, row) {
      this.$router.push({path: '/index/forumAdd', query: {id: row.id, editObj: JSON.stringify(row)}})
    },
    handleDelete(index, row) {
      this.$confirm('确定要删除这条帖子吗?', '温馨提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        let delIds = new Array();
        delIds.push(row.id);
        this.$http.post('forum/delete', delIds).then(res => {
          if (res.data.code == 0) {
            this.$message({
              type: 'success',
              message: '删除成功!',
              duration: 1500,
              onClose: () => {
                this.getMyForumList(1);
              }
            });
          }
        });
      });
    }
  }
}
</script>

<style lang="scss" scoped>
.my-publish-container {
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
    .title {
      font-size: 24px;
      font-weight: bold;
      color: #333;
      margin-right: 15px;
    }
    .subtitle {
      font-size: 14px;
      color: #999;
    }
  }

  .back-btn {
    font-size: 14px;
    color: #666;
    &:hover {
      color: #409EFF;
    }
  }
}

.table-container {
  margin-bottom: 30px;

  .custom-table {
    border-radius: 4px;
    overflow: hidden;
  }

  .post-title {
    font-weight: 500;
    color: #333;
  }
}

.pagination-box {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
