<script lang="ts">
    import type {Movie} from "src/api/types";

    export let movie: Movie
</script>

<div class="{$$props.class ?? ''}" >
    <h2 class="font-semibold">{movie.title}</h2>
    <div class="text-sm flex flex-col gap-4">
        <p class="italic">{movie.releaseDate}</p>
        {#if movie.tagline}
            <p class="font-semibold">{movie.tagline}</p>
        {/if}
        <p class="text-justify">{movie.overview ?? ''}<p>
    </div>
</div>