<script lang="ts">

  import {navigate} from "svelte-navigator";

  export let to: string
  export let target: string|undefined = undefined
  export let label = ''

  function clicked(e: Event) {
    if (target) return
    e.preventDefault()
    navigate(to)
  }
</script>

<a href={to} on:click={clicked} {target} {...$$restProps} class={$$props.class ?? 'text-link'}>
  <slot/>
  <span>{label}</span>
</a><slot name="suffix"/>
