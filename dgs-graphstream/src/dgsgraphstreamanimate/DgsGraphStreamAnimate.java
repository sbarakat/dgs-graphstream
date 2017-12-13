package dgsgraphstreamanimate;

import java.io.IOException;
import java.util.Arrays;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.ProxyPipe;
import org.graphstream.stream.file.FileSourceDGS;
import org.graphstream.stream.file.FileSinkImages;
import org.graphstream.stream.file.FileSinkImages.Quality;
import org.graphstream.stream.file.FileSinkImages.Resolutions;
import org.graphstream.stream.file.FileSinkImages.OutputType;
import org.graphstream.stream.file.FileSinkImages.OutputPolicy;
import org.graphstream.stream.file.FileSinkImages.LayoutPolicy;
import org.graphstream.stream.file.FileSinkImages.RendererType;
import org.graphstream.stream.SinkAdapter;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.layout.Layouts;
import org.graphstream.ui.layout.springbox.implementations.LinLog;
import org.graphstream.ui.view.Viewer;

/**
 *
 * @author Sami Barakat
 */
public class DgsGraphStreamAnimate extends SinkAdapter {

    private DefaultGraph g;
    private FileSinkImages fsi;
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

        //System.setProperty("org.graphstream.ui.renderer","org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        
        FileSourceDGS dgs = new FileSourceDGS();
        
        this.g = new DefaultGraph("graph");
        //this.g.addAttribute("ui.stylesheet", "url('style.css')");
        
        /*
        LinLog layout = new LinLog(false);
        
        double a = 0;
        double r = -1.3;
        double force = 3;
        
        layout.configure(a, r, true, force);
        layout.setQuality(1);
        layout.setBarnesHutTheta(0.5);
        */
        
        //this.layout = Layouts.newLayoutAlgorithm();
        
        fsi = new FileSinkImages(OutputType.PNG, Resolutions.HD720);
        fsi.setOutputPolicy(OutputPolicy.BY_STEP);
        fsi.setLayoutPolicy(LayoutPolicy.COMPUTED_FULLY_AT_NEW_IMAGE);
        fsi.setQuality(Quality.HIGH);
        fsi.setRenderer(RendererType.SCALA);
        fsi.setStyleSheet("url('style.css')");

        //dgs.addSink(layout);
        //layout.addSink(fsi);

        // chain: dgs -> fsi
        //dgs.addSink(fsi);
        
        // chain: dgs -> g -> fsi
        dgs.addSink(this.g);
        this.g.addSink(fsi);
        
        // chain: dgs -> g -> layout -> fsi
        //dgs.addSink(this.g);
        //this.g.addSink(layout);
        //this.layout.addSink(fsi);
        
        dgs.addAttributeSink(this);
        
        //Viewer viewer = this.g.display();
        //ProxyPipe pipe = viewer.newViewerPipe();
        
        System.out.println(inputDGS);
        fsi.begin(outputDirectory);
        try {
            dgs.begin(inputDGS);
            while (dgs.nextEvents()) {
                //pipe.pump();
                //layout.compute();
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

            int count = newValue.toString().length() - newValue.toString().replace(",", "").length() + 1;
            float share = 1.0f / (float)count;
            float[] pie_values = new float[count];
            Arrays.fill(pie_values, share);

            n.setAttribute("ui.style", "shape: pie-chart; fill-color: " + newValue.toString() + ";");
            n.setAttribute("ui.pie-values", pie_values);
        }
    }
}
