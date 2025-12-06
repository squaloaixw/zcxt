<template>
  <div class="main-content">
    <template v-if="showFlag">
      <div class="search-box-card">
        <el-form :inline="true" :model="searchForm" @submit.native.prevent="search()">
          <el-form-item label="管理账号">
            <el-input v-model="searchForm.guanlizhanghao" placeholder="管理账号" clearable size="medium"></el-input>
          </el-form-item>
          <el-form-item label="管理姓名">
            <el-input v-model="searchForm.guanlixingming" placeholder="管理姓名" clearable size="medium"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="search()" size="medium">查询</el-button>
            <el-button icon="el-icon-refresh" @click="resetForm" size="medium">重置</el-button>
          </el-form-item>
        </el-form>
        <el-row class="btn-group">
          <el-button v-if="isAuth('putongguanliyuan','新增')" type="primary" icon="el-icon-plus" @click="addOrUpdateHandler()" size="medium">新增管理员</el-button>
          <el-button v-if="isAuth('putongguanliyuan','删除')" type="danger" icon="el-icon-delete" :disabled="dataListSelections.length <= 0" @click="deleteHandler()" size="medium">批量删除</el-button>
        </el-row>
      </div>

      <div class="table-box-card">
        <el-table :data="dataList" border v-loading="dataListLoading" @selection-change="selectionChangeHandler" style="width: 100%;">
          <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
          <el-table-column type="index" header-align="center" align="center" label="序号" width="60"></el-table-column>
          <el-table-column prop="guanlizhanghao" header-align="center" align="center" label="管理账号"></el-table-column>
          <el-table-column prop="guanlixingming" header-align="center" align="center" label="管理姓名"></el-table-column>
          <el-table-column prop="xingbie" header-align="center" align="center" label="性别" width="80">
            <template slot-scope="scope">
              <el-tag :type="scope.row.xingbie === '男' ? '' : 'danger'" size="small" effect="plain">{{scope.row.xingbie}}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="lianxidianhua" header-align="center" align="center" label="联系电话"></el-table-column>
          <el-table-column prop="touxiang" header-align="center" align="center" width="100" label="头像">
            <template slot-scope="scope">
              <div v-if="scope.row.touxiang">
                <img class="table-img" :src="scope.row.touxiang.split(',')[0].indexOf('http') == 0 ? scope.row.touxiang.split(',')[0] : $base.url + scope.row.touxiang.split(',')[0]">
              </div>
              <div v-else>
                <el-avatar icon="el-icon-user-solid" size="small"></el-avatar>
              </div>
            </template>
          </el-table-column>
          <el-table-column header-align="center" align="center" width="200" label="操作">
            <template slot-scope="scope">
              <el-button v-if="isAuth('putongguanliyuan','查看')" type="text" icon="el-icon-view" size="small" @click="addOrUpdateHandler(scope.row.id,'info')">详情</el-button>
              <el-button v-if="isAuth('putongguanliyuan','修改')" type="text" icon="el-icon-edit" size="small" @click="addOrUpdateHandler(scope.row.id)">修改</el-button>
              <el-button v-if="isAuth('putongguanliyuan','删除')" type="text" icon="el-icon-delete" class="text-danger" size="small" @click="deleteHandler(scope.row.id)">删除</el-button>
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
        guanlizhanghao: "",
        guanlixingming: ""
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
      this.searchForm.guanlizhanghao = '';
      this.searchForm.guanlixingming = '';
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
        ...this.searchForm
      }
      for(let key in params){
        if(params[key] === '') delete params[key];
        else if(key !== 'page' && key !== 'limit' && key !== 'sort' && key !== 'order') {
          params[key] = '%' + params[key] + '%'
        }
      }
      this.$http({
        url: "putongguanliyuan/page",
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
          url: "putongguanliyuan/delete",
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
.btn-group {
  margin-top: 15px;
  border-top: 1px solid #f0f0f0;
  padding-top: 15px;
}
.text-danger {
  color: #F56C6C;
  &:hover {
    color: #ff4949;
  }
}
</style>
