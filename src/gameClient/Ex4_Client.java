package gameClient;



import java.util.ArrayList;

import java.util.Collection;

import java.util.Iterator;

import java.util.List;



import org.json.JSONException;

import org.json.JSONObject;



import Server.Game_Server;

import Server.game_service;
import algorithms.Graph_Algo;
import dataStructure.DGraph;
import dataStructure.NodeData;
import dataStructure.edgeData;
import dataStructure.edge_data;
import dataStructure.node_data;
import oop_dataStructure.OOP_DGraph;

import oop_dataStructure.oop_edge_data;

import oop_dataStructure.oop_graph;

import oop_utils.OOP_Point3D;
import utils.Point3D;

/**

 * This class represents a simple example for using the GameServer API:

 * the main file performs the following tasks:

 * 0. login as a user ("999") for testing - do use your ID.

 * 1. Creates a game_service [0,23] (user "999" has stage 9, can play in scenarios [0,9] not above

 *    Note: you can also choose -1 for debug (allowing a 600 second game).

 * 2. Constructs the graph from JSON String

 * 3. Gets the scenario JSON String 

 * 5. Add a set of robots  // note: in general a list of robots should be added

 * 6. Starts game 

 * 7. Main loop (vary simple thread)

 * 8. move the robot along the current edge 

 * 9. direct to the next edge (if on a node) 

 * 10. prints the game results (after "game over"), and write a KML: 

 *     Note: will NOT work on case -1 (debug).

 *  

 * @author boaz.benmoshe

 *

 */

public class Ex4_Client implements Runnable{




	public static void main(String[] a) {

		 Thread client = new Thread(new Ex4_Client());

		client.start();

	}

	static List<String> log;

	private int num_robots;
	private DGraph dg;

	public Game_Server game;



	@Override

