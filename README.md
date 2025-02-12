# class-projects
## Hangman
### Requirements
* code will pick random word
* user (possibly human) will provide letter guesses
  * user (could) also specify which spaces to check for letter (not sure why) 
* game ends 
  * when user gets all letters OR
  * user runs out of guesses
* difficulty levels
  * easy = user gets 15 guesses and must pick 4 locations to check per guess
  * intermediate = user gets 12 guesses and must specify 3 locations to check per guess
  * difficult = user gets 10 guesses and must specify 2 locations per guess
* incorrect guesses (letter not found in any specified locations) decrements remaining guess count
* invalid inputs do not decrement guess count
* 