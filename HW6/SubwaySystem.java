import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class SubwaySystem {
    private static final Pattern SPACE_PATTERN = Pattern.compile("\\s+");

    private final Map<String, Station> stations;
    // id : Station 형태로 저장

    private final Map<String, List<Edge>> graph;
    // id : 연결된 edge의 list 형태로 저장
    // 인접 배열(리스트) 형태로 사용

    private final Map<String, Integer> transferTimes;
    // name : transfer time 형태로 저장

    private final Map<String, List<String>> stationNameToIds;
    // name : 해당하는 모든 id의 list 형태로 저장 (역에 해당하는 모든 station id 정보)

    private int totalTravelTime;
    // 최단 경로에 해당하는 가중치 합

    public SubwaySystem() {
        stations = new HashMap<>();
        graph = new HashMap<>();
        transferTimes = new HashMap<>();
        stationNameToIds = new HashMap<>();
        totalTravelTime = 0;
    }

    public void loadData(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
        String line;

        // station과 graph의 id 구성
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) break;

            String[] tokens = SPACE_PATTERN.split(line, 3);
            if (tokens.length != 3) continue;

            String id = tokens[0];
            String name = tokens[1];
            String lineNumber = tokens[2];

            Station station = new Station(id, name, lineNumber);
            stations.put(id, station);
            graph.put(id, new ArrayList<>());

            if (stationNameToIds.containsKey(name)) {
                stationNameToIds.get(name).add(id);
            } else {
                ArrayList<String> ids = new ArrayList<>();
                ids.add(id);
                stationNameToIds.put(name, ids);
            }
        }

        // edge 구성
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) break;

            String[] tokens = SPACE_PATTERN.split(line, 3);
            if (tokens.length != 3) continue;

            String fromId = tokens[0];
            String toId = tokens[1];
            int time = Integer.parseInt(tokens[2]);

            Edge edge = new Edge(toId, time);
            graph.get(fromId).add(edge);
        }

        // transfer time 구성
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) break;

            String[] tokens = SPACE_PATTERN.split(line, 2);

            if (tokens.length != 2) continue;
            // 시간이 적혀져 있지 않은 경우 (default 5) 뒤에서 처리

            String name = tokens[0];
            int transferTime = Integer.parseInt(tokens[1]);

            transferTimes.put(name, transferTime);
        }

        reader.close();

        // 환승을 위한 edge 추가
        // 같은 이름의 역에 대해, id -> id에 환승 시간이 걸린다고 생각
        for (String stationName : stationNameToIds.keySet()) {
            List<String> ids = stationNameToIds.get(stationName);

            // 환승이 존재하는 경우
            if (ids.size() > 1) {
                int transferTime = transferTimes.getOrDefault(stationName, 5);
                // 환승을 위한 모든 edge 구성
                for (int i = 0; i < ids.size(); i++) {
                    for (int j = 0; j < ids.size(); j++) {
                        if (i != j) {
                            String fromId = ids.get(i);
                            String toId = ids.get(j);
                            graph.get(fromId).add(new Edge(toId, transferTime));
                        }
                    }
                }
            }
        }

    }

    public List<String> findShortestPath(String startName, String endName) {
        totalTravelTime = 0;

        List<String> startIds = stationNameToIds.get(startName);
        List<String> endIds = stationNameToIds.get(endName);
        if (startIds == null || endIds == null) return null;

        // 시작 역과 종착 역 존재 여부 확인

        Map<String, String> prev = new HashMap<>();

        PriorityQueue<Node> pq = new PriorityQueue<>();
        Node superSource = new Node(startName, 0);
        pq.add(superSource);
        stations.put(startName, null);
        graph.put(startName, new ArrayList<>());
        // 여러 개의 출발지를 위한 superSource 구성

        for (String startId: startIds) {
            Edge edge = new Edge(startId, 0);
            graph.get(startName).add(edge);
        }
        // superSource와 실제 출빌지 연결 (가중치 0)

        Map<String, Integer> dist = new HashMap<>();
        for (String id : stations.keySet()) dist.put(id, Integer.MAX_VALUE);
        dist.put(startName, 0);
        // 모든 역 거리 무한대로 초기화

        // Dijkstra's algorithm
        while (!pq.isEmpty()) {
            Node current = pq.poll();
            String currentId = current.id;

            if (dist.get(currentId) < current.dist) continue; // Already found a better path

            if (endIds.contains(currentId)) {
                totalTravelTime = dist.get(currentId);
                return reconstructPath(prev, currentId, startName);
            }
            // 여러 개의 도착 지점 처리 (superSink 사용 x)

            for (Edge edge : graph.get(currentId)) {
                String nextId = edge.toId;
                int newDist = dist.get(currentId) + edge.time;

                if (newDist < dist.get(nextId)) {
                    dist.put(nextId, newDist);
                    prev.put(nextId, currentId);
                    pq.add(new Node(nextId, newDist));
                }
            }

        }

        return null; // No path found
    }

    private List<String> reconstructPath(Map<String, String> prev, String endId, String startName) {
        LinkedList<String> path = new LinkedList<>();
        String currentId = endId; //종착점의 id
        String prevId = null;

        while (currentId != null) {
            Station currentStation = stations.get(currentId);
            String stationName = currentStation.name;

            if (prevId != null) {
                Station prevStation = stations.get(prevId);

                // Check if transfer occurred
                if (!currentId.equals(prevId) && stationName.equals(prevStation.name)) {
                    path.remove(stationName);
                    stationName = "[" + stationName + "]";
                }
            }

            path.addFirst(stationName);

            prevId = currentId;
            currentId = prev.get(currentId);
            // 도착지로 향하기 이전 역으로 거슬러 올라가기

            // SuperSource에 도착한 경우 종료
            if (startName.equals(currentId)) break;
        }
        return path;
    }

    public int getTotalTravelTime() {
        return totalTravelTime;
    }
}