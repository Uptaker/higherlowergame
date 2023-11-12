import './shared/ArrayExtensions'
import './global.css'
import 'highlight.js/styles/github.css'
import {initErrorHandlers} from './errorHandlers'
import App from './App.svelte'
import {initUser} from "src/stores/initUser";

(async function() {
  initErrorHandlers()
  await initUser()
  document.body.innerHTML = ''
  new App({target: document.body})
}())
