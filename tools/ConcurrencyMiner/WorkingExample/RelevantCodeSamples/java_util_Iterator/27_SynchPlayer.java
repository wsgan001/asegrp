package fi.hut.tml.xsmiles.mlfc.smil.core;

import fi.hut.tml.xsmiles.Log;

/*
 * MediaPlayers can be added to SynchPlayer. These MediaPlayers can then be synchronized
 * according to the rules of the subclasses of SynchPlayer.
 * <p>
 * Direct subclasses: {@link ParPlayer ParPlayer} {@link SeqPlayer SeqPlayer}
 */

import java.util.Iterator;
import java.util.Vector;
import javax.media.*;

public abstract class SynchPlayer extends AbstractMediaPlayer implements Controllable {

    private Vector  players;
    private int     master  = 0;
    private Vector  controllerListeners;

    private boolean isFirstPlayerStarted;

    public SynchPlayer(String s) {
        super(s);
        init();
    }

    public SynchPlayer() {
        init();
    }

    private void init() {
        players             = new Vector();
        controllerListeners = new Vector();
    }

    public void addPlayer(MediaPlayer player) {
        players.add(player);
    }

    public void addControllerListener(SmilControllerListener cl) {
        controllerListeners.add(cl);
    }

    public void notifyControllerListeners(SmilControllerEvent e) {
        Iterator i = controllerListeners.iterator();

        while(i.hasNext()) {
            ((SmilControllerListener)i.next()).controllerUpdate(e);
        }
    }

    public Vector getPlayers() {
        return players;
    }

    public abstract void play();


    public void shareTimeBase(JMFRenderer[] pls) {
        TimeBase   tb = pls[master].getPlayer().getTimeBase();

        for (int i = 0; i < pls.length; i++) {
            JMFRenderer r = pls[i];
            try {
                r.getPlayer().setTimeBase(tb);
            } catch (IncompatibleTimeBaseException e){
                Log.info("Incompatible time base, skipping...");
            }
        }
    }

    //
    // Controllable interface
    //
    public abstract void start();

    public void stop() {
        Iterator i = players.iterator();

        while(i.hasNext()) {
            Controllable c = (Controllable)i.next();
            if (c.isActive()) {
                c.stop();
            }
        }
        setActive(false);
        setFirstPlayerStarted(false);

    }

    public void close() {
        Iterator i = players.iterator();

        while(i.hasNext()) {
            Controllable c = (Controllable)i.next();
            c.close();
        }
        setActive(false);
    }

    public void pause() {
        Iterator i = players.iterator();

        while(i.hasNext()) {
            ((Controllable)i.next()).pause();
        }
    }

    public abstract void fetchMedia();

    protected boolean isFirstPlayerStarted() {
        return isFirstPlayerStarted;
    }

    protected void setFirstPlayerStarted(boolean value) {
        isFirstPlayerStarted = value;
    }

}
