import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class test {
    public static void main(String[] args) {
        try (
                Scanner scanner = new Scanner(new InputStreamReader(System.in, StandardCharsets.UTF_8));
                PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8), true)
        ) {
            out.println("한국어 입력을 테스트합니다. 입력해주세요:");
            if (scanner.hasNextLine()) {
                String input = scanner.nextLine();
                out.println("입력된 내용: " + input);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
