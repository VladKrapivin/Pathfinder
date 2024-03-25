import java.util.LinkedList; 
import java.util.List;

public class Vertex {

  public String name;
  public int x;
  public int y;
  public boolean known;
  public double distance;
  public Vertex prev;
  public List<Edge> adjacentEdges;

  public Vertex(String name, int x, int y) {
    this.name = name;
    this.x = x;
    this.y = y;
    adjacentEdges = new LinkedList<Edge>();
    prev = null;
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null) {
      return false;
    }
    if (!(o instanceof Vertex)) {
      return false;
    }
    Vertex oVertex = (Vertex) o;

    return name.equals(oVertex.name) && x == oVertex.x && y == oVertex.y;
  }

  public void addEdge(Edge edge) {
    adjacentEdges.add(edge);
  }

  public String toString() {
    return name + " (" + x + ", " + y + ")";
  }

}