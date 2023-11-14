<script lang="ts">
    import MainPageLayout from 'src/layout/MainPageLayout.svelte'
    import type {GameSession} from "src/api/types";
    import {Category} from "src/api/types";
    import Button from "src/components/Button.svelte";
    import api from "src/api/api";
    import {navigate} from "svelte-navigator";
    import Card from "src/components/Card.svelte";
    import {humanReadableCategories} from "src/humanReadableUtils";

    let gameSessions: GameSession[]

    async function load() {
        gameSessions = await api.get('game-sessions')
    }

    async function pickCategory(category: Category) {
        const gameId = await api.post('game-sessions/start/' + category)
        navigate('/' + gameId)
    }

    $: highestScore = gameSessions?.max(s => s.score)
    $: load()
</script>

<MainPageLayout>
    <div class="flex flex-col items-center gap-10 mt-12 mb-40">
        <h1>Higher lower</h1>
        <div class="flex gap-4">
            {#each Object.values(Category) as category}
                <Button on:click={() => pickCategory(category)}>{humanReadableCategories[category]}</Button>
            {/each}
        </div>
    </div>


    {#if gameSessions?.length}
        <h2>My games</h2>
        <h3>Highest score: <b>{highestScore}</b></h3>
        <div class="flex flex-col gap-2 mb-10">
        {#each gameSessions as game}
            <div on:click={() => navigate('/' + game.id)}>
                <Card title="Score: {game.score}" subtitle={new Date(game.createdAt).toLocaleString()} padding="px-6"
                      class="hover:bg-slate-50 hover:cursor-pointer"/>
            </div>
        {/each}
        </div>
    {/if}
</MainPageLayout>
