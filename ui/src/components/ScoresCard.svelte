<script lang="ts">
    import type {GameSessionScores} from "src/api/types";
    import api from "src/api/api";
    import Card from "src/components/Card.svelte";
    import CategoryScore from "src/components/CategoryScore.svelte";

    let scores: GameSessionScores

    async function load() {
        scores = await api.get('game-sessions/scores')
    }

    $: load()
</script>

{#if scores}
    <Card class="{$$props.class ?? ''}">
        <div class="text-center text-3xl mb-8">High Score: <b>{scores.highScore}</b></div>
        <div class="grid grid-cols-4">
            <CategoryScore score={scores.voteAverageScore} label="Vote average"/>
            <CategoryScore score={scores.runtimeScore} label="Runtime"/>
            <CategoryScore score={scores.revenueScore} label="Revenue"/>
            <CategoryScore score={scores.popularityScore} label="Popularity"/>
        </div>
    </Card>
{/if}