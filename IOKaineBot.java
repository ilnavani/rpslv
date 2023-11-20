import java.util.ArrayList;
import java.util.Random;

public class IOKaineBot implements RoShamBot {
    private ArrayList<Strategy> strategies;
    private int[] scores;
    private Action lastMyMove = Action.ROCK;
    
    public IOKaineBot() {
        strategies = new ArrayList<Strategy>();

        // Add the strategies
        strategies.add(new FrequencyStrategy());
        strategies.add(new OpponentHistoryStrategy());
        strategies.add(new BothHistoryStrategy());
        strategies.add(new RotationStrategy());
        strategies.add(new ReverseRotationStrategy());
        strategies.add(new RandomStrategy());

        // Assume the opponent uses the same strategies against us and add the swap strategies
        strategies.add(new SwapStrategy(new FrequencyStrategy()));
        strategies.add(new SwapStrategy(new OpponentHistoryStrategy()));
        strategies.add(new SwapStrategy(new BothHistoryStrategy()));
        strategies.add(new SwapStrategy(new RotationStrategy()));
        strategies.add(new SwapStrategy(new ReverseRotationStrategy()));

        scores = new int[strategies.size()];
    }
    
    // Return actions that can defeat a given action
    public Action[] defeatedBy(Action action1) {
        Action[] actions = new Action[2];
        switch (action1) {
            case ROCK:
                actions[0] = Action.SPOCK;
                actions[1] = Action.PAPER;
                break;
            case PAPER:
                actions[0] = Action.LIZARD;
                actions[1] = Action.SCISSORS;
                break;
            case SCISSORS:
                actions[0] = Action.SPOCK;
                actions[1] = Action.ROCK;
                break;
            case LIZARD:
                actions[0] = Action.ROCK;
                actions[1] = Action.SCISSORS;
                break;
            case SPOCK:
                actions[0] = Action.LIZARD;
                actions[1] = Action.PAPER;
                break;
        }
        return actions;
    }

    public boolean isDefeatedBy(Action action1, Action action2) {
        Action[] responses = defeatedBy(action1);
        return responses[0] == action2 || responses[1] == action2;
    }

    public boolean defeats(Action action1, Action action2) {
        return isDefeatedBy(action2, action1);
    }
    
    // Record previous moves made by us and the opponent
    public void addHistory(Action lastMyMove, Action lastOpponentMove) {
        updateScores(lastOpponentMove);
        for (Strategy strategy : strategies)
            strategy.addHistory(lastMyMove, lastOpponentMove);
    }

     // Update the score of each strategy 
        private void updateScores(Action lastOpponentMove) {
        for (int i = 0; i < strategies.size(); i++) {
            Action[] nextMoves = strategies.get(i).getNextMoves();

            if (defeats(nextMoves[0], lastOpponentMove) && defeats(nextMoves[1], lastOpponentMove)) {
                scores[i]++;
            }
            else {
                scores[i] = 0;
            }
        }
    }
    
    // Get next best move using the strategy with the highest score
    public Action getBestMove(Action bestMove) {
        int bestScore = Integer.MIN_VALUE;

        for (int i = 0; i < strategies.size(); i++)
            if (scores[i] > bestScore) {
                bestMove = strategies.get(i).getNextMove();
                bestScore = scores[i];
            }
        return bestMove;
    }

    public Action getNextMove(Action lastOpponentMove) {
        addHistory(lastMyMove, lastOpponentMove);
        lastMyMove = getBestMove(lastMyMove);
        return lastMyMove;
    }
}


// Algorithm used to predict the opponent's next move
abstract class Strategy {
    private Random random;

    public Strategy() {
        random = new Random();
    }
    public abstract void addHistory(Action myMove, Action opponentMove);

    protected abstract Action predict();
    
    public Action[] defeatedBy(Action action1) {
        Action[] actions = new Action[2];
        switch (action1) {
            case ROCK:
                actions[0] = Action.SPOCK;
                actions[1] = Action.PAPER;
                break;
            case PAPER:
                actions[0] = Action.LIZARD;
                actions[1] = Action.SCISSORS;
                break;
            case SCISSORS:
                actions[0] = Action.SPOCK;
                actions[1] = Action.ROCK;
                break;
            case LIZARD:
                actions[0] = Action.ROCK;
                actions[1] = Action.SCISSORS;
                break;
            case SPOCK:
                actions[0] = Action.LIZARD;
                actions[1] = Action.PAPER;
                break;
        }
        return actions;
    }

    public Action[] getNextMoves() {
        return defeatedBy(predict());
    }

    public Action getNextMove() {
        return getNextMoves()[random.nextInt(2)];
    }
}

//Assume the opponent uses the given strategy against us
class SwapStrategy extends Strategy {
    private Strategy strategy;

