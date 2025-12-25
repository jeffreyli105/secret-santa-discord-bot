# secret-santa-discord-bot
A discord bot to be able to play Secret Santa with your friends and family!

# config.properties
+ botToken: Generate a bot token in https://discord.com/developers/applications
+ emailFrom: Put an email you want to send emails from
+ appPassword: Create an app password for the email
+ ownerID: After running /register on your discord, go to registrations.json and copy your ID here

# Commands

**/echo [text]:** echoes back the text you provide. Eg. /echo hi

**/info:** displays information about the bot. Eg. /info

**/ping:** checks the bot’s latency to Discord’s gateway. Eg. /ping

**/register:** registers a user
How to use:
1) Type /register
2) Choose the language
3) Answer the personality test questions
4) A modal will appear asking you to fill in name, password, email, and Wishlist (password is used to activate /reveal later)
5) Press Submit

**/start:** Starts the Secret Santa game (admin only)
How to use:
1) Type /start
2) An email will be sent to all users who used /register with the instructions for the next steps
3) Assignments will be generated so /reveal is now unlocked

**/reveal:** reveal your Secret Santa assignment (password required)
How to use:
1) Type /reveal
2) A modal will appear asking you to enter the password you created during /register
3) Press Submit

**/unregister:** deletes your registration from the Secret Santa system. Eg. /unregister

**/vote:** start voting rounds (admin only)
How to use:
1) Type /vote
2) For each voting round, user A’s personality test answers are displayed and everyone needs to vote on who they think gifted user B.
The users that guessed correctly (excluding user A) earn 1 point.
After everyone has finished voting for each round, it’s revealed that user A gifted user B.
3) After all rounds have completed, a leaderboard is displayed showing the points every user has earned in total.


# How to play:
1) Run /register on everyone's accounts
2) Fill in config.properties
3) Run /start on admin account
4) Run /reveal on everyone's accounts
5) Save the registrations.json and assignments.json files to your local as backup
6) On the day of play, copy the files into the data folder if not already there
7) Run /vote


