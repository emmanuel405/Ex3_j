package GUI;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.*;

import javax.swing.JFrame;

import algorithms.Graph_Algo;
import dataStructure.*;
import utils.Point3D;

import java.util.LinkedList;
import java.util.List;

public class Graph_gui extends JFrame implements ActionListener, MouseListener, MouseMotionListener{
	public DGraph dg = null;
	Graph_Algo g_a = new Graph_Algo(dg);
	Point3D point_pressed = null;
	LinkedList<node_data> list = new LinkedList<node_data>();
	LinkedList<Point3D> mPoints = new LinkedList<Point3D>();
	LinkedList<Point3D> ans_Point = new LinkedList<Point3D>(); // I put src & dest after pressing on someone node.

	private boolean connect = false;
	private boolean press_connect = false; // if we press on 'connected'
	private boolean press_path = false; // if we press on 'shorted path'
	private boolean press_dist = false; // if we press on 'shorted path dist'
	private boolean press_tsp = false; // if we press on 'TSP	'
	private int BIGGER = 5;
	private double PathDist=0;
	private List<node_data> list1;

	public Graph_gui() {
		initGUI();
	}

	private void initGUI() {
		this.setSize(1000, 800);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		MenuBar mBar = new MenuBar();
		Menu m = new Menu("Menu");
		Menu m1 = new Menu("Algo");
		mBar.add(m);
		mBar.add(m1);
		this.setMenuBar(mBar);

		MenuItem item = new MenuItem("Load");
		item.addActionListener(this);
		MenuItem item1 = new MenuItem("Save");
		item1.addActionListener(this);
		MenuItem item2 = new MenuItem("Clean graph");
		item2.addActionListener(this);

		m.add(item);
		m.add(item1);
		m.add(item2);

		MenuItem item3 = new MenuItem("Connected");
		item3.addActionListener(this);
		MenuItem item4 = new MenuItem("Shorted path dist");
		item4.addActionListener(this);
		MenuItem item5 = new MenuItem("TSP");
		item5.addActionListener(this);
		MenuItem item6 = new MenuItem("Shorted path");
		item6.addActionListener(this);


		m1.add(item3);
		m1.add(item6);
		m1.add(item4);
		m1.add(item5);

		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}

	public void paint(Graphics g) {
		super.paint(g);

		// press on 'connected' item
		if(press_connect) {
			String ans = "";
			if(connect) {
				ans += "the graph is CONNECTED";
				g.setColor(Color.BLUE);
			}
			else {
				ans += "the graph is NOT CONNECTED";
				g.setColor(Color.RED);
			}
			g.drawString(ans, 700, 180);
		}

		// press on 'Shorted path' item
		if(press_path) {

			if(list.size() == 2) {
				node_data n = list.get(0);
				node_data n1 = list.get(1);
				list1 = g_a.shortestPath(n.getKey(), n1.getKey());
				node_data tmp = null;
				for (node_data nd : list1) {
					if(tmp != null) {
						g.setColor(Color.GREEN);
						g.fillOval(tmp.getLocation().ix() - BIGGER, tmp.getLocation().iy() - BIGGER,
								(int)2.5*BIGGER, (int)2.5*BIGGER);

						g.fillOval(nd.getLocation().ix() - BIGGER, nd.getLocation().iy() - BIGGER,
								(int)2.5*BIGGER, (int)2.5*BIGGER);

						g.drawLine(tmp.getLocation().ix(), tmp.getLocation().iy(), nd.getLocation().ix(), nd.getLocation().iy());
					}
					g.fillOval(nd.getLocation().ix() - BIGGER, nd.getLocation().iy() - BIGGER,
							(int)2.5*BIGGER, (int)2.5*BIGGER);
					tmp = nd;
				}
			}
		}


		// press on 'Shorted path dist' item
		if(press_dist) {
			String ans = "the shorted distance is: "+PathDist;
			g.setColor(Color.BLACK);
			g.drawString(ans, 400, 100);
		}
		
		// press on 'TSP' item
		if(press_tsp) {

		}
		// for gal
		g.setColor(Color.BLACK);
		g.drawString("бс''г", 900, 80);
		
		if (null == dg) return;
		for (node_data n : dg.Vertex) {

			g.setColor(Color.BLUE);
			g.fillOval((int)n.getLocation().x() - BIGGER, (int)n.getLocation().y() - BIGGER,
					(int)2.5*BIGGER, (int)2.5*BIGGER);

			NodeData nn = (NodeData)n;
			for (NodeData m :nn.outgoing ) {
				g.setColor(Color.RED);
				g.drawLine(n.getLocation().ix(), n.getLocation().iy(),
						m.getLocation().ix(), m.getLocation().iy());

				edge_data ed = dg.getEdge(n.getKey(), m.getKey());
				g.drawString(String.format("%.2f", ed.getWeight()),
						drawOnLine(n.getLocation().x(), m.getLocation().x(), 0.75),
						drawOnLine(n.getLocation().y(), m.getLocation().y(), 0.75));

				g.setColor(Color.BLACK);
				g.fillOval(drawOnLine(n.getLocation().x(), m.getLocation().x(), 0.85),
						drawOnLine(n.getLocation().y(), m.getLocation().y(), 0.85),
						4, 4);
			}



		}
		press_connect = press_path =
				press_dist = press_tsp = false;
	}


