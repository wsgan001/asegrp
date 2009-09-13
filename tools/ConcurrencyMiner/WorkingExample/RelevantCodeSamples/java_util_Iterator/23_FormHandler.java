package fi.hut.tml.xsmiles.mlfc.xslfo.render;

import fi.hut.tml.xsmiles.Log;
import fi.hut.tml.xsmiles.XLink;
import fi.hut.tml.xsmiles.mlfc.xslfo.XSLFOMLFC;
import fi.hut.tml.xsmiles.mlfc.general.XFormsHandler;
import fi.hut.tml.xsmiles.mlfc.general.XForm;
import fi.hut.tml.xsmiles.mlfc.xslfo.layout.FormWidgetArea;
import fi.hut.tml.xsmiles.mlfc.xslfo.form.FormElement;
import fi.hut.tml.xsmiles.EventBroker;

import java.util.Hashtable;
import java.util.Vector;
import java.util.Iterator;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.FileWriter;
import java.io.File;
import java.net.URL;

import org.w3c.dom.*;
import org.xml.sax.SAXException;


/**
 * FormHandler is used by XSmilesAWTRenderer
 *
 * It links the form input widgets to the corresponding
 * fi.hut.tml.xsmiles.mlfc.general.XForm instance
 *
 */

public class FormHandler implements ActionListener{

	Hashtable by_form;
	Hashtable by_component;
/*

This class maps the Widgets by
	- java.awt.component
	- form id

 */
    public FormHandler() {
		by_form=new Hashtable();
	    by_component=new Hashtable();
    }
	/**
	 *
	 * Add a new widget to the internal data structures
	 *
	 */
	public void addFormWidget(FormWidgetArea widget, int page)
	{
		//Log.debug("addFormWidget: "+widget.getFormElement().getElementName());
		// What if widget's id is null ?
		if (widget.getFormId()==null)
			Log.error("Form widget "+widget.getFormElement().getElementName()+" has no form element, which has valid id.");
		else if (!by_component.containsKey(widget.getComponent()))
		{
			// If by_form doesn't have the vector for the widget's formid, then make one
			if (!by_form.containsKey(widget.getFormId()))
			{
				Vector vect = new Vector();
				by_form.put(widget.getFormId(),vect);
			}
			Vector vect = (Vector)by_form.get(widget.getFormId());
			vect.addElement(widget);

			// index by the component also
			by_component.put(widget.getComponent(),widget);
		}
	}

	/**
	 *
	 * Find a widget by component
	 *
	 */
	public FormWidgetArea getWidget(Component c)
	{
		return (FormWidgetArea)by_component.get(c);
	}
	/**
	 *
	 * Find a component's form id
	 *
	 */
	public String getFormId(Component c)
	{
		FormWidgetArea area= this.getWidget(c);
		if (area==null) return null;
		else return area.getFormId();
	}

	/**
	 *
	 * Loads the widget's value from the corresponding XForm instance
	 *
	 */
	public void loadWidgetValue(FormWidgetArea widget) throws SAXException
	{
		if (widget.isInputComponent())
		{
			XForm form=this.getXForm(widget);
			widget.setComponentValue(form.getValue(widget.getFormElement().getElementName()));
		}
//		else Log.debug(widget.getFormElement().getElementName()+" is not an input component");
	}
	/**
	 *
	 * set the widget's size to default value's size
	 *
	 */
	public void setWidgetSize(FormWidgetArea widget) throws SAXException
	{
		// This currently works for InputFields only
		if (widget.isInputComponent())
		{
			XForm form=this.getXForm(widget);
			String value = form.getValue(widget.getFormElement().getElementName());
			widget.setFieldSize(value.length());
		}
		else Log.debug("setWidgetSize: "+widget.getFormElement().getElementName()+" is not an input component");
	}
	/**
	 *
	 * Saves the widget's value to the corresponding XForm instance
	 *
	 */
	public void saveWidgetValue(FormWidgetArea widget) throws SAXException
	{
		if (widget.isInputComponent())
		{
			XForm form=this.getXForm(widget);
			form.setValue(widget.getFormElement().getElementName(),widget.getComponentValue());
		}
		else Log.debug(widget.getFormElement().getElementName()+" is not an input component");
	}
	/**
	 *
	 * Saves all of the forms widget's values to the corresponding XForm instance
	 *
	 */
	public void saveWidgetValues(String form_id) throws SAXException
	{
		Vector vect = (Vector) by_form.get(form_id);
		if (vect==null) Log.error("saveWidgetValues: no such form as:"+form_id);
		else
		{
			Iterator i = vect.iterator();
			while (i.hasNext())
			{
				FormWidgetArea area = (FormWidgetArea)i.next();
				saveWidgetValue(area);
			}
		}
	}

	/**
	 * Find the XForm instance which corresponds to this widget. If form not
	 * found, throws an SAXException
	 * @return the XForm instance
	 *
	 */
	protected XForm getXForm(FormWidgetArea widget) throws SAXException
	{
			String form_id = widget.getFormId();
			XForm form=null;
			if (form_id==null) form = XSLFOMLFC.xforms.getDefaultForm();
			else form = XSLFOMLFC.xforms.getForm(widget.getFormId());
			if (form==null)
			{
				throw new SAXException("Form id '"+form_id+"' in form widget "+widget.getFormElement().getElementName()+" is invalid. (No such xform instance).");
			} else
			return form;
	}
	/**
	 * This handles the form button clicks
 	 *
	 */

	public void actionPerformed(ActionEvent ae)
	{
		String arg = ae.getActionCommand();
		Component source = (Component)ae.getSource();
		Log.debug("Action performed. arg="+arg+", source="+source+"formid:"+this.getFormId(source));
		XForm form = XSLFOMLFC.xforms.getForm(this.getFormId(source));
		if (form!=null)
		{
			try
			{
				this.saveWidgetValues(this.getFormId(source));
				form.submit();
			} catch (Exception e)
			{
				Log.error(e);
			}
		}
		else Log.error("No XForm instance with id '"+this.getFormId(source)+"' found.");
	}

}
