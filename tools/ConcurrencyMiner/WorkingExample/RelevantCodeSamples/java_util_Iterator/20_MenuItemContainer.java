package fi.hut.tml.xsmiles.gui.libretto;


import java.util.ArrayList;
import java.awt.MenuItem;
import java.util.Iterator;

public class MenuItemContainer {

  private ArrayList list;

  public MenuItemContainer() {
    init();
  }

  private void init() {
    list = new ArrayList();
  }

  public void add(MenuItem m) {
    list.add(m);
  }

  public MenuItem get(int index) {
    return (MenuItem)list.get(index);
  }

  public boolean isEmpty() {
    return list.isEmpty();
  }

  public Iterator iterator() {
    return list.iterator();
  }
}
