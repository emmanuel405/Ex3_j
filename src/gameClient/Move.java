package gameClient;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import GUI.Graph_gui;
import Server.game_service;
import algorithms.Graph_Algo;
import dataStructure.DGraph;
import dataStructure.graph;
import utils.Point3D;

/** 
 * Moves each of the robots along the edge, 
 * in case the robot is on a node the next destination (next edge) is chosen (randomly).
 * @param game
 * @param gg
 * @param log
 */

class Move implements Runnable {
	Thread t;
	Graph_gui gui;
	game_service game;
	DGraph gg;

	public Move(game_service game, DGraph gg, Graph_gui gui) {
		this.game = game;
		this.gg = gg;
		this.gui = gui;
	}

	private void moveRobots(game_service game, DGraph gg) throws InterruptedException {
		t.sleep(100);
		///move the robots 1 step
		List<String> log = this.game.move();
		if(log!=null) {
			long t = this.game.timeToEnd();
			int num_robots = 0;
			///run on every robot and see if we need to enter new direction to robot
			for(int i=0;i<log.size();i++, num_robots++) {
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
						dest = nextNode(gg, src, rid);///choose the node
						game.chooseNextEdge(rid, dest);///sent to server
						System.out.println("Turn to node: "+dest+"  time to end:"+(t/1000));
						System.out.println(ttt);
					}
					this.gg.Robots.get(rid).pos = ans;					

				}
				catch (JSONException e) {e.printStackTrace();}
			}

			this.gui.addGraph(gg);
			this.gui.setVisible(true);
		}

	}

	private double scale(double data, double r_min, double r_max, 
			double t_min, double t_max){
		double res = ((data - r_min) / (r_max-r_min)) * (t_max - t_min) + t_min;
		return res;
	}

	/**
	 * implementation imanuel algo!
	 * @param g
	 * @param src
	 * @return
	 */
	private int nextNode(graph g, int src, int rid) {
		////////creat th targets list
		if (this.gg.Robots.get(rid).path==null) {
			double minpath=Integer.MAX_VALUE;///the dist to the fruit
			for (Fruit fr : gg.Fruits) {
				Graph_Algo gr= new Graph_Algo();
				gr.init(this.gg);
				double dist=gr.shortestPathDist(src, fr.ed.getSrc());///the dist to the  current fruit
				if (fr.withrob==-1&&dist<minpath);////the fruit not cout
				minpath=dist;
				this.gg.Robots.get(rid).dest=fr.ed.getSrc();///the trget of this robot
			}
			this.gg.Robots.get(rid).path=this.gg.getPath(this.gg.getNode(this.gg.Robots.get(rid).src),
					this.gg.getNode(this.gg.Robots.get(rid).dest));///the way of this robot
			return this.gg.Robots.get(rid).dest;///finish
		}

		////////////the list exist
		else if (gg.Robots.get(rid).path.size()>0){///thers more then 1
			gg.Robots.get(rid).path.remove(0);

			////////the list is empty after remove
			if (gg.Robots.get(rid).path.isEmpty()) {
				double minpath=Integer.MAX_VALUE;///the dist to the fruit
				for (Fruit fr : gg.Fruits) {
					Graph_Algo gr= new Graph_Algo();
					gr.init(gg);
					double dist=gr.shortestPathDist(src, fr.ed.getSrc());///the dist to the  current fruit
					if (fr.withrob==-1&&dist<minpath);////the fruit not cout
					minpath=dist;
					this.gg.Robots.get(rid).dest=fr.ed.getSrc();///the trget of this robot
				}
				this.gg.Robots.get(rid).path=gg.getPath(this.gg.getNode(this.gg.Robots.get(rid).src),
						this.gg.getNode(this.gg.Robots.get(rid).dest));///the way of this robot
			}
			else///the list not empty
				this.gg.Robots.get(rid).dest=this.gg.Robots.get(rid).path.get(0).getKey();
		}

		return this.gg.Robots.get(rid).dest;
	}

	@Override
	public void run() {
		while(this.game.isRunning()) {
			try {
				moveRobots(this.game, this.gg);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}


} // Move