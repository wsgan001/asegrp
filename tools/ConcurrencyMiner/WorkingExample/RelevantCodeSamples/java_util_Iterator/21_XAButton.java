package fi.hut.tml.xsmiles.gui.components.awt;


import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.event.*;
import fi.hut.tml.xsmiles.Log;
/**
 * Similar functionality as normal AWT button.
 * Features:
 * <ul>
 * <li>lightweight</li>
 * <li>three different states (focus, non-focus, pressed), each with graphical representations.</li>
 * <li>not all button functionality is included.</li>
 * </ul>
 */

public class XAButton extends XAComponent{

public  final static int NORMAL  = 0;
public  final static int FOCUSED = 1;
public  final static int PRESSED = 2;

private ArrayList actionListeners;
private String    actionCommand;
private String    label;
private String    paramString;

private int       state;
private boolean   isActive;

private XAImageIcon image;
private XAImageIcon imagePressed;
private XAImageIcon imageRollOver;

private MouseHandler      mouseHandler;
private XABorderDecorator borderDecorator;

public XAButton(String iName) {
    super();
    init();
    setImage        (iName);
    setImageRollOver(iName);
    setimagePressed (iName);
}

public XAButton(String i1Name, String i2Name) {
    super();
    init();
    setImage        (i1Name);
    setImageRollOver(i1Name);
    setimagePressed (i2Name);
}

public XAButton(String i1Name, String i2Name, String i3Name) {
    super();
    init();
    setImage        (i1Name);
    setImageRollOver(i2Name);
    setimagePressed (i3Name);
}

public XAButton() {
    super();
    init();
}

private void init() {
    state           = NORMAL;
    actionListeners = new ArrayList();
    borderDecorator = new XABorderDecorator(this);
    mouseHandler    = new MouseHandler(this);
    addMouseListener(mouseHandler);
}

public void addActionListener(ActionListener l){
    actionListeners.add(l);
}

public String getActionCommand(){
    return actionCommand;
}

public String getLabel(){
    return label;
}

protected String paramString(){
    return paramString;
}

protected void processActionEvent(ActionEvent e){
//    super.processActionEvent(e);
    Iterator i = actionListeners.iterator();
    while(i.hasNext()) {
        ((ActionListener) i.next()).actionPerformed(e);
    }
}

//protected void processEvent(AWTEvent e){

//}

public void removeActionListener(ActionListener l){
    actionListeners.remove(l);
}

public void setActionCommand(String command){
    actionCommand = command;
}

public void setLabel(String l){
    label = l;
}

public void paint(Graphics g) {
    super.paint(g);
    borderDecorator.decorate(g);
    if (label != null) {
    //    g.setColor(Color.black);
    //    g.drawString(label, 10, getHeight() / 2);
    }
    switch(state) {
    case NORMAL:
        image.paint(g);
        break;
    case FOCUSED:
        imageRollOver.paint(g);
        break;
    case PRESSED:
        imagePressed.paint(g);
        break;
    }

}

public void setActive(boolean b) {
    isActive = b;
}

public boolean isActive() {
    return isActive;
}

public void setImage(XAImageIcon  i) {
    image = i;
}

public void setimagePressed(XAImageIcon  i) {
    imagePressed = i;
}

public void setImageRollOver(XAImageIcon  i) {
    imageRollOver = i;
}

public void setImage(String fn) {
    image = new XAImageIcon(fn);
}

public void setimagePressed(String fn) {
    imagePressed = new XAImageIcon(fn);
}

public void setImageRollOver(String fn) {
    imageRollOver = new XAImageIcon(fn);
}

public int getState() {
    return state;
}

public void setState(int s) {
    state = s;
}

public void setSize(Dimension d) {
    setSize(d.width, d.height);
}

public void setSize(int w, int h) {
    super.setSize(w, h);
    image.setSize(w, h);
    imagePressed.setSize(w, h);
    imageRollOver.setSize(w, h);
}

public void setBounds(int x, int y, int w, int h) {
    super.setBounds(x, y, w, h);
    image.setSize(w, h);
    imagePressed.setSize(w, h);
    imageRollOver.setSize(w, h);
}

public class MouseHandler extends MouseAdapter {

private XAButton button;

public MouseHandler(XAButton b) {
    super();
    button = b;
}

public void mouseClicked(MouseEvent e){
}

public void mouseEntered(MouseEvent e){
    setState(FOCUSED);
    repaint();
}

public void mouseExited(MouseEvent e){
    setState(NORMAL);
    repaint();
}

public void mousePressed(MouseEvent e){
    setState(PRESSED);
    processActionEvent(new ActionEvent(button, e.getID(), label));
    repaint();
}

public void mouseReleased(MouseEvent e){
    setState(FOCUSED);
    repaint();
}
}

}
