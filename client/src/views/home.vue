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
            <div class="card-panel-num">1,203</div>
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
            <div class="card-panel-num">8,450</div>
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
            <div class="card-panel-num">¥ 92,805</div>
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
            <div class="card-panel-num">126</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="24">
      <el-col :span="24">
        <div class="chart-wrapper">
          <div class="chart-header">系统月度数据概览</div>
          <div id="main-chart" style="width: 100%;height: 400px;"></div>
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
      chart: null
    };
  },
  mounted(){
    this.init();
    this.initChart();
    window.addEventListener('resize', this.resizeChart);
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.resizeChart);
    if (this.chart) {
      this.chart.dispose();
    }
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
          }
        });
      }else{
        router.push({ name: 'login' })
      }
    },
    resizeChart() {
      if (this.chart) {
        this.chart.resize();
      }
    },
    initChart() {
      // 初始化Echarts示例图表
      const chartDom = document.getElementById('main-chart');
      this.chart = echarts.init(chartDom);
      const option = {
        color: ['#00c292', '#36a3f7', '#f4516c'],
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
            data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
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
            barWidth: '30%',
            data: [120, 200, 150, 80, 70, 110, 130]
          },
          {
            name: '访问量',
            type: 'line',
            smooth: true,
            data: [220, 282, 291, 234, 290, 330, 310],
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
                offset: 0, color: 'rgba(54, 163, 247, 0.3)'
              }, {
                offset: 1, color: 'rgba(54, 163, 247, 0)'
              }])
            }
          }
        ]
      };
      this.chart.setOption(option);
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
