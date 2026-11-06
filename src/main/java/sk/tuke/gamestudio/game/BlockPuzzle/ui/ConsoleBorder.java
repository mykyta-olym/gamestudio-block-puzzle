package sk.tuke.gamestudio.game.BlockPuzzle.ui;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class ConsoleBorder {

    // ANSI Color Codes for styling
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_GREEN = "\u001B[32m"; // Додавання нового кольору для тексту

    private static String stripAnsiCodes(String text) {
        return text.replaceAll("\u001B\\[[;\\d]*m", "");
    }

    private static String repeat(String str, int count) {
        return new String(new char[count]).replace("\0", str);
    }

    private static void printTextWithAnimation(String text) {
        for (char ch : text.toCharArray()) {
            System.out.print(ch);
            // Delay to simulate typing
            try {
                TimeUnit.MILLISECONDS.sleep(15); // Adjust this value to speed up or slow down the animation
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void printTextInBorder(String text) {
        String[] lines = text.split("\n");
        int maxLength = Arrays.stream(lines).mapToInt(line -> stripAnsiCodes(line).length()).max().orElse(0);

        // Покращена горизонтальна межа з використанням ANSI_GREEN для додавання кольору
        String horizontalBorder = ANSI_GREEN + "+" + repeat("═", maxLength + 2) + "+" + ANSI_RESET;
        System.out.println(horizontalBorder);

        for (String line : lines) {
            int paddingLength = maxLength - stripAnsiCodes(line).length();
            System.out.print(ANSI_GREEN + "║" + ANSI_RESET + " ");
            // Використання ANSI_BLUE для кольору тексту всередині рамки
            printTextWithAnimation(ANSI_BLUE + line + repeat(" ", paddingLength) + ANSI_RESET);
            System.out.println(ANSI_RESET + ANSI_GREEN + "║" + ANSI_RESET);
        }

        System.out.println(horizontalBorder);
    }

}
