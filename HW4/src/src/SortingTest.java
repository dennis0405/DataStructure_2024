import java.io.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SortingTest {
	public static void main(String args[]) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		try {
			boolean isRandom = false;
			int[] value;
			String nums = br.readLine();

			if (nums.charAt(0) == 'r') {
				isRandom = true;

				String[] nums_arg = nums.split(" ");

				int numsize = Integer.parseInt(nums_arg[1]);
				int rminimum = Integer.parseInt(nums_arg[2]);
				int rmaximum = Integer.parseInt(nums_arg[3]);
				//SortingTimeTest(numsize, rminimum, rmaximum);

				Random rand = new Random();

				value = new int[numsize];
				for (int i = 0; i < value.length; i++)
					value[i] = rand.nextInt(rmaximum - rminimum + 1) + rminimum;
			} else {
				int numsize = Integer.parseInt(nums);

				value = new int[numsize];
				for (int i = 0; i < value.length; i++)
					value[i] = Integer.parseInt(br.readLine());
			}
			while (true) {
				int[] newvalue = (int[]) value.clone();
				char algo = ' ';

				if (args.length == 4) {
					return;
				}

				String command = args.length > 0 ? args[0] : br.readLine();

				if (args.length > 0) {
					args = new String[4];
				}

				long t = System.currentTimeMillis();
				switch (command.charAt(0)) {
					case 'B':    // Bubble Sort
						DoBubbleSort(newvalue);
						break;
					case 'I':    // Insertion Sort
						DoInsertionSort(newvalue);
						break;
					case 'H':    // Heap Sort
						DoHeapSort(newvalue);
						break;
					case 'M':    // Merge Sort
						DoMergeSort(newvalue);
						break;
					case 'Q':    // Quick Sort
						DoQuickSort(newvalue);
						break;
					case 'R':    // Radix Sort
						DoRadixSort(newvalue);
						break;
					case 'S':    // Search
						algo = DoSearch(newvalue);
						break;
					case 'X':
						return;
					default:
						throw new IOException("잘못된 정렬 방법을 입력했습니다.");
				}
				if (isRandom) {
					System.out.println((System.currentTimeMillis() - t) + " ms");
				} else {
					if (command.charAt(0) != 'S') {
						for (int i = 0; i < newvalue.length; i++) {
							System.out.println(newvalue[i]);
						}
					} else {
						System.out.println(algo);
					}
				}

			}
		} catch (IOException e) {
			System.out.println("입력이 잘못되었습니다. 오류 : " + e.toString());
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	private static void swap(int[] value, int i, int j) {
		int tmp = value[i];
		value[i] = value[j];
		value[j] = tmp;
	}

	private static void DoBubbleSort(int[] value) {
		for (int i = 1; i < value.length; i++) {
			for (int j = 0; j < value.length - i; j++) {
				if (value[j] > value[j + 1]) {
					swap(value, j, j + 1);
				}
			}
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	private static void DoInsertionSort(int[] value) {
		for (int i = 1; i < value.length; i++) {
			int pointer = value[i];
			int j = i - 1;

			while (j >= 0 && value[j] > pointer) {
				value[j + 1] = value[j];
				j--;
			}
			value[j + 1] = pointer;
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	private static void BuildHeap(int[] value, int n) {
		if (n == 1) return;

		for (int i = (n - 2) / 2; i >= 0; i--) {
			percolateDown(value, i, n);
		}
	}

	private static void percolateDown(int[] value, int i, int n) {
		if (n == 1) return;

		int max = i;
		int left = 2 * i + 1;
		int right = 2 * i + 2;

		if (left < n && value[left] > value[max]) max = left;
		if (right < n && value[right] > value[max]) max = right;

		if (max != i) {
			swap(value, max, i);
			percolateDown(value, max, n);
		}
	}

	private static void DoHeapSort(int[] value) {
		BuildHeap(value, value.length);

		for (int i = 1; i < value.length; i++) {
			swap(value, 0, value.length - i);
			percolateDown(value, 0, value.length - i);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	private static void Merge(int[] value, int start, int mid, int end) {
		int start_2 = mid + 1;
		int[] sorted = new int[end - start + 1];
		int index = 0;
		int originalStart = start;

		while (start <= mid && start_2 <= end) {
			if (value[start] <= value[start_2]) {
				sorted[index++] = value[start++];
			} else {
				sorted[index++] = value[start_2++];
			}
		}

		while (start <= mid) {
			sorted[index++] = value[start++];
		}

		while (start_2 <= end) {
			sorted[index++] = value[start_2++];
		}

		System.arraycopy(sorted, 0, value, originalStart, sorted.length);
	}

	private static void MergeSort(int[] value, int start, int end) {
		if (start >= end) return;

		int mid = (start + end) / 2;
		MergeSort(value, start, mid);
		MergeSort(value, mid + 1, end);
		Merge(value, start, mid, end);
	}

	private static void DoMergeSort(int[] value) {
		MergeSort(value, 0, value.length - 1);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	private static int Partition(int[] value, int start, int end) {
		int pivot = value[end];
		int position = start;

		for (int i = start; i < end; i++) {
			if (value[i] < pivot) {
				swap(value, position++, i);
			}
		}
		swap(value, position, end);

		return position;
	}

	private static void QuickSort(int[] value, int start, int end, Random rand) {
		if (start >= end) return;

		swap(value, start + rand.nextInt(end - start + 1), end);
		int pivot = Partition(value, start, end);

		QuickSort(value, start, pivot - 1, rand);
		QuickSort(value, pivot + 1, end, rand);
	}

	private static void DoQuickSort(int[] value) {
		Random rand = new Random();
		QuickSort(value, 0, value.length - 1, rand);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	private static int FindMaxDigit(int[] value) {
		int maxDigits = 0;
		for (int num : value) {
			int digits = Integer.toString(Math.abs(num)).length();
			if (digits > maxDigits) {
				maxDigits = digits;
			}
		}
		return maxDigits;
	}

	private static void SortByDigit(int[] value, int exp, boolean asc) {
		int n = value.length;
		int[] sorted = new int[n];
		int[] count = new int[10];

		for (int j : value) {
			int digit = (j / exp) % 10;
			count[digit]++;
		}

		if (asc) {
			for (int i = 1; i < 10; i++) count[i] += count[i - 1];
		} else {
			for (int i = 9; i > 0; i--) count[i - 1] += count[i];
		}

		for (int i = n - 1; i >= 0; i--) {
			int digit = (value[i] / exp) % 10;
			sorted[count[digit] - 1] = value[i];
			count[digit]--;
		}

		System.arraycopy(sorted, 0, value, 0, n);
	}

	private static void DoRadixSort(int[] value) {
		int[] positive = Arrays.stream(value).filter(x -> x >= 0).toArray();
		int[] negative = Arrays.stream(value).filter(x -> x < 0).map(x -> -x).toArray();

		if (positive.length > 0) {
			int maxDigit = FindMaxDigit(positive);
			int exp = 1;

			while (maxDigit-- > 0) {
				SortByDigit(positive, exp, true);
				exp *= 10;
			}
		}

		if (negative.length > 0) {
			int maxDigit = FindMaxDigit(negative);
			int exp = 1;

			while (maxDigit-- > 0) {
				SortByDigit(negative, exp, false);
				exp *= 10;
			}

			for (int i = 0; i < negative.length; i++) negative[i] *= -1;
		}

		System.arraycopy(negative, 0, value, 0, negative.length);
		System.arraycopy(positive, 0, value, negative.length, positive.length);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	private static double FindDuplicateFraction(int[] value) {
		Map<Integer, Long> duplicateCounts = Arrays.stream(value)
				.boxed()
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

		int totalDuplicates = 0;
		for (int key : duplicateCounts.keySet()) {
			if (duplicateCounts.get(key) > 1) {
				totalDuplicates += (int) (duplicateCounts.get(key) - 1);
			}
		}

		return (double) totalDuplicates / value.length;
	}

	private static double FindPreSortedFraction(int[] value) {
		if (value.length <= 1) {
			return 1.0;
		}
		int count = 0;
		for (int i = 1; i < value.length; i++) {
			if (value[i - 1] <= value[i]) {
				count++;
			}
		}
		return (double) count / (value.length - 1);
	}

	private static char DoSearch(int[] value) {
		double duplicateFraction = FindDuplicateFraction(value);
		double preSortedFraction = FindPreSortedFraction(value);
		int maxDigit = FindMaxDigit(value);
		int length = value.length;

		if (length <= 410) {
			if (length <= 178) return ('I');

			if (length <= 244) {
				if (preSortedFraction <= 0.39) return ('Q');
				else return ('I');
			}

			// length 244~410
			else {
				if (preSortedFraction <= 0.45) return ('Q');

				if (preSortedFraction <= 0.60) {
					if (duplicateFraction <= 0.41) return ('I');
					else return ('Q');
				}
				// preSortedFraction 60% above
				else return ('I');
			}
		}

		// length 410 above
		else {
			if (maxDigit <= 4) {
				if (duplicateFraction <= 0.89) {
					if (length <= 14600) {
						if (maxDigit <= 3) return ('R');
						else return ('Q');
					} else return ('R');
				}
				else {
					if (length <= 3046) {
						if (maxDigit <= 2) return ('R');
						else return ('Q');
					} else return ('R');
				}
			}

			// maxDigit above 4
			else {
				if (preSortedFraction >= 0.99) return ('I');
				else {
					if (length <= 1100) {
						if (preSortedFraction <= 0.75) return ('Q');
						else return ('I');
					} else {
						return ('Q');
					}
				}
			}
		}


	}
	////////////////////////////////////////////////////////////////////////////////////////////////////
	private static void SortingTimeTest(int numsize, int rminimum, int rmaximum) {
		Map<String, Consumer<int[]>> algorithms = new LinkedHashMap<>();
		//algorithms.put("Bubble Sort", SortingTest::DoBubbleSort);
		//algorithms.put("Insertion Sort", SortingTest::DoInsertionSort);
		algorithms.put("Heap Sort", SortingTest::DoHeapSort);
		algorithms.put("Merge Sort", SortingTest::DoMergeSort);
		algorithms.put("Quick Sort", SortingTest::DoQuickSort);
		algorithms.put("Radix Sort", SortingTest::DoRadixSort);

		Random rand = new Random();
		Map<String, Long> SortAndTime = new LinkedHashMap<>();

		for (int k = 0; k < 10; k++) {
			int[] value = new int[numsize];
			for (int i = 0; i < value.length; i++) {
				value[i] = rand.nextInt(rmaximum - rminimum + 1) + rminimum;
			}
			for (Map.Entry<String, Consumer<int[]>> entry : algorithms.entrySet()) {
				String algorithmName = entry.getKey();
				Consumer<int[]> algorithm = entry.getValue();

				int[] tempArray = value.clone();
				long startTime = System.currentTimeMillis();
				algorithm.accept(tempArray);
				long duration = System.currentTimeMillis() - startTime;

				SortAndTime.put(algorithmName, SortAndTime.getOrDefault(algorithmName, 0L) + duration);
			}
		}

		for (String key : SortAndTime.keySet()) {
			System.out.println(key + ", " + SortAndTime.get(key) / 10.0);
		}
	}

	private static void CompareTime(){
		Random rand = new Random();
		int[] value = generateArray(30000, 10, rand);

		long start = System.nanoTime();
		char fastest_sort = DoSearch(value);
		long end = System.nanoTime();
		long duration = end - start;

		System.out.println("DoSearch performance time " + duration + "ns," + " Fastest sort found by Search is " + fastest_sort);

		int[] bubbleArr = value.clone();
		int[] insertionArr = value.clone();
		int[] mergeArr = value.clone();
		int[] heapArr = value.clone();
		int[] quickArr = value.clone();
		int[] radixArr = value.clone();
		String[] sortNames = {"B", "I", "M", "H", "Q", "R"};

		long start_2 = System.nanoTime();

		long bubbleTime = SortTime(SortingTest::DoBubbleSort, bubbleArr);
		long insertionTime = SortTime(SortingTest::DoInsertionSort, insertionArr);
		long mergeTime = SortTime(SortingTest::DoMergeSort, mergeArr);
		long heapTime = SortTime(SortingTest::DoHeapSort, heapArr);
		long quickTime = SortTime(SortingTest::DoQuickSort, quickArr);
		long radixTime = SortTime(SortingTest::DoRadixSort, radixArr);

		long[] sortTimes = {bubbleTime, insertionTime, mergeTime, heapTime, quickTime, radixTime};

		long minTime = sortTimes[0];
		String fastestSortActual = sortNames[0];
		for (int i = 1; i < sortTimes.length; i++) {
			if (sortTimes[i] < minTime) {
				minTime = sortTimes[i];
				fastestSortActual = sortNames[i];
			}
		}

		long end_2 = System.nanoTime();
		long duration_2 = end_2 - start_2;

		System.out.println("Doing all sort performance time " + duration_2 + "ns, " + "Fastest sort found by actual sorting is " + fastestSortActual);
	}

	public static long SortTime(Consumer<int[]> algorithm, int[] value){
		long start = System.nanoTime();
		algorithm.accept(value);
		long end = System.nanoTime();
		return end - start;
	}

	public static int[] generateArray(int length, int maxDigits, Random rand) {
		int[] arr = new int[length];

		int numDigits = rand.nextInt(maxDigits) + 1;
		int digitMinValue = (int) Math.pow(10, numDigits - 1);

		int digitMaxValue;
		if(numDigits == 10) digitMaxValue = Integer.MAX_VALUE;
		else digitMaxValue = (int) Math.pow(10, numDigits) - 1;

		int range = digitMaxValue - digitMinValue + 1;
		if (range <= 0) {
			range = Integer.MAX_VALUE - digitMinValue;
		}

		for (int k = 0; k < length; k++) {
			int value = rand.nextInt(range) + digitMinValue;
			if (rand.nextBoolean()) {
				value = -value;
			}
			arr[k] = value;
		}

		// 배열 정렬
		shuffleArray(arr);

		return arr;
	}

	public static void shuffleArray(int[] arr) {
		Random rand = new Random();
		for (int i = arr.length - 1; i > 0; i--) {
			int j = rand.nextInt(i + 1);
			int temp = arr[i];
			arr[i] = arr[j];
			arr[j] = temp;
		}
	}
}


