import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import './styles/tokens.css'
import './styles/base.css'

const app = createApp(App)

app.config.errorHandler = (error) => {
  // 生产环境可替换成监控上报；开发期至少不要在控制台静默吞错。
  console.error('[Start Night]', error)
}

app.use(createPinia())  //全局状态管理
app.use(router)
app.mount('#app') //挂载到index.html的id为app的div上
