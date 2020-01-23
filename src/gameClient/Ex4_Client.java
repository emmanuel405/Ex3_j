package gameClient;

//import java.util.ArrayList;
//import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import Server.Game_Server;
import Server.game_service;
import dataStructure.DGraph;
//import dataStructure.edge_data;
//import dataStructure.graph;
//import utils.Point3D;
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
	DGraph gg = new DGraph();
	game_service game;
	
	public static void main(String[] a) {
		Thread client = new Thread(new Ex4_Client());
		client.start();
	}

	@Override
	public void run() {
		int scenario_num = 2; // current "stage is 9, can play[0,9], can NOT 10 or above
		int id = 324708676;
		int num_robots = 0;
		Game_Server.login(id);
		game = Game_Server.getServer(scenario_num); // you have [0,23] games

		String g = game.getGraph();
		List<String> fruits = game.getFruits();
		
		gg.init(g);
		init(game);

		String info = game.toString();
		System.out.println(info);
		JSONObject line;
		////info of game
		try {
			line = new JSONObject(info);
			JSONObject ttt = line.getJSONObject("GameServer");
			num_robots = ttt.getInt("robots");	//num of robots
			
			addedFruits(game, gg, line);
			
			spread(num_robots);
			addedRobot(game, gg, line);
			
			
			
		} catch (JSONException e1) {e1.printStackTrace();}

		game.startGame();
		int ind=0;
		long dt=200;
		int jj = 0;


		while(game.isRunning()) {
			Move move_robot = new Move(game, gg, scenario_num);
			move_robot.start();
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

	private void spread(int num_robots) {
		int pizur = gg.Vertex.size() / num_robots;
		int stati = pizur;

		for(int a = 0; a<num_robots; a++) {
			game.addRobot((pizur-1) % gg.Vertex.size());
			pizur += stati;
		}		
	}

	private void addedFruits(game_service game, DGraph gg, JSONObject line) {
		Iterator<String> f_iter = game.getFruits().iterator();
		try {
			while(f_iter.hasNext()) {
				line = new JSONObject(f_iter.next());
				JSONObject fru = line.getJSONObject("Fruit");
				Fruit ans=new Fruit(fru.getDouble("value"),fru.getInt("type"),
						fru.getString("pos"));
				gg.addfruit(ans);
			}
		}catch (JSONException e) {
			System.out.println("*** NO => 1 ***");
			e.printStackTrace();}
	}

	private void addedRobot(game_service game, DGraph gg, JSONObject line) {
		Iterator<String> r_iter = game.getRobots().iterator();
		
		while(r_iter.hasNext()) {
			try {
				line = new JSONObject(r_iter.next());
				JSONObject ro = line.getJSONObject("Robot");
				Robot ans = new Robot(ro.getInt("id"),ro.getInt("value"),
						ro.getInt("src"),ro.getInt("dest"),
						ro.getInt("speed"),ro.getString("pos"));
				gg.addrobot(ans);
			} catch (JSONException e) {
				System.out.println("*** NO => 2 ***");
				e.printStackTrace();
			}
		}
	}

	/*	*//** 
	 * Moves each of the robots along the edge, 
	 * in case the robot is on a node the next destination (next edge) is chosen (randomly).
	 * @param game
	 * @param gg
	 * @param log
	 *//*
	private static void moveRobots(game_service game, oop_graph gg) {
		List<String> log = game.move();
		ArrayList<OOP_Point3D> rs = new ArrayList<OOP_Point3D>();
		List<String> fs =  game.getFruits();
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
					String p = ttt.getString("pos");
					OOP_Point3D pp = new OOP_Point3D(p);
					rs.add(pp);
					double speed =  ttt.getInt("speed");

					if(dest==-1) {			
						dest = nextNode(gg, src);
						game.chooseNextEdge(rid, dest);
			//			System.out.println("Turn to node: "+dest+"  time to end:"+(t/1000));
					}
				} 
				catch (JSONException e) {e.printStackTrace();}
			}
		}
	}
	  *//**
	  * a very simple random walk implementation!
	  * @param g
	  * @param src
	  * @return
	  *//*
	private static int nextNode(oop_graph g, int src) {
		int ans = -1;
		Collection<oop_edge_data> ee = g.getE(src);
		Iterator<oop_edge_data> itr = ee.iterator();
		int s = ee.size();
		int r = (int)(Math.random()*s);
		int i=0;
		while(i<r) {itr.next();i++;}
		ans = itr.next().getDest();
		return ans;
	}
	   */	
	private void init(game_service game) {

		String g = game.getGraph();
		List<String> fruits = game.getFruits();
		DGraph gg = new DGraph();
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
}
