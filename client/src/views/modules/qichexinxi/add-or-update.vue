<template>
  <div class="addEdit-block">
    <el-form
        class="add-update-preview"
        ref="ruleForm"
        :model="ruleForm"
        :rules="rules"
        label-width="110px"
    >
      <div class="section-title">
        <span class="icon"></span>
        <span>基础信息</span>
      </div>
      <el-row :gutter="30">
        <el-col :span="12">
          <el-form-item label="车牌号" prop="chepaihao">
            <el-input v-model="ruleForm.chepaihao" placeholder="请输入车牌号" clearable :readonly="ro.chepaihao"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="车辆品牌" prop="cheliangpinpai">
            <el-input v-model="ruleForm.cheliangpinpai" placeholder="请输入车辆品牌" clearable :readonly="ro.cheliangpinpai"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="车辆型号" prop="cheliangxinghao">
            <el-input v-model="ruleForm.cheliangxinghao" placeholder="请输入车辆型号" clearable :readonly="ro.cheliangxinghao"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="汽车类别" prop="qicheleibie">
            <el-select v-if="type!='info'" :disabled="ro.qicheleibie" v-model="ruleForm.qicheleibie" placeholder="请选择汽车类别" style="width: 100%;">
              <el-option v-for="(item,index) in qicheleibieOptions" :key="index" :label="item" :value="item"></el-option>
            </el-select>
            <el-input v-else v-model="ruleForm.qicheleibie" placeholder="汽车类别" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="颜色" prop="yanse">
            <el-input v-model="ruleForm.yanse" placeholder="请输入颜色" clearable :readonly="ro.yanse"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="出厂年份" prop="chuchangnianfen">
            <el-input v-model="ruleForm.chuchangnianfen" placeholder="请输入出厂年份" clearable :readonly="ro.chuchangnianfen"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item class="upload" label="车辆照片" prop="cheliangzhaopian">
            <file-upload
                v-if="type!='info' && !ro.cheliangzhaopian"
                tip="点击上传车辆照片"
                action="file/upload"
                :limit="3"
                :multiple="true"
                :fileUrls="ruleForm.cheliangzhaopian?ruleForm.cheliangzhaopian:''"
                @change="cheliangzhaopianUploadChange"
            ></file-upload>
            <div v-else-if="ruleForm.cheliangzhaopian">
              <img v-if="ruleForm.cheliangzhaopian.substring(0,4)=='http'" class="upload-img" v-for="(item,index) in ruleForm.cheliangzhaopian.split(',')" :key="index" :src="item">
              <img v-else class="upload-img" v-for="(item,index) in ruleForm.cheliangzhaopian.split(',')" :key="index" :src="$base.url+item">
            </div>
          </el-form-item>
        </el-col>
      </el-row>

      <div class="section-title">
        <span class="icon"></span>
        <span>车辆参数</span>
      </div>
      <el-row :gutter="30">
        <el-col :span="12">
          <el-form-item label="租金(元/天)" prop="jiage">
            <el-input v-model="ruleForm.jiage" placeholder="请输入租金" clearable :readonly="ro.jiage"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="车辆原价" prop="qichejiage">
            <el-input v-model="ruleForm.qichejiage" placeholder="请输入车辆原价" clearable :readonly="ro.qichejiage">
              <template #append>万元</template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="换挡方式" prop="huandangfangshi">
            <el-select v-if="type!='info'" :disabled="ro.huandangfangshi" v-model="ruleForm.huandangfangshi" placeholder="请选择换挡方式" style="width: 100%;">
              <el-option v-for="(item,index) in huandangfangshiOptions" :key="index" :label="item" :value="item"></el-option>
            </el-select>
            <el-input v-else v-model="ruleForm.huandangfangshi" placeholder="换挡方式" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="座位数" prop="zuoweishu">
            <el-input v-model="ruleForm.zuoweishu" placeholder="请输入座位数" clearable :readonly="ro.zuoweishu"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="汽车排量" prop="qichepailiang">
            <el-input v-model="ruleForm.qichepailiang" placeholder="请输入汽车排量" clearable :readonly="ro.qichepailiang"></el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <div class="section-title">
        <span class="icon"></span>
        <span>管理与状态</span>
      </div>
      <el-row :gutter="30">
        <el-col :span="12">
          <el-form-item label="状态" prop="zhuangtai">
            <el-select v-if="type!='info'" :disabled="ro.zhuangtai" v-model="ruleForm.zhuangtai" placeholder="请选择状态" style="width: 100%;">
              <el-option v-for="(item,index) in zhuangtaiOptions" :key="index" :label="item" :value="item"></el-option>
            </el-select>
            <el-input v-else v-model="ruleForm.zhuangtai" placeholder="状态" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="登记日期" prop="dengjiriqi">
            <el-date-picker
                style="width: 100%;"
                v-if="type!='info'"
                format="yyyy 年 MM 月 dd 日"
                value-format="yyyy-MM-dd"
                v-model="ruleForm.dengjiriqi"
                type="date"
                :readonly="ro.dengjiriqi"
                placeholder="请选择登记日期">
            </el-date-picker>
            <el-input v-else v-model="ruleForm.dengjiriqi" placeholder="登记日期" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="管理账号" prop="guanlizhanghao">
            <el-input v-model="ruleForm.guanlizhanghao" placeholder="管理账号" clearable :readonly="ro.guanlizhanghao"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="管理姓名" prop="guanlixingming">
            <el-input v-model="ruleForm.guanlixingming" placeholder="管理姓名" clearable :readonly="ro.guanlixingming"></el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <div class="section-title">
        <span class="icon"></span>
        <span>车辆位置</span>
      </div>
      <el-form-item label="停放地点" prop="address">
        <div v-if="type!='info'" class="map-search-bar">
          <el-input
              v-model="searchKeyword"
              placeholder="输入地名搜索 (如: 北京西站)"
              style="width: 300px; margin-right: 10px;"
              clearable
              @keyup.enter.native="searchLocation"
          ></el-input>
          <el-button type="primary" icon="el-icon-search" @click="searchLocation">搜索</el-button>
        </div>

        <div style="margin-bottom: 10px;">
          <el-input
              v-model="ruleForm.address"
              placeholder="请在地图上点击选择位置，系统将自动识别地址"
              :readonly="type=='info'"
              style="width: 100%;">
            <template slot="prepend"><i class="el-icon-location-information"></i></template>
          </el-input>
          <input type="hidden" v-model="ruleForm.longitude">
          <input type="hidden" v-model="ruleForm.latitude">
        </div>

        <div id="map-container" style="width: 100%; height: 400px; border: 1px solid #dcdfe6; border-radius: 4px;"></div>
      </el-form-item>

      <div class="section-title">
        <span class="icon"></span>
        <span>详细介绍</span>
      </div>
      <el-form-item label="汽车简介" prop="qichejianjie">
        <editor
            v-if="type!='info'"
            style="min-width: 200px; max-width: 100%;"
            v-model="ruleForm.qichejianjie"
            class="editor"
            action="file/upload">
        </editor>
        <span v-else v-html="ruleForm.qichejianjie" class="rich-text-content"></span>
      </el-form-item>

      <div class="form-btn-group">
        <el-button v-if="type!='info'" type="primary" class="btn-success" @click="onSubmit">提交保存</el-button>
        <el-button v-if="type!='info'" class="btn-close" @click="back()">取消编辑</el-button>
        <el-button v-if="type=='info'" class="btn-close" @click="back()">返回列表</el-button>
      </div>
    </el-form>
  </div>