	@Override

	public void actionPerformed(ActionEvent action) {
		String s = action.getActionCommand();

		switch(s) {

		case "Load":

			break;

		case "Save":

			break;

		case "Clean graph":				
			dg.v.clear();
			repaint();
			break;

		case "Connected":
			Graph_Algo g1 = new Graph_Algo();
			g1.init(dg);
			press_connect = true;
			connect = g1.isConnected();

			repaint();
			break;

		case "Shorted path":
			Graph_Algo g3 = new Graph_Algo();
			g3.init(dg);
			press_path = true;
			
			repaint();
			break;

		case "Shorted path dist":
			Graph_Algo g = new Graph_Algo();
			g.init(dg);
			press_dist = true;
			
			repaint();
			break;

		case "TSP":
			Graph_Algo gr = new Graph_Algo();
			gr.init(dg);
			press_tsp = true;
			
			repaint();
			break;
		}

	}

	@Override
	public void mouseDragged(MouseEvent m_e) {

	}

	@Override
	public void mouseMoved(MouseEvent m_e) {

	}

	@Override
	public void mouseClicked(MouseEvent m_e) {
		System.out.println("clicked !");
	}

	@Override
	public void mousePressed(MouseEvent m_e) {
		int x = m_e.getX();
		int y = m_e.getY();
		Point3D tmp = new Point3D(x, y);
		int min_dist = (int)(BIGGER * 1.5);
		double best_dist = 10000;
		for (node_data nd : dg.Vertex) {
			Point3D p = nd.getLocation();
			double dist = tmp.distance3D(p);
			if (dist < min_dist && dist < best_dist) {
				best_dist = dist;
				point_pressed = p;
			}
		}
		for (node_data nd : dg.Vertex) {
			if(point_pressed == nd.getLocation()) {
				list.add(nd);
				System.out.println(nd.getKey());
			}
		}
		System.out.println("press !");
	}


	@Override
	public void mouseReleased(MouseEvent m_r) {
		System.out.println("release !");
	}

	@Override
	public void mouseEntered(MouseEvent m_e) {
	}

	@Override
	public void mouseExited(MouseEvent m_e) {
	}

	public void addGraph(DGraph dg) {
		this.dg = dg;
		for (int i = 0; i < dg.nodeSize(); i++) {
			mPoints.add(dg.v.get(i).getLocation());
		}
		this.repaint();
	}

	/**
	 * @param start, fin, proportion
	 * 
	 * to drawn on the line near the black point.
	 * 
	 * @return the result
	 */
	private int drawOnLine(double start, double fin, double proportion) {
		return (int)(start + proportion*(fin-start));
	}

}
