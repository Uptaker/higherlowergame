<script lang="ts">
  export let title = ''
</script>

<svelte:head>
  {#if title}
    <title>{title}</title>
  {/if}
</svelte:head>

<div class="min-h-screen relative bg-gradient-to-tl from-orange-100 via-orange-200 to-orange-100">
  <div class="absolute inset-0 flex items-center justify-center -z-10"></div>
</div>
