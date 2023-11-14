import type {Movie} from "src/api/types";

export const humanReadableCategories = {
    VOTE_AVERAGE: "Vote average",
    POPULARITY: "Popularity",
    RUNTIME: "Runtime",
    REVENUE: "Revenue"
}

export const humanReadableGameCategoryQuestions = (currentMovie: Movie, nextMovie: Movie) => ({
    VOTE_AVERAGE: `Does <i><i>${currentMovie.title}</i></i> have a higher <b>vote average</b> than <i>${nextMovie.title}</i>?`,
    POPULARITY: `Is <i>${currentMovie.title}</i> more <b>popular</b> than <i>${nextMovie.title}</i>?`,
    RUNTIME: `Does <i>${currentMovie.title}</i> have a longer <b>runtime</b> than <i>${nextMovie.title}</i>?`,
    REVENUE: `Did <i>${currentMovie.title}</i> gross more <b>revenue</b> than <i>${nextMovie.title}</i>?`
})