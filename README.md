# class-projects
## Hangman
### Requirements
#### Setup
* code will pick random mystery word
* user selects difficulty level
  * easy         = user gets 15 guesses and up to 4 locations to check per guess
  * intermediate = user gets 12 guesses and up to 3 locations to check per guess
  * difficult    = user gets 10 guesses and up to 2 locations per guess

#### Game Play
* user will provide:
  * letter guesses
    * if multiple letters entered use first letter 
  * which spaces to check for letter (not sure why) 
  * OR user may enter "solve" and then try to provide the mystery word

#### Game Rules
* incorrect guesses (letter not found in any specified locations) costs 1 guess
* incorrect solve attempt costs 1 guess
* invalid inputs do not cost a guess
*
#### Game End
* game ends 
  * when user guesses all letters OR
  * user successfully solves mystery word OR
  * user runs out of guesses
* at end of game user can choose to play again (without exiting program)
 
