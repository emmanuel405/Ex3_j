package gameClient;

import java.io.File;
import java.io.PrintWriter;
import java.time.LocalDateTime;

/**
 create  kml file to  view the game course in a specific level

 */
class KML_Logger {
   
    private int stage;//num level
    public StringBuffer kmltxt;//contains the kml txt

    
    KML_Logger(int level) {
    	System.out.println("start");
        this.stage = level;
        kmltxt = new StringBuffer();
        //KML_Play();
        _StartKML();
    }
    
    
    
    private void _StartKML(){
    	kmltxt.append(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
                        "<kml xmlns=\"http://earth.google.com/kml/2.2\">\r\n" +
                        "  <Document>\r\n" +
                        "    <name>" + "Game stage :"+stage + "</name>" +"\r\n"
        );
    	_nodeKML();
    }
   
    
    
    private void _nodeKML(){
    	kmltxt.append(" <Style id=\"node\">\r\n" +
                "      <IconStyle>\r\n" +
                "        <Icon>\r\n" +
                "          <href>http://maps.google.com/mapfiles/kml/pal3/icon35.png</href>\r\n" +
                "        </Icon>\r\n" +
                "        <hotSpot x=\"32\" y=\"1\" xunits=\"pixels\" yunits=\"pixels\"/>\r\n" +
                "      </IconStyle>\r\n" +
                "    </Style>"
        );
    	_FruitKML();
    }
    
    
    
    private void _FruitKML(){
    	kmltxt.append(
                " <Style id=\"fruit_-1\">\r\n" +
                        "      <IconStyle>\r\n" +
                        "        <Icon>\r\n" +
                        "          <href>http://maps.google.com/mapfiles/kml/paddle/purple-stars.png</href>\r\n" +
                        "        </Icon>\r\n" +
                        "        <hotSpot x=\"32\" y=\"1\" xunits=\"pixels\" yunits=\"pixels\"/>\r\n" +
                        "      </IconStyle>\r\n" +
                        "    </Style>" +
                        " <Style id=\"fruit_1\">\r\n" +
                        "      <IconStyle>\r\n" +
                        "        <Icon>\r\n" +
                        "          <href>http://maps.google.com/mapfiles/kml/paddle/red-stars.png</href>\r\n" +
                        "        </Icon>\r\n" +
                        "        <hotSpot x=\"32\" y=\"1\" xunits=\"pixels\" yunits=\"pixels\"/>\r\n" +
                        "      </IconStyle>\r\n" +
                        "    </Style>"
        );
    	_RobotKML();
    }
    
    
    
    private void _RobotKML(){
    	kmltxt.append(
                " <Style id=\"robot\">\r\n" +
                        "      <IconStyle>\r\n" +
                        "        <Icon>\r\n" +
                        "          <href>http://maps.google.com/mapfiles/kml/shapes/motorcycling.png></href>\r\n" +
                        "        </Icon>\r\n" +
                        "        <hotSpot x=\"32\" y=\"1\" xunits=\"pixels\" yunits=\"pixels\"/>\r\n" +
                        "      </IconStyle>\r\n" +
                        "    </Style>"
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
                "    <Placemark>\r\n" +
                        "      <TimeStamp>\r\n" +
                        "        <when>" + Present_time+ "</when>\r\n" +
                        "      </TimeStamp>\r\n" +
                        "      <styleUrl>#" + id + "</styleUrl>\r\n" +
                        "      <Point>\r\n" +
                        "        <coordinates>" + location + "</coordinates>\r\n" +
                        "      </Point>\r\n" +
                        "    </Placemark>\r\n"
        );
    }

////////////////////////end
    
    void KML_Stop()
    {

    	kmltxt.append("  \r\n</Document>\r\n" +
                "</kml>");
        SaveFile();
    }

    /**
     * save to file
     */
    private void SaveFile(){
        try
        {
            File file=new File("data/"+"oz"+stage+".kml");
            PrintWriter pw=new PrintWriter(file);
            pw.write(kmltxt.toString());
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
