package gameClient;

import java.io.PrintWriter;
import java.time.LocalDateTime;

/**
 create kml file to  view the game course in a specific level
 */
class KML_Logger {
   
    private int scenario; //num of scenario
    public StringBuffer kmltxt;//contains the kml txt

    
    KML_Logger(int scenario) {
    	System.out.println("start;");
        this.scenario = scenario;
        kmltxt = new StringBuffer();
        //KML_Play();
        StartOfKML();
    }
    
    private void StartOfKML(){
    	kmltxt.append(
    			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
    			+ "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n"
    			+ "<Document>\n"
    			+ "<name>" + "Game scenario: " + scenario + "</name>\n\n"
    			);
    	_nodeKML();
    }
    
    private void _nodeKML(){
    	kmltxt.append(
    			"<Style id=\"node\">\n"
    			+ 	"<IconStyle>\n"
    			+ 		"<Icon>\n"
    			+ 			"<href> "
    			+ 				"http://maps.google.com/mapfiles/kml/pal5/icon11.png"
    			+ 			" </href>\n"
   				+ 		"</Icon>\n"
   				+ 		"<hotSpot x=\"32\" y=\"1\" xunits=\"pixels\" yunits=\"pixels\"/>\n"
   				+ 	"</IconStyle>\n"
   				+ "</Style>\n\n"
    			);
    	_FruitKML();
    }
    
    private void _FruitKML(){
    	kmltxt.append(
    			// first style: type of fruit = -1
                 "<Style id=\"fruit_-1\">\r\n"
                + 	"<IconStyle>\n"
                + 		"<Icon>\n"
                + 			"<href>"
                + 				"http://maps.google.com/mapfiles/kml/pal2/icon54.png"
                + 			"</href>\r\n"
                + 		"</Icon>\r\n"
                + 		"<hotSpot x=\"32\" y=\"1\" xunits=\"pixels\" yunits=\"pixels\"/>\r\n"
                + 	"</IconStyle>\r\n"
                +"</Style>\n"
                
                // second style: type of fruit = 1
                +"<Style id=\"fruit_1\">\r\n"
                +	"<IconStyle>\r\n"
                +		"<Icon>\r\n"
                + 			"<href>"
                + 				"http://maps.google.com/mapfiles/kml/pal2/icon55.png"
                + 			"</href>\r\n"
                + 		"</Icon>\r\n"
                +		"<hotSpot x=\"32\" y=\"1\" xunits=\"pixels\" yunits=\"pixels\"/>\r\n"
                + 	"</IconStyle>\n"
                + "</Style>\n\n"
        );
    	_RobotKML();
    }
    
    private void _RobotKML(){
    	kmltxt.append(
                "<Style id=\"robot\">\n"
                + 	"<IconStyle>\n"
                + 		"<Icon>\n"
                +			"<href>\n"
                + 				"http://maps.google.com/mapfiles/kml/shapes/heliport.png"
                + 			"</href>\n"
                + 		"</Icon>\n"
                + 		"<hotSpot x=\"32\" y=\"1\" xunits=\"pixels\" yunits=\"pixels\"/>\n"
                + 	"</IconStyle>\n"
                + "</Style>\n\n"
        );
    }

    /**
     * this function is used in "paint"
     * after painting each element
     * the function enters to kml the location of each element
     * @param id
     * @param location
     */
    void Place_Mark(String id, String location) {
        LocalDateTime time = LocalDateTime.now();
        String[]split =location.split(",");
        Double x=re_scale(Double.parseDouble(split[0]),35.186179,35.2142,0,1000);
        Double y=re_scale(Double.parseDouble(split[1]),32.100148,32.109347,100,600);
        location=""+(-1)*x+","+(-1)*y;
        kmltxt.append(
        		 "<Placemark>\n"
        		+ 	"<TimeStamp>\n"
        		+ 		"<when>\n"
        		+ 			time 
        		+ 		"</when>\n"
        		+ 	"</TimeStamp>\n"
        		+ 	"<name>\n"
        		+ 		"Node"
        		+	"</name>\n"
        		+	"<styleUrl>\n"
        		+			id
        		+	"</styleUrl>\n"
        		+ 	"<Point>\n"
        		+ 		"<coordinates>\n"
        		+ 			location
        		+ 		"</coordinates>\n"
        		+ 	"</Point>\n"
        		+"</Placemark>\n\n"
        		);
       
    }

    //////////////end
    void KML_End(){

    	kmltxt.append(
    			"</Document>\n"
    			+"</kml>\n"
    			);
        System.out.println(SaveFile());
    }

    /**
     * save to file
     */
    private boolean SaveFile(){
        try{
            PrintWriter print = new PrintWriter("data/"+scenario+".kml");
            print.write(kmltxt.toString());
            print.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    private static double re_scale(double res, double r_min, double r_max, 
			double t_min, double t_max){
		double data = res*((r_max-r_min)/(t_max - t_min) )-r_min;
		return data;
	}
}
