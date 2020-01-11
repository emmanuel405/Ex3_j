package gameClient;

import utils.Point3D;

public class Robot {
	int id;
	int src;
	int dest;
	int speed=1;
	int value=0; 
	Point3D pos;
	int num=0;
	public Robot() {
		this. value=0;
		this. id=num++;
		this.src=-1;
		this.dest=-1;
	}

	public Robot(int src, String pos,int id,int dest,int value,int speed) {
		this. value=value;
		this. id=id;
		this.src=src;
		this.dest=dest;
		this.speed=speed;

		String[] cord= pos.split(",");
		Point3D ans=new Point3D(Double.parseDouble(cord[0]),Double.parseDouble(cord[1]));
		this. pos=ans;
	}
}