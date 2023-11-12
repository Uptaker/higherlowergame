import {get, writable} from 'svelte/store'
export enum ToastType {
  SUCCESS = 'success', WARNING = 'warning', INFO = 'info', ERROR = 'error'
}

interface ToastOptions {
  title?: string
  type: ToastType
  timeoutSec: number
  html?: boolean
}

interface Toast extends ToastOptions {
  id: number
  message: string
}

export const toastStore = writable<Array<Toast>>([])

export function versionRefreshNeeded() {
  const title = 'Application updated'
  if (get(toastStore).find(t => t.title == title)) return
  const message = 'Refreshing the page is needed for correct functioning' + `
    <a onclick="location.reload()" class="btn success block mt-3">Refresh now</a>
  `
  showToast(message, {title, type: ToastType.WARNING, timeoutSec: 10000, html: true})
}

export function showToast(message: string, options?: Partial<ToastOptions>): Toast {
  const toast: Toast = {id: Math.random(), type: ToastType.SUCCESS, timeoutSec: options?.type == ToastType.ERROR ? 10 : 5, message, ...options}
  toastStore.update(a => [...a, toast])
  setTimeout(() => hideToast(toast), toast.timeoutSec * 1000)
  return toast
}

export function hideToast(toast: Toast) {
  toastStore.update(a => a.filter(t => t !== toast))
}
