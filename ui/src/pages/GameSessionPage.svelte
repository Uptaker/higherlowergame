<script lang="ts">
    import MainPageLayout from 'src/layout/MainPageLayout.svelte'
    import type {GameRoundView, GameSession} from "src/api/types";
    import {GameRoundState} from "src/api/types";
    import Button from "src/components/Button.svelte";
    import api from "src/api/api";
    import {showToast} from "src/stores/toasts";
    import {Link} from "svelte-navigator";

    export let gameSessionId: string
    let gameSession: GameSession
    let gameRound: GameRoundView
    let gameEnded: boolean

    async function load() {
        gameSession = await api.get(`game-sessions/${gameSessionId}`)
        await loadRound()
        gameEnded = gameRound.state === GameRoundState.FAIL
    }

    $: load()

    async function loadRound() {
        gameRound = await api.get(`game-sessions/${gameSessionId}/round`)
    }

    async function choose(isHigher: boolean) {
        const correct = (await api.post(`game-sessions/${gameSessionId}/guess`, {isHigher}) as {correct: boolean}).correct
        if (correct) {
            showToast('Hurrah! Correct!')
            await loadRound()
        } else gameEnded = true
    }
</script>

<MainPageLayout>
    {#if gameSession && gameRound}
    <div>{gameSession.category} - Higher or lower?</div>
    <div class="grid grid-cols-12 justify-between gap-2 justify-items-center">
        <div class="col-span-5 w-full">
            <h2>{gameRound.current.title}</h2>
        </div>
        <div>inbetween</div>
        <div class="col-span-5 w-full">
            <h2>{gameRound.next.title}</h2>
            {#if gameEnded}
                {@const score = gameRound.score}
                <h4>Game over!</h4>
                <p>Your score: <b>{gameRound.score}</b></p>
                {#if score === 0}
                    <p>Pfft! Did ya even try?</p>
                {/if}

                <div class="mt-10">
                    <Link to="/">Go back</Link>
                </div>
            {:else}
                <div class="flex flex-col gap-6">
                    <Button on:click={() => choose(true)} label="Higher"/>
                    <Button on:click={() => choose(false)} label="Lower"/>
                </div>
            {/if}
        </div>
    </div>
    {/if}
</MainPageLayout>
