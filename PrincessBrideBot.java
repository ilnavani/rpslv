import java.util.Random;

public class PrincessBrideBot implements RoShamBot {
    private int index = 0;

    @Override
    public Action getNextMove(Action lastOpponentMove) {
        // The ASCII characters of the quote
        String quote = "I do not mean to pry, but you don't by any chance happen to have six fingers on your right hand? My father was slaughtered by a six-fingered man. He was a great swordmaker, my father. And when the six-fingered man appeared and requested a special sword, my father took the job. He slaved a year before he was done. The six-fingered man returned and demanded it, but at one-tenth his promised price. My father refused. Without a word, the six-fingered man slashed him through the heart. I loved my father, so, naturally, challenged his murderer to a duel ... I failed ... the six-fingered man did leave me alive with the six-fingered sword, but he gave me these. (He touches a scar on each cheek) I was eleven years old. When I was strong enough, I dedicated my life to the study of fencing. So the next time we meet, I will not fail. I will go up to the six-fingered man and say, \"Hello, my name is Inigo Montoya. You killed my father. Prepare to die.\"";

        // Check if the index is at the end of the quote and reset it if needed
        if (index >= quote.length()) {
            index = 0;
        }
        
        // Get the ASCII value of the current character and take modulo 3
        int actionValue = quote.charAt(index++) % 3;

        // Map the action value to an Action
        switch (actionValue) {
            case 0:
                return Action.ROCK;
            case 1:
                return Action.PAPER;
            case 2:
                return Action.SCISSORS;
            case 3:
                return Action.LIZARD;
            case 4:
                return Action.SPOCK;
            default:
                // Should not reach here, but return a random move as a fallback
                return getRandomMove();
        }
    }

    private Action getRandomMove() {
        // Return a random move if needed
        Random random = new Random();
        int randomValue = random.nextInt(3);
        return Action.values()[randomValue];
    }
}