package gameClient;

import java.util.ArrayList;
import java.util.Iterator;
//import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import Server.game_service;
import algorithms.Graph_Algo;
import dataStructure.DGraph;
import dataStructure.NodeData;
import dataStructure.edgeData;
//import dataStructure.edge_data;
//import dataStructure.graph;
import dataStructure.node_data;
import utils.Point3D;

/** 
 * Moves each of the robots along the edge, 
 * in case the robot is on a node the next destination (next edge) is chosen (randomly).
 * @param game
 * @param gg
 * @param log
 */

public class Move extends Thread {
	List<String> log;
	game_service game;
	DGraph gg;
	long level;
	MyGameGui gu;

	public Move(game_service game, DGraph gg, MyGameGui gu, long level_sleep) {
		this.game = game;
		this.gg = gg;
		this.gu = gu;
		this.level = level_sleep;
	}

	@Override
	public void run() {
		while(true) {
			moveRobots(this.game, this.gg);
			try {
				sleep(level);
			} catch (InterruptedException e) {e.printStackTrace();}
		}
	}

	private void moveRobots(game_service game, DGraph gg) {
		///move the robots 1 step
		log = this.game.move();
		if(log!=null) {
			long t = this.game.timeToEnd();
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

					if(dest == -1) {		///no direcrtion	
						dest = nextNode(src, rid);///choose the node
						game.chooseNextEdge(rid, dest);///sent to server
						System.out.println("Turn to node: "+dest+"  time to end:"+(t/1000));
						System.out.println(ttt);
					}
					this.gg.Robots.get(rid).pos = ans;					
					
				}
				catch (JSONException e) {e.printStackTrace();}
			}

		}

	}

	private double scale(double data, double r_min, double r_max, 
			double t_min, double t_max){
		double res = ((data - r_min) / (r_max-r_min)) * (t_max - t_min) + t_min;
		return res;
	}

	/**
	 * implementation manuel algo!
	 * @param g
	 * @param src
	 * @return
	 * @throws JSONException 
	 */
	private int nextNode(int src, int rid) throws JSONException {
		this.gg.Robots.get(rid).src = src;
		//create the targets list at first iterate
		if (null == this.gg.Robots.get(rid).path) {
			double minpath=Integer.MAX_VALUE;///the dist to the fruit
			for (Fruit fr : gg.Fruits) {
				Graph_Algo gr= new Graph_Algo();
				gr.init(this.gg);
				double dist = gr.shortestPathDist(src, fr.ed.getSrc());///the dist to the  current fruit
				if (fr.withrob == -1 && dist < minpath);////the fruit not cout
				minpath = dist;
				this.gg.Robots.get(rid).dest = fr.ed.getSrc();///the trget of this robot
			}
			this.gg.Robots.get(rid).path = this.gg.getPath(this.gg.getNode(this.gg.Robots.get(rid).src),
					this.gg.getNode(this.gg.Robots.get(rid).dest));///the way of this robot
			return this.gg.Robots.get(rid).path.get(0).getKey();///finish

		}
		////////////the list exist
		else if (gg.Robots.get(rid).path.size()>0){///thers more then 1 target
			gg.Robots.get(rid).path.remove(0);

			////////the list is empty after remove beacuse the fruit has been eaten
			if (gg.Robots.get(rid).path.size()==0) {
				gg.Fruits=new ArrayList<Fruit>();				
				//reset fruit in gragh
				//boolean flag=true;
				Iterator<String> f_iter = game.getFruits().iterator();
				while(f_iter.hasNext()) {
					JSONObject line = new JSONObject(f_iter.next());
					JSONObject fru = line.getJSONObject("Fruit");
					Fruit ans=new Fruit(fru.getDouble("value"),fru.getInt("type"),fru.getString("pos"));

					gg.addfruit(ans);
				}
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

				double minpath = Integer.MAX_VALUE;///the dist to the fruit

				for (Fruit fr : gg.Fruits) {
					Graph_Algo gr= new Graph_Algo();
					gr.init(gg);
					double dist=gr.shortestPathDist(src, fr.ed.getSrc());///the dist to the  current fruit
					if (fr.withrob==-1&&dist<minpath) {////the fruit not cout
						minpath=dist;
						this.gg.Robots.get(rid).dest=fr.ed.getSrc();///the trget of this robot
					}
					if (!(this.gg.Robots.get(rid).dest==this.gg.Robots.get(rid).src)) {
						this.gg.Robots.get(rid).path=gg.getPath(this.gg.getNode(this.gg.Robots.get(rid).src),
								this.gg.getNode(this.gg.Robots.get(rid).dest));///the way of this robot
					}
					else {
						this.gg.Robots.get(rid).path=gg.getPath(this.gg.getNode(this.gg.Robots.get(rid).src),
								gg.getNode(fr.ed.getDest()));
						this.gg.Robots.get(rid).dest=fr.ed.getDest();
					}

				}
				return this.gg.Robots.get(rid).path.get(0).getKey();

			}
			///////////////the list not empty
			return this.gg.Robots.get(rid).path.get(0).getKey();

		}
		return this.gg.Robots.get(rid).path.get(0).getKey();

	}

	private boolean check_on_line(Point3D p, Point3D src, Point3D dest) {
		double e = 0.0000001;
		if((src.distance2D(p)+p.distance2D(dest)-src.distance2D(dest)) < e) return true;
		return false;
	}

} // Move
