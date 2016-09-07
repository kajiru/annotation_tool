import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.*;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class AnnotationScheme {

	private File srcFile; 
	private Map<String, Map<String, ArrayList<String>>> schemeElements; 
	private Map<String, Color> annotationTypes; 

	public AnnotationScheme(File srcFile){
		setSrcFile(srcFile);
		setSchemeElements();
		setAnnotationTypes(); 
	}

	public void setSrcFile(File srcFile){
		this.srcFile = srcFile; 
	}

	public File getSrcFile(){
		return srcFile; 
	}
	
	private void setAnnotationTypes() {
		annotationTypes = new HashMap<String, Color>();
		for(String annotationType: schemeElements.keySet()){
			Random rand = new Random();
			Color annotationColor = new Color(rand.nextInt(254),rand.nextInt(254),rand.nextInt(254));
			annotationTypes.put(annotationType, annotationColor);
		}
	}
	
	public Map<String, Color> getAnnotationTypes(){
		return annotationTypes; 
	}

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

	private void extractSchemeElements(Document doc){ 
		Element rootElement = doc.getRootElement();
		List<Element> children = rootElement.getChildren();
		for(Element el : children){
			String elementTitle = (el.getAttributeValue("title"));
			Map<String, ArrayList<String>> attributes = getElementAttributes(el.getChildren());
			this.schemeElements.put(elementTitle, attributes);
		}
	}

	private Map<String, ArrayList<String>> getElementAttributes(List<Element> xmlElementAttributes){
		if(xmlElementAttributes.isEmpty())return null; 
		Map<String, ArrayList<String>> elementAttributes = new HashMap<String, ArrayList<String>>();	
		for(Element attr : xmlElementAttributes){
			ArrayList<String> attributeOptions = getElementAttributeOptions(attr.getChildren());
			elementAttributes.put(attr.getAttributeValue("name"), attributeOptions);
		}
		return elementAttributes; 
	}

	private ArrayList<String> getElementAttributeOptions(List<Element> xmlOptions){
		if(xmlOptions.isEmpty()) return null; 
		ArrayList<String> attributeOptions = new ArrayList<String>(); 
		for(Element option: xmlOptions){
			attributeOptions.add(option.getAttributeValue("name"));
		}
		return attributeOptions; 
	}

	public Map<String, Map<String, ArrayList<String>>> getSchemeElements(){
		return this.schemeElements; 
	}

}
