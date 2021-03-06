package dgsgraphstreamanimate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
import org.graphstream.ui.layout.springbox.implementations.LinLog;
import org.graphstream.ui.view.Viewer;

/**
 *
 * @author Sami Barakat
 */
public class DgsGraphStreamAnimate extends SinkAdapter {

    private DefaultGraph g;
    private FileSinkImages fsi;
    private ProxyPipe pipe;
    private LinLog layout;
        
    private void AnimateDgs(String inputDGS, String outputDirectory, Boolean linlog, Boolean display)
            throws java.io.IOException {

        System.setProperty("org.graphstream.ui.renderer","org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        
        FileSourceDGS dgs = new FileSourceDGS();
        
        this.g = new DefaultGraph("graph");
        this.g.addAttribute("ui.stylesheet", "url('style.css')");
        
        if (linlog) {
            layout = new LinLog(false);
            double a = 0;
            double r = -1.9;
            double force = 3;

            layout.configure(a, r, true, force);
            layout.setQuality(1);
            layout.setBarnesHutTheta(0.5);
            //layout.setStabilizationLimit(0);
        }
        
        fsi = new FileSinkImages(OutputType.PNG, Resolutions.HD720);
        fsi.setOutputPolicy(OutputPolicy.BY_STEP);
        fsi.setLayoutPolicy(LayoutPolicy.COMPUTED_FULLY_AT_NEW_IMAGE);
        fsi.setQuality(Quality.HIGH);
        fsi.setRenderer(RendererType.SCALA);
        fsi.setStyleSheet("url('style.css')");

        if (linlog) {
            // chain: dgs -> g -> layout -> fsi
            dgs.addSink(this.g);
            this.g.addSink(layout);
            layout.addAttributeSink(this.g);
            layout.addSink(fsi);
            
        }
        else {
            // chain: dgs -> g -> fsi
            dgs.addSink(this.g);
            this.g.addSink(fsi);
        }

        dgs.addAttributeSink(this);

        Viewer viewer = null;
        
        if (display) {
            viewer = this.g.display();
            viewer.enableAutoLayout(layout);
            //pipe = viewer.newViewerPipe();
            pipe = viewer.newThreadProxyOnGraphicGraph();
        }
        
        fsi.begin(outputDirectory);
        try {
            dgs.begin(inputDGS);
            while (dgs.nextEvents()) {
                
                if (linlog) {
                    layout.compute();
                }

                if (display) {
                    pipe.pump();
                }
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
    
    public static void main(String[] args) {
        
        Map<String, List<String>> params = new HashMap<>();
        List<String> options = null;

        for (String a : args) {
            if (a.charAt(0) == '-') {
                if (a.length() < 2) {
                    System.err.println("Error at argument " + a);
                    return;
                }

                options = new ArrayList<>();
                params.put(a.substring(1), options);
            }
            else if (options != null) {
                options.add(a);
            }
            else {
                System.err.println("Illegal parameter usage");
                return;
            }
        }
        
        Boolean error = false;
        if (!params.containsKey("dgs")) {
            System.out.println("Missing required option: -dgs\n");
            error = true;
        }
        if (!params.containsKey("out")) {
            System.out.println("Missing required option: -out\n");
            error = true;
        }
        if (error || params.containsKey("help") || params.containsKey("h")) {
            System.out.println("usage: DgsGraphStreamAnimate.jar [OPTIONS]...");
            System.out.println("-dgs <arg>      input GraphStream DGS file");
            System.out.println("-out <arg>      frame filenames are prepended with this path");
            System.out.println("-layout <arg>   layout option to use. options: [default|linlog]");
            System.out.println("-display screen layout option to use. options: [screen]");
            System.out.println("-h,-help        display this help and exit");
            System.exit(1);
        }
        
        Boolean linlog = false;
        Boolean display = false;
        if (params.containsKey("layout") && params.get("layout").get(0).equals("linlog")) {
            linlog = true;
        }
        if (params.containsKey("display") && params.get("display").get(0).equals("screen")) {
            display = true;
        }
        
        try {
            System.out.println(params.get("dgs").get(0));
            DgsGraphStreamAnimate a = new DgsGraphStreamAnimate();
            a.AnimateDgs(params.get("dgs").get(0), params.get("out").get(0), linlog, display);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
