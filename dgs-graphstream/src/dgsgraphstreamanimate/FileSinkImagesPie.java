/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dgsgraphstreamanimate;

import org.graphstream.stream.file.FileSinkImages;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;

/**
 *
 * @author sami
 */
public class FileSinkImagesPie extends FileSinkImages {

    private SpriteManager sm;
    
    FileSinkImagesPie(OutputType outputType, Resolutions resolutions) {
        super(outputType, resolutions);
    }

    void setPieValues(String nodeId, String values) {
        this.gg.getNode(nodeId).addAttribute("ui.style", "shape: pie-chart; fill-color: blue,red;size: 70px;");
        this.gg.getNode(nodeId).addAttribute("ui.pie-values", new double[]{0.5,0.5});
        //System.out.println(this.gg.getNode(nodeId).getAttribute("ui.pie-values").toString());
        
        /*if (sm == null) {
            sm = new SpriteManager(this.gg);
        }
        
        Sprite pie = sm.addSprite("pie"+nodeId);
        pie.addAttribute("ui.style", "shape: pie-chart; fill-color: blue,red;size: 70px;");
        pie.addAttribute("ui.pie-values", new double[]{0.5,0.5});
        pie.attachToNode(nodeId);*/
    }
    
    @Override
    public void nodeAdded(String sourceId, long timeId, String nodeId) {
        super.nodeAdded(sourceId, timeId, nodeId);
        
        //this.gg.getNode(nodeId).addAttribute("ui.style", "shape: pie-chart; fill-color: blue,red;size: 70px;");
        //this.gg.getNode(nodeId).setAttribute("ui.pie-values", "0.5,0.5");
        //System.out.println(this.gg.getNode(nodeId).getAttribute("ui.pie-values").toString());
        
        
    }
    
}
