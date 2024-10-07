import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
  
  
public class BigInteger
{
    public static final String QUIT_COMMAND = "quit";
    public static final String MSG_INVALID_INPUT = "Wrong Input";

    public static final Pattern EXPRESSION_PATTERN = Pattern.compile("\\s*([+-]?)\\s*(\\d+)\\s*([+\\-*])\\s*([+-]?)\\s*(\\d+)\\s*");
    private boolean isNegative;
    private byte[] value;

    public BigInteger(String s)
    {
        this.isNegative = false;

        if(s.startsWith("-")){
            this.isNegative = true;
            s = s.substring(1);
        } else if (s.startsWith("+")){
            s = s.substring(1);
        }

        this.value = parseStringToByte(s);
    }

    public void setNegative (boolean isNegative){
        this.isNegative = isNegative;
    }

    public BigInteger(byte[] result){
        this.value = result;
    }


    public byte[] parseStringToByte (String s){
        byte[] ByteArray = new byte[s.length()];
        int indexEnd = s.length() - 1;
        for (int i=0; i<s.length(); i++){
            ByteArray[i] = (byte) (s.charAt(indexEnd - i) - '0');
        }
        return ByteArray;
    }
  
    public BigInteger add(BigInteger big)
    {
        byte[] result = new byte[Math.max(this.value.length, big.value.length) + 1];
        int carry = 0;
        for (int i = 0; i < result.length; i++) {
            int sum = 0;
            if (i < this.value.length) sum += this.value[i];
            if (i < big.value.length) sum += big.value[i];
            sum += carry;
            carry = sum / 10;
            result[i] = (byte) (sum % 10);
        }
        return new BigInteger(result);
    }
  
    public BigInteger subtract(BigInteger big)
    {
        if (this.isBiggerThan(big)) {
            byte[] result = new byte[this.value.length];
            int shorter_length = big.value.length;
            int borrow = 0;

            for (int i = 0; i < result.length; i++) {
                int sub = 0;
                if (i < shorter_length) sub = this.value[i] - big.value[i] + borrow;
                else sub = this.value[i] + borrow;

                if (sub >= 0) {
                    result[i] = (byte) (sub);
                    borrow = 0;
                } else {
                    result[i] = (byte) (sub + 10);
                    borrow = -1;
                }
            }
            return new BigInteger(result);
        }
        else {
            return big.subtract(this);
        }
    }
  
    public BigInteger multiply(BigInteger big)
    {
        if (this.isBiggerThan(big)) {
            byte[] result = new byte[this.value.length + big.value.length];

            for (int i = 0; i < big.value.length; i++) {
                byte[] multiplication = new byte[this.value.length + 1];
                int carry = 0;
                for (int j = 0; j < this.value.length; j++) {
                    int mul = this.value[j] * big.value[i];
                    multiplication[j] = (byte) ((mul + carry) % 10);
                    carry = (mul + carry) / 10;
                }
                multiplication[this.value.length] = (byte) (carry);

                int temp_carry = 0;
                for (int k = i; k < multiplication.length + i; k++) {
                    int temp_add = result[k] + multiplication[k - i] + temp_carry;
                    result[k] = (byte) (temp_add % 10);
                    temp_carry = temp_add / 10;
                }
            }

            return new BigInteger(result);
        }
        else {
            return big.multiply(this);
        }
    }
  
    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();

        for (int i = this.value.length - 1; i >= 0; i--) {
            result.append((char) (this.value[i] + '0'));
        }
        while (result.length() > 0 && result.charAt(0) == '0') {
            result.deleteCharAt(0);
        }

        if (result.length()  == 0) return "0";
        if (this.isNegative) result.insert(0, '-');

        return result.toString();
    }

    public boolean isBiggerThan(BigInteger big){
        if (this.value.length > big.value.length) return true;
        else if (this.value.length < big.value.length) return false;
        else {
            for (int i=this.value.length-1; i >= 0; i--) {
                if (this.value[i] > big.value[i]) return true;
                else if (this.value[i] < big.value[i]) return false;
                else continue;
            }
            return true;
        }
    }
  
    static BigInteger evaluate(String input) throws IllegalArgumentException
    {
        Matcher matcher = EXPRESSION_PATTERN.matcher(input);

        if(!matcher.matches()){
            throw new IllegalArgumentException();
        }

        BigInteger first_operand = new BigInteger(matcher.group(1) + matcher.group(2));
        String operator = matcher.group(3);
        BigInteger second_operand = new BigInteger(matcher.group(4) + matcher.group(5));

        switch (operator){
            case "+":
                if (first_operand.isNegative ^ second_operand.isNegative){
                    BigInteger result = first_operand.subtract(second_operand);

                    if (first_operand.isBiggerThan(second_operand)){
                        if (first_operand.isNegative){
                            result.setNegative(true);
                        }
                        return result;
                    }
                    else {
                        if (!first_operand.isNegative){
                            result.setNegative(true);
                        }
                        return result;
                    }
                }
                else {
                    BigInteger result = first_operand.add(second_operand);
                    if (first_operand.isNegative) {
                        result.setNegative(true);
                    }
                    return result;
                }
            case "-":
                if (first_operand.isNegative ^ second_operand.isNegative){
                    BigInteger result = first_operand.add(second_operand);
                    if (first_operand.isNegative){
                        result.setNegative(true);
                    }
                    return result;
                }
                else {
                    BigInteger result = first_operand.subtract(second_operand);

                    if (first_operand.isBiggerThan(second_operand)) {
                        if (first_operand.isNegative){
                            result.setNegative(true);
                        }
                        return result;
                    }
                    else {
                        if (!first_operand.isNegative) {
                            result.setNegative(true);
                        }
                        return result;
                    }
                }
            case "*":
                if (first_operand.isNegative ^ second_operand.isNegative){
                    BigInteger result = first_operand.multiply(second_operand);
                    result.setNegative(true);
                    return result;
                }
                else {
                    return first_operand.multiply(second_operand);
                }
            default:
                throw new IllegalArgumentException();
        }

    }
  
    public static void main(String[] args) throws Exception
    {
        try (InputStreamReader isr = new InputStreamReader(System.in))
        {
            try (BufferedReader reader = new BufferedReader(isr))
            {
                boolean done = false;
                while (!done)
                {
                    String input = reader.readLine();
  
                    try
                    {
                        done = processInput(input);
                    }
                    catch (IllegalArgumentException e)
                    {
                        System.err.println(MSG_INVALID_INPUT);
                    }
                }
            }
        }
    }
  
    static boolean processInput(String input) throws IllegalArgumentException
    {
        boolean quit = isQuitCmd(input);
  
        if (quit)
        {
            return true;
        }
        else
        {
            BigInteger result = evaluate(input);
            System.out.println(result.toString());
  
            return false;
        }
    }
  
    static boolean isQuitCmd(String input)
    {
        return input.equalsIgnoreCase(QUIT_COMMAND);
    }
}
