package gameClient;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import GUI.Graph_gui;
import Server.Game_Server;
import Server.game_service;
import dataStructure.DGraph;
import dataStructure.NodeData;
import dataStructure.edge_data;
import dataStructure.graph;
import dataStructure.node_data;
import utils.Point3D;
/**
 * This class represents a simple example for using the GameServer API:
 * the main file performs the following tasks:
 * 1. Creates a game_service [0,23] (line 36)
 * 2. Constructs the graph from JSON String (lines 37-39)
 * 3. Gets the scenario JSON String (lines 40-41)
 * 4. Prints the fruits data (lines 49-50)
 * 5. Add a set of robots (line 52-53) // note: in general a list of robots should be added
 * 6. Starts game (line 57)
 * 7. Main loop (should be a thread) (lines 59-60)
 * 8. move the robot along the current edge (line 74)
 * 9. direct to the next edge (if on a node) (line 87-88)
 * 10. prints the game results (after "game over"): (line 63)
 *  
 * @author boaz.benmoshe
 *
 */
public class SimpleGameClient {
	public static void main(String[] a) throws JSONException {
		test1();}
	public static void test1() throws JSONException {
		
		int scenario_num = 22;
		game_service game = Game_Server.getServer(scenario_num); // you have [0,23] games
		String g = game.getGraph();
	    DGraph gg = new DGraph();
		gg.init(g);
		
		String info = game.toString();
		System.out.println(info);
		JSONObject line;
		int rs=0;//num of robots
		try {
			////info of game
			line = new JSONObject(info);
			JSONObject ttt = line.getJSONObject("GameServer");
			 rs = ttt.getInt("robots");//num of robots
			
			////////////////////////////////////////////////////////////enter fruit to gragh/////////
			// the list of fruits should be considered in your solution
			Iterator<String> f_iter = game.getFruits().iterator();
			while(f_iter.hasNext()) {
				line = new JSONObject(f_iter.next());
				JSONObject fru = line.getJSONObject("Fruit");
				Fruit ans=new Fruit(fru.getDouble("value"),fru.getInt("type"),fru.getString("pos"));
				gg.addfruit(ans);
				}	
			
			
			///////////////location  robots/////////////////////////////our algorithem begin here
			int src_node = 0;  // arbitrary node, we should start at one of the fruits
			int pizur= gg.Vertex.size()/rs;
			for(int a = 0;a<rs;a++) {
				game.addRobot((pizur-1)%gg.Vertex.size());
				pizur+=pizur;
			}
			
			
		//*************************************************//
			/**
			 * I want to know where are the fruit in which edge. 
			 * so I have a location of all fruits, and I put in ed list
			 * the edge has a fruit on him.
			 * 
			 */
			LinkedList<edge_data> ed = null;

			Iterator<Fruit> fruit = gg.Fruits.iterator();
			//while(fruit.hasNext()) {
			//	Point3D p = fruit.next().getLocation();
			//	Iterator<node_data> nd = gg.Vertex.iterator();
			//	while(nd.hasNext()) {
			//		NodeData n = (NodeData)nd;
			//		Iterator<NodeData> nd_out = n.outgoing.iterator();
			//		while(nd_out.hasNext()) {
			//			if(check_on_line(p, n.location, nd_out.next().location)) {
			//				edge_data e = gg.ve.get(n.getKey()).get(nd_out);
			//				ed.add(e);
			//			}
			//		}

			//	}
			//}

			//************************************************//

		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		///////////////////////start game///////////////////////////
		game.startGame();
	////enter robots to gragh
		Iterator<String> r_iter = game.getRobots().iterator();
		while(r_iter.hasNext()) {
			line = new JSONObject(r_iter.next());
			JSONObject ro = line.getJSONObject("Robot");
			Robot ans=new Robot(ro.getInt("id"),ro.getInt("value"),ro.getInt("src"),ro.getInt("dest"),ro.getInt("speed"),ro.getString("pos"));

			gg.addrobot(ans);
			}	
		///////first gui show
		Graph_gui gu = new Graph_gui();
		gu.addGraph(gg);
		gu.setVisible(true);
		// should be a Thread!!!

		while(game.isRunning()) {
			/////////////////////////////////////where each robot move////////////////
			moveRobots(game, gg);
			
		}
		////////////////////////////////////////end game/////////////
		String results = game.toString();
		System.out.println("Game Over: "+results);
	}
	private static boolean check_on_line(Point3D p, Point3D src, Point3D dest) {
		double e = 0.001;
		if(src.distance2D(dest) - (src.distance2D(p)+p.distance2D(dest)) < e) return true;
		return false;
	}
	/** 
	 * Moves each of the robots along the edge, 
	 * in case the robot is on a node the next destination (next edge) is chosen (randomly).
	 * @param game
	 * @param gg
	 * @param log
	 */
	private static void moveRobots(game_service game, graph gg) {
		List<String> log = game.move();
		if(log!=null) {
			long t = game.timeToEnd();
			for(int i=0;i<log.size();i++) {
				String robot_json = log.get(i);
				try {
					JSONObject line = new JSONObject(robot_json);
					JSONObject ttt = line.getJSONObject("Robot");
					int rid = ttt.getInt("id");
					int src = ttt.getInt("src");
					int dest = ttt.getInt("dest");
				
					if(dest==-1) {	
						dest = nextNode(gg, src);
						game.chooseNextEdge(rid, dest);
						System.out.println("Turn to node: "+dest+"  time to end:"+(t/1000));
						System.out.println(ttt);
					}
				} 
				catch (JSONException e) {e.printStackTrace();}
			}
		}
	}
	/**
	 * a very simple random walk implementation!
	 * @param g
	 * @param src
	 * @return
	 */
	private static int nextNode(graph g, int src) {
		int ans = -1;
		Collection<edge_data> ee = g.getE(src);
		Iterator<edge_data> itr = ee.iterator();
		int s = ee.size();
		int r = (int)(Math.random()*s);
		int i=0;
		while(i<r) {itr.next();i++;}
		ans = itr.next().getDest();
		return ans;
	}

}
