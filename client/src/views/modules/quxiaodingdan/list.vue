<template>
  <div class="main-content">
    <template v-if="showFlag">
      <div class="search-box-card">
        <el-form :inline="true" :model="searchForm" @submit.native.prevent="search()" class="search-form-content">
          <el-form-item label="车牌号">
            <el-input v-model="searchForm.chepaihao" placeholder="车牌号" clearable size="medium"></el-input>
          </el-form-item>
          <el-form-item label="车辆品牌">
            <el-input v-model="searchForm.cheliangpinpai" placeholder="品牌" clearable size="medium"></el-input>
          </el-form-item>
          <el-form-item label="汽车类别">
            <el-input v-model="searchForm.qicheleibie" placeholder="类别" clearable size="medium"></el-input>
          </el-form-item>
          <el-form-item label="姓名">
            <el-input v-model="searchForm.xingming" placeholder="客户姓名" clearable size="medium"></el-input>
          </el-form-item>
          <el-form-item label="审核状态">
            <el-select v-model="searchForm.sfsh" placeholder="状态" clearable size="medium">
              <el-option label="通过" value="是"></el-option>
              <el-option label="不通过" value="否"></el-option>
              <el-option label="待审核" value="待审核"></el-option>
            </el-select>
          </el-form-item>
        </el-form>

        <el-row class="btn-group">
          <el-button type="primary" icon="el-icon-search" @click="search()" size="medium">查询</el-button>
          <el-button icon="el-icon-refresh" @click="resetForm" size="medium">重置</el-button>
          <el-button type="success" icon="el-icon-plus" @click="addOrUpdateHandler()" size="medium">新增取消</el-button>
          <el-button type="danger" icon="el-icon-delete" :disabled="dataListSelections.length <= 0" @click="deleteHandler()" size="medium">批量删除</el-button>
        </el-row>
      </div>

      <div class="table-box-card">
        <el-table :data="dataList" border v-loading="dataListLoading" @selection-change="selectionChangeHandler" style="width: 100%;">
          <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
          <el-table-column prop="dingdanbianhao" header-align="center" align="center" label="订单编号" min-width="160" show-overflow-tooltip></el-table-column>
          <el-table-column prop="chepaihao" header-align="center" align="center" label="车牌号"></el-table-column>
          <el-table-column prop="cheliangpinpai" header-align="center" align="center" label="品牌"></el-table-column>
          <el-table-column prop="zongjia" header-align="center" align="center" label="退款金额">
            <template slot-scope="scope">
              <span style="color: #F56C6C; font-weight: bold;">￥{{ scope.row.zongjia }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="quxiaoriqi" header-align="center" align="center" label="取消日期" width="100"></el-table-column>
          <el-table-column prop="xingming" header-align="center" align="center" label="姓名"></el-table-column>
          <el-table-column prop="shouji" header-align="center" align="center" label="手机" width="120"></el-table-column>

          <el-table-column prop="ispay" header-align="center" align="center" label="支付状态" width="100">
            <template slot-scope="scope">
              <el-tag v-if="scope.row.ispay=='已支付'" type="success" size="small">已退款</el-tag>
              <el-button v-else type="primary" size="mini" plain @click="payHandler(scope.row)">去退款</el-button>
            </template>
          </el-table-column>

          <el-table-column prop="sfsh" header-align="center" align="center" label="审核状态">
            <template slot-scope="scope">
              <el-tag v-if="scope.row.sfsh=='是'" type="success" effect="dark" size="small">通过</el-tag>
              <el-tag v-else-if="scope.row.sfsh=='否'" type="danger" effect="dark" size="small">拒绝</el-tag>
              <el-tag v-else type="warning" effect="dark" size="small">待审核</el-tag>
            </template>
          </el-table-column>

          <el-table-column header-align="center" align="center" width="220" label="操作" fixed="right">
            <template slot-scope="scope">
              <el-button v-if="isAuth('quxiaodingdan','审核') && scope.row.sfsh=='待审核'" type="warning" size="mini" @click="shDialog(scope.row)">审核</el-button>
              <el-button v-if="isAuth('quxiaodingdan','查看')" type="text" icon="el-icon-view" size="small" @click="addOrUpdateHandler(scope.row.id,'info')">详情</el-button>
              <el-button v-if="isAuth('quxiaodingdan','修改')" type="text" icon="el-icon-edit" size="small" @click="addOrUpdateHandler(scope.row.id)">修改</el-button>
              <el-button v-if="isAuth('quxiaodingdan','删除')" type="text" icon="el-icon-delete" class="text-danger" size="small" @click="deleteHandler(scope.row.id)">删除</el-button>
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

    <el-dialog title="取消申请审核" :visible.sync="sfshVisiable" width="40%" center>
      <el-form ref="form" :model="shForm" label-width="80px">
        <el-form-item label="审核结果">
          <el-radio-group v-model="shForm.sfsh">
            <el-radio label="是" border>通过</el-radio>
            <el-radio label="否" border>不通过</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核意见">
          <el-input type="textarea" :rows="4" v-model="shForm.shhf" placeholder="请输入审核回复..."></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="sfshVisiable = false">取 消</el-button>
        <el-button type="primary" @click="shHandler">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import AddOrUpdate from "./add-or-update";
export default {
  data() {
    return {
      searchForm: {
        chepaihao: "",
        cheliangpinpai: "",
        qicheleibie: "",
        xingming: "",
        sfsh: ""
      },
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      dataListSelections: [],
      showFlag: true,
      sfshVisiable: false,
      shForm: {},
      addOrUpdateFlag:false,
      sfshOptions: ['是','否','待审核']
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
        chepaihao: "",
        cheliangpinpai: "",
        qicheleibie: "",
        xingming: "",
        sfsh: ""
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
      // 修复：只有当有值时才添加参数
      if(this.searchForm.chepaihao) params['chepaihao'] = '%' + this.searchForm.chepaihao + '%'
      if(this.searchForm.cheliangpinpai) params['cheliangpinpai'] = '%' + this.searchForm.cheliangpinpai + '%'
      if(this.searchForm.qicheleibie) params['qicheleibie'] = '%' + this.searchForm.qicheleibie + '%'
      if(this.searchForm.xingming) params['xingming'] = '%' + this.searchForm.xingming + '%'
      if(this.searchForm.sfsh) params['sfsh'] = this.searchForm.sfsh

      this.$http({
        url: "quxiaodingdan/page",
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
    payHandler(row){
      this.$storage.set('paytable','quxiaodingdan');
      this.$storage.set('payObject',row);
      this.$router.push('pay');
    },
    shDialog(row){
      this.sfshVisiable = true;
      this.shForm = {
        sfsh: row.sfsh === '待审核' ? '' : row.sfsh,
        shhf: row.shhf,
        id: row.id
      }
    },
    shHandler(){
      if(!this.shForm.sfsh){
        this.$message.error("请选择审核结果");
        return;
      }
      this.$confirm(`确定提交审核?`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        this.$http({
          url: "quxiaodingdan/update",
          method: "post",
          data: this.shForm
        }).then(({ data }) => {
          if (data && data.code === 0) {
            this.$message.success("审核成功");
            this.sfshVisiable = false;
            this.getDataList();
          } else {
            this.$message.error(data.msg);
          }
        });
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
          url: "quxiaodingdan/delete",
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
/* 增加表单和下方按钮区域的间距 */
.search-form-content {
  margin-bottom: 20px;
}

.btn-group {
  border-top: 1px solid #f0f0f0;
  padding-top: 20px;
}

.text-danger {
  color: #F56C6C;
  &:hover {
    color: #ff4949;
  }
}
</style>
