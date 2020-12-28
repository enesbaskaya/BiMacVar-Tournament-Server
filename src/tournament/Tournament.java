package tournament;

import tournament.check.CheckTournamentStatus;
import tournament.controller.Controller;

public class Tournament {


    public final static int PORT = 25696;
    public final static int TOURNAMENT_SIZE = 16;
    public static final CheckTournamentStatus status = new CheckTournamentStatus();


    public static int MAX_TOURNAMENT() {
        int value = TOURNAMENT_SIZE;
        int i = 0;
        while (value != 1) {
            value /= 2;
            i++;
        }
        return i;
    }

    public static void main(String[] args) {
        Controller controller = new Controller();
        controller.run();
    }


}
