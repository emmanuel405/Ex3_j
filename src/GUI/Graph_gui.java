package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.*;

import javax.swing.JFrame;
import javax.swing.JLabel;


import algorithms.Graph_Algo;
import dataStructure.*;
import gameClient.Fruit;
import gameClient.Robot;
import utils.Point3D;
import java.util.Scanner;

public class Graph_gui extends JFrame implements ActionListener, MouseListener, MouseMotionListener{
	public DGraph dg = null;
	private boolean connect = false;
	private boolean press_connect = false; // if we press on 'connected'
	private boolean press_shorted = false; // if we press on 'shorted path'
	private boolean press_tsp = false; // if we press on 'TSP	'
	private int BIGGER = 5;
	private double PathDist=0;
	
	public Graph_gui() {
        initGUI();
    }

    private void initGUI() {
        this.setSize(1000, 800);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        MenuBar mBar = new MenuBar();
        Menu m = new Menu("Menu");
        Menu m1 = new Menu("Algo");
        mBar.add(m);
        mBar.add(m1);
        this.setMenuBar(mBar);

        MenuItem item = new MenuItem("Load");
        item.addActionListener(this);
        MenuItem item1 = new MenuItem("Save");
        item1.addActionListener(this);
        MenuItem item2 = new MenuItem("Clean graph");
        item2.addActionListener(this);

        m.add(item);
        m.add(item1);
        m.add(item2);

        MenuItem item3 = new MenuItem("Connected");
        item3.addActionListener(this);
        MenuItem item4 = new MenuItem("Shorted path");
        item4.addActionListener(this);
         MenuItem item5 = new MenuItem("TSP");
        item5.addActionListener(this);

        m1.add(item3);
        m1.add(item4);
        m1.add(item5);
	    
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    public void paint(Graphics g) {
    	super.paint(g);
    	
	// press on 'connected'
    	if(connect && press_connect) {
    		String ans = "the graph is CONNECTED";
    		g.setColor(Color.GREEN);
    		g.drawString(ans, 700, 100);
    	}
    	if(!connect && press_connect == true) {
    		String ans = "the graph is NOT CONNECTED";
    		g.setColor(Color.red);
    		g.drawString(ans, 700, 100);	
    	}
    	if(press_shorted) {
    		String ans = ""+PathDist;
    		g.setColor(Color.GREEN);
    		g.drawString(ans, 700, 100);
    	}
    	if(press_tsp) {
    		
    	}
	    
    
    	
    	
    	if (null == dg) return;

    	for (node_data n : dg.Vertex) {
    		
			g.setColor(Color.GREEN);
			g.fillOval((int)n.getLocation().x() - BIGGER, (int)n.getLocation().y() - BIGGER,
					10, 10);
			
			NodeData nn = (NodeData)n;
	    	for (NodeData m :nn.outgoing ) {
                g.setColor(Color.RED);
                g.drawLine(n.getLocation().ix(), n.getLocation().iy(),
                        m.getLocation().ix(), m.getLocation().iy());

                edge_data ed = dg.getEdge(n.getKey(), m.getKey());
                g.drawString(String.format("%.2f", ed.getWeight()),
                        drawOnLine(n.getLocation().x(), m.getLocation().x(), 0.75),
                        drawOnLine(n.getLocation().y(), m.getLocation().y(), 0.75));
                
                g.setColor(Color.BLACK);
                g.fillOval(drawOnLine(n.getLocation().x(), m.getLocation().x(), 0.85),
                		   drawOnLine(n.getLocation().y(), m.getLocation().y(), 0.85),
                		   											 			4, 4);
            }
			

          
    	}
    	//////////////append robots
    	for (Robot n : dg.Robots) {
			g.setColor(Color.blue);
    		g.fillOval((int)n.getLocation().x() - BIGGER, (int)n.getLocation().y() - BIGGER,
					(int)2.5*BIGGER, (int)2.5*BIGGER);
    	}
    	/////append fruits
    	for (Fruit n : dg.Fruits) {
			g.setColor(Color.orange);
    		g.fillOval((int)n.getLocation().x() - BIGGER, (int)n.getLocation().y() - BIGGER,
					(int)2.5*BIGGER, (int)2.5*BIGGER);
    	}
    }


	@Override
	
	public void actionPerformed(ActionEvent action) {
		String s = action.getActionCommand();
		
		switch(s) {
		
		case "Load":
			
		break;
		
		case "Save":
			
		break;

		case "Clean graph":
			dg.v = null;
			repaint();
		break;

		case "Connected":
			Graph_Algo g1 = new Graph_Algo();
			g1.init(dg);
			press_connect = true;
			connect = g1.isConnected();
			
			repaint();
		break;

		case "Shorted path":
			Graph_Algo g = new Graph_Algo();
			g.init(dg);
			press_shorted = true;

			Scanner ss = new Scanner(System.in);
		    System.out.print("Enter the your src id : ");
		    // Below Statement used for getting String including sentence
		    int s1 = ss.nextInt();
		    System.out.print("Enter the your dest id : ");
		    int s2 = ss.nextInt();
		    PathDist=g.shortestPathDist(s1, s2);
		    

			repaint();

		break;
		
		case "TSP":
			Graph_Algo gr = new Graph_Algo();
			gr.init(dg);
			press_tsp = true;
			
			repaint();
		break;

		}
		
	}
	
	@Override
	public void mouseDragged(MouseEvent m_e) {
		
	}

	@Override
	public void mouseMoved(MouseEvent m_e) {
		
	}

	@Override
	public void mouseClicked(MouseEvent m_e) {
		System.out.println("clicked !");
	}

	@Override
	public void mousePressed(MouseEvent m_e) {
		System.out.println("press !");
	}

	@Override
	public void mouseReleased(MouseEvent m_r) {
		System.out.println("release !");
	}
	
	@Override
	public void mouseEntered(MouseEvent m_e) {
	}

	@Override
	public void mouseExited(MouseEvent m_e) {
	}

	public void addGraph(DGraph dg1) {
		this.dg = dg1;
		this.repaint();
	}
	
	///////////////////////////////
	/// *** private methods *** ///
	///////////////////////////////
	
	/**
	 * @param start
	 * @param fin
	 * @param proportion
	 * 
	 * @return the result
	 */
	private int drawOnLine(double start, double fin, double proportion) {
		return (int)(start + proportion*(fin-start));
	}
	
}
