package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;

import algorithms.Graph_Algo;
import dataStructure.*;

class Graph_AlgoTest implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	node_data n = new NodeData();
	node_data n1 = new NodeData();
	node_data n2 = new NodeData();
	node_data n3 = new NodeData();
	node_data n4 = new NodeData();

	@Test
	void init_graphTest() {
		
		graph dg = new DGraph();
		dg.addNode(n1);
		dg.addNode(n);
		dg.connect(n1.getKey(), n.getKey(), 3);
		
		Graph_Algo g = new Graph_Algo();
		g.init(dg);
		
		assertTrue(g.a.edgeSize()==1);

	}

	@Test
	void copyTest() {
		
		graph dg = new DGraph();
		
		dg.addNode(n1);
		dg.addNode(n);
		dg.connect(n1.getKey(), n.getKey(), 3);

		Graph_Algo g=new Graph_Algo();
		g.init(dg);
		
		graph new_g = new DGraph();
		new_g = g.copy();

		assertEquals(new_g.edgeSize(), g.a.edgeSize());

	}

	@Test
	void isConnectTest() {
		
		graph dg = new DGraph();
		dg.addNode(n1);
		dg.addNode(n);
		dg.connect(n1.getKey(), n.getKey(), 3);
		dg.connect(n.getKey(), n1.getKey(), 3);

		Graph_Algo g=new Graph_Algo();
		g.init(dg);
		
		assertTrue(g.isConnected());

	}

	@Test
	void shortestPathDistTest() {
		
		graph dg = new DGraph();
		dg.addNode(n1);
		dg.addNode(n);
		dg.addNode(n3);
		dg.connect(n.getKey(), n1.getKey(), 10);
		dg.connect(n1.getKey(), n3.getKey(), 10);

		Graph_Algo g=new Graph_Algo();
		g.init(dg);
		
		assertTrue(g.shortestPathDist(n.getKey(), n3.getKey()) == 20.0);

	}

	@Test 
	void shortestPathTest() {
		
		node_data[] arr = {n,n1,n2,n3,n4};

		graph dg = new DGraph();
		for (int i = 0; i < arr.length; i++)
			dg.addNode(arr[i]);
		
		dg.connect(n.getKey(), n1.getKey(), 2);
		dg.connect(n.getKey(), n2.getKey(), 5);
		dg.connect(n.getKey(), n3.getKey(), 14);
		dg.connect(n1.getKey(), n3.getKey(), 4);
		dg.connect(n2.getKey(), n3.getKey(), 1.5);
		
		Graph_Algo g = new Graph_Algo();
		g.init(dg);
		
		List<node_data> good = g.shortestPath(n.getKey(), n3.getKey());
		
		assertNotEquals(null, good);
		
		List<node_data> bad = g.shortestPath(n.getKey(), n4.getKey());
		
		assertEquals(null, bad);
		
	}
	
	@Test 
	void TSPTest() {
		node_data[] arr = {n,n1,n2,n3,n4};

		graph dg = new DGraph();
		for (int i = 0; i < arr.length; i++)
			dg.addNode(arr[i]);
		
		dg.connect(n.getKey(), n1.getKey(), 2);
		dg.connect(n.getKey(), n2.getKey(), 5);
		dg.connect(n.getKey(), n3.getKey(), 4);
		dg.connect(n1.getKey(), n.getKey(), 2.3);
		dg.connect(n1.getKey(), n2.getKey(), 0.4);
		dg.connect(n1.getKey(), n3.getKey(), 4);
		dg.connect(n2.getKey(), n1.getKey(), 1.5);
		dg.connect(n2.getKey(), n.getKey(), 1.5);
		dg.connect(n2.getKey(), n3.getKey(), 1.5);
		dg.connect(n3.getKey(), n.getKey(), 1.5);
		dg.connect(n3.getKey(), n1.getKey(), 1.5);
		dg.connect(n3.getKey(), n2.getKey(), 1.5);
		
		Graph_Algo g = new Graph_Algo();
		g.init(dg);
		
		LinkedList<Integer> good = new LinkedList<>();
		good.add(n1.getKey());
		good.add(n.getKey());
		
		LinkedList<Integer> bad = new LinkedList<>();
		bad.add(n1.getKey());
		bad.add(n3.getKey());
		bad.add(n4.getKey());
		
		assertNotEquals(null, g.TSP(good));

		List<node_data> s = g.TSP(bad);
		assertEquals(null, s);
		
		
	}

	@Test
	void initStirngTest() {
		graph dg = new DGraph();
		dg.addNode(n1);
		dg.addNode(n);
		dg.connect(n1.getKey(), n.getKey(), 3);
		dg.connect(n.getKey(), n1.getKey(), 3.2);

		Graph_Algo g = new Graph_Algo();
		g.init(dg);
		g.save("myObj.txt");
	
	
		Graph_Algo g_a = new Graph_Algo();
String fi="myObj";
		g_a.init(fi);
		
		assertTrue(g_a.isConnected());
		

	}

}
