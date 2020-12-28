package tournament.client;

import tournament.Tournament;

import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread {

    private final Socket client;
    private final BufferedReader in;
    private final PrintWriter out;


    public ClientHandler(Socket socket) throws IOException {
        this.client = socket;
        this.in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        this.out = new PrintWriter(client.getOutputStream(), true);
        this.start();
    }

    @Override
    public void run() {
        try {
            while (true) {

                String request = in.readLine();
                if (request.contains(" ")) {//COMMANDS
                    String[] commandLine = request.split(" ");
                    String command = commandLine[0];


                    if (command.equalsIgnoreCase("check")) {
                        String[] splitValue = commandLine[1].split(";");
                        long teamId = Long.parseLong(splitValue[0]);
                        long teamCityId = Long.parseLong(splitValue[1]);
                        boolean check = check(teamId, teamCityId);
                        out.println(check);
                    } else if (command.equalsIgnoreCase("delete")) {
                        String[] splitValue = commandLine[1].split(";");
                        long teamId = Long.parseLong(splitValue[0]);
                        long teamCityId = Long.parseLong(splitValue[1]);
                        boolean deleteResponse = delete(teamId, teamCityId);
                        out.println(deleteResponse);
                    } else if (command.equalsIgnoreCase("quit")) {
                        break;
                    } else if (command.equalsIgnoreCase("round")) {
                        int round = Integer.parseInt(commandLine[1]);
                        boolean response = Tournament.status.nextRound(round);
                        out.println(response);
                    }
                    continue;

                } else if (request.contains(";")) { // INSERT
                    boolean saveResponse = save(request);
                    out.println(saveResponse);
                    continue;
                } else if (request.contains("select")) { // INSERT
                    boolean response = Tournament.status.createTournament();
                    out.println(response);
                    continue;
                }
                out.println("null");
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            out.close();
            System.out.println("Writer closed...");
            try {
                client.close();
                System.out.println("Client closed...");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean delete(long teamId, long teamCityId) {
        File inputFile = new File(teamCityId + "/List.txt");
        File tempFile = new File(teamCityId + "/temporary.txt");

        BufferedReader reader;
        BufferedWriter writer;
        try {
            reader = new BufferedReader(new FileReader(inputFile));
            writer = new BufferedWriter(new FileWriter(tempFile));

            String currentLine;

            while ((currentLine = reader.readLine()) != null) {

                String trimmedLine = currentLine.trim();
                long fileTeamId = Long.parseLong(trimmedLine.split(";")[0]);
                if (fileTeamId == teamId) {
                    continue;
                }
                writer.write(currentLine + System.getProperty("line.separator"));
            }

            writer.close();
            reader.close();


        } catch (IOException e) {
            e.printStackTrace();
        }

        return inputFile.delete() && tempFile.renameTo(inputFile);
    }

    private boolean check(long teamId, long teamCityId) {
        BufferedReader reader;
        try {
            File file = new File(teamCityId + "/List.txt");
            reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null) {
                if (Long.parseLong(line) == teamId) {
                    return true;
                }
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean save(String request) {
        try {
            String[] splitValue = request.split(";");
            long teamId = Long.parseLong(splitValue[0]);
            long teamCityId = Long.parseLong(splitValue[1]);
            File myFile = new File(teamCityId + "/List.txt");
            if (!myFile.exists()) {
                myFile.getParentFile().mkdirs();
                myFile.createNewFile();
            }

            BufferedWriter myWriter = new BufferedWriter(new FileWriter(myFile, true));
            myWriter.write(teamId + "\n");
            myWriter.close();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