</template>

<script>
import { isNumber, isIntNumer, isEmail, isPhone, isMobile, isURL, checkIdCard } from "@/utils/validate";

export default {
  // 注意：这里删除了 props: ["parent"]
  data() {
    var validateNumber = (rule, value, callback) => {
      if(!value){ callback(); } else if (!isNumber(value)) { callback(new Error("请输入数字")); } else { callback(); }
    };
    var validateIntNumber = (rule, value, callback) => {
      if(!value){ callback(); } else if (!isIntNumer(value)) { callback(new Error("请输入整数")); } else { callback(); }
    };

    return {
      id: '',
      type: '',
      map: null,
      geoc: null,
      searchKeyword: '',

      ro: {
        chepaihao: false,
        cheliangxinghao: false,
        qicheleibie: false,
        cheliangpinpai: false,
        jiage: false,
        yanse: false,
        zhuangtai: false,
        huandangfangshi: false,
        cheliangzhaopian: false,
        zuoweishu: false,
        qichepailiang: false,
        qichejiage: false,
        chuchangnianfen: false,
        dengjiriqi: false,
        qichejianjie: false,
        guanlizhanghao: false,
        guanlixingming: false,
        clicktime: false,
        clicknum: false,
        address: false
      },

      ruleForm: {
        chepaihao: '',
        cheliangxinghao: '',
        qicheleibie: '',
        cheliangpinpai: '',
        jiage: '',
        yanse: '',
        zhuangtai: '未出租',
        huandangfangshi: '',
        cheliangzhaopian: '',
        zuoweishu: '',
        qichepailiang: '',
        qichejiage: '',
        chuchangnianfen: '',
        dengjiriqi: '',
        qichejianjie: '',
        guanlizhanghao: '',
        guanlixingming: '',
        clicktime: '',
        longitude: '',
        latitude: '',
        address: ''
      },

      qicheleibieOptions: [],
      zhuangtaiOptions: [],
      huandangfangshiOptions: [],

      rules: {
        chepaihao: [{ required: true, message: '车牌号不能为空', trigger: 'blur' }],
        cheliangpinpai: [{ required: true, message: '车辆品牌不能为空', trigger: 'blur' }],
        jiage: [{ validator: validateNumber, trigger: 'blur' }],
        zuoweishu: [{ validator: validateIntNumber, trigger: 'blur' }],
        qichejiage: [{ validator: validateNumber, trigger: 'blur' }],
        clicknum: [{ validator: validateIntNumber, trigger: 'blur' }],
        address: [{ required: true, message: '请在地图上选择车辆位置', trigger: 'change' }]
      }
    };
  },
  created() {
    this.ruleForm.dengjiriqi = this.getCurDate();
  },
  methods: {
    init(id, type) {
      if (id) {
        this.id = id;
        this.type = type;
      }
      if (this.type == 'info' || this.type == 'else') {
        this.info(id);
      } else if (this.type == 'cross') {
        var obj = this.$storage.getObj('crossObj');
        for (var o in obj) {
          if (o == 'chepaihao') { this.ruleForm.chepaihao = obj[o]; this.ro.chepaihao = true; continue; }
          // ... 略过中间字段 ...
          if (o == 'longitude') { this.ruleForm.longitude = obj[o]; continue; }
          if (o == 'latitude') { this.ruleForm.latitude = obj[o]; continue; }
          if (o == 'address') { this.ruleForm.address = obj[o]; continue; }
        }
        this.ruleForm.zhuangtai = '未出租';
      }

      this.$http({
        url: `${this.$storage.get('sessionTable')}/session`,
        method: "get"
      }).then(({ data }) => {
        if (data && data.code === 0) {
          var json = data.data;
          if(((json.guanlizhanghao!=''&&json.guanlizhanghao) || json.guanlizhanghao==0) && this.$storage.get("role")!="管理员"){
            this.ruleForm.guanlizhanghao = json.guanlizhanghao
            this.ro.guanlizhanghao = true;
          }
          if(((json.guanlixingming!=''&&json.guanlixingming) || json.guanlixingming==0) && this.$storage.get("role")!="管理员"){
            this.ruleForm.guanlixingming = json.guanlixingming
            this.ro.guanlixingming = true;
          }
        }
      });

      this.$http({
        url: `option/qicheleibie/qicheleibie`,
        method: "get"
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.qicheleibieOptions = data.data;
        }
      });
      this.zhuangtaiOptions = "已出租,未出租".split(',');
      this.huandangfangshiOptions = "手动挡,自动挡".split(',');

      this.$nextTick(() => {
        this.initMap();
      });
    },

    // ---------------- 地图功能 ----------------
    initMap() {
      if (typeof BMap === 'undefined') return;
      this.map = new BMap.Map("map-container");
      this.geoc = new BMap.Geocoder();

      let point = new BMap.Point(116.404, 39.915);
      this.map.centerAndZoom(point, 15);
      this.map.enableScrollWheelZoom(true);

      if(this.ruleForm.longitude && this.ruleForm.latitude){
        let startPoint = new BMap.Point(this.ruleForm.longitude, this.ruleForm.latitude);
        this.map.centerAndZoom(startPoint, 16);
        this.setMapMarker(startPoint);
      }

      if (this.type !== 'info') {
        const self = this;
        this.map.addEventListener("click", function(e) {
          self.ruleForm.longitude = e.point.lng;
          self.ruleForm.latitude = e.point.lat;
          self.setMapMarker(e.point);

          self.geoc.getLocation(e.point, function(rs){
            let addr = rs.address;
            if(!addr) {
              var addComp = rs.addressComponents;
              addr = (addComp.province||"") + (addComp.city||"") + (addComp.district||"") + (addComp.street||"") + (addComp.streetNumber||"");
            }
            self.$set(self.ruleForm, 'address', addr);
            self.searchKeyword = addr;
            self.$forceUpdate();
            self.$refs['ruleForm'].validateField('address');
          });
        });
      }
    },

    searchLocation() {
      if (!this.searchKeyword || typeof BMap === 'undefined') return;
      const self = this;

      const local = new BMap.LocalSearch(this.map, {
        onSearchComplete: function(results) {
          if (local.getStatus() == BMAP_STATUS_SUCCESS) {
            const pp = results.getPoi(0);
            const point = pp.point;

            self.map.centerAndZoom(point, 16);
            self.setMapMarker(point);

            self.ruleForm.longitude = point.lng;
            self.ruleForm.latitude = point.lat;

            let finalAddress = pp.title + (pp.address ? " (" + pp.address + ")" : "");
            self.$set(self.ruleForm, 'address', finalAddress);
            self.searchKeyword = finalAddress;
            self.$forceUpdate();

            self.$refs['ruleForm'].validateField('address');
            self.$message.success(`已定位到: ${pp.title}`);
          } else {
            self.$message.error("未找到相关地址，请尝试更详细的关键词");
          }
        }
      });
      local.search(this.searchKeyword);
    },

    setMapMarker(point) {
      if (!this.map) return;
      this.map.clearOverlays();
      let marker = new BMap.Marker(point);
      this.map.addOverlay(marker);
      this.map.panTo(point);
    },

    info(id) {
      this.$http({
        url: `qichexinxi/info/${id}`,
        method: "get"
      }).then(({ data }) => {
        if (data && data.code === 0) {
          // 合并数据防止丢失
          this.ruleForm = { ...this.ruleForm, ...data.data };

          // 【注意】这里原本是直接回显数据库存的地址，如果数据库没存地址，这里就是空的
          if (this.ruleForm.address) {
            this.searchKeyword = this.ruleForm.address;
          }

          let reg = new RegExp('../../../upload', 'g');
          if(this.ruleForm.qichejianjie) {
            this.ruleForm.qichejianjie = this.ruleForm.qichejianjie.replace(reg, '../../../springboot1ma2x/upload');
          }

          // ---------- 修改开始：在地图渲染完成后，自动解析地址 ----------
          this.$nextTick(() => {
            if (typeof BMap !== 'undefined' && this.ruleForm.longitude && this.ruleForm.latitude) {
              let point = new BMap.Point(this.ruleForm.longitude, this.ruleForm.latitude);
              this.map.centerAndZoom(point, 16);
              this.setMapMarker(point);

              // 新增逻辑：如果当前经纬度存在，调用百度接口解析中文地址
              // 确保 geoc 对象存在（防止 initMap 还没跑完）
              let geocoder = this.geoc || new BMap.Geocoder();

              geocoder.getLocation(point, (rs) => {
                let addr = rs.address;
                if(!addr) {
                  var addComp = rs.addressComponents;
                  addr = (addComp.province||"") + (addComp.city||"") + (addComp.district||"") + (addComp.street||"") + (addComp.streetNumber||"");
                }

                // 将解析到的地址赋值给表单，这样输入框就会自动显示了
                this.$set(this.ruleForm, 'address', addr);
                this.searchKeyword = addr;
              });
            }
          });
          // ---------- 修改结束 ----------

        } else {
          this.$message.error(data.msg);
        }
      });
    },

    onSubmit() {
      if (this.ruleForm.cheliangzhaopian != null) {
        this.ruleForm.cheliangzhaopian = this.ruleForm.cheliangzhaopian.replace(new RegExp(this.$base.url, "g"), "");
      }

      this.$refs["ruleForm"].validate(valid => {
        if (valid) {
          this.$http({
            url: `qichexinxi/${!this.ruleForm.id ? "save" : "update"}`,
            method: "post",
            data: this.ruleForm
          }).then(({ data }) => {
            if (data && data.code === 0) {
              this.$message({
                message: "操作成功",
                type: "success",
                duration: 1500,
                onClose: () => {
                  // 修改点：使用 $emit 通知父组件
                  this.$emit('refreshDataList');
                }
              });
            } else {
              this.$message.error(data.msg);
            }
          });
        }
      });
    },

    getUUID() { return new Date().getTime(); },
    back() {
      // 修改点：使用 $emit 通知父组件
      this.$emit('cancel');
    },
    cheliangzhaopianUploadChange(fileUrls) {
      this.ruleForm.cheliangzhaopian = fileUrls;
    },
  }
};
</script>

