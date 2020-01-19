package GUI;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.*;

import Server.Game_Server;
import Server.game_service;

import javax.swing.JFrame;

import org.json.JSONException;
import org.json.JSONObject;

import algorithms.Graph_Algo;
import dataStructure.*;
import gameClient.Fruit;
import gameClient.Robot;
import utils.Point3D;

import java.util.Iterator;
import java.util.LinkedList;

public class Graph_gui extends JFrame implements ActionListener, MouseListener, MouseMotionListener{
	Thread t;
	game_service game;
	int rs;
	public DGraph dg = null;
	Graph_Algo g_a = new Graph_Algo(dg);
	Point3D point_pressed = null;
	public LinkedList<node_data> list_of_press = new LinkedList<node_data>();
	LinkedList<Point3D> node_loc = new LinkedList<Point3D>();

	boolean AUTO = false;
	boolean MANU = false;
	boolean NUMBER = false;

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

		MenuItem item = new MenuItem("Manual Game");
		item.addActionListener(this);
		MenuItem item1 = new MenuItem("Automatic Game");
		item1.addActionListener(this);
		MenuItem item2 = new MenuItem("Senario Number");
		item2.addActionListener(this);

		m.add(item2);
		m.add(item);
		m.add(item1);


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

			//////////////append fruits
			for (Fruit n : dg.Fruits) {
				g.setColor(Color.orange);
				g.fillOval(n.getLocation().ix() - BIGGER, n.getLocation().iy() - BIGGER,
						(int)2.5*BIGGER, (int)2.5*BIGGER);
			}

			// The player press on Automatic or Manual Game
			if(AUTO || MANU) {
				if(AUTO) {
					//////////////append robots
					for (Robot n : dg.Robots) {
						g.setColor(Color.blue);
						g.fillOval(n.getLocation().ix() - BIGGER, n.getLocation().iy() - BIGGER,
								(int)2.5*BIGGER, (int)2.5*BIGGER);
					}
				}	
				else {
					while(MANU) {
						if (list_of_press.size() == rs) MANU = false;
						for (node_data n : list_of_press) {
							game.addRobot(n.getKey());
					
					
//					for (node_data n : list_of_press) {
//						Robot r = new Robot();
//						r.setLocation(n.getLocation());
						g.setColor(Color.blue);
						g.fillOval(n.getLocation().ix() - BIGGER, n.getLocation().iy() - BIGGER,
								(int)2.5*BIGGER, (int)2.5*BIGGER);
//					}
						}
					}
				}
				
			}
			t.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		/*if(NUMBER) {
		this.setSize(500, 150);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		g.setColor(Color.BLACK);
		g.drawString("Choose your Senario Number from 0 to 23", 100, 100);
	}
	NUMBER = false;*/


	}

	@Override

	public void actionPerformed(ActionEvent action) {
		String s = action.getActionCommand();

		switch(s) {

		case "Senario Number":
			NUMBER = true;
			int rs = 1;
			int scenario = choose_num();
			
			repaint();
			break;

		case "Manual Game":
			MANU = true;

			repaint();
			break;

		case "Automatic Game":
			AUTO = true;

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

	private int number_robots(game_service game) {
		String info = game.toString();
		System.out.println(info);
		JSONObject line;
		int rs = -1;//num of robots
		try {
			////info of game
			line = new JSONObject(info);
			JSONObject ttt = line.getJSONObject("GameServer");
			rs = ttt.getInt("robots"); //num of robots
		} catch (JSONException e) {
			e.printStackTrace();
		}
//		if()
		return 0;
	}		

	private int choose_num() {
		
		return 0;	
	}


}
