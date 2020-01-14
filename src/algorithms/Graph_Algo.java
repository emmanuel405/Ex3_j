package algorithms;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import dataStructure.*;

/**
 * This empty class represents the set of graph-theory algorithms
 * which should be implemented as part of Ex2 - Do edit this class.
 * @author 
 *
 */

public class Graph_Algo implements graph_algorithms,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public  graph a;
	
	public Graph_Algo(graph _graph) {
		this.a=_graph;
	}
	
	public Graph_Algo() {
		this.a=new DGraph();

	}

	@Override
	public void init(graph g) {
		this.a=g;
	}

	@Override
	public void init(String file_name) {
		DGraph b = (DGraph)this.a;
		b.init1(file_name);
	}

	@Override
	public void save(String file_name) {
		DGraph b = (DGraph)this.a;
		b.save(file_name);
	} 
	

	@Override
	public boolean isConnected() {
		DGraph ans=(DGraph)a;
		return ans.DepthFirstSearch();
	}

	@Override
	public double shortestPathDist(int src, int dest) {
		
		DGraph dig=(DGraph)a;
		List<node_data> path = dig.getPath(a.getNode(src), a.getNode(dest));
		NodeData ans=(NodeData)path.get(path.size()-1);
		return ans.getminDistance();
	}

	public List<node_data> shortestPath(int src, int dest){
		DGraph dig=(DGraph)a;
		List<node_data> path = dig.getPath(a.getNode(src), a.getNode(dest));
		
		return path;
	}

	/**
	 * @param Lisr targets, List ans
	 * we check if all the targets contains on our list of vertex key
	 * yes => we past on all member's 'targets' and we put two number that
	 * adjacent each other in shorsetPath. and we added the result in ans
	 * 
	 * @return ans
	 */
	@Override
	public List<node_data> TSP(List<Integer> targets) {
		LinkedList<node_data> ans = new LinkedList<>();
		for (int i = 1; i < targets.size(); i++) {
			List<node_data> tmp = shortestPath(targets.get(i-1), targets.get(i));
			if (tmp.contains(null)) return null;
			ans.addAll(tmp);
		}
		// remove node who is same key was adjacent to each other
		for (int i = 1; i < ans.size(); i++) {
			if(ans.get(i-1) == ans.get(i)) ans.remove(i);
		}
		return ans;
	}

	/**
	 * @param DGraph ans, tmp[<= a]
	 * to do a good copy, we put all of H-M in tmp to H-M of ans
	 * and copy the list of vertex
	 * 
	 * @return ans
	 */
	@Override
	public graph copy() { // Vertex , HM v , HM ve,
		DGraph ans = new DGraph();
		DGraph tmp = (DGraph)this.a;
		
		ans.Vertex.addAll(tmp.getV());
		ans.v.putAll(tmp.v);
		ans.ve.putAll(tmp.ve);
		
		tmp.mc = ans.mc;
	
		return ans;

	}

}
