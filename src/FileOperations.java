import java.io.*;
import java.util.*;
import java.awt.Color;

public class FileOperations {

  public static List<Location> locationParser(String filename, List<Route> routes) {
    List<Location> cities = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
      String line;
      while ((line = reader.readLine()) != null) {
        String[] tokens = line.split(",", -1);

        if (tokens.length < 4) continue; // Skip malformed lines

        String country = tokens[0];
        String city = tokens[1];
        double latitude = Double.parseDouble(tokens[2]);
        double longitude = Double.parseDouble(tokens[3]);

        Location node = new Location(country, city, latitude, longitude);

        for (Route route : routes) {
          if (route.getOriginS().equals(node.getCapital())) {
            route.setOrigin(node);
            node.getRoutes().add(route);
          } else if (route.getDestinationS().equals(node.getCapital())) {
            route.setDestination(node);
          }
        }

        cities.add(node);
      }
      System.out.println("Cities Parsed from: " + filename);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return cities;
  }

  public static List<Route> routeParser(String filename) {
    List<Route> allRoutes = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
      String line;
      while ((line = reader.readLine()) != null) {
        String[] tokens = line.split(",", -1);

        if (tokens.length < 6) continue; // Skip malformed lines

        String originS = tokens[0];
        String destinationS = tokens[1];
        String type = tokens[2];
        float time = Float.parseFloat(tokens[3]);
        float cost = Float.parseFloat(tokens[4]);
        String note = tokens[5];

        Route edge = new Route(null, null, type, time, cost, note);
        edge.setDestinationS(destinationS);
        edge.setOriginS(originS);

        allRoutes.add(edge);
      }
      System.out.println("Routes Parsed from: " + filename);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return allRoutes;
  }

  public static void outputGenerator(String filename, Stack<Location> cities, Stack<Route> routes, boolean costOrTime) {
    try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
      writer.println("<HTML><HEAD><TITLE>Shortest path from Italy to Kazakhstan</TITLE></HEAD>");
      writer.println("<script type='text/javascript' src='http://maps.google.com/maps/api/js?sensor=false'></script>");
      writer.println("<script>function initialize() {");
      writer.println("var myOptions = { zoom: 3, center: new google.maps.LatLng(0, 0), mapTypeId: google.maps.MapTypeId.ROADMAP };");
      writer.println("var map = new google.maps.Map(document.getElementById('map'), myOptions);");

      int markerCount = 0;
      int contentStringCount = 0;

      while (!cities.isEmpty() && !routes.isEmpty()) {
        Location origin = cities.peek();
        cities.pop();
        Location destination = cities.peek();
        Route route = routes.peek();
        routes.pop();

        writer.printf("var marker%d = new google.maps.Marker({ position: new google.maps.LatLng(%f, %f), map: map, title: \"%s, %s\"});%n",
                markerCount++, origin.getLat(), origin.getLon(), origin.getCapital(), origin.getCountry());
        writer.printf("var marker%d = new google.maps.Marker({ position: new google.maps.LatLng(%f, %f), map: map, title: \"%s, %s\"});%n",
                markerCount++, destination.getLat(), destination.getLon(), destination.getCapital(), destination.getCountry());

        float cost = route.getCost();
        if (route.getTransport().equals("plane")) {
          cost = cost / Route.MULTI;
        }

        writer.printf("var contentString%d = \"%s, %s --> %s, %s (%s - %f hours - $%f)\";%n",
                contentStringCount, origin.getCapital(), origin.getCountry(), destination.getCapital(), destination.getCountry(),
                route.getTransport(), route.getTime(), cost);
        writer.printf("var path%d = new google.maps.Polyline({ path: [new google.maps.LatLng(%f, %f), new google.maps.LatLng(%f, %f)], strokeColor: '#0000FF', strokeOpacity: 1.0, strokeWeight: 2 });%n",
                contentStringCount, origin.getLat(), origin.getLon(), destination.getLat(), destination.getLon());
        writer.printf("path%d.setMap(map); google.maps.event.addListener(path%d, 'click', function(event) { alert(contentString%d); });%n",
                contentStringCount, contentStringCount, contentStringCount);

        contentStringCount++;
      }

      writer.println("} google.maps.event.addDomListener(window, 'load', initialize); </script></HEAD><BODY><div id='map' style='width:100%;height:100%;'></div></BODY></HTML>");
      System.out.println("Output File Generated: " + filename);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
