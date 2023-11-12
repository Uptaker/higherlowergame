import './shared/ArrayExtensions'
import './global.css'
import 'highlight.js/styles/github.css'
import {initErrorHandlers} from './errorHandlers'
import App from './App.svelte'

(async function() {
  initErrorHandlers()
  document.body.innerHTML = ''
  new App({target: document.body})
}())
