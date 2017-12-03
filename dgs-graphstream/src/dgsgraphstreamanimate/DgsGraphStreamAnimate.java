package dgsgraphstreamanimate;

import java.io.IOException;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.ProxyPipe;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.stream.file.FileSourceDGS;
import org.graphstream.stream.file.FileSinkImages;
import org.graphstream.stream.file.FileSinkImages.Quality;
import org.graphstream.stream.file.FileSinkImages.Resolutions;
import org.graphstream.stream.file.FileSinkImages.OutputType;
import org.graphstream.stream.file.FileSinkImages.OutputPolicy;
import org.graphstream.stream.file.FileSinkImages.LayoutPolicy;
import org.graphstream.stream.SinkAdapter;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.Layouts;
import org.graphstream.ui.layout.springbox.implementations.LinLog;
import org.graphstream.ui.view.Viewer;
import static scala.xml.TopScope.prefix;

/**
 *
 * @author Sami Barakat
 */
public class DgsGraphStreamAnimate extends SinkAdapter {

    private DefaultGraph g;
    private FileSinkImagesPie fsi;
    private Layout layout;
    
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
        
        //System.setProperty("org.graphstream.ui", "org.graphstream.ui.swing.util.Display");
        System.setProperty("org.graphstream.ui.renderer","org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        
        FileSourceDGS dgs = new FileSourceDGS();
        
        this.g = new DefaultGraph("graph");
        this.g.addAttribute("ui.stylesheet", "url('style.css')");
        
        /*
        LinLog layout = new LinLog(false);
        
        double a = 0;
        double r = -1.3;
        double force = 3;
        
        layout.configure(a, r, true, force);
        layout.setQuality(1);
        layout.setBarnesHutTheta(0.5);
        */
        
        this.layout = Layouts.newLayoutAlgorithm();
        
        fsi = new FileSinkImagesPie(OutputType.PNG, Resolutions.HD720);
        fsi.setStyleSheet("url('style.css')");
        fsi.setOutputPolicy(OutputPolicy.BY_STEP);
        fsi.setLayoutPolicy(LayoutPolicy.COMPUTED_FULLY_AT_NEW_IMAGE);
        fsi.setQuality(Quality.HIGH);

        //dgs.addSink(layout);
        //layout.addSink(fsi);

        // chain: dgs -> g -> fsi
        dgs.addSink(this.g);
        this.g.addSink(fsi);
        
        // chain: dgs -> g -> layout -> fsi
        //dgs.addSink(this.g);
        //this.g.addSink(layout);
        //this.layout.addSink(fsi);
        
        //dgs.addAttributeSink(this);
        dgs.addElementSink(this);
        
        Viewer viewer = this.g.display();
        ProxyPipe pipe = viewer.newViewerPipe();
        pipe.addAttributeSink(this.g);
        
        System.out.println(inputDGS);
        fsi.begin(outputDirectory);
        try {
            dgs.begin(inputDGS);
            while (dgs.nextEvents()) {
                //pipe.pump();
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
    public void nodeAdded(String sourceId, long timeId, String nodeId) {
        Node n = this.g.getNode(nodeId);
        Object[] data = new Object[]{"0.5","0.5"};
        
        n.addAttribute("ui.style", "shape: pie-chart; fill-color: red,blue;size: 60px;");
        //n.setAttribute("ui.style", "shape:pie-chart; fill-color: blue, red;");
        //n.setAttribute("ui.pie-values", new double[]{0.5,0.5});
        //n.setAttribute("ui.pie-values", new Object[]{"0.5","0.5"});
        n.setAttribute("ui.pie-values", "0.5,0.5");
        
        //this.fsi.setPieValues(nodeId, "0.5,0.5");
        
        System.out.println(nodeId);
        //System.out.println(n.getAttribute("ui.pie-values").toString());
    }
    
    @Override
    public void nodeAttributeChanged(String sourceId, long timeId,
                    String nodeId, String attribute, Object oldValue, Object newValue) {

        if (attribute.equals("c")) {
            Node n = this.g.getNode(nodeId);
            //n.addAttribute("ui.color", newValue);
            //n.addAttribute("ui.style", "fill-color: " + newValue + ";");

            /*
            SpriteManager sm = new SpriteManager(this.g);
            Sprite pie = sm.addSprite("pie");
            pie.addAttribute("ui.style", "shape: pie-chart; fill-color: #FF0000, #00FF00;size: 40px;");
            pie.addAttribute("ui.pie-values", new double[]{0.5,0.5});
            pie.attachToNode(nodeId);
            */
            
            //Object[] data = new Object[]{"0.5","0.5"};
            
            //n.setAttribute("ui.style", "shape:pie-chart; fill-color: red, blue;");
            //n.setAttribute("ui.pie-values", data); //new double[]{0.5,0.5});
            
            System.out.println(nodeId);
            //System.out.println(n.getAttribute("ui.pie-values").toString());
            
            
        }
    }
    

}
