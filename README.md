# PokerGame

## The course of game

### Joining
When the server is running player threads can be ran.<br/>
Then players receive a message from server and can put in<br/>
these commands:<br/>

- \help - show all commands 
- \exit - disconnect from server 
- \ready - sets you as ready, game starts when every player is ready

### Gameplay

After all players enter (1-4) the game starts.<br/>
Players receive info about their cards, whos move is it <br/>
and a command \actions to check all commands available at the time:

- \my_hand - see your hand
- \balance - see your account balance
- \pool - show how much money is on the table 
- \exit - disconnect from server 
- \check - check a turn 
- \bet amount - bet a given amount of money
- \fold - end your round 
- \all_in - bet all your money 
- \call - call the current bet

If it's not your move you can only use the first 4 <br/>

After the first betting round players get to swap cards (up to 3) <br/>
Available commands at the time are:
- \my_hand
- \swap i1,i2,i3 (i1,i2,i3 are indexes of cards you want to swap (1-5))
- \end_swap if you don't want to swap anything

After that we have two more betting rounds, <br/>
winner is evaluated and the next rounds starts. <br/>
if all players except one are bankrupt the game ends.
