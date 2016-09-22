
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
* The AnnotationScheme class uses a user-defined XML to define how 
* different documents are annotated.  
* 
* @author  Jasper Kajiru
*/


public class AnnotationScheme {
	
	private File srcFile; 
	private Map<String, Map<String, ArrayList<String>>> schemeElements; 
	private Map<String, Color> annotationTypes; 
	private String schemeElementTitleAttribute= "title"; 
	private String attributeOptionNameAttribute = "name"; 

	public AnnotationScheme(File srcFile){
		setSrcFile(srcFile);
		setSchemeElements();
		setAnnotationTypes(); 
	}
	
	/**
	 * Sets the XML source file that will be used to generate an Annotation Scheme 
	 * @param srcFile
	 */
	public void setSrcFile(File srcFile){
		this.srcFile = srcFile; 
	}

	/**
	 * Get the XML source file
	 * @return File the XML source file
	 */
	public File getSrcFile(){
		return srcFile; 
	}
	
	/**
	 * Colors used to highlight different annotation Types 
	 * 
	 */
	private void setAnnotationTypes() {
		annotationTypes = new HashMap<String, Color>();
		annotationTypes.put("condition", new Color(203,117,182));
		annotationTypes.put("eventRate", new Color(196,118,35));
		annotationTypes.put("lost", new Color(207,178,10));
		annotationTypes.put("short", new Color(87,123,240));
		annotationTypes.put("threshold", new Color(224,224,108));
		annotationTypes.put("time", new Color(217,120,9));
		annotationTypes.put("gs", new Color(102,255,51));
		annotationTypes.put("outcome", new Color(255,204,51));
		annotationTypes.put("age", new Color(189,37,115));
		annotationTypes.put("group", new Color(56,208,239));
		annotationTypes.put("on", new Color(0,253,207));
		annotationTypes.put("population", new Color(244,128,108));
	}
	
	/**
	 * 
	 * @return Map
	 */
	public Map<String, Color> getAnnotationTypes(){
		return annotationTypes; 
	}

	/**
	 * Set up a Map containing all targeted elements used in the annotation scheme
	 */
	public void setSchemeElements(){
		this.schemeElements = new HashMap<String, Map<String, ArrayList<String>>>(); 
		try{
			SAXBuilder saxBuilder = new SAXBuilder(); 
			Document document = saxBuilder.build(srcFile);
			extractSchemeElements(document);
		}catch(JDOMException e){
			e.printStackTrace();
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
	}

	/**
	 * Get the scheme elements form the XML source file
	 * @param doc
	 */
	private void extractSchemeElements(Document doc){ 
		Element rootElement = doc.getRootElement();
		List<Element> children = rootElement.getChildren();
		for(Element el : children){
			String elementTitle = (el.getAttributeValue(schemeElementTitleAttribute));
			Map<String, ArrayList<String>> attributes = getElementAttributes(el.getChildren());
			this.schemeElements.put(elementTitle, attributes);
		}
	}

	/**
	 * Get the attributes associated to every given element
	 * @param xmlElementAttributes
	 * @return Map<String, ArrayList<String>> element attributes
	 */
	private Map<String, ArrayList<String>> getElementAttributes(List<Element> xmlElementAttributes){
		if(xmlElementAttributes.isEmpty())return null; 
		Map<String, ArrayList<String>> elementAttributes = new HashMap<String, ArrayList<String>>();	
		for(Element attr : xmlElementAttributes){
			ArrayList<String> attributeOptions = getElementAttributeOptions(attr.getChildren());
			elementAttributes.put(attr.getAttributeValue(attributeOptionNameAttribute), attributeOptions);
		}
		return elementAttributes; 
	}
	
	/**
	 * Get a List of strings containing all options
	 * @param xmlOptions options as Elements
	 * @return attributeOptions options as Strings
	 */
	private ArrayList<String> getElementAttributeOptions(List<Element> xmlOptions){
		if(xmlOptions.isEmpty()) return null; 
		ArrayList<String> attributeOptions = new ArrayList<String>(); 
		for(Element option: xmlOptions){
			attributeOptions.add(option.getAttributeValue(attributeOptionNameAttribute));
		}
		return attributeOptions; 
	}

	/**
	 * Gets all the schemeElements in an annotationScheme
	 * @return schemeElements
	 */
	public Map<String, Map<String, ArrayList<String>>> getSchemeElements(){
		return this.schemeElements; 
	}

}
