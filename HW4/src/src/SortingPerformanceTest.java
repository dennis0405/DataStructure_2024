import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SortingPerformanceTest {

    // 정렬 알고리즘 이름 목록
    private static final String[] SORT_ALGORITHMS = {
            "I", "H", "M", "Q", "R"
    };

    // Random 객체 재사용
    private static final Random rand = new Random();

    public static void main(String[] args) {
        int numberOfTestsPerCombination = 100; // 각 조합당 생성할 테스트 수

        // 각 특성을 구간으로 분할
        int[] arrayLengthBins = {1, 100, 1000, 5000, 10000, 20000, 30000, 40000, 50000};
        double[] presortedRateBins = {0.0, 0.25, 0.5, 0.75, 1.0};
        double[] duplicateRateBins = {0.0, 0.25, 0.5, 0.75, 1.0};
        int[] maxDigitsBins = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

        // 스레드 풀 생성 (CPU 코어 수에 맞게 설정)
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = Executors.newFixedThreadPool(availableProcessors);

        List<Future<?>> futures = new ArrayList<>();

        // 모든 조합에 대해 데이터 생성
        for (int arrayLengthBinIndex = 0; arrayLengthBinIndex < arrayLengthBins.length - 1; arrayLengthBinIndex++) {
            for (int presortedRateBinIndex = 0; presortedRateBinIndex < presortedRateBins.length - 1; presortedRateBinIndex++) {
                for (int duplicateRateBinIndex = 0; duplicateRateBinIndex < duplicateRateBins.length - 1; duplicateRateBinIndex++) {
                    for (int maxDigitsBinIndex = 0; maxDigitsBinIndex < maxDigitsBins.length; maxDigitsBinIndex++) {

                        int lengthLower = arrayLengthBins[arrayLengthBinIndex];
                        int lengthUpper = arrayLengthBins[arrayLengthBinIndex + 1];
                        double presortedRateLower = presortedRateBins[presortedRateBinIndex];
                        double presortedRateUpper = presortedRateBins[presortedRateBinIndex + 1];
                        double duplicateRateLower = duplicateRateBins[duplicateRateBinIndex];
                        double duplicateRateUpper = duplicateRateBins[duplicateRateBinIndex + 1];
                        int currentMaxDigits = maxDigitsBins[maxDigitsBinIndex];

                        for (int test = 0; test < numberOfTestsPerCombination; test++) {
                            // 각 테스트를 별도의 스레드로 실행
                            final int finalLengthLower = lengthLower;
                            final int finalLengthUpper = lengthUpper;
                            final double finalPresortedRateLower = presortedRateLower;
                            final double finalPresortedRateUpper = presortedRateUpper;
                            final double finalDuplicateRateLower = duplicateRateLower;
                            final double finalDuplicateRateUpper = duplicateRateUpper;
                            final int finalMaxDigits = currentMaxDigits;

                            futures.add(executorService.submit(() -> {
                                runTest(finalLengthLower, finalLengthUpper,
                                        finalPresortedRateLower, finalPresortedRateUpper,
                                        finalDuplicateRateLower, finalDuplicateRateUpper,
                                        finalMaxDigits);
                            }));
                        }
                    }
                }
            }
        }

        // 모든 작업이 완료될 때까지 대기
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        executorService.shutdown();
    }

    // 각 테스트를 실행하는 함수
    public static void runTest(int lengthLower, int lengthUpper,
                               double presortedRateLower, double presortedRateUpper,
                               double duplicateRateLower, double duplicateRateUpper,
                               int maxDigits) {
        // 각 구간에서 무작위 값 선택
        int length = rand.nextInt(lengthUpper - lengthLower) + lengthLower;
        double presortedRate = rand.nextDouble() * (presortedRateUpper - presortedRateLower) + presortedRateLower;
        double duplicateRate = rand.nextDouble() * (duplicateRateUpper - duplicateRateLower) + duplicateRateLower;

        // 배열 생성
        int[] arr = generateArray(length, presortedRate, duplicateRate, maxDigits, rand);

        // 실제 중복 비율 및 사전 정렬 비율 계산
        double actualDuplicateRate = calculateDuplicateRate(arr);
        double actualPresortedRate = calculatePresortedRate(arr);

        // 배열 요소의 최대 자릿수 계산
        int actualMaxDigits = calculateMaxDigits(arr);

        // 각 정렬 알고리즘 실행 및 시간 측정
        Map<String, Long> sortTimes = new LinkedHashMap<>();

        // 정렬 알고리즘 실행
        for (String algorithm : SORT_ALGORITHMS) {
            long duration = executeSortingAlgorithm(algorithm, arr.clone());
            sortTimes.put(algorithm, duration);
        }

        // 가장 빠른 정렬 알고리즘 찾기
        String fastestSort = null;
        long minTime = Long.MAX_VALUE;
        for (Map.Entry<String, Long> entry : sortTimes.entrySet()) {
            long time = entry.getValue();
            if (time < minTime) {
                minTime = time;
                fastestSort = entry.getKey();
            }
        }

        // 모든 알고리즘이 실패한 경우 처리
        if (fastestSort == null) {
            fastestSort = "None";
        }

        // 결과를 CSV 파일에 저장
        writeResultsToCSV(length, actualPresortedRate, actualDuplicateRate, actualMaxDigits, fastestSort);
    }

    // 정렬 알고리즘 실행 및 시간 측정 함수
    public static long executeSortingAlgorithm(String algorithm, int[] arr) {
        long duration = Long.MAX_VALUE;
        try {
            long startTime = System.nanoTime();

            switch (algorithm) {
                case "I":
                    insertionSort(arr);
                    break;
                case "H":
                    heapSort(arr);
                    break;
                case "M":
                    mergeSort(arr);
                    break;
                case "Q":
                    quickSort(arr);
                    break;
                case "R":
                    radixSort(arr);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown sorting algorithm: " + algorithm);
            }

            long endTime = System.nanoTime();
            duration = endTime - startTime;
        } catch (Throwable t) {
            // 예외 처리
            System.err.println(algorithm + "에서 예외 발생: " + t.getMessage());
        }
        return duration;
    }

    // 배열 생성 함수 수정: 효율적인 요소 생성
    public static int[] generateArray(int length, double presortedRate, double duplicateRate, int maxDigits, Random rand) {
        int[] arr = new int[length];

        // 중복 요소를 생성하기 위한 값의 범위 설정
        int uniqueElementsCount = Math.max(1, (int) (length * (1 - duplicateRate)));
        int[] uniqueElements = new int[uniqueElementsCount];

        // 고유한 요소 생성
        for (int i = 0; i < uniqueElementsCount; i++) {
            int numDigits = rand.nextInt(maxDigits) + 1;
            int digitMinValue = (int) Math.pow(10, numDigits - 1);

            int digitMaxValue;
            if(numDigits == 10) digitMaxValue = Integer.MAX_VALUE;
            else digitMaxValue = (int) Math.pow(10, numDigits) - 1;

            int range = digitMaxValue - digitMinValue + 1;
            if (range <= 0) {
                range = Integer.MAX_VALUE - digitMinValue;
            }

            int value = rand.nextInt(range) + digitMinValue;

            // 음수 또는 양수 결정
            if (rand.nextBoolean()) {
                value = -value;
            }

            uniqueElements[i] = value;
        }
        System.arraycopy(uniqueElements, 0, arr, 0, uniqueElementsCount);

        for (int k = uniqueElementsCount; k < length; k++) {
            arr[k] = uniqueElements[rand.nextInt(uniqueElementsCount)];
        }

        // 배열 정렬
        shuffleArray(arr);

        int ascendingLength = (int) (length * presortedRate);
        int descendingLength = length - ascendingLength;

        // 앞 부분 오름차순 정렬
        if (ascendingLength > 0) {
            Arrays.sort(arr, 0, ascendingLength);
        }

        // 뒷 부분 내림차순 정렬
        if (descendingLength > 0) {
            Arrays.sort(arr, ascendingLength, length);
            reverseSubArray(arr, ascendingLength, length - 1);
        }

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

    public static void reverseSubArray(int[] arr, int start, int end) {
        while (start < end) {
            int temp = arr[start];
            arr[start] = arr[end];
            arr[end] = temp;
            start++;
            end--;
        }
    }

    // 중복 비율 계산 함수
    public static double calculateDuplicateRate(int[] value) {
        Map<Integer, Long> duplicateCounts = Arrays.stream(value)
                .boxed()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        int totalDuplicates = 0;
        for(int key : duplicateCounts.keySet()) {
            if(duplicateCounts.get(key) > 1) {
                totalDuplicates += (int) (duplicateCounts.get(key) - 1);
            }
        }

        return (double) totalDuplicates / value.length;
    }

    // 사전 정렬 비율 계산 함수
    public static double calculatePresortedRate(int[] arr) {
        if (arr.length <= 1) {
            return 1.0;
        }
        int count = 0;
        for (int i = 1; i < arr.length; i++) {
            if (arr[i - 1] <= arr[i]) {
                count++;
            }
        }
        return (double) count / (arr.length - 1);
    }

    // 배열 요소의 최대 자릿수 계산 함수
    public static int calculateMaxDigits(int[] arr) {
        int maxDigits = 0;
        for (int num : arr) {
            int digits = Integer.toString(Math.abs(num)).length();
            if (digits > maxDigits) {
                maxDigits = digits;
            }
        }
        return maxDigits;
    }

    // 결과를 CSV 파일에 저장하는 함수 (동기화 처리)
    public static void writeResultsToCSV(int length, double presortedRate, double duplicateRate, int maxDigits, String fastestSort) {
        File csvFile = new File("sort_results.csv");
        boolean isNewFile = !csvFile.exists();

        synchronized (SortingPerformanceTest.class) {
            try (FileWriter csvWriter = new FileWriter(csvFile, true)) {
                // 새 파일이면 헤더 추가
                if (isNewFile) {
                    csvWriter.append("Array Length,Presorted Rate,Duplicate Rate,Max Digits,Fastest Sort\n");
                }

                csvWriter.append(String.join(",",
                        String.valueOf(length),
                        String.format("%.4f", presortedRate),
                        String.format("%.4f", duplicateRate),
                        String.valueOf(maxDigits),
                        fastestSort));
                csvWriter.append("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void swap(int[] value, int i, int j) {
        int tmp = value[i];
        value[i] = value[j];
        value[j] = tmp;
    }

    public static void insertionSort(int[] value) {
        for (int i=1; i<value.length; i++) {
            int pointer = value[i];
            int j = i-1;

            while (j >= 0 && value[j] > pointer) {
                value[j + 1] = value[j];
                j--;
            }
            value[j+1] = pointer;
        }
    }

    private static void BuildHeap(int[] value, int n) {
        if (n == 1) return;

        for(int i=(n-2)/2; i>=0; i--){
            percolateDown(value, i, n);
        }
    }

    private static void percolateDown(int[] value, int i, int n) {
        if (n == 1) return;

        int max = i;
        int left = 2*i + 1;
        int right = 2*i + 2;

        if(left < n && value[left] > value[max]) max = left;
        if(right < n && value[right] > value[max]) max = right;

        if(max != i){
            swap(value, max, i);
            percolateDown(value, max, n);
        }
    }

    public static void heapSort(int[] value) {
        BuildHeap(value, value.length);

        for(int i=1; i<value.length; i++) {
            swap(value, 0, value.length-i);
            percolateDown(value, 0, value.length-i);
        }
    }

    private static void Merge(int[] value, int start, int mid, int end) {
        int start_2 = mid + 1;
        int[] sorted = new int[end - start + 1];
        int index = 0;
        int originalStart = start;

        while (start <= mid && start_2 <= end) {
            if (value[start] <= value[start_2]) {
                sorted[index++] = value[start++];
            }
            else {
                sorted[index++] = value[start_2++];
            }
        }

        while (start <= mid){
            sorted[index++] = value[start++];
        }

        while (start_2 <= end){
            sorted[index++] = value[start_2++];
        }

        System.arraycopy(sorted, 0, value, originalStart, sorted.length);
    }

    public static void DoMergeSort(int[] value, int start, int end) {
        if (start >= end) return;

        int mid = (start + end) / 2;
        DoMergeSort(value, start, mid);
        DoMergeSort(value, mid + 1, end);
        Merge(value, start, mid, end);
    }

    private static void mergeSort(int[] value) {
        DoMergeSort(value, 0, value.length-1);
    }

    private static int Partition(int[] value, int start, int end) {
        int pivot = value[end];
        int position = start;

        for(int i=start; i<end; i++){
            if(value[i] < pivot){
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

    public static void quickSort(int[] value) {
        Random rand	= new Random();
        QuickSort(value, 0, value.length-1, rand);
    }

    private static int FindMaxDigit(int[] value) {
        int max = Math.abs(value[0]);
        for (int i=1; i<value.length; i++) {
            max = Math.max(max, Math.abs(value[i]));
        }
        return Integer.toString(max).length();
    }

    private static void SortByDigit(int[] value, int exp, boolean asc) {
        int n = value.length;
        int[] sorted = new int[n];
        int[] count = new int[10];

        for (int j : value) {
            int digit = (j / exp) % 10;
            count[digit]++;
        }

        if(asc) {
            for (int i=1; i < 10; i++) count[i] += count[i-1];
        }
        else {
            for (int i=9; i > 0; i--) count[i-1] += count[i];
        }

        for (int i = n-1; i >= 0; i--) {
            int digit = (value[i] / exp) % 10;
            sorted[count[digit] - 1] = value[i];
            count[digit]--;
        }

        System.arraycopy(sorted, 0, value, 0, n);
    }

    public static void radixSort(int[] value) {
        int[] positive = Arrays.stream(value).filter(x -> x >= 0).toArray();
        int[] negative = Arrays.stream(value).filter(x -> x < 0).map(x -> -x).toArray();

        if(positive.length > 0) {
            int maxDigit = FindMaxDigit(positive);
            int exp = 1;

            while(maxDigit-- > 0){
                SortByDigit(positive, exp, true);
                exp *= 10;
            }
        }

        if(negative.length > 0) {
            int maxDigit = FindMaxDigit(negative);
            int exp = 1;

            while(maxDigit-- > 0) {
                SortByDigit(negative, exp, false);
                exp *= 10;
            }

            for (int i=0; i<negative.length; i++) negative[i] *= -1;
        }

        System.arraycopy(negative, 0, value, 0, negative.length);
        System.arraycopy(positive, 0, value, negative.length, positive.length);
    }
}
