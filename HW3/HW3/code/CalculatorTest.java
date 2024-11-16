import java.io.*;
import java.util.*;

public class CalculatorTest {
	public static void main(String args[]) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		while (true) {
			try {
				String input = br.readLine();
				if (input.compareTo("q") == 0)
					break;

				command(input);
			} catch (Exception e) {
				System.out.println("ERROR");
			}
		}
	}

	private static final Map<Character, Integer> precedence = new HashMap<>();
	static {
		precedence.put('(', 0);
		precedence.put(')', 0);
		precedence.put('+', 1);
		precedence.put('-', 1);
		precedence.put('*', 2);
		precedence.put('/', 2);
		precedence.put('%', 2);
		precedence.put('~', 3);
		precedence.put('^', 3);
	}

	private static boolean isOperator(char c) {
		return precedence.containsKey(c);
	}

	private static boolean isRightAssociative(char c){
		return c == '^';
	}

	private static String infixToPostfix(String infix) throws Exception {
		//infix를 postfix로 변환하는 shunting yard algorithm 사용 (GPT 도움)
		Stack<Character> stack = new Stack<>();
		StringBuilder postfix = new StringBuilder();

		//infix를 token으로 parse
		StringTokenizer tokenizer = new StringTokenizer(infix, "()+-*/%^ \t", true);

		boolean isUnary = true;
		int ParenthesisCount = 0;
		boolean previousOperator = true;
		boolean previousNumber = false;


		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken().trim();

			//공백, \t 처리
			if (token.isEmpty()) continue;

			char c = token.charAt(0);

			//숫자인 경우
			if (token.matches("\\d+")) {
				if(previousNumber) throw new Exception("Space between numbers");

				postfix.append(token).append(' ');
				isUnary = false;
				previousOperator = false;
				previousNumber = true;
			}

			//연산자인 경우
			else if (isOperator(c)) {
				if (c == '('){
					ParenthesisCount++;
					stack.push(c);
					isUnary = true;
					previousOperator = true;
					previousNumber = false;
				}
				else if (c == ')'){
					//')' 개수가 더 많으면 유효하지 않은 식
					ParenthesisCount--;
					if (ParenthesisCount < 0) throw new Exception("Invalid parenthesis");

					while (!stack.isEmpty() && stack.peek() != '('){
						postfix.append(stack.pop()).append(' ');
					}

					//혹시 모를 EmptyStackException 처리 (위에서 Invalid Parenthesis로 처리되겠지만, 혹시 몰라 작성)
					//'('를 pop
					if (!stack.isEmpty()) stack.pop();
					isUnary = false;
					previousOperator = false;
					previousNumber = false;
				}
				else if (c == '-'){
					//Unary인 경우, right associative 라서 '~'로 바로 push
					if (isUnary) stack.push('~');
					//Opeartor인 경우
					else {
						while (!stack.isEmpty() && precedence.get(stack.peek()) >= precedence.get(c)){
							postfix.append(stack.pop()).append(' ');
						}
						stack.push(c);
						isUnary = true;
						previousOperator = true;
						previousNumber = false;
					}
				}
				//-를 제외한 Operator인 경우
				else {
					//연속된 operator처리
					if (previousOperator) throw new Exception("Invalid operator");

					while (!stack.isEmpty() && precedence.get(stack.peek()) >= precedence.get(c) && !isRightAssociative(c)){
						postfix.append(stack.pop()).append(' ');
					}
					//Right associative 면 바로 push, 아니면 pop 을 진행한 후에 push
					stack.push(c);
					isUnary = true;
					previousOperator = true;
					previousNumber = false;
				}
			}
			// 숫자도 아니고, operator도 아닌 경우
			else {
				throw new Exception("Invalid Operation");
			}
		}
		// stack에 남아있는 연산자 출력
		while (!stack.isEmpty()){
			postfix.append(stack.pop()).append(' ');
		}

		// infix notation의 마지막이 ')'를 제외한 연산자인 경우
		if (previousOperator) throw new Exception("Invalid Operation");

		// '('의 수가 더 많은 경우
		if (ParenthesisCount > 0) throw new Exception("Invalid Parenthesis");

		// 맨 마지막의 공백 trim
		return postfix.toString().trim();
	}

	private static Long CalculatePostfix(String postfix) throws Exception {
		Stack<Long> stack = new Stack<>();
		String[] postfixList = postfix.split(" ");

        for (String s : postfixList) {
            char ch = s.charAt(0);

			// 숫자인 경우
            if (s.matches("\\d+")) {
                stack.push(Long.valueOf(s));
            }
			// 연산자인 경우
            if (isOperator(ch)) {
                switch (ch) {
                    case '+':
						// 알아서 long 범위 내에서 순환 처리가 됨
                        stack.push(stack.pop() + stack.pop());
                        break;
                    case '-':
                        stack.push((stack.pop() - stack.pop()) * -1);
						break;
                    case '*':
                        stack.push(stack.pop() * stack.pop());
                        break;
                    case '/':
                        long a = stack.pop();
						long b = stack.pop();
						if (a == 0) throw new Exception("Invalid Denominator");
						stack.push(b / a);
                        break;
                    case '%':
                        long c = stack.pop();
						long d = stack.pop();
						if (c == 0) throw new Exception("Invalid Denominator");
						stack.push(d % c);
                        break;
                    case '~':
                        stack.push(stack.pop() * -1);
                        break;
                    case '^':
                        long e = stack.pop();
                        long f = stack.pop();
						if (f == 0 && e < 0) throw new Exception("Invalid Denominator");
                        stack.push((long) Math.pow(f, e));
                        break;
                }
            }
        }

		return stack.pop();
	}

	private static void command(String input)
	{
		try {
			//infix to postfix, postfix calculation 먼저 둘 다 진행
			//변환 과정 혹은 계산 과정에서 exception 발생 시 error 출력을 위함
			String postfix = infixToPostfix(input);
			Long result = CalculatePostfix(postfix);

			System.out.println(postfix);
			System.out.println(result);

		} catch (Exception e) {
			System.out.println("ERROR");
		}
	}
}
