package gameClient;

import utils.Point3D;

public class Fruit {
	public double value;
	public int type;
	public Point3D pos;
	
	public Fruit(double value,int type,String pos) {
		this. value=value;
		this. type=type;
		
	    String[] cord= pos.split(",");
		Point3D ans=new Point3D(Double.parseDouble(cord[0]),Double.parseDouble(cord[1]));
		this. pos=ans;
	}

	public Point3D getLocation() {
		return this.pos;

	}

}
