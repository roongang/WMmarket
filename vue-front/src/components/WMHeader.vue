<template>
  <header>
    <img class="logo-img" alt="Vue logo" src="../assets/logo.png">
    WM MARKET
    <span class="signupBtn" type="button" v-on:click="signupToggle">
      <i class="fas fa-user-plus"></i>
    </span>
  </header>

  <!-- signup modal -->
  <div class="loginModalBlack" v-show="isSignup">
    <div class="loginModalWhite">
      <h5>회원가입창</h5>
      <!-- TODO : 이메일 중복체크 -->
      <label for="input_email">Email : </label>
      <input id="input_email" v-model="emailId" placeholder="이메일을 입력하세요">
      @
      <select v-model="emailDomain">
        <!-- TODO : 이메일 직접 입력 -->
        <option disabled value="">이메일 선택</option>
        <option>gmail.com</option>
        <option>naver.com</option>
        <option>daum.net</option>
      </select>
      <button v-on:click="checkEmailExist">이메일 중복 확인</button>
      <br>
      <label for="input_password">Password : </label>
      <input id="input_password" :type="passwordType" v-model="user.password" placeholder="비밀번호를 입력하세요">
      <button v-on:click="showPassword">{{passwordButtonText}}</button>
      <!-- TODO : 이미지 첨부 처리 -->
      <br>
      <label for="input_nickname">Nickname : </label>
      <input id="input_nickname" v-model="user.nickname" placeholder="닉네임을 입력하세요">
      <br>
      Role :
      <input type="radio" id="USER" value="USER" v-model="user.role">
      <label for="USER">USER</label>
      <input type="radio" id="ADMIN" value="ADMIN" v-model="user.role">
      <label for="ADMIN">ADMIN</label>
      <br>
      <button v-on:click="signup">확인</button>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
//import vueSession from 'vue-session';

export default {
  name: "WMHeader",
  data(){
    return{
      isSignup:false,
      user:{
        // signup info
        email:"",
        password:'',
        image:'none',
        nickname:'',
        role:''
      },
      passwordType:"password",
      passwordButtonText:"비밀번호 확인",
      emailId:'',
      emailDomain:''
    }
  },
  watch:{
    emailId(){
      this.user.email=this.emailId+"@"+this.emailDomain;
    },
    emailDomain(){
      this.user.email=this.emailId+"@"+this.emailDomain;
    }
  },
  methods:{
    signupToggle(){
      this.isSignup = !this.isSignup;
    },
    checkEmailExist(){
      axios.get('/api/v1/user/isExist',{
          params: {
            email: this.user.email
          }
      })
      .then(response => {
        if(response.data === true){
          alert("이미 가입된 이메일입니다.")
        }
        else{
          alert("가입 가능한 이메일입니다.")
        }
      })
      .catch(error => console.error(error))
    },
    signup(){
      axios.post('/api/v1/user', {
        email: this.user.email,
        password: this.user.password,
        image: this.user.image,
        nickname: this.user.nickname,
        role: this.user.role
      })
      .then(response => {
        console.info(response);
        alert("회원가입 성공!");
        this.isSignup=false;
        // sign in
        this.signIn();
      }).catch(error => {
        console.error(error);
        alert("회원가입 실패!");
      });
    },
    signIn(){
      axios.post('/api/v1/user/signIn',{
        email: this.user.email,
        password: this.user.password
      })
      .then(response => {
        console.log(response);
        console.log("로그인 성공~!");
      }).catch(error => {
        console.error(error);
      });
    },
    showPassword(){
      if(this.passwordType === "password"){
        this.passwordType = "text"
        this.passwordButtonText = "비밀번호 숨기기"
      }
      else{
        this.passwordType = "password"
        this.passwordButtonText = "비밀번호 확인"
      }
    }
  }
}
</script>

<style scoped>
body{
  margin: 0;
}
div{
  box-sizing: border-box;
}
.logo-img{
  width: 30px;
}
.signupBtn{
  position: fixed;
  right: 10px;
}
.loginModalBlack{
  width: 100%; height: 100%;
  background: rgba(0,0,0,0.5);
  position: fixed; padding: 20px;
}
.loginModalWhite{
  width: 70%; background: white;
  border-radius: 10px; padding: 20px;
}
</style>