package dataStructure;

import java.io.Serializable;
import java.util.ArrayList;

import utils.Point3D;

public class NodeData implements node_data, Comparable<NodeData>, Serializable{

	int key, tag;
	double weight;
	 public Point3D location;
	public String info;
	public NodeData previous = null;
	double minDistance = Integer.MAX_VALUE;
	public ArrayList<NodeData>outgoing;
	
	private static int num = 0;
	
	public NodeData() {
		this.key = num++;
		this.info="UNVISITED";
		outgoing=new ArrayList<NodeData>();

	}
	public NodeData(int key) {
		this.key = key;
		this.info="UNVISITED";
		outgoing=new ArrayList<NodeData>();
	}
	public NodeData(int key, Point3D location) {
		this.key = key;
		this.location = location;
		this.info="UNVISITED";
		outgoing=new ArrayList<NodeData>();
	}
	
	
	@Override
	public int getKey() {
		return this.key;
	}

	@Override
	public Point3D getLocation() {
		return this.location;
	}

	@Override
	public void setLocation(Point3D p) {
		this.location = p;
	}

	@Override
	public double getWeight() {		
		return this.weight;
	}

	@Override
	public void setWeight(double w) {
		this.weight = w;
	}

	@Override
	public String getInfo() {
		return this.info;
	}

	@Override
	public void setInfo(String s) {
		this.info = s;
	}

	@Override
	public int getTag() {
		return this.tag;
	}
	
	public double getminDistance() {
		return this.minDistance;
	}
	@Override
	public void setTag(int t) {
		this.tag = t;
	}
	
	@Override

	public int compareTo(NodeData other){
		return Integer.compare((int)minDistance,(int) other.minDistance);
	}

}
