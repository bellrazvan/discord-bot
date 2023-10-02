# Discord bot
## Project info
> [!NOTE]
> This project is currently unfinished. More featues are yet to be added.
  
This bot was made using `Java Discord API` ([JDA](https://github.com/discord-jda/JDA). See documentation [here](https://jda.wiki/)).

Other APIs and dependencies used:
- [Dotenv](https://github.com/cdimascio/dotenv-java) (used for the environment table)
- [JokeApi](https://github.com/khurozov/jokeapi-java) (returns a random joke)
- [Jackson Core](https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-core) (used for mapping)
- [Nasa APOD API](https://api.nasa.gov) (APOD stands for Astronomy Photo of the Day. We'll touch on this API [later](https://github.com/bellrazvan/discord-bot#nasa-api-key))
- [Riddles API](https://riddles-api.vercel.app) (returns a random riddle)

## Features
Currently, the bot's features are as follows:
1. Slash commands
    - `/welcome` - sends a welcome message
    - `/echo` - repeats a given message
    - `/nasa` - sends NASA's Astronomy Photo of the Day
    - `/joke` - displays a random joke
    - `/roles` - lists all the server's roles
    - `/riddle` - sends a random riddle
    - `/giverole` - gives a role to a selected user
      
2. Context menu commands
    - User Context
        * `Get user avatar` - sends the user's avatar 
        * `Get online status` - sends the user's current status
        * `Mutual servers` - sends all the user's mutual servers
          
    - Message Context
        * `Count words` - counts the words in a message
        * `Reverse message` - reverses a message
        * `Shuffle message` - shuffles the words in a message

3. Event listeners
    - Message events
        * When a user sends a greeting (more specifically 'hello', 'hi' or 'hey')  
          and the word 'bot' in the same message, the `bot greets the user`
          
    - Guild (server) events
        * Sends a `greeting when it joins a guild`
        * Sends a `greeting when a user joins the guild`
        * Sends a `message when a new channel (voice or text) is created`

## How to use
As you can see in the repo, there is a `.env.example` file containing the following lines:

```
TOKEN=YOUR_TOKEN
NASA_API_KEY=YOUR_NASA_API_KEY
```

You need to rename it to `.env` and replace `YOUR_TOKEN` and `YOUR_NASA_API_KEY`.

### Bot creation (and bot token)
1. You must access the [Discord Developer Portal](https://discord.com/developers/applications)
   
2. Go to 'Applications' and then click on `New Application`

    ![Screenshot_1](https://github.com/bellrazvan/discord-bot/assets/90152385/0e03e2db-3cbd-4902-a1b5-90e6addb1d64)
    ![Screenshot_2](https://github.com/bellrazvan/discord-bot/assets/90152385/638d70f0-c778-4010-97bd-d9d3246a4909)

3. Enter a name for the application
   
   ![Screenshot_3](https://github.com/bellrazvan/discord-bot/assets/90152385/5224b3e4-012d-4904-97a4-23ab34166ea3)

4. Now access the 'Bot' page. Here you can change the bot's name

   ![Screenshot_4](https://github.com/bellrazvan/discord-bot/assets/90152385/394d8899-e684-40e6-a4b5-477cab9afa6c)

5. Check all 3 `Gateway Intents` as seen below
   
   ![Screenshot_5](https://github.com/bellrazvan/discord-bot/assets/90152385/df81b037-fd77-472f-9692-94a7d003fbf8)

6. Now click on `Reset Token` to get your token

   ![Screenshot_6](https://github.com/bellrazvan/discord-bot/assets/90152385/87f8897e-4482-4225-bb61-9097f45ec6e1)

7. Then copy the token and replace it with `YOUR_TOKEN` in the `.env` file

    For example:
    ```
    TOKEN=MTE1ODQ5NZF2Njg1NzU3MjG4NQ.GSAXIy.ByRaHxxAkdayiTB33Itli8Iwa1kaS_hU7F4m84F
    ```
    
8. Now go to 'OAuth2' and then 'URL Generator'

9. Check `applications.commands` and `bot` in the 'Scopes' menu
   
   ![Screenshot_7](https://github.com/bellrazvan/discord-bot/assets/90152385/99715586-0fc2-4d1c-a993-2bbfbb55aacf)

10. The 'Bot Permissions' menu should pop up. Select `Administrator` and then copy the generated URL
    
     (This is the bot's invite URL)

### Nasa API key
1. Access the [Nasa API portal](https://api.nasa.gov)
   
2. Sign up (it's free)

   ![Screenshot_8](https://github.com/bellrazvan/discord-bot/assets/90152385/5395abf9-2ec5-4245-a9f4-5c6d8c128d47)

3. You should get a mail that looks like this

   ![Screenshot_9](https://github.com/bellrazvan/discord-bot/assets/90152385/a87c1593-7536-45d6-ac0c-c072cf17c8d7)

4. Now replace `YOUR_NASA_API_KEY` in the `.env` file with the API key you just received 

    For example:
    ```
    NASA_API_KEY=0fMDv2Y7ayOBSyfXIeP7TXKidn3pbrADhajDrudT
    ```

5. Congrats! Now you have your own working bot 😄👍

<!-- ADD `HOW TO HOST` -->
