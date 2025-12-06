<template>
  <div class="main-content">
    <template v-if="showFlag">
      <div class="search-box-card">
        <el-form :inline="true" :model="searchForm" @submit.native.prevent="search()">
          <el-form-item label="车牌号">
            <el-input v-model="searchForm.chepaihao" placeholder="请输入车牌号" clearable size="medium"></el-input>
          </el-form-item>
          <el-form-item label="车辆品牌">
            <el-input v-model="searchForm.cheliangpinpai" placeholder="请输入品牌" clearable size="medium"></el-input>
          </el-form-item>
          <el-form-item label="汽车类别">
            <el-select v-model="searchForm.qicheleibie" placeholder="请选择类别" clearable size="medium">
              <el-option v-for="(item,index) in qicheleibieOptions" :key="index" :label="item" :value="item"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.zhuangtai" placeholder="请选择状态" clearable size="medium">
              <el-option v-for="(item,index) in zhuangtaiOptions" :key="index" :label="item" :value="item"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="search()" size="medium">查询</el-button>
            <el-button icon="el-icon-refresh" @click="resetForm" size="medium">重置</el-button>

            <el-button v-if="isAuth('qichexinxi','新增')" type="success" icon="el-icon-plus" @click="addOrUpdateHandler()" size="medium">新增车辆</el-button>
            <el-button v-if="isAuth('qichexinxi','删除')" type="danger" icon="el-icon-delete" :disabled="dataListSelections.length <= 0" @click="deleteHandler()" size="medium">批量删除</el-button>
          </el-form-item>
        </el-form>
      </div>

      <div class="table-box-card">
        <el-table :data="dataList" border v-loading="dataListLoading" @selection-change="selectionChangeHandler" style="width: 100%;">
          <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
          <el-table-column type="index" header-align="center" align="center" label="序号" width="60"></el-table-column>

          <el-table-column prop="chepaihao" header-align="center" align="center" label="车牌号" min-width="100"></el-table-column>
          <el-table-column prop="cheliangxinghao" header-align="center" align="center" label="车辆型号" show-overflow-tooltip></el-table-column>
          <el-table-column prop="cheliangpinpai" header-align="center" align="center" label="品牌"></el-table-column>
          <el-table-column prop="qicheleibie" header-align="center" align="center" label="类别">
            <template slot-scope="scope">
              <el-tag size="small">{{ scope.row.qicheleibie }}</el-tag>
            </template>
          </el-table-column>

          <el-table-column prop="cheliangzhaopian" header-align="center" align="center" width="120" label="车辆照片">
            <template slot-scope="scope">
              <div v-if="scope.row.cheliangzhaopian">
                <el-image
                    class="table-img"
                    :src="scope.row.cheliangzhaopian.split(',')[0].indexOf('http') == 0 ? scope.row.cheliangzhaopian.split(',')[0] : $base.url + scope.row.cheliangzhaopian.split(',')[0]"
                    :preview-src-list="[scope.row.cheliangzhaopian.split(',')[0].indexOf('http') == 0 ? scope.row.cheliangzhaopian.split(',')[0] : $base.url + scope.row.cheliangzhaopian.split(',')[0]]">
                </el-image>
              </div>
              <div v-else>无图</div>
            </template>
          </el-table-column>

          <el-table-column prop="jiage" header-align="center" align="center" label="日租金">
            <template slot-scope="scope">
              <span style="color: #F56C6C; font-weight: bold;">￥{{ scope.row.jiage }}</span>
            </template>
          </el-table-column>

          <el-table-column prop="zhuangtai" header-align="center" align="center" label="状态">
            <template slot-scope="scope">
              <el-tag :type="scope.row.zhuangtai === '已出租' ? 'warning' : 'success'" effect="dark" size="small">{{ scope.row.zhuangtai }}</el-tag>
            </template>
          </el-table-column>

          <el-table-column header-align="center" align="center" width="220" label="操作" fixed="right">
            <template slot-scope="scope">
              <el-button v-if="isAuth('qichexinxi','查看')" type="text" icon="el-icon-view" size="small" @click="addOrUpdateHandler(scope.row.id,'info')">详情</el-button>
              <el-button v-if="isAuth('qichexinxi','租赁汽车')" type="text" icon="el-icon-shopping-cart-full" size="small" @click="zuchedingdanCrossAddOrUpdateHandler(scope.row,'cross','','zhuangtai','该车辆已出租','已出租,未出租'.split(',')[0])">租赁</el-button>
              <el-button v-if="isAuth('qichexinxi','修改')" type="text" icon="el-icon-edit" size="small" @click="addOrUpdateHandler(scope.row.id)">修改</el-button>
              <el-button v-if="isAuth('qichexinxi','删除')" type="text" icon="el-icon-delete" class="text-danger" size="small" @click="deleteHandler(scope.row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-pagination
            @size-change="sizeChangeHandle"
            @current-change="currentChangeHandle"
            :current-page="pageIndex"
            :page-sizes="[10, 20, 50, 100]"
            :page-size="pageSize"
            :total="totalPage"
            layout="total, sizes, prev, pager, next, jumper"
            background
            class="pagination-content">
        </el-pagination>
      </div>
    </template>

    <add-or-update v-if="addOrUpdateFlag" :parent="this" ref="addOrUpdate"></add-or-update>
    <zuchedingdan-cross-add-or-update v-if="zuchedingdanCrossAddOrUpdateFlag" :parent="this" ref="zuchedingdanCrossaddOrUpdate"></zuchedingdan-cross-add-or-update>
  </div>
