package gameClient;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.*;

import Server.Game_Server;
import Server.game_service;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.json.JSONException;
import org.json.JSONObject;

import dataStructure.*;
import gameClient.Fruit;
import gameClient.Move;
import gameClient.Robot;
import utils.Point3D;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.TimeoutException;

public class MyGameGui extends JFrame implements ActionListener, MouseListener, MouseMotionListener {
	game_service game;
	DGraph dg;
	Point3D point_pressed = null;
	public LinkedList<node_data> list_of_press = new LinkedList<node_data>();
	LinkedList<Point3D> node_loc = new LinkedList<Point3D>();

	int num_robots;
	int scenario;

	boolean CAN_PRINT_ROBOT = false;
	boolean AUTO = false;
	boolean MANU = false;
	boolean NUMBER = false;

	int count = 0;
	
	boolean FIRST = true;

	private final int BIGGER = 5;

	public MyGameGui() {
		initGUI();
	}

	private void initGUI() {
		this.setSize(1000, 650);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		MenuBar mBar = new MenuBar();
		Menu menu = new Menu("Menu");
		mBar.add(menu);
		this.setMenuBar(mBar);

		MenuItem item = new MenuItem("Manual Game");
		item.addActionListener(this);
		MenuItem item1 = new MenuItem("Automatic Game");
		item1.addActionListener(this);
		MenuItem item2 = new MenuItem("Scenario Number");
		item2.addActionListener(this);

		menu.add(item2);
		menu.add(item);
		menu.add(item1);

		this.addMouseListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent action) {
		String s = action.getActionCommand();

		switch(s) {

		case "Scenario Number":
			NUMBER = true;
			try {
				choose_num();
			} catch (TimeoutException e) {
				e.printStackTrace();
			}
			repaint();
			break;

		case "Manual Game":
			MANU = true;
			oneOfThem();
			start_of_game();
			break;

		case "Automatic Game":
			AUTO = true;
			oneOfThem();
			start_of_game();
			break;

		}

	}

	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(Color.BLACK);
		g.drawString("בס''ד", 950, 70);

		if(FIRST) {
			game = Game_Server.getServer(scenario);
			String graph_game = game.getGraph();
			dg = new DGraph();
			/////////////////////////first push to gragh
			dg.init(graph_game);
			num_robots = 0;
		}
		try {
			String info = game.toString();
			System.out.println(info);
			JSONObject line;
			////info of game
			line = new JSONObject(info);
			JSONObject ttt = line.getJSONObject("GameServer");
			num_robots = ttt.getInt("robots");	//num of robots

			Iterator<String> f_iter = game.getFruits().iterator();
			while(f_iter.hasNext()) {
				line = new JSONObject(f_iter.next());
				JSONObject fru = line.getJSONObject("Fruit");
				Fruit ans=new Fruit(fru.getDouble("value"),fru.getInt("type"),fru.getString("pos"));
				dg.addfruit(ans);
			}

		} catch (JSONException e) {e.printStackTrace();} 
		
		System.out.println(count+") "+" "+CAN_PRINT_ROBOT);
		if (null == dg) return;
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
		fruitEdge();
		//////////////append fruits
		for (Fruit n : dg.Fruits) {
			g.setColor(Color.orange);
			g.fillOval(n.getLocation().ix() - BIGGER, n.getLocation().iy() - BIGGER,
					(int)2.5*BIGGER, (int)2.5*BIGGER);
		}

