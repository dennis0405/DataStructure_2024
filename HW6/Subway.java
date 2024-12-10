import java.io.*;
import java.util.*;

public class Subway {
    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("Usage: java Subway [data]");
            return;
        }

        Scanner scanner = new Scanner(System.in);

        String data = args[0];
        SubwaySystem subwaySystem = new SubwaySystem();

        try {
            subwaySystem.loadData(data);
        } catch (IOException e) {
            System.err.println("Error loading data: " + e.getMessage());
            scanner.close();
            return;
        }

        while (true) {
            String inputLine = scanner.nextLine().trim();
            if (inputLine.equals("QUIT")) {
                break;
            }

            String[] tokens = inputLine.split("\\s+");
            if (tokens.length != 2) {
                System.err.println("Invalid input format. Please enter 'StartStation EndStation'");
                continue;
            }

            String startStationName = tokens[0];
            String endStationName = tokens[1];

            List<String> path = subwaySystem.findShortestPath(startStationName, endStationName);
            if (path != null) {
                for (int i = 0; i < path.size(); i++) {
                    System.out.print(path.get(i));
                    if (i < path.size() - 1) {
                        System.out.print(" ");
                    }
                }
                System.out.println();

                int totalTime = subwaySystem.getTotalTravelTime();
                System.out.println(totalTime);
            } else {
                System.out.println("No path found.");
            }
        }

        scanner.close();
    }
}



