<script lang="ts">
    import MainPageLayout from 'src/layout/MainPageLayout.svelte'
    import type {GameRoundView, GameSession} from "src/api/types";
    import {GameRoundState} from "src/api/types";
    import Button from "src/components/Button.svelte";
    import api from "src/api/api";
    import {showToast} from "src/stores/toasts";
    import {navigate} from "svelte-navigator";
    import Card from "src/components/Card.svelte";
    import MovieCard from "src/components/MovieCard.svelte";
    import {humanReadableGameCategoryQuestions} from "src/humanReadableUtils";

    export let gameSessionId: string
    let session: GameSession
    let gameRound: GameRoundView
    let rounds: GameRoundView[]
    let gameEnded: boolean

    async function load() {
        await api.get<{ session: GameSession, rounds: GameRoundView[] }>(`game-sessions/${gameSessionId}`).then(result => {
            session = result.session
            rounds = result.rounds
        })
        await loadRound()
        gameEnded = gameRound.state === GameRoundState.FAIL
    }

    $: load()

    async function loadRound() {
        gameRound = await api.get(`game-sessions/${gameSessionId}/round`)
    }

    async function choose(isHigher: boolean) {
        const correct = (await api.post(`game-sessions/${gameSessionId}/guess`, {isHigher}) as { correct: boolean }).correct
        if (correct) {
            showToast('Hurrah! Correct!')
            await loadRound()
        } else {
            await load()
        }
    }

    function homePage() {
        navigate('/')
    }
</script>

<MainPageLayout>
    {#if session && gameRound}
        <div class="text-center mt-3 my-6 text-2xl">
            <p class="mb-3"><b class="text-green-600">HIGHER</b> or <b class="text-red-600">LOWER</b>?</p>
            <p>{@html humanReadableGameCategoryQuestions(gameRound.current, gameRound.next)[session.category]}</p>
        </div>
        <div class="grid grid-cols-12 justify-between gap-6 justify-items-center">
            <MovieCard movie={gameRound.current} class="col-span-5 w-full"/>
            <div class="flex flex-col gap-4 justify-center items-center" class:invisible={gameEnded}>
                <Button on:click={() => choose(true)} icon="arrow-up" class="text-white bg-green-600"/>
                <Button on:click={() => choose(false)} icon="arrow-down" class="text-white bg-red-600"/>
            </div>
            <MovieCard movie={gameRound.next} class="col-span-5 w-full"/>
        </div>
        {#if gameEnded && rounds.length}
            {@const score = gameRound.score}
            <div class="flex flex-col items-center my-10">
                <h4>Game over!</h4>
                <p>Your score: <b>{gameRound.score}</b></p>
                {#if score === 0}
                    <p>Pfft! Did ya even try?</p>
                {/if}
                <Button class="primary my-3" on:click={homePage} label="Try again"/>
            </div>
            <div class="flex flex-col gap-4 mb-3">
                {#each rounds as round}
                    <Card title="{round.current.title} vs {round.next.title}" padding="px-6"
                          subtitle={new Date(round.createdAt).toLocaleString()}
                          class="{round.state === 'WIN' ? 'bg-green-200' : 'bg-red-200'}"/>
                {/each}
            </div>
        {/if}
    {/if}
</MainPageLayout>