<style lang="scss" scoped>
.addEdit-block {
  width: 100%;
  padding: 20px;
}

.add-update-preview {
  background: #ffffff;
  padding: 30px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 20px;
  padding-left: 10px;
  border-left: 4px solid #409EFF;
  line-height: 1.2;
  display: flex;
  align-items: center;

  &:not(:first-child) {
    margin-top: 30px;
    border-top: 1px dashed #eee;
    padding-top: 20px;
  }
}

.add-update-preview .el-input /deep/ .el-input__inner,
.add-update-preview .el-select /deep/ .el-input__inner,
.add-update-preview .el-date-editor /deep/ .el-input__inner {
  height: 40px;
  line-height: 40px;
  border-radius: 4px;
  border: 1px solid #dcdfe6;
  &:focus {
    border-color: #409EFF;
  }
}

.add-update-preview /deep/ .el-upload--picture-card {
  background: #fbfdff;
  border: 1px dashed #c0ccda;
  border-radius: 6px;
  width: 100px;
  height: 100px;
  line-height: 100px;
}

.upload-img {
  width: 100px;
  height: 100px;
  border-radius: 6px;
  margin-right: 10px;
  object-fit: cover;
  border: 1px solid #eee;
}

.form-btn-group {
  text-align: center;
  margin-top: 40px;

  .el-button {
    min-width: 120px;
    margin: 0 15px;
  }
}

.rich-text-content {
  background: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
  display: block;
  min-height: 100px;
}

.map-search-bar {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}
</style>
