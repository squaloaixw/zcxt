<template>
  <div class="dashboard-editor-container">
    <div class="welcome-panel">
      <div class="welcome-content">
        <div class="avatar-wrapper">
          <img src="@/assets/img/avator.png" class="user-avatar">
        </div>
        <div class="text-content">
          <h1 class="display-name">早安，{{ this.$storage.get('adminName') }}，祝你开心每一天！</h1>
          <p class="weather-text">{{ projectTitle }} | {{ currentDate }}</p>
        </div>
      </div>
      <div class="stat-items">
        <div class="stat-item">
          <div class="stat-title">当前角色</div>
          <div class="stat-value">{{ this.$storage.get('role') }}</div>
        </div>
      </div>
    </div>

    <el-row :gutter="24" class="panel-group">
      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <div class="card-panel">
          <div class="card-panel-icon-wrapper icon-people">
            <i class="el-icon-user-solid card-panel-icon"></i>
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">总用户数</div>
            <div class="card-panel-num">{{ homeData.yonghuCount }}</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <div class="card-panel">
          <div class="card-panel-icon-wrapper icon-message">
            <i class="el-icon-s-order card-panel-icon"></i>
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">总订单数</div>
            <div class="card-panel-num">{{ homeData.dingdanCount }}</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <div class="card-panel">
          <div class="card-panel-icon-wrapper icon-money">
            <i class="el-icon-s-data card-panel-icon"></i>
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">总交易额</div>
            <div class="card-panel-num">¥ {{ homeData.totalMoney }}</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
        <div class="card-panel">
          <div class="card-panel-icon-wrapper icon-shopping">
            <i class="el-icon-truck card-panel-icon"></i>
          </div>
          <div class="card-panel-description">
            <div class="card-panel-text">车辆库存</div>
            <div class="card-panel-num">{{ homeData.qicheCount }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="24">
      <el-col :span="24">
        <div class="chart-wrapper">
          <div class="chart-header" style="display: flex; justify-content: space-between; align-items: center;">
            <span>系统订单趋势分析</span>
            <el-radio-group v-model="timeType" size="mini" @change="getChartData">
              <el-radio-button label="day">按日</el-radio-button>
              <el-radio-button label="month">按月</el-radio-button>
              <el-radio-button label="year">按年</el-radio-button>
            </el-radio-group>
          </div>
          <div id="main-chart" style="width: 100%;height: 400px;"></div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="24">
      <el-col :span="24">
        <div class="chart-wrapper">
          <div class="chart-header" style="display: flex; justify-content: space-between; align-items: center;">
            <span>销售额统计</span>
            <el-radio-group v-model="timeTypeAmount" size="mini" @change="getChartAmountData">
              <el-radio-button label="day">按日</el-radio-button>
              <el-radio-button label="month">按月</el-radio-button>
              <el-radio-button label="year">按年</el-radio-button>
            </el-radio-group>
          </div>
          <div id="amount-chart" style="width: 100%;height: 400px;"></div>
        </div>
      </el-col>
    </el-row>
    <el-row :gutter="24">
      <el-col :span="12">
        <div class="chart-wrapper">
          <div class="chart-header">车型销售额占比</div>
          <div id="leibie-chart" style="width: 100%;height: 400px;"></div>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="chart-wrapper">
          <div class="chart-header">品牌销售额占比</div>
          <div id="pinpai-chart" style="width: 100%;height: 400px;"></div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import router from '@/router/router-static'
import * as echarts from 'echarts'

export default {
  data() {
    return {
      projectTitle: this.$project.projectName,
      currentDate: new Date().toLocaleDateString(),

      // 统计卡片数据
      homeData: {
        yonghuCount: 0,
        dingdanCount: 0,
        totalMoney: 0,
        qicheCount: 0
      },

      // 图表实例
      chart: null,        // 订单数量趋势图
      chartAmount: null,  // 销售额趋势图
      chartLeibie: null,  // 车型分布饼图
      chartPinpai: null,  // 品牌分布饼图

      // 筛选条件
      timeType: 'day',       // 订单数量时间维度：day, month, year
      timeTypeAmount: 'day'  // 销售额时间维度：day, month, year
    };
  },
  mounted(){
    this.init();
    window.addEventListener('resize', this.resizeAllCharts);
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.resizeAllCharts);
    // 销毁所有图表实例，防止内存泄漏
    if (this.chart) this.chart.dispose();
    if (this.chartAmount) this.chartAmount.dispose();
    if (this.chartLeibie) this.chartLeibie.dispose();
    if (this.chartPinpai) this.chartPinpai.dispose();
  },
  methods:{
    init(){
      if(this.$storage.get('Token')){
        this.$http({
          url: `${this.$storage.get('sessionTable')}/session`,
          method: "get"
        }).then(({ data }) => {
          if (data && data.code != 0) {
            router.push({ name: 'login' })
          } else {
            // 登录验证通过后加载所有数据
            this.getHomeStats();       // 加载卡片数据
            this.getChartData();       // 加载订单趋势图
            this.getChartAmountData(); // 加载销售额趋势图
            this.getChartLeibieData(); // 加载类别饼图
            this.getChartPinpaiData(); // 加载品牌饼图
          }
        });
      }else{
        router.push({ name: 'login' })
      }
    },

    // 1. 获取首页卡片统计数据
    getHomeStats() {
      this.$http({
        url: "index/count",
        method: "get"
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.homeData = data.data;
        }
      });
    },

    // 2. 获取订单数量趋势数据 (支持日/月/年切换)
    getChartData() {
      this.$http({
        url: `zuchedingdan/chart/${this.timeType}`,
        method: "get"
      }).then(({ data }) => {
        if (data && data.code === 0) {
          let xData = [];
          let yData = [];
          if (data.data && data.data.length > 0) {
            data.data.forEach(item => {
              xData.push(item.x);
              yData.push(item.y);
            });
          }
          this.renderChart(xData, yData);
        }
      });
    },
    // 渲染订单趋势图
    renderChart(xData, yData) {
      const chartDom = document.getElementById('main-chart');
      if (!chartDom) return;

      if (this.chart) {
        this.chart.clear();
      } else {
        this.chart = echarts.init(chartDom);
      }

      // 空数据处理
      if(xData.length === 0) {
        xData = ['暂无数据'];
        yData = [0];
      }

      const option = {
        title: { text: '' },
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: [
          {
            type: 'category',
            data: xData,
            axisTick: { alignWithLabel: true },
            axisLine: { lineStyle: { color: '#eee' } },
            axisLabel: { color: '#666' }
          }
        ],
        yAxis: [
          {
            type: 'value',
            axisLine: { show: false },
            axisTick: { show: false },
            splitLine: { lineStyle: { color: '#eee' } }
          }
        ],
        series: [
          {
            name: '订单量',
            type: 'bar',
            barWidth: '40%',
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#36a3f7' },
                { offset: 1, color: '#68c5fc' }
              ])
            },
            data: yData
          },
          {
            name: '趋势',
            type: 'line',
            smooth: true,
            itemStyle: { color: '#f4516c' }, // 红色折线
            data: yData
          }
        ]
      };
      this.chart.setOption(option);
    },

    // 3. 获取销售额趋势数据 (支持日/月/年切换)
    getChartAmountData() {
      this.$http({
        url: `zuchedingdan/chart/amount/${this.timeTypeAmount}`,
        method: "get"
      }).then(({ data }) => {
        if (data && data.code === 0) {
          let xData = [];
          let yData = [];
          if (data.data && data.data.length > 0) {
            data.data.forEach(item => {
              xData.push(item.x);
              yData.push(item.y);
            });
          }
          this.renderAmountChart(xData, yData);
        }
      });
    },
    // 渲染销售额图表
    renderAmountChart(xData, yData) {
      const chartDom = document.getElementById('amount-chart');
      if (!chartDom) return;

      if (this.chartAmount) {
        this.chartAmount.clear();
      } else {
        this.chartAmount = echarts.init(chartDom);
      }

      if(xData.length === 0) {
        xData = ['暂无数据'];
        yData = [0];
      }

      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'shadow' },
          formatter: '{b}<br />{a}: ¥{c}' // 格式化提示框加人民币符号
        },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: [{
          type: 'category',
          data: xData,
          axisTick: { alignWithLabel: true },
          axisLine: { lineStyle: { color: '#eee' } },
          axisLabel: { color: '#666' }
        }],
        yAxis: [{
          type: 'value',
          axisLine: { show: false },
          splitLine: { lineStyle: { color: '#eee' } }
        }],
        series: [{
          name: '销售额',
          type: 'bar',
          barWidth: '40%',
          itemStyle: { color: '#f4516c' }, // 粉色柱状图区分
          data: yData
        }]
      };
      this.chartAmount.setOption(option);
    },

    // 4. 获取车型销售额占比 (饼图)
    getChartLeibieData() {
      this.$http({
        url: `zuchedingdan/chart/leibie/amount`,
        method: "get"
      }).then(({ data }) => {
        if (data && data.code === 0) {
          let pData = [];
          if (data.data && data.data.length > 0) {
            data.data.forEach(item => {
              pData.push({ name: item.x, value: item.y });
            });
          }
          this.renderPieChart('leibie-chart', pData, 'chartLeibie', '车型分布');
        }
      });
    },

    // 5. 获取品牌销售额占比 (饼图)
    getChartPinpaiData() {
      this.$http({
        url: `zuchedingdan/chart/pinpai/amount`,
        method: "get"
      }).then(({ data }) => {
        if (data && data.code === 0) {
          let pData = [];
          if (data.data && data.data.length > 0) {
            data.data.forEach(item => {
              pData.push({ name: item.x, value: item.y });
            });
          }
          this.renderPieChart('pinpai-chart', pData, 'chartPinpai', '品牌分布');
        }
      });
    },

    // 通用饼图渲染函数
    renderPieChart(domId, data, chartInstanceName, name) {
      const chartDom = document.getElementById(domId);
      if (!chartDom) return;

      // 动态获取/初始化实例
      if (this[chartInstanceName]) {
        this[chartInstanceName].clear();
      } else {
        this[chartInstanceName] = echarts.init(chartDom);
      }

      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: ¥{c} ({d}%)' // 显示金额和百分比
        },
        legend: {
          orient: 'vertical',
          left: 'left',
          type: 'scroll' // 如果项很多，允许滚动
        },
        series: [
          {
            name: name,
            type: 'pie',
            radius: ['40%', '70%'], // 环形图效果
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 5,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: { show: false, position: 'center' },
            emphasis: {
              label: { show: true, fontSize: '18', fontWeight: 'bold' }
            },
            labelLine: { show: false },
            data: data
          }
        ]
      };
      this[chartInstanceName].setOption(option);
    },

    // 窗口大小改变时重绘所有图表
    resizeAllCharts() {
      if (this.chart) this.chart.resize();
      if (this.chartAmount) this.chartAmount.resize();
      if (this.chartLeibie) this.chartLeibie.resize();
      if (this.chartPinpai) this.chartPinpai.resize();
    }
  }
};
</script>
<style lang="scss" scoped>
.dashboard-editor-container {
  padding-bottom: 20px;
}

