import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Subway {
    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("Usage: java Subway [data]");
            return;
        }

        Scanner scanner = new Scanner(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8), true);
        PrintWriter err = new PrintWriter(new OutputStreamWriter(System.err, StandardCharsets.UTF_8), true);

        String data = args[0];
        SubwaySystem subwaySystem = new SubwaySystem();

        try {
            subwaySystem.loadData(data);
        } catch (IOException e) {
            err.println("Error loading data: " + e.getMessage());
            return;
        }

        while (true) {
            String inputLine = scanner.nextLine().trim();
            if (inputLine.equals("QUIT")) {
                break;
            }

            String[] tokens = inputLine.split(" ");
            if (tokens.length != 2) {
                err.println("Invalid input format. Please enter 'StartStation EndStation'");
                continue;
            }

            String startStationName = tokens[0];
            String endStationName = tokens[1];

            List<String> path = subwaySystem.findShortestPath(startStationName, endStationName);
            if (path != null) {
                for (int i = 0; i < path.size(); i++) {
                    out.print(path.get(i));
                    if (i < path.size() - 1) {
                        out.print(" ");
                    }
                }
                out.println();

                int totalTime = subwaySystem.getTotalTravelTime();
                out.println(totalTime);
            } else {
                out.println("No path found.");
            }
        }

        scanner.close();
    }
}



