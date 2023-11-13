<script lang="ts">
    import MainPageLayout from 'src/layout/MainPageLayout.svelte'
    import {Category, GameRound} from "src/api/types";
    import Button from "src/components/Button.svelte";
    import api from "src/api/api";

    export let gameSessionId: string
    let gameRound: GameRound

    const humanReadableCategories = {
        VOTE_AVERAGE: "Vote average",
        POPULARITY: "Popularity",
        RUNTIME: "Runtime",
        REVENUE: "Revenue"
    }

    async function load() {
        await api.get(`game-sessions/${gameSessionId}/round`)
    }

    $: load()

    async function pickCategory(category: Category) {
        await api.post('game-sessions/start/' + category)
    }
</script>

<MainPageLayout>
    <p>Choose category</p>
    <div class="flex gap-4">
        {#each Object.values(Category) as category}
            <Button on:click={() => pickCategory(category)}>{humanReadableCategories[category]}</Button>
        {/each}
    </div>
</MainPageLayout>
