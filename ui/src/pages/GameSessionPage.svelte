<script lang="ts">
    import MainPageLayout from 'src/layout/MainPageLayout.svelte'
    import type {GameRoundView, GameSession, Movie} from "src/api/types";
    import {Category, GameRoundState} from "src/api/types";
    import Button from "src/components/Button.svelte";
    import api from "src/api/api";
    import {showToast} from "src/stores/toasts";
    import {navigate} from "svelte-navigator";
    import Card from "src/components/Card.svelte";
    import MovieCard from "src/components/MovieCard.svelte";
    import {humanReadableGameCategoryQuestions} from "src/humanReadableUtils";
    import {fade, fly, type TransitionConfig} from 'svelte/transition'
    import type {FlyParams} from "svelte/types/runtime/transition";
    import HardModeBadge from "src/components/HardModeBadge.svelte";

    export let gameSessionId: string
    let session: GameSession
    let gameRound: GameRoundView
    let rounds: GameRoundView[]
    let transitioning = false

    async function load() {
        await api.get<{ session: GameSession, rounds: GameRoundView[] }>(`game-sessions/${gameSessionId}`).then(result => {
            session = result.session
            rounds = result.rounds
        })
        await loadRound()
    }

    async function loadRound() {
        gameRound = await api.get(`game-sessions/${gameSessionId}/round`)
    }

    $: gameEnded = gameRound?.state === GameRoundState.FAIL

    async function choose(isHigher: boolean) {
        const correct = (await api.post(`game-sessions/${gameSessionId}/guess`, {isHigher}) as { correct: boolean }).correct
        if (correct) {
            transitioning = true
            showToast('Hurrah! Correct!') &&
            setTimeout(() => loadRound().then(() => transitioning = false), 3000)
        } else await load()
    }

    function homePage() {
        navigate('/')
    }

    function withTransition(node: Element, options: FlyParams | undefined): TransitionConfig {
        return gameEnded ? (() => {}) as TransitionConfig : fly(node, options);
    }

    function getCategoryValue(movie: Movie) {
        switch (session.category) {
            case Category.VOTE_AVERAGE: return movie.voteAverage
            case Category.POPULARITY: return movie.popularity
            case Category.RUNTIME: return movie.runtime
            case Category.REVENUE: return movie.revenue
        }
    }

    $: showResults = gameEnded || transitioning
    $: load()
</script>

<MainPageLayout>
    {#if session && gameRound}
        <div class="flex flex-col gap-4 justify-center mt-6 items-center">
            <HardModeBadge hard={session.hard}/>
            <div class="text-2xl">{@html humanReadableGameCategoryQuestions(gameRound.current, gameRound.next)[session.category]}</div>
        </div>
        {#key gameRound}
        <div class="grid grid-cols-12 justify-between gap-6 justify-items-center">
            <div in:withTransition={{x: 600, duration: 1500}} class="col-span-5 w-full">
                <MovieCard movie={gameRound.current}/>
            </div>
            <div class="flex flex-col gap-4 justify-center items-center" class:invisible={gameEnded}>
                <Button on:click={() => choose(true)} icon="arrow-up" class="text-white bg-green-600"/>
                <Button on:click={() => choose(false)} icon="arrow-down" class="text-white bg-red-600"/>
            </div>
            <div in:withTransition={{x: 2000, duration: 1000, delay: 500}} class="col-span-5 w-full">
                <MovieCard movie={gameRound.next}/>
            </div>
        </div>
            {#if showResults}
                <div class="grid grid-cols-2 justify-center items-center text-center" in:fade>
                    <div class="text-2xl">{getCategoryValue(gameRound.current)}</div>
                    <div class="text-2xl">{getCategoryValue(gameRound.next)}</div>
                </div>
            {/if}
        {/key}
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
