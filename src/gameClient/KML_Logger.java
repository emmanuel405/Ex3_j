package gameClient;

import java.io.File;
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
        _StartKML();
    }
    
    private void _StartKML(){
    	kmltxt.append(
    			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
    			+ "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n"
    			+ "<name>" + "Game scenario: " + scenario + "</name>\n"
    			);
    	_nodeKML();
    }
    
    private void _nodeKML(){
    	kmltxt.append(
    			"<Style id=\"node\">\n"
    			+ 	"<IconStyle>\n"
    			+ 		"<Icon>\n"
    			+ 			"<href>"
    			+ 				"http://maps.google.com/mapfiles/kml/pal5/icon11.png"
    			+ 			"</href>\n"
   				+ 		"</Icon>\r\n"
   				+ 		"<hotSpot x=\"32\" y=\"1\" xunits=\"pixels\" yunits=\"pixels\"/>\n"
   				+ 	"</IconStyle>\n"
   				+ "</Style>"
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
                + 	"</IconStyle>\r\n"
                + "</Style>"
        );
    	_RobotKML();
    }
    
    private void _RobotKML(){
    	kmltxt.append(
                "<Style id=\"robot\">\r\n"
                + 	"<IconStyle>\r\n"
                + 		"<Icon>\r\n"
                +			"<href>"
                + 				"http://maps.google.com/mapfiles/kml/shapes/heliport.png"
                + 			"</href>\r\n"
                + 		"</Icon>\r\n"
                + 		"<hotSpot x=\"32\" y=\"1\" xunits=\"pixels\" yunits=\"pixels\"/>\r\n"
                + 	"</IconStyle>\r\n"
                + "</Style>"
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
        LocalDateTime Present_time = LocalDateTime.now();
        kmltxt.append(
        		 "<Placemark>"
        		+ 	"<TimeStamp>"
        		+ 		"<when>"
        		+ 			Present_time 
        		+ 		"</when>"
        		+ 	"</TimeStamp>\n"
        		+ 	"<name>"
        		+ 		"Node"
        		+	"</name>\n"
        		+	"<styleUrl>"
        		+		id
        		+	"</styleUrl>\n"
        		+ 	"<Point>"
        		+ 		"<coordinates>"
        		+ 			location
        		+ 		"</coordinates>\n"
        		+ 	"</Point>"
        		+"</Placemark>"
        		);
       
    }

////////////////////////end
    
    void KML_Stop(){

    	kmltxt.append(
    			"</Document>\n"
    			+"</kml>"
    			);
        SaveFile();
    }

    /**
     * save to file
     */
    private void SaveFile(){
        try{
            File file=new File("data/"+"manu&oz"+scenario+".kml");
            PrintWriter pw=new PrintWriter(file);
            pw.write(kmltxt.toString());
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
