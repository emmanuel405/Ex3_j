package tests;

import dataStructure.*;
import gameClient.Fruit;
import utils.Point3D;
import GUI.Graph_gui;

public class GuiTseting {

	public static void main(String[] args) {

		NodeData n = new NodeData(0);
		NodeData n1 = new NodeData(1);
		NodeData n2 = new NodeData(2);
		NodeData n3 = new NodeData(3);
		NodeData n4 = new NodeData(4);
		NodeData n5 = new NodeData(5);
		
		Point3D p = new Point3D(700, 300);
		Point3D p1 = new Point3D(300, 300);
		Point3D p2 = new Point3D(200, 250);
		
		Point3D p3 = new Point3D(100, 500);
		Point3D p4 = new Point3D(300, 500);
		Point3D p5 = new Point3D(200, 550);
		
		n.setLocation(p);
		n1.setLocation(p1);
		n2.setLocation(p2);
		n3.setLocation(p3);
		n4.setLocation(p4);
		n5.setLocation(p5);
		
		DGraph dg1 = new DGraph();
		
		dg1.addNode(n);
		dg1.addNode(n1);
		dg1.addNode(n2);
		dg1.addNode(n3);
		dg1.addNode(n4);
		dg1.addNode(n5);
		
		dg1.connect(n.getKey(), n1.getKey(), 5);
		dg1.connect(n1.getKey(), n5.getKey(), 4);
		dg1.connect(n.getKey(), n5.getKey(), 1);
		
		dg1.connect(n3.getKey(), n4.getKey(), 0);
		dg1.connect(n4.getKey(), n2.getKey(), 2);
		dg1.connect(n3.getKey(), n2.getKey(), 10);
		//Fruit f= new Fruit("35.18753053591606,32.10378225882353");
		//dg1.addfruit(n);
		
		
		Graph_gui g = new Graph_gui();
		g.addGraph(dg1);
		g.setVisible(true);
	
	}

}
