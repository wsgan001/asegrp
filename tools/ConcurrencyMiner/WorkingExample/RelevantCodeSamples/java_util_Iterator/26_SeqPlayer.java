package fi.hut.tml.xsmiles.mlfc.smil.core;

import java.util.Vector;
import java.util.Enumeration;
import java.util.Iterator;
import javax.media.*;

import fi.hut.tml.xsmiles.Log;


import fi.hut.tml.xsmiles.mlfc.smil.objectmodel.*;
/**
 * A {@link SynchPlayer SynchPlayer} that displays one or more
 * media objects in sequence (i.e. one after another).
 */

public class SeqPlayer extends SynchPlayer implements Controllable {

    private SeqListener seqListener;
    private SMILSeq     smilObject;

    private Enumeration  playerElements;
    private Controllable currentPlayingElement;

    public SeqPlayer(SMILSeq s) {
        this(s.getId());
        smilObject = s;
    }

    public SeqPlayer(String s) {
        super(s);
        init();
    }

    public SeqPlayer() {
        init();
    }

    private void init() {
        seqListener = new SeqListener();
        setDependencyHandler(new JMFDependencyHandler());
    }

    public void fetchMedia() {
        Vector players = getPlayers();

        seqListener.init(players.size(), this);
        Iterator i = players.iterator();
        while(i.hasNext()) {
            MediaPlayer p = ((MediaPlayer)i.next());
            p.addControllerListener(seqListener);
            try {
                p.fetchMedia();
            } catch(Exception e) {
                handleException(e);
            }
        }
    }

    private void handleException(Exception e) {
        Log.debug("Not waiting for one player...");
        seqListener.removePlayer();
    }

    public void play() {
        start();
    }

    public void start() {
        setActive(true);
        Vector players = getPlayers();
        playerElements = players.elements();

        Log.debug("Starting 1st player of seq " + getName());
        if (hasNextPlayer()) {
            Controllable c = getNextPlayer();
            currentPlayingElement = c;
            c.start();
        }
    }

    public void startPlayer(int n) {
        if (n < getPlayers().size()) {
            if (getPlayers().elementAt(n) != null) {
                ((Controllable)getPlayers().elementAt(n)).start();
            }
        }
    }

    public boolean hasNextPlayer() {
        return playerElements.hasMoreElements();
    }

    public Controllable getNextPlayer() {
        return (Controllable)playerElements.nextElement();
    }

    public void setCurrentPlayingElement(Controllable p) {

        Log.debug("Setting current element for seq " + this.getName() + " to " + ((MediaPlayer)p).getName());
        if (currentPlayingElement == p) {
            return;
        }
        if (currentPlayingElement == null) {
            setActive(true);
            currentPlayingElement = p;
            Log.debug("There were no playing elements");
            return;
        }
        currentPlayingElement = p;
    }

    public void findAndSetCurrentPlayingElement(Controllable p) {
        Log.debug("Finding current element for seq " + this.getName() + " to " + ((MediaPlayer)p).getName());
        playerElements = getPlayers().elements();
        while(hasNextPlayer()) {
            if (p == getNextPlayer()) {
                currentPlayingElement = p;
                break;
            }

        }
    }


    class SeqListener implements SmilControllerListener {
        private int       nPlayers;
        private int       nEndPlayers;
        private int       nStartPlayers;
        private int       nPrefetchPlayers;
        private int       nRealizedPlayers;
        private SeqPlayer seqPlayer;

        public void init(int n, SeqPlayer s) {
            nPlayers         = n;
            nEndPlayers      = 0;
            nStartPlayers    = 0;
            nPrefetchPlayers = 0;
            nRealizedPlayers = 0;
            seqPlayer        = s;
        }


        public synchronized void removePlayer() {
            nPlayers -= 1;
        }

