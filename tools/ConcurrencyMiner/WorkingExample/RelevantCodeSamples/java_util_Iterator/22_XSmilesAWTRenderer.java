

package fi.hut.tml.xsmiles.mlfc.xslfo.render;
import org.apache.fop.render.awt.*;


//XSmiles
import fi.hut.tml.xsmiles.Log;
import fi.hut.tml.xsmiles.XLink;
import fi.hut.tml.xsmiles.mlfc.MLFC;
import fi.hut.tml.xsmiles.mlfc.smil.SMILMLFC;
import fi.hut.tml.xsmiles.mlfc.xslfo.layout.InputArea;
import fi.hut.tml.xsmiles.mlfc.xslfo.XSLFOMLFC;
import fi.hut.tml.xsmiles.mlfc.xslfo.layout.FormWidgetArea;
import fi.hut.tml.xsmiles.mlfc.xslfo.widget.LinkComponent;
import fi.hut.tml.xsmiles.mlfc.xslfo.image.XSmilesImage;
import fi.hut.tml.xsmiles.mlfc.general.XForm;
import fi.hut.tml.xsmiles.mlfc.general.XFormsHandler;

import org.apache.fop.layout.*;
import org.apache.fop.messaging.MessageHandler;
import org.apache.fop.datatypes.*;
import org.apache.fop.image.*;
import org.apache.fop.svg.*;
import org.apache.fop.dom.svg.*;
import org.apache.fop.dom.svg.SVGArea;
import org.apache.fop.render.pdf.*;
import org.apache.fop.viewer.*;
import org.apache.fop.apps.*;

import org.xml.sax.SAXException;

import org.w3c.dom.svg.*;

import java.awt.*;
import java.awt.Image;
import java.awt.image.*;
import java.awt.geom.*;
import java.awt.font.*;
import java.util.*;
import java.net.URL;
import java.io.*;
import java.beans.*;
import javax.swing.*;
import java.awt.print.*;
import java.awt.image.BufferedImage;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Iterator;



import org.apache.fop.render.Renderer;
import org.apache.fop.render.awt.AWTRenderer;
/**
 * XSMiles extensions to the org.apache.fop.awt.AWTRenderer.
 * - Form components
 * - Links
 * - Embedded SVG & SMIL
 *
 * @author       Mikko Honkala
 * @version      $Revision: 1.1 $
 */

public class XSmilesAWTRenderer extends AWTRenderer2 implements Renderer, Printable, Pageable {
  protected Translator res;
  protected static Container container;
//  protected int count;
  protected Hashtable widgets;
  protected Hashtable mlfcList;
  protected Vector linkComponents;
  protected FormHandler formhandler;

  protected final static int PADX=0,PADY=0;

  /**
   * Constructor.
   * @param cont The container to add form and link widgets
   *
   */
  public XSmilesAWTRenderer(Translator aRes,Container cont)
  {
    super(aRes);
	container = cont;
	res = aRes;
	widgets = new Hashtable();
	mlfcList = new Hashtable();
    linkComponents = new Vector();
	formhandler = new FormHandler();
	this.setParent(cont);
  }

//  public static void setPanel(Panel p)
//  {
//  	panel = p;
//  }

  public void render(AreaTree areaTree, int aPageNumber)
  throws IOException {
  		Log.debug("------- Render page: "+aPageNumber+" -----------");
		removeComponents(widgets,this.container);
        removeLinks();
  	    widgets=new Hashtable();
        Page page = (Page)areaTree.getPages().elementAt(aPageNumber);
        // wollen wir links abbilden? -- Ja!
        super.render(areaTree, aPageNumber);
        if (page.hasLinks()) {
            renderLinks(page.getLinkSets());
        }
        Log.debug("");
	}

  private void renderLinks(Vector linkSets) {
      Iterator i = linkSets.iterator();

      while(i.hasNext()) {
          LinkSet ls = (LinkSet) i.next();
          renderLinkSet(ls);
      }
  }

  private void renderLinkSet(LinkSet linkSet) {
      String   dest = linkSet.getDest();
      Iterator i    = linkSet.getRects().iterator();

      while(i.hasNext()) {
          LinkedRectangle lr = (LinkedRectangle)i.next();
          Rectangle r = lr.getRectangle();
          if (r != null) {
              Component c = createLinkComponent(r, dest);
				//Log.debug("Adding link: "+r+" dest:"+dest);
              c.setVisible(true);
              container.add(c, 0);
              container.invalidate();
              linkComponents.add(c);
          }
      }
  }

