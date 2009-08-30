package fault.induccing.inputs.isolation;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class MySaxApp extends DefaultHandler {
	public MySaxApp() {
		super();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		XMLReader xr;
		try {
			xr = XMLReaderFactory.createXMLReader();
			MySaxApp handler = new MySaxApp();
			xr.setContentHandler(handler);
			xr.setErrorHandler(handler);

			// Parse each file provided on the
			// command line.
			for (int i = 0; i < args.length; i++) {
				FileReader r = new FileReader(args[i]);
				xr.parse(new InputSource(r));
				
			}
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
