<template>
  <div class="main-content">
    <template v-if="showFlag">
      <div class="search-box-card">
        <el-form :inline="true" :model="searchForm" @submit.native.prevent="search()">
          <el-form-item label="车牌号">
            <el-input v-model="searchForm.chepaihao" placeholder="车牌号" clearable size="medium"></el-input>
          </el-form-item>
          <el-form-item label="姓名">
            <el-input v-model="searchForm.xingming" placeholder="客户姓名" clearable size="medium"></el-input>
          </el-form-item>
          <el-form-item label="审核状态">
            <el-select v-model="searchForm.sfsh" placeholder="选择状态" clearable size="medium">
              <el-option v-for="(item,index) in sfshOptions" :key="index" :label="item" :value="item"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" icon="el-icon-search" @click="search()" size="medium">查询</el-button>
            <el-button icon="el-icon-refresh" @click="resetForm" size="medium">重置</el-button>
          </el-form-item>
        </el-form>

        <el-row class="btn-group">
          <el-button v-if="isAuth('zuchedingdan','新增')" type="primary" icon="el-icon-plus" @click="addOrUpdateHandler()" size="medium">新增订单</el-button>
          <el-button v-if="isAuth('zuchedingdan','删除')" type="danger" icon="el-icon-delete" :disabled="dataListSelections.length <= 0" @click="deleteHandler()" size="medium">批量删除</el-button>
        </el-row>
      </div>

      <div class="table-box-card">
        <el-table :data="dataList" border v-loading="dataListLoading" @selection-change="selectionChangeHandler" style="width: 100%;">
          <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
          <el-table-column prop="dingdanbianhao" header-align="center" align="center" label="订单编号" width="180" fixed="left"></el-table-column>
          <el-table-column prop="chepaihao" header-align="center" align="center" label="车牌号"></el-table-column>
          <el-table-column prop="cheliangpinpai" header-align="center" align="center" label="品牌"></el-table-column>
          <el-table-column prop="jiage" header-align="center" align="center" label="日租金"></el-table-column>
          <el-table-column prop="zuqi" header-align="center" align="center" label="租期(天)"></el-table-column>
          <el-table-column prop="zongjia" header-align="center" align="center" label="总价">
            <template slot-scope="scope">
              <span style="color: #F56C6C; font-weight: bold;">{{ scope.row.zongjia }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="chuzuriqi" header-align="center" align="center" label="出租日期" width="100"></el-table-column>
          <el-table-column prop="xingming" header-align="center" align="center" label="客户姓名"></el-table-column>
          <el-table-column prop="shouji" header-align="center" align="center" label="手机号" width="120"></el-table-column>

          <el-table-column prop="ispay" header-align="center" align="center" label="支付状态" width="100">
            <template slot-scope="scope">
              <el-tag v-if="scope.row.ispay=='已支付'" type="success" size="small">已支付</el-tag>
              <el-button v-else type="primary" size="mini" plain @click="payHandler(scope.row)">去支付</el-button>
            </template>
          </el-table-column>

          <el-table-column prop="sfsh" header-align="center" align="center" label="审核状态">
            <template slot-scope="scope">
              <el-tag v-if="scope.row.sfsh=='是'" type="success" effect="dark" size="small">通过</el-tag>
              <el-tag v-else-if="scope.row.sfsh=='否'" type="danger" effect="dark" size="small">拒绝</el-tag>
              <el-tag v-else type="warning" effect="dark" size="small">待审核</el-tag>
            </template>
          </el-table-column>

          <el-table-column header-align="center" align="center" width="260" label="操作" fixed="right">
            <template slot-scope="scope">
              <el-button v-if="isAuth('zuchedingdan','审核') && scope.row.sfsh=='待审核'" type="warning" size="mini" @click="shDialog(scope.row)">审核</el-button>

              <el-button v-if="isAuth('zuchedingdan','查看')" type="text" size="small" @click="addOrUpdateHandler(scope.row.id,'info')">详情</el-button>
              <el-button v-if="isAuth('zuchedingdan','归还汽车')" type="text" size="small" @click="haichexinxiCrossAddOrUpdateHandler(scope.row,'cross','是','[1]','你已归还')">还车</el-button>
              <el-button v-if="isAuth('zuchedingdan','取消订单')" type="text" size="small" style="color:#E6A23C" @click="quxiaodingdanCrossAddOrUpdateHandler(scope.row,'cross','是','[1]','你已取消')">取消</el-button>
              <el-button v-if="isAuth('zuchedingdan','删除')" type="text" class="text-danger" size="small" @click="deleteHandler(scope.row.id)">删除</el-button>
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
    <haichexinxi-cross-add-or-update v-if="haichexinxiCrossAddOrUpdateFlag" :parent="this" ref="haichexinxiCrossaddOrUpdate"></haichexinxi-cross-add-or-update>
    <quxiaodingdan-cross-add-or-update v-if="quxiaodingdanCrossAddOrUpdateFlag" :parent="this" ref="quxiaodingdanCrossaddOrUpdate"></quxiaodingdan-cross-add-or-update>

    <el-dialog title="订单审核" :visible.sync="sfshVisiable" width="40%" center>
      <el-form ref="form" :model="shForm" label-width="80px">
        <el-form-item label="审核结果">
          <el-radio-group v-model="shForm.sfsh">
            <el-radio label="是" border>通过</el-radio>
            <el-radio label="否" border>不通过</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核意见">
          <el-input type="textarea" :rows="4" v-model="shForm.shhf" placeholder="请输入审核回复内容..."></el-input>
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
import haichexinxiCrossAddOrUpdate from "../haichexinxi/add-or-update";
import quxiaodingdanCrossAddOrUpdate from "../quxiaodingdan/add-or-update";
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
      haichexinxiCrossAddOrUpdateFlag: false,
      quxiaodingdanCrossAddOrUpdateFlag: false,
      sfshOptions: ['是','否','待审核']
    };
  },
  components: {
    AddOrUpdate,
    haichexinxiCrossAddOrUpdate,
    quxiaodingdanCrossAddOrUpdate,
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
        ...this.searchForm
      }
      // 简单处理参数
      for(let key in params){
        if(params[key] === '') delete params[key];
        else if(key != 'page' && key != 'limit' && key != 'sort' && key != 'order' && key != 'sfsh') {
          params[key] = '%' + params[key] + '%'
        }
      }
      this.$http({
        url: "zuchedingdan/page",
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
    haichexinxiCrossAddOrUpdateHandler(row,type,crossOptAudit,statusColumnName,tips,statusColumnValue){
      if(crossOptAudit=='是'&&row.sfsh!='是') {
        this.$message.warning("请审核通过后再操作");
        return
      }
      this.showFlag = false;
      this.addOrUpdateFlag = false;
      this.haichexinxiCrossAddOrUpdateFlag = true;
      this.$storage.set('crossObj',row);
      this.$storage.set('crossTable','zuchedingdan');
      this.$storage.set('statusColumnName',statusColumnName);
      this.$storage.set('statusColumnValue',statusColumnValue);
      this.$storage.set('tips',tips);
      // ... (原有逻辑保持不变)
      this.$nextTick(() => {
        this.$refs.haichexinxiCrossaddOrUpdate.init(row.id,type);
      });
    },
    quxiaodingdanCrossAddOrUpdateHandler(row,type,crossOptAudit,statusColumnName,tips,statusColumnValue){
      if(crossOptAudit=='是'&&row.sfsh!='是') {
        this.$message.warning("请审核通过后再操作");
        return
      }
      this.showFlag = false;
      this.addOrUpdateFlag = false;
      this.quxiaodingdanCrossAddOrUpdateFlag = true;
      this.$storage.set('crossObj',row);
      this.$storage.set('crossTable','zuchedingdan');
      // ... (原有逻辑保持不变)
      this.$nextTick(() => {
        this.$refs.quxiaodingdanCrossaddOrUpdate.init(row.id,type);
      });
    },
    payHandler(row){
      this.$storage.set('paytable','zuchedingdan');
      this.$storage.set('payObject',row);
      this.$router.push('pay');
    },
    shDialog(row){
      this.sfshVisiable = true;
      this.shForm = {
        // 只保留审核需要的字段即可，或者像原来一样全赋值
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
          url: "zuchedingdan/update",
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
          url: "zuchedingdan/delete",
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
