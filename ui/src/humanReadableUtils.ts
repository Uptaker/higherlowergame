import type {Movie} from "src/api/types";

export const humanReadableCategories = {
    VOTE_AVERAGE: "Vote average",
    POPULARITY: "Popularity",
    RUNTIME: "Runtime",
    REVENUE: "Revenue"
}

export const humanReadableGameCategoryQuestions = (currentMovie: Movie, nextMovie: Movie) => ({
    VOTE_AVERAGE: `Does <i>${nextMovie.title}</i> have a higher <b>vote average</b> than <i>${currentMovie.title}</i>?`,
    POPULARITY: `Is <i>${nextMovie.title}</i> more <b>popular</b> than <i>${currentMovie.title}</i>?`,
    RUNTIME: `Does <i>${nextMovie.title}</i> have a longer <b>runtime</b> than <i>${currentMovie.title}</i>?`,
    REVENUE: `Did <i>${nextMovie.title}</i> gross more <b>revenue</b> than <i>${currentMovie.title}</i>?`
})