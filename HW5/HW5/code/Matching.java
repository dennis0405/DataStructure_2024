import java.io.*;
import java.util.LinkedList;

public class Matching
{
	private static HashTable<String, int[]> hashTable = new HashTable<>();

	public static void main(String args[])
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		while (true)
		{
			try
			{
				String input = br.readLine();
				if (input.compareTo("QUIT") == 0)
					break;

				command(input);
			}
			catch (IOException e)
			{
				System.out.println("입력이 잘못되었습니다. 오류 : " + e.toString());
			}
		}
	}

	private static void command(String input)
	{
		String[] tokens = input.split(" ", 2);
		String cmd = tokens[0];
		String argument = tokens[1];

		switch (cmd) {
			case "<":
				loadFile(argument);
				break;
			case "@":
				int index = Integer.parseInt(argument);
				hashTable.printSlot(index);
				break;
			case "?":
				searchPattern(argument);
				break;
			default:
				System.out.println("잘못된 명령어입니다.");
				break;
		}
	}

	private static void loadFile(String filename) {
		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			hashTable = new HashTable<>();
			String line;
			int i = 1;
			while ((line = reader.readLine()) != null) {
				for (int j = 0; j <= line.length() - 6; j++) {
					String substring = line.substring(j, j + 6); // substring position = (i, j+1)
					int[] position = {i, j+1};
					hashTable.insert(substring, position);
				}
				i++;
			}
		} catch (IOException e) {
			System.out.println("파일을 읽을 수 없습니다: " + e.getMessage());
		}
	}

	private static void searchPattern(String pattern) {
		int pattern_number = pattern.length() / 6;
		int offset = pattern.length() % 6;
		if (offset != 0) {
			pattern_number += 1;
		}

		String[] slices = new String[pattern_number];
		for (int i=0; i<pattern_number-1; i++) {
			slices[i] = pattern.substring(6*i, 6*i + 6);
		}
		slices[pattern_number-1] = pattern.substring(pattern.length()-6);

		StringBuilder result = new StringBuilder();

		LinkedList<LinkedList<int[]>> searchResults = new LinkedList<>();
		// 입력된 pattern의 각 slice 위치를 미리 찾아서 보관
		for (String slice : slices) {
			LinkedList<int[]> positions = hashTable.search(slice);

			if (positions == null) {
				System.out.println("(0, 0)");
				return;
			}

			searchResults.add(positions);
		}

		LinkedList<int[]> firstPositions = searchResults.get(0);
		for(int[] firstPosition : firstPositions) {
			int line = firstPosition[0];
			int index = firstPosition[1];
			boolean isConnected = true;

			for (int i=1; i<slices.length; i++) {
				boolean found = false;
				LinkedList<int[]> nextPositions = searchResults.get(i);
				int expectedIndex;

				if (i < slices.length - 1) {
					expectedIndex = index + 6 * i;
				} else {
					// 마지막 슬라이스의 시작 인덱스 계산
					if (offset != 0) {
						expectedIndex = index + 6 * (i - 1) + offset;
					} else {
						expectedIndex = index + 6 * i;
					}
				}

				for (int[] pos : nextPositions) {
					if (pos[0] == line && pos[1] == expectedIndex) {
						found = true;
						break;
					}
				}

				if (!found) {
					isConnected = false;
					break;
				}
			}

			if(isConnected){
				result.append("(").append(line).append(", ").append(index).append(") ");
			}
		}

		if (result.toString().isEmpty()){
			System.out.println("(0, 0)");
			return;
		}

		System.out.println(result.toString().trim());
	}
}