    public SwapStrategy(Strategy strategy) {
        this.strategy = strategy;
    }
    @Override
    public void addHistory(Action me, Action opponent) {
        strategy.addHistory(opponent, me);
    }
    @Override
    protected Action predict() {
        throw new UnsupportedOperationException();
    }
    public Action[] defeatedBy(Action action1) {
        Action[] actions = new Action[2];
        switch (action1) {
            case ROCK:
                actions[0] = Action.SPOCK;
                actions[1] = Action.PAPER;
                break;
            case PAPER:
                actions[0] = Action.LIZARD;
                actions[1] = Action.SCISSORS;
                break;
            case SCISSORS:
                actions[0] = Action.SPOCK;
                actions[1] = Action.ROCK;
                break;
            case LIZARD:
                actions[0] = Action.ROCK;
                actions[1] = Action.SCISSORS;
                break;
            case SPOCK:
                actions[0] = Action.LIZARD;
                actions[1] = Action.PAPER;
                break;
        }
        return actions;
    }
    public boolean isDefeatedBy(Action action1, Action action2) {
        Action[] responses = defeatedBy(action1);
        return responses[0] == action2 || responses[1] == action2;
    }
    public boolean defeats(Action action1, Action action2) {
        return isDefeatedBy(action2, action1);
    }
    
    @Override
    public Action[] getNextMoves() {
        Action[] opponentNextMoves = strategy.getNextMoves();
        Action[] nextMoves = new Action[2];
        for (Action action : Action.values())
            if (defeats(action, opponentNextMoves[0]) && defeats(action, opponentNextMoves[1]))
                nextMoves[0] = nextMoves[1] = action;
        return nextMoves;
    }
}

// Strategy to find the opponent's most frequent move and predict that he will choose it.
class FrequencyStrategy extends Strategy {
    private Action[] Actions;
    private int[] frequencies;

    public FrequencyStrategy() {
        Actions = Action.values();
        frequencies = new int[Actions.length];
    }

    @Override
    public void addHistory(Action myMove, Action opponentMove) {
        frequencies[opponentMove.ordinal()]++;
    }

    @Override
    protected Action predict() {
        Action action = Action.ROCK;
        int max = Integer.MIN_VALUE;

        for (int i = 0; i < frequencies.length; i++)
            if (frequencies[i] > max) {
                action = Actions[i];
                max = frequencies[i];
            }

        return action;
    }
}


// Recording pairwise history of our move and the opponent's move
class History<T> extends ArrayList<T> {
    public T match(T defaultValue) {
        for (int length = 20; length >= 1; length--) {
            int right = size() - length;
            
            for (int left = right - length; left >= 0; left--) {
                boolean exist = true;

                for (int i = 0; i < length; i++)
                    if (!get(left + i).equals(get(right + i))) {
                        exist = false;
                        break;
                    }

                if (exist)
                    return get(left + length);
            }
        }
        return defaultValue;
    }
}

// Strategy to predict the opponent's next move by finding repeating patterns in their history. 
class OpponentHistoryStrategy extends Strategy {
    private History<Action> history;

    public OpponentHistoryStrategy() {
        history = new History<Action>();
    }
    @Override
    public void addHistory(Action myMove, Action opponentMove) {
        history.add(opponentMove);
    }
    @Override
    protected Action predict() {
        return history.match(Action.ROCK);
    }
}

// Strategy to predict the opponent's next move by finding repeating patterns in the opponent's history and our history.
class BothHistoryStrategy extends Strategy {
    private class Pair {
        Action myMove;
        Action opponentMove;
        public Pair(Action myMove, Action opponentMove) {
            this.myMove = myMove;
            this.opponentMove = opponentMove;
        }
        @Override
        public boolean equals(Object obj) {
            Pair pair = (Pair) obj;
            return myMove == pair.myMove && opponentMove == pair.opponentMove;
        }
    }

    private History<Pair> history;

    public BothHistoryStrategy() {
        history = new History<Pair>();
    }
    @Override
    public void addHistory(Action myMove, Action opponentMove) {
        history.add(new Pair(myMove, opponentMove));
    }
    @Override
    protected Action predict() {
        return history.match(new Pair(Action.ROCK, Action.ROCK)).opponentMove;
    }
}

// Strategy to predict the opponent's next move by assuming their next move will be rotating their last move to the right.
class RotationStrategy extends Strategy {
    private Action[] actions;
    private Action last;

    public RotationStrategy() {
        actions = Action.values();
    }
    @Override
    public void addHistory(Action myMove, Action opponentMove) {
        last = opponentMove;
    }
    @Override
    protected Action predict() {
        if (last == null)
            return Action.ROCK;
        return actions[(last.ordinal() + 1) % actions.length];
    }
}

// Strategy to predict the opponent's next move by assuming their next move will be rotating their last move to the left.
class ReverseRotationStrategy extends Strategy {
    private Action[] actions;
    private Action last;

    public ReverseRotationStrategy() {
        actions = Action.values();
    }
    @Override
    public void addHistory(Action myMove, Action opponentMove) {
        last = opponentMove;
    }
    @Override
    protected Action predict() {
        if (last == null)
            return Action.ROCK;
        return actions[(last.ordinal() - 1 + actions.length) % actions.length];
    }
}

// Strategy to predict the opponent's next move by randomly choosing a Action.
class RandomStrategy extends Strategy {
    private Action[] actions;
    private Random random;

    public RandomStrategy() {
        actions = Action.values();
        random = new Random();
    }
    @Override
    public void addHistory(Action myMove, Action opponentMove) {}
    
    @Override
    protected Action predict() {
        return actions[random.nextInt(actions.length)];
    }
}