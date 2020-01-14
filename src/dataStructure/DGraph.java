package dataStructure;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dataStructure.*;
import gameClient.Fruit;
import gameClient.Robot;
import utils.Point3D;

public class DGraph implements graph, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int mc;

	public ArrayList<node_data> Vertex;
	public HashMap<Integer, node_data> v;
	public HashMap<Integer, HashMap<Integer, edge_data>> ve;
	public ArrayList<Robot> Robots=new ArrayList<Robot>();
	public ArrayList<Fruit> Fruits=new ArrayList<Fruit>();


	public DGraph() {
		v = new HashMap<Integer, node_data>();
		ve = new HashMap<Integer, HashMap<Integer, edge_data>>();
		Vertex= new ArrayList<node_data>();
		this.mc = 0;
	}

	/**
	 * @return the node that key is his data  
	 */
	@Override
	public node_data getNode(int key) {
		if(v.get(key)!=null)
			return v.get(key);
		return null;
	}

	/**
	 * at first we check if 've' is empty
	 * if not, we check if the inside Hashmap of ve is empty too
	 * if not we return this edge that inside the value.
	 * 
	 * @return ed = edge we searching
	 */
	@Override
	public edge_data getEdge(int src, int dest) {
		if (ve.get(src)==null) {
			return null;
		}
		HashMap<Integer, edge_data> fi = ve.get(src);
		if (fi.get(dest)==null)
			return null;

		edge_data ed= fi.get(dest);

		return ed;
	}

	/**
	 * we put to 'v' the node with this key
	 */
	@Override
	public void addNode(node_data n) {
		int key = n.getKey();
		v.put(key, n);
		Vertex.add(n);

		this.mc++;
	}

	/**
	 * if the nodes contains in v;
	 * we create an edge and added in arrayList 'e'.
	 * build H-M 'in' put inside a node_data with dest id.
	 * and put this H-M in 've' H-M
	 *  
	 */
	@Override
	public void connect(int src, int dest, double w) {
		if (v.containsKey(src) && v.containsKey(dest)) {
			edge_data edge = new edgeData(src, dest, w);
			HashMap<Integer, edge_data> a=new  HashMap<Integer, edge_data>();

			if(!ve.containsKey(src)) {
				ve.put(src, a);
			}
			HashMap<Integer, edge_data> in =ve.get(src);
			if (in==null) {
				in=new HashMap<Integer, edge_data>();
			}
			in.put(dest, edge);
			a=in;
			NodeData te=(NodeData)v.get(src);
			te.outgoing.add((NodeData)v.get(dest));

			mc++;
		}
		else{
			System.out.println("One of them no create yet, we can't use connect methods.");
		}
	}

	@Override
	public Collection<node_data> getV() {
		return Vertex;
	}

	/**
	 * @param node_id, ed
	 * we create H-M 'first', we copy all H-M that in 've' inside our node_id,
	 * and we past all of the node in 'first'.
	 * all the edge that we find we added to ed arrayList
	 * 
	 * @return ed
	 */
	@Override
	public Collection<edge_data> getE(int node_id) {
		HashMap<Integer, edge_data> first = ve.get(node_id);
		return first.values();
	}

	/**
	 * @param key, nd
	 * 
	 * we check if this the a node with key id exist (if no = return null)
	 * we past in all ve's values,
	 * we remove the node nd from v and we promote mc.
	 * so we remove the node from v.
	 * 
	 * @return node to remove
	 */
	@Override
	public node_data removeNode(int key) {
		if(v.get(key) == null) 
			return null;
		else {
			Vertex.remove(v.get(key).getKey());
			for (HashMap<Integer, edge_data> nd : ve.values()) {
				if (nd.containsKey(key))
					nd.remove(key);
			}
			for (node_data n : Vertex) {
				NodeData temp=(NodeData)n;
				if(temp.outgoing.contains(this.getNode(key)))
					temp.outgoing.remove(key);
			}
			this.mc++;
			return v.remove(key);
		}
	}


	/**
	 * @param first(H-M), edge(edge_data)
	 * 
	 * we copy to first the H-M that we find with the src id,
	 * and to 'edge' we put a result of remove the node with dest id.
	 * edge = null/dest
	 * if he null we don't promote mc and don't substract sunOfEdge.
	 * 
	 * @return edge to remove
	 */
	@Override
	public edge_data removeEdge(int src, int dest) {
		HashMap<Integer, edge_data> first = ve.get(src);
		edge_data edge = first.remove(dest);
		if(edge != null) {
			this.mc++;
			NodeData n=(NodeData)this.v.get(src);
			n.outgoing.remove(v.get(dest));
		}
		return edge;
	}

	@Override
	public int nodeSize() {
		return Vertex.size();
	}

	@Override
	public int edgeSize() {
		return ve.size();
	}

	@Override
	public int getMC() {
		return this.mc;
	}

	/**
	 * Performs a recursive Depth First Search on the
	 * 'root' node (the first vertex created)
	 * @return true if connected, false if empty or not connected
	 */
	public boolean DepthFirstSearch(){
		if (Vertex.isEmpty()) return true;
		for (node_data n : Vertex) {

			clearStates();

			// get first node
			NodeData root =(NodeData) n;
			if (root==null) return false;

			// call recursive function
			DepthFirstSearch((NodeData)root);
			if (isConnected1()==false)
				return false;
		}
		return true;
	}

	/**
	 * Helper for Depth first search
	 * @param v vertex
	 */
	private void DepthFirstSearch(NodeData v){
		v.setInfo("VISITED");

		// loop through neighbors
		for ( NodeData each : v.outgoing){
			if (each.getInfo().equalsIgnoreCase("UNVISITED")){
				DepthFirstSearch(each);
			}
		}
		v.setInfo("COMPLETE");
	}

	/**
	 * Sets all states to UNVISITED
	 */
	private void clearStates(){
		for (node_data each : Vertex){
			each.setInfo("UNVISITED");
		}
	}


	/**
	 * Test if DFS or BFS returned a connected graph
	 * @return true if connected, false if not.
	 */
	public boolean isConnected1(){
		for (node_data each :this.Vertex){
			if (!each.getInfo().equalsIgnoreCase("COMPLETE"))
				return false;
		}
		return true;
	}

	/**
	 * Creates path information on the graph using the Dijkstra Algorithm,
	 * Puts the info into the Vertices, based on given starting vertex.
	 * @param from starting vertex
	 * @return true if successfull, false if empty or not found.
	 */
	private boolean Dijkstra(NodeData from){
		if (Vertex.isEmpty())
			return false;

		// reset all vertices minDistance and previous
		resetDistances();

		NodeData source = (NodeData)this.getNode(from.getKey());
		// set to 0 and add to heap
		source.minDistance = 0;
		PriorityQueue<NodeData> pq = new PriorityQueue<>();
		pq.add(source);

		while (!pq.isEmpty()){
			//pull off top of queue
			NodeData prev = pq.poll();

			// loop through adjacent vertices
			for (NodeData next : prev.outgoing){

				// get edge
				edge_data e = this.getEdge(prev.getKey(), next.getKey());
				if (e == null)
					return false;

				// add cost to current
				double totalDistance = prev.minDistance + e.getWeight();
				if (totalDistance < next.minDistance){

					// new cost is smaller, set it and add to queue
					pq.remove(prev);
					next.minDistance = totalDistance;

					// link vertex
					next.previous = prev;
					pq.add(next);
				}
			}
		}
		return true;
	}


	/**
	 * Goes through the result tree created by the Dijkstra method
	 * and steps backward
	 * @param target Vertex end of path
	 * @return string List of vertices and costs
	 */
	private List<node_data> getShortestPath1(node_data target){
		List<node_data> path = new ArrayList<node_data>();
		NodeData tar=(NodeData)target;

		// check for no path found
		if (tar.minDistance==Integer.MAX_VALUE){
			path.add(null);
			return path;
		}

		// loop through the vertices from end target 
		for (NodeData v = tar; v !=null; v = v.previous){
			path.add(v );
		}

		// flip the list
		Collections.reverse(path);
		return path;
	}

	/**
	 * for Dijkstra, resets all the path tree fields
	 */
	private void resetDistances(){
		for (node_data each : Vertex){
			NodeData a=(NodeData)each;
			a.minDistance = Integer.MAX_VALUE;
			a.previous = null;
		}
	}

	/**
	 * PUBLIC WRAPPER FOR PRIVATE FUNCTIONS
	 * Calls the Dijkstra method to build the path tree for the given
	 * starting vertex, then calls getShortestPath method to return
	 * a list containg all the steps in the shortest path to
	 * the destination vertex.
	 * @param from value of type T for Vertex 'from'
	 * @param to value of type T for vertex 'to'
	 * @return ArrayList of type String of the steps in the shortest path.
	 */
	public  List<node_data> getPath(node_data from, node_data to){
		if(from==null||to==null)
			System.out.println("One of them no create yet, we can't use connect methods.");

		boolean test = Dijkstra((NodeData)from);
		if (test==false) {
			return null;
		}
		List<node_data> path = getShortestPath1((NodeData)this.getNode(to.getKey()));

		return path;
	}

	public void init1(String file_name) {
		DGraph b;
		try{   

			FileInputStream file = new FileInputStream(file_name); 

			ObjectInputStream in = new ObjectInputStream(file);

			b = (DGraph)in.readObject();
			this.v=b.v;
			this.ve=b.ve;
			this.Vertex=b.Vertex;
			in.close();
			file.close();

		} 

		catch(IOException ex) { 
			System.out.println("IOException is caught"); 
		} 

		catch(ClassNotFoundException ex) { 
			System.out.println("ClassNotFoundException is caught"); 
		}
	}

	public void save(String file_name) {
		try {    
			FileOutputStream file = new FileOutputStream(file_name);
			ObjectOutputStream out = new ObjectOutputStream(file);
			out.writeObject(this); 

			out.close();
			file.close(); 
		} catch(IOException ex) { 
			System.out.println("IOException is caught"); 
		}
	}

	public void init(String g) {
		JSONObject line;
		try {
			line = new JSONObject(g);//////////make the string be JSONObject
			JSONArray arr = line.getJSONArray("Nodes");	//////make array of nodes from json

			for (int i = 0; i < arr.length(); i++)//run on array and creat every node
			{
				int id = arr.getJSONObject(i).getInt("id");
				String pos = arr.getJSONObject(i).getString("pos");
				String[] cord= pos.split(",");
				this.addNode(new NodeData(id,new Point3D(scale(Double.parseDouble(cord[0]),35.186179,35.2142,0,1000),
						scale(Double.parseDouble(cord[1]),32.100148,32.109347,100,600))));

			}		

			JSONArray arr2 = line.getJSONArray("Edges");	//////make array of edge from json
			for (int i = 0; i < arr2.length(); i++)//run on array and creat every edge
			{
				int src = arr2.getJSONObject(i).getInt("src");
				int dest = arr2.getJSONObject(i).getInt("dest");
				double  w = arr2.getJSONObject(i).getDouble("w");

				this.connect(src, dest, w);

			}			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}



	/**
	 * we put to Fruits the fruit n
	 */
	public void addfruit(Fruit n) {
		//scale
		n.pos=new Point3D(scale(n.pos.x(),35.186179,35.2142,0,1000),scale(n.pos.y(),32.100148,32.109347,100,600));
		Fruits.add(n);
		this.mc++;
	} 

	/**
	 * we put to robots the robot n
	 */
	public void addrobot(Robot n) {
		//scale
		n.pos=new Point3D(scale(n.pos.x(),35.186179,35.2142,0,1000),scale(n.pos.y(),32.100148,32.109347,100,600));
		Robots.add(n);

		this.mc++;
	}

	////////scale the cordinnants
	public double scale(double data, double r_min, double r_max, 
			double t_min, double t_max)
	{

		double res = ((data - r_min) / (r_max-r_min)) * (t_max - t_min) + t_min;
		return res;
	}
}
