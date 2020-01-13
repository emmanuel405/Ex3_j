package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import dataStructure.*;

class DGraphTest {
	node_data n = new NodeData();
	node_data n1 = new NodeData();
	int key_n = n.getKey(), key_n1 = n1.getKey();
	
	@Test
	void addNodeTest() {
		DGraph dg = new DGraph();
		dg.addNode(n);
		
		assertTrue(dg.v.containsKey(n.getKey()));
	}
	
	@Test
	void connectTest() {
		
		DGraph dg = new DGraph();
		dg.addNode(n);
		dg.addNode(n1);
		dg.connect(key_n, key_n1, 2);
		assertTrue(dg.ve.get(key_n).containsKey(key_n1));
	}
	
	@Test
	void removeNodeTest() {
		DGraph dg = new DGraph();
		dg.addNode(n);
		dg.addNode(n1);
		
		dg.removeNode(key_n1);
		
		assertFalse(dg.v.containsKey(key_n1));
	}
	
	@Test
	void removeEdgeTest() {	
		DGraph dg = new DGraph();
		dg.addNode(n);
		dg.addNode(n1);
		
		dg.connect(key_n, key_n1, 2);
		
		dg.removeEdge(key_n, key_n1);
		
		assertFalse(dg.ve.get(key_n).containsKey(key_n1));
	}
	
}
