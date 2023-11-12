<script lang="ts">
    import icons from './icons'

    export let name: string
  export let size: '' | 'sm' | 'lg' | 'xl' | 'xxl' | 'xxxl' | 'xxxxl' = ''
</script>

{@html icons[name].replace('<svg', `<svg class="icon ${name} ${size} ${$$props.class ?? ''}" focusable="false"`)}

<style global>
  .icon {
    @apply w-5 h-5
  }

  .icon.sm {
    @apply w-4 h-4
  }

  .icon.lg {
    @apply w-6 h-6
  }

  .icon.xl {
      @apply w-10 h-10
  }

  .icon.xxl {
      @apply w-16 h-16
  }

  .icon.xxxl {
      @apply w-28 h-28
  }

  .icon.xxxxl {
      @apply w-36 h-36
  }
</style>
