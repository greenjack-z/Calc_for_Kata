package calculator;

import calculator.exceptions.ExpressionInvalid;
import calculator.exceptions.ExpressionLongException;
import calculator.exceptions.ExpressionShortException;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {
    private final Logger logger = Logger.getLogger(App.class.getName());
    private int operand1;
    private int operand2;
    private String operator;
    private boolean roman;


    public void run() {
        logger.setLevel(Level.FINE);
        readConsole();
        int result = calculate();
        if (roman) {
            if (result < 0) {
                throw new ExpressionInvalid();
            }
            System.out.println(decimalToRoman(result));
        } else {
            System.out.println(result);
        }
    }

    private void readConsole() {
        try (Scanner scanner = new Scanner(System.in)) {
            String[] input = scanner.nextLine().split("\\s+");
            if (input.length < 3) {
                throw new ExpressionShortException();
            }
            if (input.length > 3) {
                throw new ExpressionLongException();
            }
            parseOperands(input[0].toUpperCase(), input[2].toUpperCase());
            operator = input[1];
        }
    }

    private void parseOperands(String operandInput1, String operandInput2) {
        String decimalPattern = "\\d|10";
        String romanPattern = "^(?=[MDCLXVI])M*(C[MD]|D?C{0,3})(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})$";
        if (operandInput1.matches(decimalPattern) && operandInput2.matches(decimalPattern)) {
            logger.info("operands are decimal");
            operand1 = Integer.parseInt(operandInput1);
            operand2 = Integer.parseInt(operandInput2);
        } else if (operandInput1.matches(romanPattern) && operandInput2.matches(romanPattern)) {
            logger.info("operands are roman");
            roman = true;
            operand1 = romanToDecimal(operandInput1);
            operand2 = romanToDecimal(operandInput2);
        } else {
            throw new ExpressionInvalid();
        }
    }

    private int romanToDecimal(String romanNumber) {
        return (int) evaluateNextRomanNumeral(romanNumber, romanNumber.length() - 1, 0);
    }

    private double evaluateNextRomanNumeral(String roman, int pos, double rightNumeral) {
        if (pos < 0) return 0;
        char ch = roman.charAt(pos);
        double value = Math.floor(Math.pow(10, "IXCM".indexOf(ch))) + 5 * Math.floor(Math.pow(10, "VLD".indexOf(ch)));
        return value * Math.signum(value + 0.5 - rightNumeral) + evaluateNextRomanNumeral(roman, pos - 1, value);
    }

    private String decimalToRoman(int decimalNumber) {
        String[] arrayM = {"", "M", "MM", "MMM"};
        String[] arrayC = { "",  "C",  "CC",  "CCC",  "CD", "D", "DC", "DCC", "DCCC", "CM" };
        String[] arrayX = { "",  "X",  "XX",  "XXX",  "XL", "L", "LX", "LXX", "LXXX", "XC" };
        String[] arrayI = { "",  "I",  "II",  "III",  "IV", "V", "VI", "VII", "VIII", "IX" };

        String thousands = arrayM[decimalNumber / 1000];
        String hundreds = arrayC[(decimalNumber % 1000) / 100];
        String tens = arrayX[(decimalNumber % 100) / 10];
        String ones = arrayI[decimalNumber % 10];

        return thousands + hundreds + tens + ones;
    }

    private int calculate() {
        switch (operator) {
            case "+" -> {
                return operand1 + operand2;
            }
            case "-" -> {
                return operand1 - operand2;
            }
            case "*" -> {
                return operand1 * operand2;
            }
            case "/" -> {
                return operand1 / operand2;
            }
            default -> {
                return Integer.MAX_VALUE;
            }
        }
    }
}