  private Component createLinkComponent(Rectangle r, String dest) {
      LinkComponent c = new LinkComponent();
	  double divisor = (1000.0 / (scaleFactor/100.0));
      c.setBounds((int)(r.x / divisor),
	  	(int)( (pageHeight - (r.y / 1000)) *(scaleFactor/100)),
	  	(int)(r.width / divisor),
		(int)(r.height / divisor));
      c.setDestination(dest);
      return c;
  }
	/**
	*  set's components on the previous page invisible
	*
	*/
	protected void removeComponents(Hashtable fields,Container p)
	{
		for (Enumeration e = fields.elements();e.hasMoreElements();)
		{
			Object obj=e.nextElement();
			if (obj instanceof FormWidgetArea)
			{
				FormWidgetArea formArea = (FormWidgetArea)obj;
				Component tf = formArea.getComponent();
				tf.setVisible(false);
			} else if (obj instanceof XSmilesImage)
			{
				// XSmilesImages contain containers
				((XSmilesImage)obj).getContainer().setVisible(false);
				Object obj2 = mlfcList.get(obj);
				MLFC mlfc = (MLFC)obj2;
				if (mlfc!=null)
				{
					Log.debug("Trying to pause MLFC:"+mlfc.getClass());
					if (mlfc instanceof SMILMLFC) ((SMILMLFC)mlfc).pauseMLFC();
				} else Log.error("XSmilesAWTRenderer.removeComponents:null");
			}
		}
	}

    private void removeLinks() {
        Iterator i = linkComponents.iterator();
        while(i.hasNext()) {
            Component c = (Component) i.next();
            container.remove(c);
            c.setVisible(false);
        }
        linkComponents = new Vector();
    }

    public void renderFormArea(FormWidgetArea area) {

	    org.apache.fop.layout.Area app=area;
		Rectangle2D rect = this.getBounds(area);
	    double x = rect.getX();
	    double y = rect.getY();
	    double divisor = (1000.0 / (scaleFactor/100.0));
	    this.showWidget(area,new Double(x/divisor).intValue(),new Double((pageHeight-(y/1000.0))*(scaleFactor/100.0)).intValue());
	    int h = area.getHeight();
	    this.currentYPosition -= h;
    }


	/**
	* This is called for every widget on the current page.
	* It checks whether the component exists in the area object
	* if it doesn't -> it is created
	* if it does -> it is set visible
	*
	*/
	protected void showWidget(FormWidgetArea area, int posx, int posy)
	{
		//Log.debug("showWidget "+area.getFormElement().getElementName());
		//this.count++;
		Component t=area.getComponent();

		// If this page has been once rendered, the component already exists
		// Otherwise we have to create it
		org.apache.fop.layout.Area parent = area.getParent();
		//org.apache.fop.layout.Area parent2=parent.getParent();
		if (t!=null) t.setVisible(true);
		else
		{
			t = area.createComponent(formhandler);
			try
			{
				// add the new widget to the formhandler
				formhandler.addFormWidget(area,0);
				// let the formhandler set the initial value
				formhandler.loadWidgetValue(area);
				// let the formhandler set the initial size
				formhandler.setWidgetSize(area);
			} catch (SAXException e)
			{
				Log.error(e);
			}
			t.setVisible(true);
			container.add(t,0);
			container.invalidate();
		}
		// We have all the inputs in the hashtable and
		//textfields.put(area.getInput().getName(),t);
		widgets.put(area.getComponent(),area);
		t.setLocation(posx,posy);
		t.setVisible(true);
		t.invalidate();
	}
	public void setAreaTree(AreaTree atree)
	{
		tree=atree;
	}
	/**
	 * XSmiles extension: External SVG images using SVGMLFC
	 *
	 */
	public void renderImageArea(ImageArea area) {

		FopImage img = area.getImage();
		Log.debug("AWTRender.renderImageArea() "+img.getURL());
		XSmilesImage ximg=null;
		if (null==img)
		{
			super.renderImageArea(area);
		}
		if (img instanceof XSmilesImage)
		{
			// XSMILES SVG EXTENSION
			// SVGMLFC uses CSIRO viewer
			ximg = (XSmilesImage)img;
			String urlString = ximg.getURL();
			Container p=ximg.getContainer();
			this.widgets.put(ximg,ximg);
			Rectangle2D rect = this.getBounds(area);
			double x = rect.getX();
			double y = rect.getY();

			int w = area.getContentWidth();
			int h = area.getHeight();
			Rectangle bounds = new Rectangle((int)(x / 1000),
					   (int)(pageHeight - y / 1000),
					   w / 1000,
					   h / 1000);

			// Check if the container already exists
			if (p!=null)
			{
				// Just set the container visible
				p.setBounds(bounds);
				p.setVisible(true);
				MLFC mlfc = (MLFC)mlfcList.get(ximg);
				if (mlfc!=null)
				{
					if (mlfc instanceof SMILMLFC) ((SMILMLFC)mlfc).continueMLFC();
				}

			} else
			{
				try {
					// Create the container and the external MLFC
					URL url = new URL(urlString);
					// Should this be just container?
					p = new Panel();
					p.setLayout(null); // why do we have to use null layout here?
					this.container.add(p,0);
					ximg.setContainer(p);
					// create an XLink object
					XLink link = new XLink(url);
					p.setBounds(bounds);
					// call MLFCManager.activateSecondaryMLFC(xlink,p)
					MLFC mlfc = XSLFOMLFC.browser.getMLFCManager().activateSecondaryMLFC(link,p);
					mlfcList.put(ximg,mlfc);

				} catch(java.net.MalformedURLException mue) {
					// cannot normally occur because, if URL is wrong, constructing FopImage
					// will already have failed earlier on
				}
			}
			currentYPosition -= h;
		}
		else super.renderImageArea(area);
	}
}


