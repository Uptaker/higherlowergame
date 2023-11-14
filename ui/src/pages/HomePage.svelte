<script lang="ts">
    import MainPageLayout from 'src/layout/MainPageLayout.svelte'
    import type {GameSession} from "src/api/types";
    import {Category} from "src/api/types";
    import Button from "src/components/Button.svelte";
    import api from "src/api/api";
    import {navigate} from "svelte-navigator";
    import Card from "src/components/Card.svelte";
    import {humanReadableCategories} from "src/humanReadableUtils";
    import ScoresCard from "src/components/ScoresCard.svelte";
    import CheckboxField from "src/forms/CheckboxField.svelte";
    import HardModeBadge from "src/components/HardModeBadge.svelte";

    let gameSessions: GameSession[]
    let hardMode = false

    async function load() {
        gameSessions = await api.get('game-sessions')
    }

    async function pickCategory(category: Category) {
        const query = ('game-sessions/start/' + category).concat(hardMode ? '?hardMode=true' : '')
        const gameId = await api.post(query)
        navigate('/' + gameId)
    }

    $: load()
</script>

<MainPageLayout>
    <div class="flex flex-col items-center gap-10 mt-12 mb-40">
        <div class="flex gap-4">
            {#each Object.values(Category) as category}
                <Button on:click={() => pickCategory(category)}>{humanReadableCategories[category]}</Button>
            {/each}
        </div>
        <CheckboxField bind:checked={hardMode} label="Hard mode"/>
    </div>



    {#if gameSessions?.length}
        <ScoresCard/>

        <h2>My games</h2>
        <div class="flex flex-col gap-2 mb-10">
            {#each gameSessions as game}
                <div on:click={() => navigate('/' + game.id)}>
                    <Card padding="px-6" class="hover:bg-slate-50 hover:cursor-pointer">
                        <div class="flex justify-between my-5">
                            <div class="text-lg">Score: <span class="font-extrabold">{game.score}</span></div>
                            <HardModeBadge hard={game.hard}/>
                        </div>
                        <div class="flex justify-between mb-4">
                            <div class="text-sm">{new Date(game.finishedAt ?? game.createdAt).toLocaleString()}</div>
                            {#if !game.finishedAt}
                                <p class="text-warning-700 bg-yellow-200 rounded-md px-2">Game in progress</p>
                            {/if}
                        </div>
                    </Card>
                </div>
            {/each}
        </div>
    {/if}
</MainPageLayout>
