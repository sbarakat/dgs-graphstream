package dgsgraphstreamanimate;

import java.io.IOException;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSourceDGS;
import org.graphstream.stream.file.FileSinkImages;
import org.graphstream.stream.file.FileSinkImages.Quality;
import org.graphstream.stream.file.FileSinkImages.Resolutions;
import org.graphstream.stream.file.FileSinkImages.OutputType;
import org.graphstream.stream.file.FileSinkImages.OutputPolicy;
import org.graphstream.stream.file.FileSinkImages.LayoutPolicy;
import org.graphstream.stream.SinkAdapter;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.layout.springbox.implementations.LinLog;
import static scala.xml.TopScope.prefix;

/**
 *
 * @author Sami Barakat
 */
public class DgsGraphStreamAnimate extends SinkAdapter {

    private DefaultGraph g;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: DgsGraphStreamAnimate.jar input.dgs output/frame_");
            System.exit(1);
        }
        try{
            DgsGraphStreamAnimate a = new DgsGraphStreamAnimate();
            a.AnimateDgs(args[0], args[1]);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    private void AnimateDgs(String inputDGS, String outputDirectory)
            throws java.io.IOException {
        
        FileSourceDGS dgs = new FileSourceDGS();
        
        this.g = new DefaultGraph("graph");
        
        /*
        LinLog layout = new LinLog(false);
        
        double a = 0;
        double r = -1.3;
        double force = 3;
        
        layout.configure(a, r, true, force);
        layout.setQuality(1);
        layout.setBarnesHutTheta(0.5);
        */
        
        FileSinkImages fsi = new FileSinkImages(OutputType.PNG, Resolutions.HD720);
        fsi.setStyleSheet(
               "graph { padding: 50px; fill-color: white; }" +
               "node { fill-color: #3d5689; }" +
               "edge { fill-color: black; }");
        fsi.setOutputPolicy(OutputPolicy.BY_STEP);
        fsi.setLayoutPolicy(LayoutPolicy.COMPUTED_FULLY_AT_NEW_IMAGE);
        fsi.setQuality(Quality.HIGH);

        //dgs.addSink(layout);
        //layout.addSink(fsi);

        dgs.addSink(this.g);
        this.g.addSink(fsi);
        
        this.g.addAttributeSink(this);
        
        
        System.out.println(inputDGS);
        fsi.begin(outputDirectory);
        try {
            dgs.begin(inputDGS);
            while (dgs.nextEvents()) {
                //layout.compute();
                //fsi.nodeAttributeAdded(inputDGS, 0, inputDGS, inputDGS, fsi);
                
            }
            dgs.end();
            fsi.end();
        } catch (IOException e1) {
            e1.printStackTrace();
            System.exit(1);
        }
    }
    
    @Override
    public void nodeAttributeChanged(String sourceId, long timeId,
                    String nodeId, String attribute, Object oldValue, Object newValue) {

        if (attribute.equals("c")) {
            Node n = this.g.getNode(nodeId);
            n.addAttribute("ui.color", newValue);
            n.addAttribute("ui.style", "fill-color: " + newValue + ";");
        }
    }
}
