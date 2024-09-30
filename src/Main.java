import java.awt.*;
import java.io.*;
import java.util.*;

public class Main {
  public static void main(String[] args) {
    String citiesFilename;
    String routesFilename;
    String outputFilename;
    String origin;
    String destination;
    String preference;
    boolean biPreference;


    Scanner scanner = new Scanner(System.in);

    // Read filenames and other inputs from command line or user input
    if (args.length > 0) {
      citiesFilename = args[0];
    } else {
      System.out.print("Enter filename containing cities: ");
      citiesFilename = scanner.nextLine();
    }

    if (args.length > 1) {
      routesFilename = args[1];
    } else {
      System.out.print("Enter filename containing routes: ");
      routesFilename = scanner.nextLine();
    }

    if (args.length > 2) {
      outputFilename = args[2];
    } else {
      System.out.print("Enter filename for output (.html): ");
      outputFilename = scanner.nextLine();
    }

    if (args.length > 3) {
      origin = args[3];
    } else {
      System.out.print("Origin Country name: ");
      origin = scanner.nextLine();
    }

    if (args.length > 4) {
      destination = args[4];
    } else {
      System.out.print("Destination Country name: ");
      destination = scanner.nextLine();
    }

    if (args.length > 5) {
      preference = args[5];
    } else {
      System.out.print("Enter a preference (fastest/cheapest): ");
      preference = scanner.nextLine();
    }

    if (preference.equals("cheapest")) {
      biPreference = true;
    } else if (preference.equals("fastest")) {
      biPreference = false;
    } else {
      System.out.println("Invalid entry");
      return;
    }


    // Create a Graph object and perform operations
    Graph graph = new Graph(citiesFilename, routesFilename);

    if (graph.getCity(origin) == null || graph.getCity(destination) == null) {
      System.out.println("Invalid Origin/Destination.");
      return;
    }

    graph.dijkstra(origin, biPreference);


    Stack<Location> cityStack = graph.cityStacker(destination);
    Stack<Route> routeStack = graph.routeStacker(destination, biPreference);
    try{
      FileOperations.outputGenerator(outputFilename, cityStack, routeStack, biPreference);
      System.out.println("Process completed.");
      openHtmlFile(outputFilename);
    } catch (Exception e){
      System.out.println("Invalid" + e.getMessage());
    }
  }
  private static void openHtmlFile(String filePath) {
    File htmlFile = new File(filePath);
    if (Desktop.isDesktopSupported()) {
      Desktop desktop = Desktop.getDesktop();
      if (htmlFile.exists()) {
        try {
          desktop.open(htmlFile);
        } catch (IOException e) {
          e.printStackTrace();
        }
      } else {
        System.out.println("File does not exist: " + filePath);
      }
    } else {
      System.out.println("Desktop API is not supported on this platform.");
    }
  }
}