/* 欢迎面板 */
.welcome-panel {
  background: #fff;
  padding: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
  margin-bottom: 24px;

  .welcome-content {
    display: flex;
    align-items: center;

    .avatar-wrapper {
      margin-right: 20px;
      .user-avatar {
        width: 72px;
        height: 72px;
        border-radius: 50%;
      }
    }

    .text-content {
      .display-name {
        font-size: 20px;
        color: #333;
        margin-bottom: 12px;
        font-weight: 600;
      }
      .weather-text {
        color: #999;
        font-size: 14px;
      }
    }
  }

  .stat-items {
    display: flex;
    .stat-item {
      text-align: right;
      padding: 0 20px;
      .stat-title {
        font-size: 14px;
        color: #999;
        margin-bottom: 6px;
      }
      .stat-value {
        font-size: 24px;
        color: #333;
        font-weight: 600;
      }
    }
  }
}

/* 卡片组 */
.panel-group {
  margin-bottom: 18px;

  .card-panel-col {
    margin-bottom: 24px;
  }

  .card-panel {
    height: 108px;
    cursor: pointer;
    font-size: 12px;
    position: relative;
    overflow: hidden;
    color: #666;
    background: #fff;
    box-shadow: 0 1px 4px rgba(0,21,41,.08);
    border-radius: 8px;
    display: flex;
    align-items: center;
    transition: all 0.3s;

    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 4px 12px rgba(0,0,0,.1);

      .card-panel-icon-wrapper {
        color: #fff;
      }
      .icon-people { background: #40c9c6; }
      .icon-message { background: #36a3f7; }
      .icon-money { background: #f4516c; }
      .icon-shopping { background: #00c292; }
    }

    .card-panel-icon-wrapper {
      float: left;
      margin: 14px 0 0 14px;
      padding: 16px;
      transition: all 0.38s ease-out;
      border-radius: 6px;

      .card-panel-icon {
        float: left;
        font-size: 48px;
      }
    }

    .icon-people { color: #40c9c6; }
    .icon-message { color: #36a3f7; }
    .icon-money { color: #f4516c; }
    .icon-shopping { color: #00c292; }

    .card-panel-description {
      float: right;
      font-weight: bold;
      margin: 26px;
      margin-left: 0px;
      flex: 1;
      text-align: right;

      .card-panel-text {
        line-height: 18px;
        color: rgba(0, 0, 0, 0.45);
        font-size: 16px;
        margin-bottom: 12px;
      }

      .card-panel-num {
        font-size: 20px;
        color: #666;
      }
    }
  }
}

/* 图表容器 */
.chart-wrapper {
  background: #fff;
  padding: 16px 16px 0;
  margin-bottom: 32px;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);

  .chart-header {
    padding: 10px 0 20px 10px;
    font-size: 18px;
    font-weight: 600;
    color: #333;
    border-bottom: 1px solid #eee;
    margin-bottom: 20px;
  }
}
</style>
