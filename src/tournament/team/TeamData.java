package tournament.team;

public class TeamData implements Comparable<TeamData> {

    private final long teamId;
    private final int wins;
    private final int loses;
    private final int draws;

    public TeamData(long teamId, int wins, int loses, int draws) {
        this.teamId = teamId;
        this.wins = wins;
        this.loses = loses;
        this.draws = draws;
    }

    public TeamData(long teamId) {
        this(teamId, 0, 0, 0);
    }


    public long getTeamId() {
        return teamId;
    }

    public int getWins() {
        return wins;
    }

    public int getDraws() {
        return draws;
    }

    public int getLoses() {
        return loses;
    }

    public int point() {
        return (wins * 3) + draws;
    }

    @Override
    public int compareTo(TeamData o) {
        return this.point() - o.point();
    }

    @Override
    public String toString() {
        return "<" + point() + ">";
    }
}
