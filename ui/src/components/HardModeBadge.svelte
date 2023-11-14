<script lang="ts">
    import Badge from "src/components/Badge.svelte";

    export let hard = false
</script>

<div>
    {#if hard}
        <Badge class="!font-bold" title="probably not though, let's be real here for a sec">🔥 HARD 🔥</Badge>
    {/if}
</div>
