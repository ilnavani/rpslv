public class SolidAsPaper implements RoShamBot {
 
    /** Plays the pure strategy Rock.
      * 
      * @param lastOpponentMove the action that was played by the opponent on
      *        the last round (this is disregarded).
      * @return the next action to play.
      */
    public Action getNextMove(Action lastOpponentMove) {
        return Action.PAPER;
    }
    
}