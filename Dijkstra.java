

import java.util.Collection; 
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.ArrayList;

public class Dijkstra {

	private Map<String, Vertex> vertexNames;
	public Dijkstra() {
		vertexNames = new HashMap<String, Vertex>();
	}

	public void addVertex(Vertex v) {
		if (vertexNames.containsKey(v.name))
			throw new IllegalArgumentException("Cannot create new vertex with existing name.");

		vertexNames.put(v.name, v);
	}
	public Collection<Vertex> getVertices() {
		return vertexNames.values();
	}

	public Vertex getVertex(String name) {
		return vertexNames.get(name);
	}

	public void addEdge(String nameU, String nameV, Double cost) {
		if (!vertexNames.containsKey(nameU))
			throw new IllegalArgumentException(nameU + " does not exist. Cannot create edge.");
		if (!vertexNames.containsKey(nameV))
			throw new IllegalArgumentException(nameV + " does not exist. Cannot create edge.");
		Vertex sourceVertex = vertexNames.get(nameU);
		Vertex targetVertex = vertexNames.get(nameV);
		Edge newEdge = new Edge(sourceVertex, targetVertex, cost);
		sourceVertex.addEdge(newEdge);
	}

	public void addUndirectedEdge(String nameU, String nameV, double cost) {
		addEdge(nameU, nameV, cost);
		addEdge(nameV, nameU, cost);
	}


	public double computeEuclideanDistance(double ux, double uy, double vx, double vy) {
		double Xs = Math.pow(ux-vx, 2);
		double Ys = Math.pow(uy-vy, 2);
		double distance = Math.pow((Xs+Ys), 0.5);
		return distance;
	}

	public void computeAllEuclideanDistances() {
		for (String u : vertexNames.keySet()) {
			for (Edge e : vertexNames.get(u).adjacentEdges) {
				double edgeSourceX = e.source.x;
				double edgeSourceY = e.source.y;
				double edgeTargetX = e.target.x;
				double edgeTargetY = e.target.y;
				e.distance = computeEuclideanDistance(edgeSourceX, edgeSourceY,
						edgeTargetX, edgeTargetY);
			}
		}
	}


	public void doDijkstra(String s) {

		List<String> LefttoCheck = new ArrayList<>();
		for (Vertex u : vertexNames.values()){
			LefttoCheck.add(u.name); 
		}
		int idxOfchecking = LefttoCheck.indexOf(s);
		Vertex checking = vertexNames.get(LefttoCheck.remove(idxOfchecking));

		checking.distance = 0;

		for (int i = 0; i < LefttoCheck.size(); i++){
			Vertex v = vertexNames.get(LefttoCheck.get(i));
			v.distance = 10000000;
		}

		for (Edge e : checking.adjacentEdges){
			Vertex target = e.target;
			int idxOfModifying = LefttoCheck.indexOf(target.name);
			Vertex modifying = vertexNames.get(LefttoCheck.get(idxOfModifying));
			modifying.prev = checking;
			modifying.distance = checking.distance + e.distance;
		}

		while (!LefttoCheck.isEmpty())
		{
			double smallestSoFar = 10000000;
			int indexToRemove = 0;
			for (int i = 0; i < LefttoCheck.size(); i++){
				Vertex v = vertexNames.get(LefttoCheck.get(i));
				if (v.distance < smallestSoFar){
					smallestSoFar = v.distance;
					indexToRemove = i;
				}
			}
			checking = vertexNames.get(LefttoCheck.remove(indexToRemove));

			for (Edge e : checking.adjacentEdges){

				Vertex target = e.target;

				if (LefttoCheck.contains(target.name)) {
					int idxOfModifying = LefttoCheck.indexOf(target.name);
					Vertex modifying = vertexNames.get(target.name);
					double tmp = checking.distance + e.distance;
					
					if (modifying.distance > tmp ) {
						modifying.distance = tmp;

						modifying.prev = checking;
					}
				}
			}
		}
	}

	public List<Edge> getDijkstraPath(String s, String t) {
		//Reset all the variables, go through all the vertex objects
		for (Vertex u : vertexNames.values()){
			u.distance = 10000000;
			u.prev = null;
		}

		doDijkstra(s);

		List<Edge> path = new ArrayList<>();

		Vertex v = vertexNames.get(t);
		while (v.prev != null){
			for (Edge e : v.adjacentEdges){
				if (e.target.equals(v.prev)){
					path.add(0, e);
				}
			}
			v = v.prev;
		}
		return path;
	}


	public void printAdjacencyList() {
		for (String u : vertexNames.keySet()) {
			StringBuilder sb = new StringBuilder();
			sb.append(u);
			sb.append(" -> [ ");
			for (Edge e : vertexNames.get(u).adjacentEdges) {
				sb.append(e.target.name);
				sb.append("(");
				sb.append(e.distance);
				sb.append(") ");
			}
			sb.append("]");
			System.out.println(sb.toString());
		}
	}

	public static void main(String[] argv) throws IOException {
		String vertexFile = "cityRusxy.txt";
		String edgeFile = "cityRuspairs.txt";

		Dijkstra dijkstra = new Dijkstra();
		String line;

		BufferedReader vertexFileBr = new BufferedReader(new FileReader(vertexFile));
		while ((line = vertexFileBr.readLine()) != null) {
			String[] parts = line.split(",");
			if (parts.length != 3) {
				vertexFileBr.close();
				throw new IOException("Invalid line in vertex file " + line);
			}

			String cityname = parts[0];
			int x = Integer.valueOf(parts[1]);
			int y = Integer.valueOf(parts[2]);
			Vertex vertex = new Vertex(cityname, x, y);
			dijkstra.addVertex(vertex);
		}
		vertexFileBr.close();

		//Read in the edges
		BufferedReader edgeFileBr = new BufferedReader(new FileReader(edgeFile));
		while ((line = edgeFileBr.readLine()) != null) {
			String[] parts = line.split(",");
			if (parts.length != 3) {
				edgeFileBr.close();
				throw new IOException("Invalid line in edge file " + line);
			}
			dijkstra.addUndirectedEdge(parts[0], parts[1], Double.parseDouble(parts[2]));
		}
		edgeFileBr.close();

		dijkstra.computeAllEuclideanDistances();

		dijkstra.printAdjacencyList();

		String startCity = " ";
		String endCity = " ";

		List<Edge> path = dijkstra.getDijkstraPath(startCity, endCity);

		System.out.print("Shortest path between "+startCity+" and "+endCity+": ");
		System.out.println(path);
	}
}
