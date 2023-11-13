<script lang="ts">
  import {Route, Router} from 'svelte-navigator'
  import Toasts from './components/Toasts.svelte'
  import HomePage from 'src/pages/HomePage.svelte'
  import GameSessionPage from "src/pages/GameSessionPage.svelte";
</script>

<svelte:head>
  <title>Higher lower game</title>
</svelte:head>

<Toasts/>

<Router primary={false}>
  <div class="App min-h-screen flex flex-col">
    <Route path="/" component={HomePage}/>
    <Route path="/:gameSessionId" component={GameSessionPage}/>
  </div>
</Router>
