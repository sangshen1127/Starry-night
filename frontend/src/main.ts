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

app.use(createPinia())
app.use(router)
app.mount('#app')
