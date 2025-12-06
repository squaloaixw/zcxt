<template>
  <div class="login-wrapper">
    <div class="login-container">
      <div class="login-left">
        <div class="login-mask">
          <h2 class="sys-title">汽车租赁管理系统</h2>
          <p class="sys-desc">专业 · 便捷 · 安全的出行管家</p>
        </div>
      </div>

      <div class="login-form-box">
        <h3 class="login-title">欢迎登录</h3>
        <el-form class="login-form">
          <div v-if="loginType==1" class="form-group">
            <div class="input-icon"><i class="el-icon-user"></i></div>
            <input
                class="custom-input"
                placeholder="请输入用户名"
                name="username"
                type="text"
                v-model="rulesForm.username"
            >
          </div>

          <div v-if="loginType==1" class="form-group">
            <div class="input-icon"><i class="el-icon-lock"></i></div>
            <input
                class="custom-input"
                placeholder="请输入密码"
                name="password"
                type="password"
                v-model="rulesForm.password"
            >
          </div>

          <div class="role-group" v-if="roles.length>1">
            <el-radio-group v-model="rulesForm.role">
              <el-radio v-for="item in roles" v-bind:key="item.roleName" :label="item.roleName">{{item.roleName}}</el-radio>
            </el-radio-group>
          </div>

          <div class="btn-group">
            <el-button v-if="loginType==1" type="primary" @click="login()" class="login-btn">立即登录</el-button>
            <el-button type="text" @click="register('putongguanliyuan')" class="register-btn">注册普通管理员账号</el-button>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import menu from "@/utils/menu";
export default {
  data() {
    return {
      baseUrl:this.$base.url,
      loginType: 1,
      rulesForm: {
        username: "",
        password: "",
        role: "",
        code: '',
      },
      menus: [],
      roles: [],
      tableName: "",
      codes: [{
        num: 1, color: '#000', rotate: '10deg', size: '16px'
      },{
        num: 2, color: '#000', rotate: '10deg', size: '16px'
      },{
        num: 3, color: '#000', rotate: '10deg', size: '16px'
      },{
        num: 4, color: '#000', rotate: '10deg', size: '16px'
      }],
    };
  },
  mounted() {
    let menus = menu.list();
    this.menus = menus;
    for (let i = 0; i < this.menus.length; i++) {
      if (this.menus[i].hasBackLogin=='是') {
        this.roles.push(this.menus[i])
      }
    }
  },
  created() {
    this.getRandCode()
  },
  methods: {
    register(tableName){
      this.$storage.set("loginTable", tableName);
      this.$storage.set("pageFlag", "register");
      this.$router.push({path:'/register'})
    },
    login() {
      if (!this.rulesForm.username) {
        this.$message.error("请输入用户名");
        return;
      }
      if (!this.rulesForm.password) {
        this.$message.error("请输入密码");
        return;
      }
      if(this.roles.length>1) {
        if (!this.rulesForm.role) {
          this.$message.error("请选择角色");
          return;
        }
        let menus = this.menus;
        for (let i = 0; i < menus.length; i++) {
          if (menus[i].roleName == this.rulesForm.role) {
            this.tableName = menus[i].tableName;
          }
        }
      } else {
        this.tableName = this.roles[0].tableName;
        this.rulesForm.role = this.roles[0].roleName;
      }
      this.$http({
        url: `${this.tableName}/login?username=${this.rulesForm.username}&password=${this.rulesForm.password}`,
        method: "post"
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.$storage.set("Token", data.token);
          this.$storage.set("role", this.rulesForm.role);
          this.$storage.set("sessionTable", this.tableName);
          this.$storage.set("adminName", this.rulesForm.username);
          this.$router.replace({ path: "/index/" });
        } else {
          this.$message.error(data.msg);
        }
      });
    },
    getRandCode(len = 4){
      this.randomString(len)
    },
    randomString(len = 4) {
      let chars = ["a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"]
      let colors = ["0", "1", "2","3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"]
      let sizes = ['14', '15', '16', '17', '18']
      for (let i = 0; i < len; i++) {
        let key = Math.floor(Math.random()*chars.length)
        this.codes[i].num = chars[key]
        let code = '#'
        for (let j = 0; j < 6; j++) {
          let key = Math.floor(Math.random()*colors.length)
          code += colors[key]
        }
        this.codes[i].color = code
        let rotate = Math.floor(Math.random()*60)
        let plus = Math.floor(Math.random()*2)
        if(plus == 1) rotate = '-'+rotate
        this.codes[i].rotate = 'rotate('+rotate+'deg)'
        let size = Math.floor(Math.random()*sizes.length)
        this.codes[i].size = sizes[size]+'px'
      }
    },
  }
};
</script>

<style lang="scss" scoped>
.login-wrapper {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: url(http://codegen.caihongy.cn/20221011/e8a93776b74f4f60816fea7cb08f0eab.jpg);
  background-size: cover;
  background-position: center;

  /* 添加一层深色遮罩让文字更清晰 */
  &::before {
    content: "";
    position: absolute;
    top: 0; left: 0; right: 0; bottom: 0;
    background: rgba(0, 0, 0, 0.3);
    z-index: 0;
  }
}

.login-container {
  z-index: 1;
  display: flex;
  width: 900px;
  height: 500px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.2);
  overflow: hidden;

  .login-left {
    flex: 1;
    background: linear-gradient(135deg, #00c292 0%, #0b417c 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;

    .login-mask {
      text-align: center;
      padding: 20px;

      .sys-title {
        font-size: 28px;
        font-weight: bold;
        margin-bottom: 15px;
        letter-spacing: 2px;
      }
      .sys-desc {
        font-size: 14px;
        opacity: 0.8;
      }
    }
  }

  .login-form-box {
    flex: 1;
    padding: 40px 50px;
    display: flex;
    flex-direction: column;
    justify-content: center;

    .login-title {
      font-size: 24px;
      color: #333;
      margin-bottom: 30px;
      text-align: center;
      font-weight: 600;
    }

    .form-group {
      position: relative;
      margin-bottom: 25px;

      .input-icon {
        position: absolute;
        left: 10px;
        top: 50%;
        transform: translateY(-50%);
        color: #999;
        font-size: 18px;
      }

      .custom-input {
        width: 100%;
        height: 44px;
        padding-left: 40px;
        border: 1px solid #e0e0e0;
        border-radius: 4px;
        font-size: 14px;
        outline: none;
        transition: border-color 0.3s;

        &:focus {
          border-color: #00c292;
        }
      }
    }

    .role-group {
      margin-bottom: 25px;
      text-align: center;
    }

    .btn-group {
      display: flex;
      flex-direction: column;
      align-items: center;

      .login-btn {
        width: 100%;
        height: 44px;
        font-size: 16px;
        background: linear-gradient(to right, #00c292, #0ba360);
        border: none;
        box-shadow: 0 4px 10px rgba(0, 194, 146, 0.3);

        &:hover {
          opacity: 0.9;
        }
      }

      .register-btn {
        margin-top: 15px;
        color: #666;
        &:hover {
          color: #00c292;
        }
      }
    }
  }
}
</style>
