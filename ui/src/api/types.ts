export interface Movie {
    id: string,
    originalTitle: string,
    overview: string,
    popularity: number,
    releaseDate: string,
    revenue: number,
    runtime: number,
    tagline: string,
    title: string,
    voteAverage: number,
    voteCount: number
}

export interface GameRound {
    current: Movie,
    next: Movie,
    createdAt: string
    state: GameRoundState
}

export enum GameRoundState {
    WIN = "WIN", FAIL = "FAIL", PENDING = "PENDING"
}

export enum Category {
    VOTE_AVERAGE = "VOTE_AVERAGE",
    POPULARITY = "POPULARITY",
    RUNTIME = "RUNTIME",
    REVENUE = "REVENUE"
}
