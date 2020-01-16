package gameClient;

import java.util.List;

import dataStructure.node_data;
import utils.Point3D;

public class Robot {
	public  int id, src, dest;
	public int speed=1;
	public int value=0; 
	public Point3D pos;
	public List<node_data> path;
	
	int num=0;

	public Robot() {
		this. value=0;
		this. id=num++;
		this.src=-1;
		this.dest=-1;
		this.pos=Point3D.ORIGIN;
	}

	public Robot(int id,int value,int src,int dest ,int speed,String pos) {
		this. value=value;
		this. id=id;
		this.src=src;
		this.dest=dest;
		this.speed=speed;

		String[] cord= pos.split(",");
		Point3D ans=new Point3D(Double.parseDouble(cord[0]),Double.parseDouble(cord[1]));
		this. pos=ans;
	}
	
	public Point3D getLocation() {
		return this.pos;

	}
}
