package dgsgraphstreamanimate;

import java.io.IOException;
import org.graphstream.stream.file.FileSourceDGS;
import org.graphstream.stream.file.FileSinkImages;
import org.graphstream.stream.file.FileSinkImages.Quality;
import org.graphstream.stream.file.FileSinkImages.Resolutions;
import org.graphstream.stream.file.FileSinkImages.OutputType;
import org.graphstream.stream.file.FileSinkImages.OutputPolicy;
import org.graphstream.stream.file.FileSinkImages.LayoutPolicy;
import org.graphstream.ui.layout.springbox.implementations.LinLog;

/**
 *
 * @author Sami Barakat
 */
public class DgsGraphStreamAnimate {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: DgsGraphStreamAnimate.jar input.dgs output/frame_");
            System.exit(1);
        }
        try{
            AnimateDgs(args[0], args[1]);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void AnimateDgs(String inputDGS, String outputDirectory)
            throws java.io.IOException {
        
        FileSourceDGS dgs = new FileSourceDGS();
        
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

        dgs.addSink(fsi);
        
        System.out.println(inputDGS);
        fsi.begin(outputDirectory);
        try {
            dgs.begin(inputDGS);
            while (dgs.nextEvents()) {
                //layout.compute();
            }
            dgs.end();
            fsi.end();
        } catch (IOException e1) {
            e1.printStackTrace();
            System.exit(1);
        }
    }
}
