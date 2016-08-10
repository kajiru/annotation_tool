import java.io.File;
import java.io.IOException;
import java.util.*;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class AnnotationScheme {
	
	private String sourceFilename; 
	private int nelements;
	private Map<String, ArrayList<String>> schemeElements; 
	
	public AnnotationScheme(String sourceFilename){
		setSourceFilename(sourceFilename);
		setSchemeElements();
		this.setNelements(0);   
	}
	
	public void setSourceFilename(String filename){
		this.sourceFilename = filename; 
	}
	
	public String getSourceFilename(){
		return sourceFilename; 
	}
	
	public void setSchemeElements(){
		this.schemeElements = new HashMap<String, ArrayList<String>>(); 
		try{
			//TODO: make file path absolute
			File inputFile = new File("resources/"+ this.sourceFilename);
			SAXBuilder saxBuilder = new SAXBuilder(); 
			Document document = saxBuilder.build(inputFile);
			extractSchemeElements(document);
		}catch(JDOMException e){
			e.printStackTrace();
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
	}
	
	private void extractSchemeElements(Document doc){ 
		Element rootElement = doc.getRootElement();
		List<Element> children = rootElement.getChildren();
		for(Element el : children){
			String elementTitle = (el.getAttributeValue("title"));
			List<Element> elementAttributes = el.getChildren();
			ArrayList<String> attributes = new ArrayList<String>(); 
			for(Element attr : elementAttributes){
				attributes.add(attr.getValue());
			}
		    this.schemeElements.put(elementTitle, attributes);
		    setNelements(this.getNelements() + 1); 
		}
	}
	
	/**
	 * @return the schemeElements
	 */
	public Map<String, ArrayList<String>> getSchemeElements(){
		return this.schemeElements; 
	}

	/**
	 * @return the nelements
	 */
	public int getNelements() {
		return nelements;
	}

	/**
	 * @param nelements the nelements to set
	 */
	public void setNelements(int nelements) {
		this.nelements = nelements;
	}
	
}
