<template>
  <div class="main-content">
    <template v-if="showFlag">
      <div class="search-box-card">
        <el-form :inline="true" :model="searchForm" @submit.native.prevent="search()">
          <el-form-item label="标题">
            <el-input v-model="searchForm.biaoti" placeholder="请输入标题" clearable size="medium"></el-input>
          </el-form-item>
          <el-form-item label="简介">
            <el-input v-model="searchForm.jianjie" placeholder="请输入简介" clearable size="medium"></el-input>
          </el-form-item>
          <el-form-item label="发布人">
            <el-input v-model="searchForm.faburen" placeholder="请输入发布人" clearable size="medium"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="search()" size="medium">查询</el-button>
            <el-button icon="el-icon-refresh" @click="resetForm" size="medium">重置</el-button>
            <el-button v-if="isAuth('qichezixun','新增')" type="success" icon="el-icon-plus" @click="addOrUpdateHandler()" size="medium">发布资讯</el-button>
            <el-button v-if="isAuth('qichezixun','删除')" type="danger" icon="el-icon-delete" :disabled="dataListSelections.length <= 0" @click="deleteHandler()" size="medium">批量删除</el-button>
          </el-form-item>
        </el-form>
      </div>

      <div class="table-box-card">
        <el-table :data="dataList" border v-loading="dataListLoading" @selection-change="selectionChangeHandler" style="width: 100%;">
          <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
          <el-table-column type="index" header-align="center" align="center" label="序号" width="60"></el-table-column>

          <el-table-column prop="biaoti" header-align="center" align="center" label="标题" show-overflow-tooltip min-width="150"></el-table-column>

          <el-table-column prop="fengmian" header-align="center" align="center" width="120" label="封面">
            <template slot-scope="scope">
              <div v-if="scope.row.fengmian">
                <el-image
                    class="table-img"
                    :src="scope.row.fengmian.split(',')[0].indexOf('http') == 0 ? scope.row.fengmian.split(',')[0] : $base.url + scope.row.fengmian.split(',')[0]"
                    :preview-src-list="[scope.row.fengmian.split(',')[0].indexOf('http') == 0 ? scope.row.fengmian.split(',')[0] : $base.url + scope.row.fengmian.split(',')[0]]">
                </el-image>
              </div>
              <div v-else>无图</div>
            </template>
          </el-table-column>

          <el-table-column prop="fabushijian" header-align="center" align="center" label="发布时间" width="120"></el-table-column>
          <el-table-column prop="faburen" header-align="center" align="center" label="发布人" width="120"></el-table-column>
          <el-table-column prop="clicknum" header-align="center" align="center" label="点击量" width="100">
            <template slot-scope="scope">
              <el-tag type="info" size="small"><i class="el-icon-view"></i> {{ scope.row.clicknum }}</el-tag>
            </template>
          </el-table-column>

          <el-table-column header-align="center" align="center" width="200" label="操作">
            <template slot-scope="scope">
              <el-button v-if="isAuth('qichezixun','查看')" type="text" icon="el-icon-view" size="small" @click="addOrUpdateHandler(scope.row.id,'info')">详情</el-button>
              <el-button v-if="isAuth('qichezixun','修改')" type="text" icon="el-icon-edit" size="small" @click="addOrUpdateHandler(scope.row.id)">修改</el-button>
              <el-button v-if="isAuth('qichezixun','删除')" type="text" icon="el-icon-delete" class="text-danger" size="small" @click="deleteHandler(scope.row.id)">删除</el-button>
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
        biaoti: "",
        jianjie: "",
        faburen: ""
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
      this.searchForm = {
        biaoti: "",
        jianjie: "",
        faburen: ""
      };
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
      if(this.searchForm.biaoti) params['biaoti'] = '%' + this.searchForm.biaoti + '%'
      if(this.searchForm.jianjie) params['jianjie'] = '%' + this.searchForm.jianjie + '%'
      if(this.searchForm.faburen) params['faburen'] = '%' + this.searchForm.faburen + '%'

      this.$http({
        url: "qichezixun/page",
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
          url: "qichezixun/delete",
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
