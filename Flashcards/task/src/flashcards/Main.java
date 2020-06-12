package flashcards;
import java.io.*;
import java.util.*;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static SortedMap<String, String> cards = new TreeMap<>();
    private static SortedMap<String, Integer> mistakes = new TreeMap<>();
    private static ArrayList<String> logs = new ArrayList<>();
    private static String exportFileName;

    public static void action()
    {
        addLog("Write action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
        String action = scanner.nextLine();
        logs.add(action);
        switch (action)
        {
            case "exit": exit();
                break;
            case "add": createCards();
                break;
            case "remove": removeCard();
                break;
            case "import": importCard();
                break;
            case "export": exportCard();
                break;
            case "ask": ask();
                break;
            case "log": log();
                break;
            case "hardest card": hardestCard();
                break;
            case "reset stats": resetStats();
                break;
        }
        action();
    }

    public static void addLog(String str) {
        System.out.println(str);
        logs.add(str);
    }

    public static void exit() {
        addLog("Bye bye!");
        if (exportFileName != null) {
            exportCard(exportFileName);
        }
        System.exit(-1);
    }

    public static void removeCard() {
        addLog("The card:");
        String card = scanner.nextLine();
        logs.add(card);
        if (cards.containsKey(card)) {
            mistakes.remove(card);
            cards.remove(card);
            addLog("The card has been removed.");
        } else {
            addLog("Can't remove \"" + card + "\": there is no such card.");
        }
    }

    public static void exportCard() {
        addLog("File name:");
        String fileName = scanner.nextLine();
        logs.add(fileName);
        exportCard(fileName);
    }

    public static void  exportCard(String fileName) {
        int count = 0;
        try (PrintWriter file = new PrintWriter("./" + fileName)) {
            for (var card : cards.entrySet()) {
                file.println(card.getKey() + " " + card.getValue() + " " + mistakes.getOrDefault(card.getKey(), 0));
                count++;
            }
            addLog(count + " cards have been saved.");
        } catch (IOException e) {
            addLog("An exception occurs " + e.getMessage());
        }
    }

    public static void importCard() {
        addLog("File name:");
        String fileName = scanner.nextLine();
        logs.add(fileName);
        importCard(fileName);
    }

    public static void importCard(String fileName) {
        File file = new File("./" + fileName);
        try (Scanner readingFile = new Scanner(file)) {
            int count = 0;
            while (readingFile.hasNext()) {
                String[] card = readingFile.nextLine().split(" ");
                if (cards.containsKey(card[0])) {
                    cards.replace(card[0], card[1]);
                } else {
                    cards.put(card[0], card[1]);
                }
                mistakes.put(card[0], Integer.valueOf(card[2]));
                count++;
            }
            addLog(count + " cards have been loaded.");
        } catch (FileNotFoundException e) {
            addLog("not found");
        }
    }

    public static void createCards() {
            addLog("The card");
            String card = checkCard(scanner.nextLine());
            logs.add(card);

            addLog("The definition of the card");
            String definition = checkDefinition(scanner.nextLine());
            logs.add(definition);

            cards.put(card, definition);
            addLog("The pair (\"" + card +"\":\"" + definition +"\") has been added.\n");
    }

    public static String checkCard(String card) {
        if (cards.containsKey(card)) {
            addLog("The card \"" + card + "\" already exists. Try again:");
            action();
        }
        return card;
    }

    public static String checkDefinition(String definition) {
        if (cards.containsValue(definition)) {
            addLog("The definition \"" + definition + "\" already exists. Try again:");
            action();
        }
        return definition;
    }

    public static void ask() {
        addLog("How many times to ask?");
        int number = scanner.nextInt();
        scanner.nextLine();
        logs.add(String.valueOf(number));
        int count = 0;
        Random random = new Random();
        List<String> keys = new ArrayList<>(cards.keySet());

        for (int i = count; i < number; i++) {
            String randomKey = keys.get(random.nextInt(keys.size()));
            String card = cards.get(randomKey);
            addLog("Print the definition of \"" + randomKey + "\":");
            String answer = scanner.nextLine();
            logs.add(answer);
            if (answer.equals(card)) {
                addLog("Correct answer.");
                break;
            }
            else if (cards.containsValue(answer)) {
                for (var find : cards.entrySet()) {
                    if (find.getValue().equals(answer)) {
                        addLog("Wrong answer. The correct one is \"" + card +"\", you've just written the definition of \"" + find.getKey() + "\".");
                    }
                }
            }
            else {
                addLog("Wrong answer. The correct one is \"" + card + "\".");
            }

            mistakes.put(randomKey, mistakes.getOrDefault(randomKey, 0) + 1);
        }
    }

    public static void log() {
        addLog("File name:");
        String fileName = scanner.nextLine();
        logs.add(fileName);
        try (PrintWriter file = new PrintWriter("./" + fileName)) {
            for (String log : logs) {
                file.println(log);
            }
            addLog("The log has been saved.");
        } catch (IOException e) {
            addLog("An exception occurs " + e.getMessage());
        }
    }

    public static void hardestCard() {
        int maxMistakes;
        try {
            maxMistakes = Collections.max(mistakes.values());
            if (maxMistakes == 0) {
                addLog("There are no cards with errors.");
            }
            else {
                ArrayList<String> cards = new ArrayList<>();
                for (String card : mistakes.keySet()) {
                    if(mistakes.get(card) == maxMistakes) {
                        cards.add(card);
                    }
                }
                if (cards.size() == 1) {
                    System.out.print("The hardest card is");
                    logs.add("The hardest card is");
                }
                else {
                    System.out.print("The hardest card are");
                    logs.add("The hardest card are");
                }
                for (String name : cards) {
                    System.out.print(" \"" + name +"\"");
                    logs.set(logs.size() - 1, logs.get(logs.size() - 1) + " \"" + name +"\"");

                }
                System.out.print(". You have " + maxMistakes + " errors answering it.\n");
                logs.set(logs.size() - 1, logs.get(logs.size() - 1) + ". You have " + maxMistakes + " errors answering it.");
            }
        } catch (NoSuchElementException e) {
            addLog("There are no cards with errors.");
        }
    }

    public static void resetStats() {
        mistakes.clear();
        addLog("Card statistics has been reset.");
    }

    public static void main(String[] args)
    {
        for (int i = 0; i < args.length; i += 2) {
            if (args[i].equals("-import")) {
                importCard(args[i + 1]);
            } else if (args[i].equals("-export")) {
                exportFileName = args[i + 1];
            }
        }
        action();
    }
}
