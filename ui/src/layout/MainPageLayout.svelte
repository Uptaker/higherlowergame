<script lang="ts">
  export let title = ''
</script>

<svelte:head>
  {#if title}
    <title>{title}</title>
  {/if}
</svelte:head>

<div class="min-h-screen bg-gradient-to-tl from-blue-200 via-slate-200 to-white">
    <div class="min-w-[360px] max-w-4xl mx-auto px-4">
        <slot/>
    </div>
</div>
