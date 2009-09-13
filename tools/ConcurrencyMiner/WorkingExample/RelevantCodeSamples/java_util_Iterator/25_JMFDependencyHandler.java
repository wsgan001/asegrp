package fi.hut.tml.xsmiles.mlfc.smil.core;
import javax.media.*;
import java.util.Vector;
import java.util.Iterator;

import fi.hut.tml.xsmiles.Log;



/**
 *
 *
 *
 */

public class JMFDependencyHandler extends ControllerAdapter {

    private Vector      beginDependencyObjects;
    private Vector      endDependencyObjects;

    public JMFDependencyHandler() {
        init();
    }

    private void init() {
        endDependencyObjects   = new Vector();
        beginDependencyObjects = new Vector();
    }

    public void stop(StopEvent e) {
    }


    public void start(StartEvent e) {
        informBeginDependencyObjects();
    }

    public void endOfMedia(EndOfMediaEvent e) {
        Log.debug("End of Media.");
        informEndDependencyObjects();
    }

    public void controllerError(ControllerErrorEvent e) {
        Log.error(e.toString());
    }

    public void addDependencyObject(DependencyObject o) {
        if (o.isBeginDependency()) {
            beginDependencyObjects.add(o);

        } else if (o.isEndDependency()) {
            endDependencyObjects.add(o);
        }
    }

    public void informEndDependencyObjects() {
        Iterator i = endDependencyObjects.iterator();
        while(i.hasNext()) {
            DependencyObject o = (DependencyObject)i.next();
            o.getPlayerControls().start();
        }
    }

    public void informBeginDependencyObjects() {
        Iterator i = beginDependencyObjects.iterator();
        while(i.hasNext()) {
            DependencyObject o = (DependencyObject)i.next();
            o.getPlayerControls().start();
        }
    }
}
