package interpreter.view;

import interpreter.controller.Controller;
import interpreter.exceptions.MyException;
import interpreter.model.PrgState;
import interpreter.model.adt.MyDictionary;
import interpreter.model.adt.MyList;
import interpreter.model.adt.MyStack;
import interpreter.repository.Repository;
import interpreter.view.Examples.Example;

import java.util.List;
import java.util.Scanner;

public class View {
    private final Scanner in = new Scanner(System.in);
    private Example selected = null;

    public void run() {
        while (true) {
            printMenu();
            String choice = readLine();
            if (choice == null) return;
            switch (choice.trim()) {
                case "1" -> selectProgram();
                case "2" -> runSelected();
                case "0" -> { return; }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("=== Toy interpreter ===");
        System.out.println("Selected program: " + (selected == null ? "(none)" : selected.description()));
        System.out.println("1. Select a program");
        System.out.println("2. Run the selected program");
        System.out.println("0. Exit");
        System.out.print("> ");
    }

    private void selectProgram() {
        List<Example> examples = Examples.all();
        for (int i = 0; i < examples.size(); i++)
            System.out.println((i + 1) + ". " + examples.get(i).description());
        System.out.print("Program number: ");
        String line = readLine();
        if (line == null) return;
        try {
            int n = Integer.parseInt(line.trim());
            if (n >= 1 && n <= examples.size()) {
                selected = examples.get(n - 1);
                System.out.println("Selected: " + selected.description());
                return;
            }
        } catch (NumberFormatException ignored) {
        }
        System.out.println("Invalid program number.");
    }

    private void runSelected() {
        if (selected == null) {
            System.out.println("No program selected. Choose option 1 first.");
            return;
        }
        PrgState state = new PrgState(new MyStack<>(), new MyDictionary<>(),
                new MyList<>(), selected.program());
        Controller controller = new Controller(new Repository(state), true);
        try {
            controller.allStep();
            System.out.println("Execution finished. Output: " + state.getOut());
        } catch (MyException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private String readLine() {
        return in.hasNextLine() ? in.nextLine() : null;
    }
}