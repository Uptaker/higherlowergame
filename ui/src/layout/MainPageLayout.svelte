<script lang="ts">
  export let title = ''
</script>

<svelte:head>
  {#if title}
    <title>{title}</title>
  {/if}
</svelte:head>

<div class="min-h-screen bg-gradient-to-tl from-blue-200 via-slate-200 to-white">
    <a class="text-slate-500 text-xs" href="https://github.com/Uptaker/higherlowergame" target="_blank">GitHub</a>
    <div class="min-w-[360px] max-w-4xl mx-auto px-4">
        <slot/>
    </div>
</div>