		if(CAN_PRINT_ROBOT) {
			//////////////append robots
			for (Robot n : dg.Robots) {
				g.setColor(Color.blue);
				g.fillOval(n.getLocation().ix() - BIGGER, n.getLocation().iy() - BIGGER,
						(int)2.5*BIGGER, (int)2.5*BIGGER);
			}
		}

	}

	/**
	 * we choose a number of scenario => 0-23
	 * else choose again.
	 * @throws TimeoutException
	 */
	private void choose_num() throws TimeoutException {
		if(NUMBER) {
			String input = JOptionPane.showInputDialog(null, "Please choose scenario between 0 and 23\nYour choice:");
			try {
				scenario = Integer.parseInt(input);
			} catch(Exception e) {
				choose_num();
			}
			if(0 > scenario || scenario > 23)
				choose_num();
		}
	}

	/**
	 * I want to know where are the fruit in which edge. 
	 * so I have a location of all fruits, and I put in ed list
	 * the edge has a fruit on him.
	 * 
	 */
	private void fruitEdge() {
		Iterator<Fruit> fruit = dg.Fruits.iterator();
		while(fruit.hasNext()) {
			Fruit a=fruit.next();
			Point3D p = a.getLocation();
			for (node_data nd : dg.Vertex) {
				NodeData n = (NodeData)nd;

				for (NodeData nd1 : n.outgoing) {
					Point3D dest=	nd1.getLocation();						
					if(check_on_line(p, n.location, dest)) {
						if (a.type==1) {
							if (n.getKey()< nd1.getKey()) {
								a.ed=new edgeData(n.getKey(), nd1.getKey());
							}}
						if (a.type==-1) {
							if (n.getKey()> nd1.getKey()) {
								a.ed=new edgeData(nd1.getKey(), n.getKey());

							}}
					}
					else continue;
				}
			}
		}

	}

	private static boolean check_on_line(Point3D p, Point3D src, Point3D dest) {
		double e = 0.0000001;
		if((src.distance2D(p)+p.distance2D(dest)-src.distance2D(dest)) < e) return true;
		return false;
	}

	/**
	 * 
	 * 
	 * 
	 */
	private void start_of_game() {
		if(AUTO) {
			///spread the robots on server gragh
			int pizur = dg.Vertex.size() / num_robots;
			int stati = pizur;

			for(int a = 0; a<num_robots; a++) {
				game.addRobot((pizur-1) % dg.Vertex.size());
				pizur += stati;
			}
		}
		paintRobots();
		game.startGame();

		Move m = new Move(game, dg);
		m.start();
		if(game.isRunning()) {
			///////////////////////where each robot move////////////////
			try {
				count++;
				repaint();
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		//////////////////////////////////////end game/////////////
		String results = game.toString();
		System.out.println("Game Over: "+results);
	}

	private void paintRobots() {
		// The player press on Automatic or Manual Game
		if(AUTO) Automatic_Robots();
		else if(MANU) Manual_Robots(); // 315
		CAN_PRINT_ROBOT = true;
		FIRST = false;
		repaint();
	}

	/**
	 * with iterator we pass on the string that represents robots.
	 * and pass to Json object, and add robot to 'dg'
	 * 
	 */
	private void Automatic_Robots() {
		Iterator<String> r_iter = game.getRobots().iterator();
		JSONObject line;
		while(r_iter.hasNext()) {
			try {
				line = new JSONObject(r_iter.next());
				JSONObject ro = line.getJSONObject("Robot");
				Robot ans = new Robot(ro.getInt("id"),ro.getInt("value"),ro.getInt("src"),ro.getInt("dest"),ro.getInt("speed"),ro.getString("pos"));
				dg.addrobot(ans);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}

	private void Manual_Robots() {
		if(list_of_press != null && num_robots <= list_of_press.size()) {
			for(int r = 0; r < num_robots; r++) {
				game.addRobot(list_of_press.get(r).getKey());
				addToGraph(list_of_press.get(r).getKey());	
			}
		}
		else System.out.println("GAME OVER !!\n"
				+ "Choose enough node\n"
				+ "PLAY AGAIN");
	}

	private void addToGraph(int key) {
		Robot robot = new Robot();
		robot.id = key;
		dg.addrobot(robot);
	}

	/**
	 * we press on the screen so we take a node that best near the point of pressing
	 * and we put them to 'list_of_press'
	 */
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

	///////////////////////////////
	/// *** private methods *** ///
	///////////////////////////////

	/**
	 * @param start
	 * @param fin
	 * @param proportion
	 * we want to draw on line with a direction of vector
	 *
	 */
	private int drawOnLine(double start, double fin, double proportion) {
		return (int)(start + proportion*(fin-start));
	}

	/**
	 * only one of them can be true,
	 * cause or the game is in automatic or manual
	 */
	private void oneOfThem() {
		if(MANU) AUTO = false;
		if(AUTO) MANU  = false;
	}

	public void addGraph(DGraph dg1) {
		this.dg = dg1;
		this.repaint();
	}

	@Override
	public void mouseDragged(MouseEvent m_e) {;}
	@Override
	public void mouseMoved(MouseEvent m_e) {;}
	@Override
	public void mouseClicked(MouseEvent m_e) {;}
	@Override
	public void mouseReleased(MouseEvent m_r) {;}
	@Override
	public void mouseEntered(MouseEvent m_e) {;}
	@Override
	public void mouseExited(MouseEvent m_e) {;}

}
