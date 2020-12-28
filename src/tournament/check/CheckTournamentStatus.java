package tournament.check;

import org.json.JSONObject;
import tournament.Tournament;
import tournament.team.TeamData;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CheckTournamentStatus {


    public boolean createTournament() {
        HashMap<Integer, Integer> willCreate = new HashMap<>();
        for (int i = 1; i <= 81; i++) {
            File tFile = new File(i + "/List.txt");
            if (tFile.exists()) {
                int cl = countOfLine(tFile);
                if (cl >= Tournament.TOURNAMENT_SIZE) {
                    int countOfTournament = (cl / Tournament.TOURNAMENT_SIZE);
                    willCreate.put(i, countOfTournament);
                }
            }
        }

        return matchTeams(willCreate);
    }

    public boolean nextRound(int round) {
        for (int a = 1; a <= Tournament.MAX_TOURNAMENT(); a++) {
            for (int i = 1; i <= 81; i++) {
                File tFile = new File(i + "/winners" + "-" + a + "-" + round + ".txt");
                if (tFile.exists()) {
                    int cl = countOfLine(tFile);
                    int roundSize = (int) (Tournament.TOURNAMENT_SIZE / (Math.pow(2, round)));
                    if (cl >= roundSize) {
                        List<TeamData> teams = new ArrayList<>();
                        try {
                            BufferedReader reader = new BufferedReader(new FileReader(tFile));
                            String line;
                            while ((line = reader.readLine()) != null) {
                                long teamId = Long.parseLong(line);
                                teams.add(new TeamData(teamId));
                                if (teams.size() == roundSize) {
                                    JSONObject object = new JSONObject();

                                    for (int j = 0; j < teams.size() / 2; j++) {
                                        TeamData firstTeam = teams.get(j);
                                        TeamData secondTeam = teams.get(teams.size() - j - 1);
                                        object.put(String.valueOf(firstTeam.getTeamId()), secondTeam.getTeamId());
                                    }

                                    File tournamentFile = new File(i + "/" + i + "-" + a + "-" + (round + 1) + ".json");
                                    FileWriter writer = new FileWriter(tournamentFile);
                                    writer.write(object.toString());
                                    writer.close();

                                    teams.clear();
                                }
                            }
                        } catch (Exception ignored) {

                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }


    private boolean matchTeams(HashMap<Integer, Integer> willCreate) {

        for (int city : willCreate.keySet()) {

            List<TeamData> cityTeams = new ArrayList<>();

            int currentTournament = 1;

            try {
                File file = new File(city + "/List.txt");
                BufferedReader reader = new BufferedReader(new FileReader(file));

                Connection con = null;
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    con = DriverManager.getConnection(
                            "jdbc:mysql://localhost:3306/db", "root", "");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String line;
                if (con != null) {

                    if (con.isClosed()) {
                        con = DriverManager.getConnection(
                                "jdbc:mysql://localhost:3306/db", "root", "");
                    }

                    while ((line = reader.readLine()) != null) {
                        long teamId = Long.parseLong(line);
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery("SELECT * FROM teamData WHERE teamId = " + teamId);
                        if (rs.next()) {
                            cityTeams.add(new TeamData(teamId, rs.getInt("wins"), rs.getInt("loses"), rs.getInt("draws")));
                        } else {
                            cityTeams.add(new TeamData(teamId));
                        }

                        if (cityTeams.size() == Tournament.TOURNAMENT_SIZE) {

                            Collections.sort(cityTeams);

                            JSONObject object = new JSONObject();

                            for (int i = 0; i < cityTeams.size() / 2; i++) {
                                TeamData firstTeam = cityTeams.get(i);
                                TeamData secondTeam = cityTeams.get(cityTeams.size() - i - 1);
                                object.put(String.valueOf(firstTeam.getTeamId()), secondTeam.getTeamId());
                            }

                            File tournamentFile = new File(city + "/" + city + "-" + currentTournament + ".json");
                            FileWriter writer = new FileWriter(tournamentFile);
                            writer.write(object.toString());
                            writer.close();

                            currentTournament++;
                            cityTeams.clear();
                        }
                    }
                    con.close();
                }
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private int countOfLine(File file) {
        if (file.exists())
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                int lines = 0;
                while (reader.readLine() != null) lines++;
                reader.close();
                return lines;
            } catch (IOException e) {
                e.printStackTrace();
            }
        return 0;
    }


}
