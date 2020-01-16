package tests;

import dataStructure.*;
import gameClient.Fruit;
import gameClient.Robot;
import utils.Point3D;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import GUI.Graph_gui;
import Server.Game_Server;
import Server.game_service;
import algorithms.Graph_Algo;

public class GuiTseting {

	public static Graph_gui gu;

	public static void main(String[] args) throws JSONException {

		 DGraph gg ;
		
		int scenario_num =5;

		game_service game = Game_Server.getServer(scenario_num); // you have [0,23] games

		String g = game.getGraph();

		gg = new DGraph();
////////////////////////////////////////first push to gragh
		gg.init(g);



		String info = game.toString();

		System.out.println(info);

		JSONObject line;

		int rs=0;//num of robots

		

			////info of game

			line = new JSONObject(info);

			JSONObject ttt = line.getJSONObject("GameServer");

			rs = ttt.getInt("robots");//num of robots



			////////////////////////////////////////////////////////////enter fruit to gragh/////////

			Iterator<String> f_iter = game.getFruits().iterator();
			while(f_iter.hasNext()) {
				line = new JSONObject(f_iter.next());
				JSONObject fru = line.getJSONObject("Fruit");
				Fruit ans=new Fruit(fru.getDouble("value"),fru.getInt("type"),fru.getString("pos"));
				gg.addfruit(ans);

			}	

			
			

			//******************************************connect fruit to edge

			/**

			 * I want to know where are the fruit in which edge. 

			 * so I have a location of all fruits, and I put in ed list

			 * the edge has a fruit on him.

			 * 

			 */

			LinkedList<edge_data> ed = new LinkedList<edge_data>();//the edges of fruits
			Iterator<Fruit> fruit = gg.Fruits.iterator();
			while(fruit.hasNext()) {
				Fruit a=fruit.next();
				Point3D p = a.getLocation();
				for (node_data nd : gg.Vertex) {
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

			Graph_gui gu = new Graph_gui();

			gu.addGraph(gg);

		   gu.setVisible(true);
		   
//////////////////////////////user code///////////
		  boolean flag=true;
while(flag) {
	if (gu.list.size()==rs)
		flag=false;
	System.out.println(gu.list.size());
	
}

	for (node_data n : gu.list) {
			   game.addRobot(n.getKey());
			   }
		///////////////////////start game///////////////////////////

		game.startGame();
		/////////////////////////////////////enter robots to our gragh

		Iterator<String> r_iter = game.getRobots().iterator();

		while(r_iter.hasNext()) {

			line = new JSONObject(r_iter.next());

			JSONObject ro = line.getJSONObject("Robot");

			Robot ans=new Robot(ro.getInt("id"),ro.getInt("value"),ro.getInt("src"),ro.getInt("dest"),ro.getInt("speed"),ro.getString("pos"));
			gg.addrobot(ans);
		}	
		
		///////first gui show///////////////////////////


		gu.addGraph(gg);

	   gu.setVisible(true);

		// should be a Thread!!!



		while(game.isRunning()) {

			/////////////////////////////////////where each robot move////////////////
			moveRobots(game, gg,gu);
		}

		////////////////////////////////////////end game/////////////

		String results = game.toString();

		System.out.println("Game Over: "+results);

	}

	private static boolean check_on_line(Point3D p, Point3D src, Point3D dest) {

		double e = 0.0000001;

		if(  (src.distance2D(p)+p.distance2D(dest)-src.distance2D(dest)) < e) return true;

		return false;

	}

	/** 

	 * Moves each of the robots along the edge, 

	 * in case the robot is on a node the next destination (next edge) is chosen (randomly).

	 * @param game

	 * @param gg

	 * @param log

	 */

	private static void moveRobots(game_service game, DGraph gg,Graph_gui gu  ) {
///move the robots 1 step
		List<String> log = game.move();
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
					String pos=ttt.getString("pos");
					String[] cord= pos.split(",");
					Point3D ans=new Point3D(scale(Double.parseDouble(cord[0]),35.186179,35.2142,0,1000),
							scale(Double.parseDouble(cord[1]),32.100148,32.109347,100,600));
	
					if(dest==-1) {//no direcrtion	
						dest = nextNode(gg, src,rid);///choose the node
						game.chooseNextEdge(rid, dest);///sent to server
						System.out.println("Turn to node: "+dest+"  time to end:"+(t/1000));
						System.out.println(ttt);
					}
					gg.Robots.get(rid).pos=	ans;					

				} 
				catch (JSONException e) {e.printStackTrace();}
			}
			gu.addGraph(gg);
			gu.setVisible(true);
		}
	}
	public static  double scale(double data, double r_min, double r_max, 
			double t_min, double t_max)
	{

		double res = ((data - r_min) / (r_max-r_min)) * (t_max - t_min) + t_min;
		return res;
	}

	/**

	 *  implementation imanuel algo!

	 * @param g

	 * @param src

	 * @return

	 */

	private static int nextNode(DGraph g, int src,int rid) {
////////creat th targets list
		if (g.Robots.get(rid).path==null) {
			double minpath=Integer.MAX_VALUE;///the dist to the fruit
		for ( Fruit fr : g.Fruits) {
			Graph_Algo gr= new Graph_Algo();
			gr.init(g);
			double dist=gr.shortestPathDist(src, fr.ed.getSrc());///the dist to the  current fruit
			if (fr.withrob==-1&&dist<minpath);////the fruit not cout
			minpath=dist;
			g.Robots.get(rid).dest=fr.ed.getSrc();///the trget of this robot

		}
		g.Robots.get(rid).path=g.getPath(g.getNode(g.Robots.get(rid).src), g.getNode(g.Robots.get(rid).dest));///the way of this robot
		return g.Robots.get(rid).dest;///finish

	}
////////////the list exist
		if (g.Robots.get(rid).path.size()>0){///thers more then 1
			g.Robots.get(rid).path.remove(0);
		
////////the list is empty after remove
if (g.Robots.get(rid).path.isEmpty()) {
		double minpath=Integer.MAX_VALUE;///the dist to the fruit
		for ( Fruit fr : g.Fruits) {
			Graph_Algo gr= new Graph_Algo();
			gr.init(g);
			double dist=gr.shortestPathDist(src, fr.ed.getSrc());///the dist to the  current fruit
			if (fr.withrob==-1&&dist<minpath);////the fruit not cout
			minpath=dist;
			g.Robots.get(rid).dest=fr.ed.getSrc();///the trget of this robot

		}
		g.Robots.get(rid).path=g.getPath(g.getNode(g.Robots.get(rid).src), g.getNode(g.Robots.get(rid).dest));///the way of this robot

	}
else///the list not empty
	g.Robots.get(rid).dest=g.Robots.get(rid).path.get(0).getKey();

		}


return g.Robots.get(rid).dest;
	}


		

		
		//Graph_gui gu = new Graph_gui();
		//gu.addGraph(gg);
		//gu.setVisible(true);
	
	}