	public void run() {

		int scenario_num = 1; // current "stage is 9, can play[0,9], can NOT 10 or above

		int id = 313387359;

		Game_Server.login(id);

		game_service game = Game_Server.getServer(scenario_num); // you have [0,23] games


		String g = game.getGraph();

		List<String> fruits = game.getFruits();

		dg = new DGraph();

		dg.init(g);

		/////////////////////////first push to gragh
		num_robots = 0;
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
				Fruit ans=new Fruit(fru.getDouble("value"),fru.getInt("type"),
						fru.getString("pos"));
				dg.addfruit(ans);
			}

		} catch (JSONException e) {e.printStackTrace();} 

		int pizur = dg.Vertex.size()-1 / num_robots;
		int stati = pizur;

		for(int a = 0; a<num_robots; a++) {
			game.addRobot((pizur-1) % dg.Vertex.size());
			pizur += stati;
		}

		game.startGame();
		Iterator<String> r_iter = game.getRobots().iterator();
		JSONObject line;
		while(r_iter.hasNext()) {
			try {
				line = new JSONObject(r_iter.next());
				JSONObject ro = line.getJSONObject("Robot");
				Robot ans = new Robot(ro.getInt("id"),ro.getInt("value"),
						ro.getInt("src"),ro.getInt("dest"),
						ro.getInt("speed"),ro.getString("pos"));
				dg.addrobot(ans);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		fruitEdge();

		int ind=0;

		long dt=90;

		int jj = 0;

		while(game.isRunning()) {

			moveRobots(game, dg);
			try {

				List<String> stat = game.getRobots();

				for(int i=0;i<stat.size();i++) {

					System.out.println(jj+") "+stat.get(i));

				}

				ind++;

				Thread.sleep(dt);

				jj++;

			}

			catch(Exception e) {

				e.printStackTrace();

			}

		}

		String res = game.toString();

		String remark = "This string should be a KML file!!";

		game.sendKML(remark); // Should be your KML (will not work on case -1).

		System.out.println(res);

	}

	/** 

	 * Moves each of the robots along the edge, 

	 * in case the robot is on a node the next destination (next edge) is chosen (randomly).

	 * @param game

	 * @param dg

	 * @param //log

	 */

	private  void moveRobots(game_service game, DGraph dg) {
		///move the robots 1 step
		log = game.move();
		if(log!=null) {
			long t = game.timeToEnd();
			///run on every robot and see if we need to enter new direction to robot
			for(int i=0;i<log.size();i++) {
				String robot_json = log.get(i);
				try {
					JSONObject line = new JSONObject(robot_json);
					JSONObject ttt = line.getJSONObject("Robot");
					int rid = ttt.getInt("id");
					int src = ttt.getInt("src");
					int dest = ttt.getInt("dest");
					String pos = ttt.getString("pos");
					String[] cord = pos.split(",");
					Point3D ans = new Point3D(scale(Double.parseDouble(cord[0]),35.186179,35.2142,0,1000),
							scale(Double.parseDouble(cord[1]),32.100148,32.109347,100,600));
					double distance = Math.sqrt(Math.abs(ans.x()-dg.getNode(src).getLocation().x())*Math.abs(ans.x()-dg.getNode(src).getLocation().x())
							+ Math.abs(ans.y()-dg.getNode(src).getLocation().y())*Math.abs(ans.y()-dg.getNode(src).getLocation().y()));
					if (distance<10) {
						try {
							Thread.sleep(5);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					if(dest == -1) {	///no direcrtion	
						dest = nextNode( game,src, rid);///choose the node
						game.chooseNextEdge(rid, dest);///sent to server
						System.out.println("Turn to node: "+dest+"  time to end:"+(t/1000));
						System.out.println(ttt);
					}
					dg.Robots.get(rid).pos = ans;

				}
				catch (JSONException e) {e.printStackTrace();}
			}

		}

	}
	private static double scale(double data, double r_min, double r_max, 
			double t_min, double t_max){
		double res = ((data - r_min) / (r_max-r_min)) * (t_max - t_min) + t_min;
		return res;
	}

	
	/**

	 * a very simple random walk implementation!

	 * @param src2

	 * @param src

	 * @return

	 */

	public int nextNode(game_service game2,int src, int rid) throws JSONException {
		this.dg.Robots.get(rid).src = src;
		//create the targets list at first iterate
		if (null == this.dg.Robots.get(rid).path) {
			double minpath=Integer.MAX_VALUE;///the dist to the fruit
			for (Fruit fr : dg.Fruits) {
				Graph_Algo gr= new Graph_Algo();
				gr.init(this.dg);

				double dist = gr.shortestPathDist(src, fr.ed.getSrc());///the dist to the current fruit
				if (fr.withrob == -1 && dist < minpath);////the fruit not cout
				minpath = dist;
				this.dg.Robots.get(rid).dest = fr.ed.getSrc();///the trget of this robot
			}
			this.dg.Robots.get(rid).path = this.dg.getPath(this.dg.getNode(this.dg.Robots.get(rid).src),
					this.dg.getNode(this.dg.Robots.get(rid).dest));///the way of this robot
			return this.dg.Robots.get(rid).path.get(0).getKey();///finish

		}
		////////////the list exist
		else if (dg.Robots.get(rid).path.size()>0){///thers more then 1 target
			dg.Robots.get(rid).path.remove(0);

			////////the list is empty after remove beacuse the fruit has been eaten
			if (dg.Robots.get(rid).path.size()==0) {
				dg.Fruits=new ArrayList<Fruit>();				
				//reset fruit in gragh
				//boolean flag=true;
				Iterator<String> f_iter = game2.getFruits().iterator();
				while(f_iter.hasNext()) {
					JSONObject line = new JSONObject(f_iter.next());
					JSONObject fru = line.getJSONObject("Fruit");
					Fruit ans=new Fruit(fru.getDouble("value"),fru.getInt("type"),fru.getString("pos"));

					dg.addfruit(ans);
				}
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

				double minpath = Integer.MAX_VALUE;///the dist to the fruit

				for (Fruit fr : dg.Fruits) {
					Graph_Algo gr= new Graph_Algo();
					gr.init(dg);
					double dist=gr.shortestPathDist(src, fr.ed.getSrc());///the dist to the  current fruit
					if (fr.withrob==-1&&dist<minpath) {////the fruit not cout
						minpath=dist;
						this.dg.Robots.get(rid).dest=fr.ed.getSrc();///the trget of this robot
					}
					if (!(this.dg.Robots.get(rid).dest==this.dg.Robots.get(rid).src)) {
						this.dg.Robots.get(rid).path=dg.getPath(this.dg.getNode(this.dg.Robots.get(rid).src),
								this.dg.getNode(this.dg.Robots.get(rid).dest));///the way of this robot
					}
					else {
						this.dg.Robots.get(rid).path=dg.getPath(this.dg.getNode(this.dg.Robots.get(rid).src),
								dg.getNode(fr.ed.getDest()));
						this.dg.Robots.get(rid).dest=fr.ed.getDest();
					}

				}
				return this.dg.Robots.get(rid).path.get(0).getKey();

			}
			///////////////the list not empty
			return this.dg.Robots.get(rid).path.get(0).getKey();

		}
		if (this.dg.Robots.get(rid).path.size()==0) {
			dg.Fruits=new ArrayList<Fruit>();				
			//reset fruit in gragh
			//boolean flag=true;
			Iterator<String> f_iter = game2.getFruits().iterator();
			while(f_iter.hasNext()) {
				JSONObject line = new JSONObject(f_iter.next());
				JSONObject fru = line.getJSONObject("Fruit");
				Fruit ans=new Fruit(fru.getDouble("value"),fru.getInt("type"),fru.getString("pos"));

				dg.addfruit(ans);
			}
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

			double minpath = Integer.MAX_VALUE;///the dist to the fruit

			for (Fruit fr : dg.Fruits) {
				Graph_Algo gr= new Graph_Algo();
				gr.init(dg);
				double dist=gr.shortestPathDist(src, fr.ed.getSrc());///the dist to the  current fruit
				if (fr.withrob==-1&&dist<minpath) {////the fruit not cout
					minpath=dist;
					this.dg.Robots.get(rid).dest=fr.ed.getSrc();///the trget of this robot
				}
				if (!(this.dg.Robots.get(rid).dest==this.dg.Robots.get(rid).src)) {
					this.dg.Robots.get(rid).path=dg.getPath(this.dg.getNode(this.dg.Robots.get(rid).src),
							this.dg.getNode(this.dg.Robots.get(rid).dest));///the way of this robot
				}
				else {
					this.dg.Robots.get(rid).path=dg.getPath(this.dg.getNode(this.dg.Robots.get(rid).src),
							dg.getNode(fr.ed.getDest()));
					this.dg.Robots.get(rid).dest=fr.ed.getDest();
				}

			}
			this.dg.Robots.get(rid).path.get(0).getKey();

		}
		return this.dg.Robots.get(rid).path.get(0).getKey();


	}
	private boolean check_on_line(Point3D p, Point3D src, Point3D dest) {
		double e = 0.0000001;
		if((src.distance2D(p)+p.distance2D(dest)-src.distance2D(dest)) < e) return true;
		return false;
	}
	private void init(game_service game) {



		String g = game.getGraph();

		List<String> fruits = game.getFruits();

		OOP_DGraph gg = new OOP_DGraph();

		gg.init(g);



		String info = game.toString();

		JSONObject line;

		try {

			line = new JSONObject(info);

			JSONObject ttt = line.getJSONObject("GameServer");

			int rs = ttt.getInt("robots");

			System.out.println(info);

			// the list of fruits should be considered in your solution

			Iterator<String> f_iter = game.getFruits().iterator();

			while(f_iter.hasNext()) {System.out.println(f_iter.next());}	

			int src_node = 0;  // arbitrary node, you should start at one of the fruits

			for(int a = 0;a<rs;a++) {

				game.addRobot(a);

			}

		}

		catch (JSONException e) {e.printStackTrace();}



	}
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
}