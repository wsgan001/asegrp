package fi.hut.tml.xsmiles.mlfc.smil.core;

import fi.hut.tml.xsmiles.mlfc.smil.*;
import fi.hut.tml.xsmiles.mlfc.general.*;
import fi.hut.tml.xsmiles.Log;
import javax.media.*;
import javax.media.protocol.*;
import java.net.*;
import java.io.IOException;
import java.awt.Component;
import java.awt.*;
import java.util.Vector;
import java.util.Iterator;
import java.util.Hashtable;

/**
 * Handles rendering of the media using JMF.
 *
 */

public class MediaProxy {

    private Player          currentPlayer;
    private URL             currentPlayerUrl;

    private Player          originalPlayer;
    private URL             originalPlayerUrl;

    private Player          currentVisiblePlayer;

    private Hashtable       jmfPlayers;

    private JMFRenderer     renderer;
    private PlayerListener  pl;
    private Dimension       size;
    private Vector          controllerListeners;
    private boolean         listen;
    private ZIndexContainer visualComponent;


    public MediaProxy(JMFRenderer r) {
        pl = new PlayerListener(r);
        size = new Dimension(0,0);
        renderer = r;
        init();
    }

    private void init() {
        listen = true;
        controllerListeners = new Vector();
        jmfPlayers = new Hashtable();
        visualComponent = new ZIndexContainer();
        visualComponent.setVisible(false);
        visualComponent.setSize(size);
    }


    public void displayMediaForUrl(URL url) {
        try{
            if (jmfPlayers.isEmpty()) {  // first time called - original player
                currentPlayer        = fetchMedia(url);
                currentVisiblePlayer = currentPlayer;
                originalPlayer       = currentPlayer;
                originalPlayerUrl    = url;
                currentPlayerUrl     = url;
                jmfPlayers.put(url, currentPlayer);
                displayOriginalPlayer(true);

            } else if (jmfPlayers.get(url) == null) { // first time the contents change
                currentPlayer    = fetchMedia(url);
                currentPlayerUrl = url;
                jmfPlayers.put(url, currentPlayer);
                displayCurrentPlayer(true);
                currentVisiblePlayer = currentPlayer;
            } else if (jmfPlayers.get(url) == originalPlayer){ // any time except the first time
                displayOriginalPlayer(false);
                currentVisiblePlayer = originalPlayer;
            } else {
                currentPlayer    = (Player)jmfPlayers.get(url);
                currentPlayerUrl = url;
                displayCurrentPlayer(false);
                currentVisiblePlayer = currentPlayer;
            }
        } catch (Exception ex) {
			Log.info("*** Media not found ***");
            Log.error(ex);
        }
    }

    public void displayOriginalPlayer(boolean isFirstTime) {
        if (isFirstTime) {
            originalPlayer.realize();
        } else {
            originalPlayer.getVisualComponent().setVisible(true);
            originalPlayer.getVisualComponent().setSize(size);
            visualComponent.add(originalPlayer.getVisualComponent());
            visualComponent.remove(currentVisiblePlayer.getVisualComponent());
  //          currentVisiblePlayer.stop();
            visualComponent.validate();
            visualComponent.repaint();
        }

    }

    public void displayCurrentPlayer(boolean isFirstTime) {
        if(isFirstTime) {
            currentPlayer.realize();
        } else {
	        currentPlayer.getVisualComponent().setVisible(true);
            currentPlayer.getVisualComponent().setSize(size);
            visualComponent.add(currentPlayer.getVisualComponent());
            if (currentVisiblePlayer != originalPlayer) {
//              currentVisiblePlayer.stop();
            }
            visualComponent.remove(currentVisiblePlayer.getVisualComponent());
            visualComponent.validate();
            visualComponent.repaint();
        }
    }


    public Player fetchMedia(URL url) throws Exception {
        Player newPlayer;
        MediaLocator ml;
        try {
            if ((ml = new MediaLocator(url)) == null) {
                throw new Exception("Can't build URL for " + url);
            }
            DataSource src = null;
            try  {
                src = Manager.createDataSource(ml);
                Log.debug("Player needed for mime-type: " + src.getContentType());
            } catch (NoDataSourceException e)  {
                Log.info("Cannot find url: " + url.toString());
                throw e;
            }
            try  {
                newPlayer = Manager.createPlayer(ml);
            } catch (NoPlayerException e) {
                Log.info("ElementPlayer:" + e);
                throw e;
            }
        } catch (Exception e) {
            // Loading of media failed -> display broken image.
            Log.debug("File not found, fetching broken image.");
            SMILConfig sc = SMILConfig.getInstance();
            String s  = sc.getBrokenImage();
            URL    brokenImageUrl = URLFactory.getInstance().createURL(s);
            return fetchBrokenImage(brokenImageUrl);
            //throw(e);
        }
        addListenersToPlayer(newPlayer);
        return newPlayer;
    }

    public Player fetchBrokenImage(URL url) throws Exception {
        Player newPlayer;
        MediaLocator ml;
        try {
            if ((ml = new MediaLocator(url)) == null) {
                throw new Exception("Can't build URL for " + url);
            }
            DataSource src = null;
            try  {
                src = Manager.createDataSource(ml);
                //Log.debug("Player needed for mime-type: " + src.getContentType());
            } catch (NoDataSourceException e)  {
                Log.error("Cannot find url: " + url.toString());
                throw e;
            }
            try  {
                newPlayer = Manager.createPlayer(ml);
            } catch (NoPlayerException e) {
                Log.error(e);
                throw e;
            }
        } catch (Exception e) {
            // This should never happen if brokenImage in smilConfig is set correctly,
            // but ofcourse this will happen all the time.
            Log.info("Broken image not found.");
            throw (e);
        }
        addListenersToPlayer(newPlayer);
        return newPlayer;
    }


