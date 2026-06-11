
import cli.CommandProcessor;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CommandProcessor cp = new CommandProcessor();
        System.out.println("Kalah Assistant");
        while (true) {
            System.out.print("> ");
            String line = scanner.nextLine();
            if ("quit".equalsIgnoreCase(line)) break;
            cp.execute(line);
        }
    }
}
