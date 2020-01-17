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
import gameClient.Fruit;
import gameClient.Robot;
import utils.Point3D;

import java.util.LinkedList;

public class Graph_gui extends JFrame implements ActionListener, MouseListener, MouseMotionListener{
	Thread t;
	public DGraph dg = null;
	Graph_Algo g_a = new Graph_Algo(dg);
	Point3D point_pressed = null;
	public LinkedList<node_data> list_of_press = new LinkedList<node_data>();
	LinkedList<Point3D> node_loc = new LinkedList<Point3D>();

	private int BIGGER = 5;

	public Graph_gui() {
		initGUI();
	}

	private void initGUI() {
		this.setSize(1000, 800);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		MenuBar mBar = new MenuBar();
		Menu m = new Menu("Menu");
		mBar.add(m);
		this.setMenuBar(mBar);

		MenuItem item = new MenuItem("Manual");
		item.addActionListener(this);
		MenuItem item1 = new MenuItem("Automatic");
		item1.addActionListener(this);
		MenuItem item2 = new MenuItem("Senario");
		item2.addActionListener(this);

		m.add(item);
		m.add(item1);
		m.add(item2);

		this.addMouseListener(this);
	}

	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(Color.BLACK);
		g.drawString("בס''ד", 950, 70);

		if (null == dg) return;

		try {
			for (node_data n : dg.Vertex) {
				node_loc.add(n.getLocation()); // Take a location of all nodes in the graph

				g.setColor(Color.GREEN);
				g.fillOval((int)n.getLocation().x() - BIGGER, (int)n.getLocation().y() - BIGGER,
						10, 10);

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
							drawOnLine(n.getLocation().y(), m.getLocation().y(), 0.85), 4, 4);
				}
			}
			//////////////append robots
			for (Robot n : dg.Robots) {
				g.setColor(Color.blue);
				g.fillOval(n.getLocation().ix() - BIGGER, n.getLocation().iy() - BIGGER,
						(int)2.5*BIGGER, (int)2.5*BIGGER);
			}

			//////////////append fruits
			for (Fruit n : dg.Fruits) {
				g.setColor(Color.orange);
				g.fillOval(n.getLocation().ix() - BIGGER, n.getLocation().iy() - BIGGER,
						(int)2.5*BIGGER, (int)2.5*BIGGER);
				
			}
			t.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}



	@Override

	public void actionPerformed(ActionEvent action) {
		String s = action.getActionCommand();

		switch(s) {

		case "Manual":
			
			repaint();
			break;

		case "Automatic":
			
			repaint();
			break;

		case "Senario":
			
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
				list_of_press.add(nd);
				System.out.println(nd.getKey());
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent m_r) {
	}

	@Override
	public void mouseEntered(MouseEvent m_e) {
	}

	@Override
	public void mouseExited(MouseEvent m_e) {
	}

	public void addGraph(DGraph dg1) {
		this.dg = dg1;
		this.repaint();
	}

	///////////////////////////////
	/// *** private methods *** ///
	///////////////////////////////

	/**
	 * @param start
	 * @param fin
	 * @param proportion
	 * 
	 * @return the result
	 */
	private int drawOnLine(double start, double fin, double proportion) {
		return (int)(start + proportion*(fin-start));
	}

}