        public synchronized void controllerUpdate(SmilControllerEvent event) {
            MediaPlayer     player = (MediaPlayer)event.getSource();
            ControllerEvent cEvent = event.getControllerEvent();

            if ((cEvent instanceof EndOfMediaEvent) ||
                (cEvent instanceof StopAtTimeEvent)){
                if (hasNextPlayer()) {
                    Log.debug("Seq " + seqPlayer.getName() + " one player done.");
                    Controllable p = getNextPlayer();
                    setCurrentPlayingElement(p);
                    p.start();
                } else {
                    Log.debug("Seq " + seqPlayer.getName() + " all players done.");
                    setActive(false);
                    setFirstPlayerStarted(false);
                    getDependencyHandler().informEndDependencyObjects();
                    notifyControllerListeners(event);
                }
            } else if (cEvent instanceof StartEvent) {
                if (!isFirstPlayerStarted()) {
                    setFirstPlayerStarted(true);
                    event.setSource(seqPlayer);
                    Log.debug("Seq " + seqPlayer.getName() + " notifying of start.");
                    notifyControllerListeners(event);
                    Log.debug("Seq " + seqPlayer.getName() + " setting current player to " + player.getName());
                    findAndSetCurrentPlayingElement((Controllable) player);
                } else {
                    Log.debug("Seq " + seqPlayer.getName() + " setting current player to " + player.getName());
                    setCurrentPlayingElement((Controllable)player);
                }

            } else if (cEvent instanceof PrefetchCompleteEvent) {
                prefetchComplete(event);
            } else if (cEvent instanceof RealizeCompleteEvent) {
                realizeComplete(event);
            }
        }
        private synchronized void prefetchComplete(SmilControllerEvent event) {
            //Log.debug("Seq " + getName() + ": "
            //    + (nPrefetchPlayers + 1) + " of " + nPlayers + " players prefetched.");
            if (++nPrefetchPlayers == nPlayers) {
                Log.debug("Seq: All prefetched.");
                notifyControllerListeners(event);
            }
        }

        private synchronized void realizeComplete(SmilControllerEvent event) {
            if (++nRealizedPlayers == nPlayers) {
            }
        }
    }
}
//    class SeqListener implements SmilControllerListener {
//        private int       nPlayers;
//        private int       nEndPlayers;
//        private int       nStartPlayers;
//        private int       nPrefetchPlayers;
//        private int       nRealizedPlayers;
//        private SynchPlayer seq;
//
//        public void init(int n, SeqPlayer s) {
//            seq              = s;
//            nPlayers         = n;
//            nEndPlayers      = 0;
//            nStartPlayers    = 0;
//            nPrefetchPlayers = 0;
//            nRealizedPlayers = 0;
//        }
//
//        public SynchPlayer getPlayer() {
//            return seq;
//        }
//
//        public synchronized void removePlayer() {
//            nPlayers -= 1;
//        }
//
//        public synchronized void controllerUpdate(SmilControllerEvent event) {
//            ControllerEvent cEvent = event.getControllerEvent();
//
//            if ((cEvent instanceof EndOfMediaEvent) ||
//                (cEvent instanceof StopAtTimeEvent)){
//                nEndPlayers++;
//                if (nEndPlayers < nPlayers) {
//                    Log.debug("One seq player done");
//                    ((SeqPlayer)seq).startPlayer(nEndPlayers);
//                } else if (nEndPlayers == nPlayers) {
//                    Log.debug("All Seq players done.");
//                    setActive(false);
//                    getDependencyHandler().informEndDependencyObjects();
//                    notifyControllerListeners(event);
//                    nEndPlayers = 0;
//                }
//            } else if (cEvent instanceof StartEvent) {
//                if (nStartPlayers++ == 0) {
//                    Log.debug("Seq: First player started.");
//                } else if (nStartPlayers == nPlayers) {
//                    Log.debug("Seq: All players started.");
//                    nStartPlayers = 0;
//                }
//            } else if (cEvent instanceof PrefetchCompleteEvent) {
//                prefetchComplete(event);
//            } else if (cEvent instanceof RealizeCompleteEvent) {
//                realizeComplete(event);
//            }
//        }
//
//        private synchronized void print(String s) {
//            Log.debug(s);
//        }
//
//        private synchronized void prefetchComplete(SmilControllerEvent event) {
//            Log.debug("Seq " + getName() + ": "
//                + (nPrefetchPlayers + 1) + " of " + nPlayers + " players prefetched.");
//            if (++nPrefetchPlayers == nPlayers) {
//                Log.debug("Seq: All prefetched.");
//                notifyControllerListeners(event);
//            }
//        }
//
//        private synchronized void realizeComplete(SmilControllerEvent event) {
//            if (++nRealizedPlayers == nPlayers) {
//            }
//        }
//
//    }