    public void setRenderer(JMFRenderer r) {
        renderer = r;
    }

    public void addControllerListener(ControllerListener l) {
        //Log.debug("Adding cListener " + l.toString() + " to " + renderer.getName());
        if (originalPlayer == null) {
            controllerListeners.add(l);
        }
        if (originalPlayer!= null && l != null) {
            originalPlayer.addControllerListener(l);
            controllerListeners.add(l);
        }
    }

    private void addListenersToPlayer(Player p) {
        p.addControllerListener(pl);
        Iterator i = controllerListeners.iterator();
        while(i.hasNext()) {
            p.addControllerListener((ControllerListener)i.next());
        }
    }

    public boolean hasPlayer() {
        return originalPlayer != null;
    }

    public void play() {
        listen = true;
//    	originalPlayer.start();
	}

    public void stop() {
        listen = false;
        if (originalPlayer != null) {
            originalPlayer.setMediaTime(new Time(0));
            originalPlayer.stop();
        }
        visualComponent.setVisible(false);
    }

    public void close() {
        listen = false;
        if (!jmfPlayers.isEmpty()) {
            closeAllPlayers();
            pl = null;
        } else {
            Log.error("Tried to close JMFPlayer, already closed.");
        }
        visualComponent.removeAll();
        visualComponent.setVisible(false);
        visualComponent = null;
    }

    private void closeAllPlayers() {
        java.util.Enumeration e = jmfPlayers.elements();
        while(e.hasMoreElements()) {
            ((Player) e.nextElement()).close();
        }
    }

    public void pause() {
    	originalPlayer.stop();
	}

    public void initMedia() {
    }

    public void setSize(Dimension s) {
        size = s;
        visualComponent.setSize(s);
    }

    public void setSize(int w, int h) {
        size = new Dimension(w, h);
        setSize(size);
    }

    public void setMediaTime(Time t) {
        if (originalPlayer != null) {
            originalPlayer.setMediaTime(t);
        }
    }

    public Time getMediaTime() {
        if (originalPlayer != null) {
            return originalPlayer.getMediaTime();
        }
        return null;
    }

    public void setStopTime(int time) {
        originalPlayer.setStopTime(new Time((float)time));
    }

    public void setStopTime(float time) {
        originalPlayer.setStopTime(new Time(time));
    }

    public void setStopTime(Time time) {
        originalPlayer.setStopTime(time);
    }

    public TimeBase getTimeBase() {
        if (originalPlayer != null) {
            return originalPlayer.getTimeBase();
        }
        return null;
    }

    public void setTimeBase(TimeBase tb) throws IncompatibleTimeBaseException {
        originalPlayer.setTimeBase(tb);
    }

    public void syncStart(Time t) {
        listen = true;
        currentPlayer.syncStart(t);
    }

	public void start()
	{
		listen = true;
		currentPlayer.start();

	}

    public void prefetch() {
        currentPlayer.prefetch();
    }

    public Component getVisualComponent() {
        //Component c = currentVisiblePlayer.getVisualComponent();
        //c.setSize(size);

        return visualComponent;
    }

    public Player getPlayer() {
        return originalPlayer;
    }

    class PlayerListener implements ControllerListener {

        private JMFRenderer r;

        public PlayerListener(JMFRenderer r) {
            this.r = r;
        }

        public synchronized void controllerUpdate(ControllerEvent event) {
	        if (!listen) {
                return;
            }

		    SmilControllerEvent e = new SmilControllerEvent(r, event);

            if (event instanceof RealizeCompleteEvent) {
                r.realizeComplete(e);
            } else if (event instanceof PrefetchCompleteEvent) {
                Component c = currentPlayer.getVisualComponent();
                if (c != null) {
                    //c.setVisible(true);
                    c.setSize(size);
				   	c.setLocation(0, 0);
                    visualComponent.add(c);
					visualComponent.invalidate();
//XXX                    visualComponent.validate();
                }
                if (currentPlayer == originalPlayer) {
                    r.prefetchComplete(e);
                } else {
                    currentPlayer.start();
//                    c.setVisible(true);
                }
            } else if (event instanceof StartEvent) {
                Component c = currentPlayer.getVisualComponent();
                if (c != null) {
                    c.setVisible(true);
//                    visualComponent.add(c);
					c.setSize(size);
					c.setLocation(0, 0);
    	            visualComponent.validate();
    	            visualComponent.setVisible(true);
                }
                r.mediaStarted(e);
//XXX                visualComponent.setSize(size);
//				visualComponent.setVisible(true);
//				visualComponent.validate();
            } else if (event instanceof EndOfMediaEvent) {
                r.endOfMedia(e);
            } else if (event instanceof StopAtTimeEvent) {
                r.endOfMedia(e);
			} else if (event instanceof StopByRequestEvent) {
                r.endOfMedia(e);
            } else {
                //Log.debug(r.getName() + ": event " + event.toString() + " not handled.");
                e = null;
            }
        }
    }
}
