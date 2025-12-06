<template>
  <div class="main-content">
    <template v-if="showFlag">
      <div class="search-box-card">
        <el-form :inline="true" :model="searchForm" @submit.native.prevent="search()">
          <el-form-item label="用户名">
            <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable size="medium"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="search()" size="medium">查询</el-button>
            <el-button icon="el-icon-refresh" @click="resetForm" size="medium">重置</el-button>
            <el-button v-if="isAuth('messages','新增')" type="success" icon="el-icon-plus" @click="addOrUpdateHandler()" size="medium">新增留言</el-button>
            <el-button v-if="isAuth('messages','删除')" type="danger" icon="el-icon-delete" :disabled="dataListSelections.length <= 0" @click="deleteHandler()" size="medium">批量删除</el-button>
          </el-form-item>
        </el-form>
      </div>

      <div class="table-box-card">
        <el-table :data="dataList" border v-loading="dataListLoading" @selection-change="selectionChangeHandler" style="width: 100%;">
          <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
          <el-table-column type="index" header-align="center" align="center" label="序号" width="60"></el-table-column>

          <el-table-column prop="username" header-align="center" align="center" label="用户名" width="120"></el-table-column>
          <el-table-column prop="content" header-align="center" align="center" label="留言内容" show-overflow-tooltip></el-table-column>

          <el-table-column prop="cpicture" header-align="center" align="center" width="100" label="留言图片">
            <template slot-scope="scope">
              <div v-if="scope.row.cpicture">
                <el-image
                    class="table-img"
                    :src="scope.row.cpicture.split(',')[0].indexOf('http') == 0 ? scope.row.cpicture.split(',')[0] : $base.url + scope.row.cpicture.split(',')[0]"
                    :preview-src-list="[scope.row.cpicture.split(',')[0].indexOf('http') == 0 ? scope.row.cpicture.split(',')[0] : $base.url + scope.row.cpicture.split(',')[0]]">
                </el-image>
              </div>
              <div v-else>无图</div>
            </template>
          </el-table-column>

          <el-table-column prop="reply" header-align="center" align="center" label="回复内容" show-overflow-tooltip>
            <template slot-scope="scope">
              <span v-if="scope.row.reply">{{ scope.row.reply }}</span>
              <el-tag v-else type="info" size="small">暂无回复</el-tag>
            </template>
          </el-table-column>

          <el-table-column prop="rpicture" header-align="center" align="center" width="100" label="回复图片">
            <template slot-scope="scope">
              <div v-if="scope.row.rpicture">
                <el-image
                    class="table-img"
                    :src="scope.row.rpicture.split(',')[0].indexOf('http') == 0 ? scope.row.rpicture.split(',')[0] : $base.url + scope.row.rpicture.split(',')[0]"
                    :preview-src-list="[scope.row.rpicture.split(',')[0].indexOf('http') == 0 ? scope.row.rpicture.split(',')[0] : $base.url + scope.row.rpicture.split(',')[0]]">
                </el-image>
              </div>
              <div v-else>无图</div>
            </template>
          </el-table-column>

          <el-table-column header-align="center" align="center" width="220" label="操作">
            <template slot-scope="scope">
              <el-button v-if="isAuth('messages','查看')" type="text" icon="el-icon-view" size="small" @click="addOrUpdateHandler(scope.row.id,'info')">详情</el-button>
              <el-button v-if="isAuth('messages','修改')" type="text" icon="el-icon-edit" size="small" @click="addOrUpdateHandler(scope.row.id)">修改</el-button>
              <el-button v-if="isAuth('messages','删除')" type="text" icon="el-icon-delete" class="text-danger" size="small" @click="deleteHandler(scope.row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-pagination
            @size-change="sizeChangeHandle"
            @current-change="currentChangeHandle"
            :current-page="pageIndex"
            :page-sizes="[10, 20, 50]"
            :page-size="pageSize"
            :total="totalPage"
            layout="total, sizes, prev, pager, next, jumper"
            background
            class="pagination-content">
        </el-pagination>
      </div>
    </template>

    <add-or-update v-if="addOrUpdateFlag" :parent="this" ref="addOrUpdate"></add-or-update>
  </div>
</template>

<script>
import AddOrUpdate from "./add-or-update";
export default {
  data() {
    return {
      searchForm: {
        username: ""
      },
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      dataListSelections: [],
      showFlag: true,
      addOrUpdateFlag:false,
    };
  },
  components: {
    AddOrUpdate,
  },
  created() {
    this.getDataList();
  },
  methods: {
    resetForm() {
      this.searchForm.username = "";
      this.search();
    },
    search() {
      this.pageIndex = 1;
      this.getDataList();
    },
    getDataList() {
      this.dataListLoading = true;
      let params = {
        page: this.pageIndex,
        limit: this.pageSize,
        sort: 'id',
        order: 'desc',
      }
      if(this.searchForm.username) params['username'] = '%' + this.searchForm.username + '%'

      this.$http({
        url: "messages/page",
        method: "get",
        params: params
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.dataList = data.data.list;
          this.totalPage = data.data.total;
        } else {
          this.dataList = [];
          this.totalPage = 0;
        }
        this.dataListLoading = false;
      });
    },
    sizeChangeHandle(val) {
      this.pageSize = val;
      this.pageIndex = 1;
      this.getDataList();
    },
    currentChangeHandle(val) {
      this.pageIndex = val;
      this.getDataList();
    },
    selectionChangeHandler(val) {
      this.dataListSelections = val;
    },
    addOrUpdateHandler(id,type) {
      this.showFlag = false;
      this.addOrUpdateFlag = true;
      if(type!='info'){
        type = 'else';
      }
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(id,type);
      });
    },
    deleteHandler(id) {
      var ids = id ? [Number(id)] : this.dataListSelections.map(item => Number(item.id));
      this.$confirm(`确定进行[${id ? "删除" : "批量删除"}]操作?`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        this.$http({
          url: "messages/delete",
          method: "post",
          data: ids
        }).then(({ data }) => {
          if (data && data.code === 0) {
            this.$message.success("操作成功");
            this.search();
          } else {
            this.$message.error(data.msg);
          }
        });
      });
    },
  }
};
</script>

<style lang="scss" scoped>
.text-danger {
  color: #F56C6C;
  &:hover {
    color: #ff4949;
  }
}
</style>