</template>

<script>
import AddOrUpdate from "./add-or-update";
import zuchedingdanCrossAddOrUpdate from "../zuchedingdan/add-or-update";
export default {
  data() {
    return {
      qicheleibieOptions: [],
      zhuangtaiOptions: [],
      searchForm: {
        chepaihao: "",
        qicheleibie: "",
        cheliangpinpai: "",
        zhuangtai: ""
      },
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      dataListSelections: [],
      showFlag: true,
      addOrUpdateFlag:false,
      zuchedingdanCrossAddOrUpdateFlag: false,
    };
  },
  components: {
    AddOrUpdate,
    zuchedingdanCrossAddOrUpdate,
  },
  created() {
    this.init();
    this.getDataList();
  },
  methods: {
    init () {
      this.$http({
        url: `option/qicheleibie/qicheleibie`,
        method: "get"
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.qicheleibieOptions = data.data;
        }
      });
      this.zhuangtaiOptions = "已出租,未出租".split(',')
    },
    // 重置按钮逻辑
    resetForm() {
      this.searchForm = {
        chepaihao: "",
        qicheleibie: "",
        cheliangpinpai: "",
        zhuangtai: ""
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
        ...this.searchForm
      }
      // 清理空值
      for(let key in params){
        if(params[key] === '' || params[key] === undefined){
          delete params[key];
        } else if(key !== 'page' && key !== 'limit' && key !== 'sort' && key !== 'order'){
          params[key] = '%' + params[key] + '%' // 模糊查询处理
          // 对于下拉框，通常是精确匹配
          if(key === 'qicheleibie' || key === 'zhuangtai') params[key] = this.searchForm[key];
        }
      }

      this.$http({
        url: "qichexinxi/page",
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
      this.crossAddOrUpdateFlag = false;
      if(type!='info'){
        type = 'else';
      }
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(id,type);
      });
    },
    zuchedingdanCrossAddOrUpdateHandler(row,type,crossOptAudit,statusColumnName,tips,statusColumnValue){
      this.showFlag = false;
      this.addOrUpdateFlag = false;
      this.zuchedingdanCrossAddOrUpdateFlag = true;
      this.$storage.set('crossObj',row);
      this.$storage.set('crossTable','qichexinxi');
      this.$storage.set('statusColumnName',statusColumnName);
      this.$storage.set('statusColumnValue',statusColumnValue);
      this.$storage.set('tips',tips);
      if(statusColumnName!=''&&!statusColumnName.startsWith("[")) {
        var obj = this.$storage.getObj('crossObj');
        for (var o in obj){
          if(o==statusColumnName && obj[o]==statusColumnValue){
            this.$message({
              message: tips,
              type: "success",
              duration: 1500,
              onClose: () => {
                this.getDataList();
              }
            });
            this.showFlag = true;
            this.zuchedingdanCrossAddOrUpdateFlag = false;
            return;
          }
        }
      }
      this.$nextTick(() => {
        this.$refs.zuchedingdanCrossaddOrUpdate.init(row.id,type);
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
          url: "qichexinxi/delete",
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
