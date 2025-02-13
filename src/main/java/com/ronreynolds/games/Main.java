package com.ronreynolds.games;

import com.ronreynolds.games.hangman.Hangman;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name="games", mixinStandardHelpOptions = true, version="1.0", description = "various games")
public class Main implements Callable<Integer> {
    enum Game {
        hangman(Hangman.class);
        final Class<?> handlerClass;
        Game(Class<?> handlerClass) {
            this.handlerClass = handlerClass;
        }
        void run() throws ReflectiveOperationException {
            // a bit brutal but so long as the game doesn't need cmd-line args this should work
            handlerClass.getDeclaredMethod("main", String[].class).invoke(null, (Object) new String[0]);
        }
    }

    @CommandLine.Parameters(index="0", description = "name of game", defaultValue = "hangman")
    private String gameName;

    public static void main(String[] args) {
        System.exit(new CommandLine(new Main()).execute(args));
    }

    @Override
    public Integer call() throws Exception {
        Game game = Game.valueOf(gameName);
        game.run();
        return 0;
    }
}
