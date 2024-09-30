import java.util.*;

public class Graph {
  private List<Location> cities;
  private List<Route> routes;
  private int numExists;

  public Graph(String nodesFile, String edgesFile) {
    this.routes = FileOperations.routeParser(edgesFile);
    this.cities = FileOperations.locationParser(nodesFile, routes);
    this.numExists = cities.size();
  }

  private int getCityIndex(String key) {
    for (int i = 0; i < cities.size(); i++) {
      if (cities.get(i).getCountry().equalsIgnoreCase(key)) {
        return i;
      }
    }
    return -1;
  }

  public Location getCity(String key) {
    int index = getCityIndex(key);
    if (index != -1) {
      return cities.get(index);
    } else {
      return null;
    }
  }

  private float getWeight(String startS, String endS, boolean costOrTime) {
    Location start = getCity(startS);
    Location end = getCity(endS);
    for (Route route : routes) {
      if (route.doesConnect(start, end)) {
        return costOrTime ? route.getCost() : route.getTime();
      }
    }
    return -1;
  }

  private float getWeight(Location start, Location end, boolean costOrTime) {
    for (Route route : routes) {
      if (route.doesConnect(start, end)) {
        return costOrTime ? route.getCost() : route.getTime();
      }
    }
    return -1;
  }

  public void dijkstra(String startS, boolean costOrTime) {
    // Get the starting location by its name using the getCity method.
    Location start = getCity(startS);

    // If the start location is not found, print an error message and exit the function.
    if (start == null) {
      System.out.println("Start location not found.");
      return;
    }

    // Initialize the start location's distance from itself to 0 (as this is the starting point).
    start.setLengthFromStart(0);

    // Create a priority queue (min-heap) to hold locations ordered by their "tentative" distance.
    // Location.CompareLocation defines the ordering (based on lengthFromStart).
    PriorityQueue<Location> minHeap = new PriorityQueue<>(new Location.CompareLocation());

    // Add all the cities to the priority queue (with their current tentative distances).
    for (Location city : cities) {
      minHeap.add(city);
    }

    // Main loop to process the priority queue (min-heap).
    // This loop continues until the priority queue is empty.
    while (!minHeap.isEmpty()) {
      // Continuously remove cities from the queue that have already been processed.
      // If a city is no longer "exists", it's skipped.
      while (!minHeap.isEmpty() && !minHeap.peek().isExists()) {
        minHeap.poll();
      }

      // Retrieve and remove the location with the smallest tentative distance from the heap.
      Location smallest = minHeap.poll();

      // If the heap is empty, exit the function as there are no more cities to process.
      if (smallest == null) {
        return;
      }

      // Mark this location as processed by setting its 'exists' flag to false.
      smallest.setExists(false);

      // Get the list of adjacent cities (connected by a route) to the current smallest city.
      List<Location> adjacentCities = adjacentLocations(smallest);

      // Loop through all adjacent cities to update their distances.
      for (Location adjacent : adjacentCities) {
        // Calculate the tentative distance to the adjacent city by adding the distance
        // from the start to the current city (smallest) and the weight of the route
        // between the smallest and adjacent city.
        float distance = getWeight(smallest, adjacent, costOrTime) + smallest.getLengthFromStart();

        // If the new tentative distance is smaller than the current distance of the adjacent city:
        if (distance < adjacent.getLengthFromStart()) {
          // Update the tentative distance of the adjacent city.
          adjacent.setLengthFromStart(distance);

          // Set the previous city for this adjacent city (used for path reconstruction later).
          adjacent.setPrevious(smallest);

          // Reorder the priority queue to ensure the city with the smallest tentative distance
          // is processed next. This is done by removing the adjacent city and re-adding it
          // with the updated distance.
          minHeap.remove(adjacent);
          minHeap.add(adjacent);
        }
      }
    }
  }

  public List<Location> adjacentLocations(Location city) {
    List<Location> output = new ArrayList<>();

    if (city == null || city.getRoutes() == null) {
      return output; // Return an empty list if city or its routes are null
    }

    for (Route route : city.getRoutes()) {
      if (route.getDestination() != null && route.getDestination().isExists()) {
        output.add(route.getDestination());
      }
    }

    return output;
  }

  private List<Route> adjacentRoutes(Location city) {
    List<Route> output = new ArrayList<>();
    for (Route route : routes) {
      if (route.getOrigin().getCapital().equals(city.getCapital())) {
        output.add(route);
      }
    }
    return output;
  }

  private Route getRoute(Location start, boolean costOrTime, float totalDistance) {
    List<Route> adjacentRoutes = adjacentRoutes(start);
    float epsilon = 1e-5f;

    for (Route route : adjacentRoutes) {
      if (costOrTime) {
        if (Math.abs(totalDistance - route.getCost() - route.getOrigin().getLengthFromStart()) > epsilon) {
          return route;
        }
      } else {
        if (Math.abs(totalDistance - route.getTime() - route.getOrigin().getLengthFromStart()) > epsilon) {
          return route;
        }
      }
    }
    return null;
  }

  public Stack<Location> cityStacker(String destinationS) {
    Location destination = getCity(destinationS);
    Stack<Location> stack = new Stack<>();
    while (destination != null) {
      stack.push(destination);
      destination = destination.getPrevious();
    }
    return stack;
  }

  public Stack<Route> routeStacker(String destinationS, boolean costOrTime) {
    Stack<Route> stack = new Stack<>();
    Location destination = getCity(destinationS);
    float totalDistance = destination.getLengthFromStart();

    while (destination.getPrevious() != null) {
      Route route = getRoute(destination.getPrevious(), costOrTime, totalDistance);
      if (route != null) {
        stack.push(route);
      }
      destination = destination.getPrevious();
      totalDistance = destination.getLengthFromStart();
    }
    return stack;
  }

  private void makeHeap(PriorityQueue<Location> minHeap) {
    // Create a new heap from the priority queue
    PriorityQueue<Location> newHeap = new PriorityQueue<>(minHeap.size(), new Location.CompareLocation());
    newHeap.addAll(minHeap);
    minHeap.clear();
    minHeap.addAll(newHeap);
  }
}

