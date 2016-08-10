import java.awt.Color;
import java.util.*;
import org.jdom2.*;

public class Abstract {

	private String title;  
	private Map<String, LinkedHashSet<Annotation>> annotations;
	private Element xmlAbstract; //Contains XML tags
	private String plainAbstract;  
	private Map<String, String> sections;
	private AnnotationScheme annotationScheme;
	private Map<String, Color> annotationTypesColors; 

	public Abstract(Element abst, String title){
		setTitle(title);
		setAnnotationScheme("defaultScheme.xml");
		setxmlAbstract(abst);
		setPlainAbstract();
		Annotation.setStartSearchIndex(0);
		setAnnotations(); 
	}

	/**
	 * @return the xmlAbstract
	 */
	public Element getxmlAbstract() {
		return xmlAbstract;
	}

	/**
	 * @param xmlAbstract the Abstract, containing XML tags, to set
	 */
	public void setxmlAbstract(Element xmlAbstract) {
		this.xmlAbstract = xmlAbstract;
	}
	
	public void setAnnotationScheme(String sourceFilename){
		annotationScheme = new AnnotationScheme(sourceFilename);
	}
	
	private void setTitle(String title){
		this.title = title; 
	}

	public String getTitle(){
		return title;
	}

	/**
	 * @return the sections
	 */
	public Map<String, String> getSections() {
		return sections;
	}

	public void setAnnotations() {
		this.annotations = new HashMap<String, LinkedHashSet<Annotation>>();
		this.annotationTypesColors = new HashMap<String, Color>();
		Map<String, ArrayList<String>> annotationElements = annotationScheme.getSchemeElements(); 
		List<Element> abstractTexts = this.xmlAbstract.getChildren();	 
			for (int i = 0; i < abstractTexts.size(); i++){ 
				Element abstractSection = (Element) abstractTexts.get(i); 
				if(abstractSection.getName().equals("AbstractText")){
					extractAnnotations(abstractSection,annotationElements); 
				}      		
			}	  
		

	} 

	private void extractAnnotations(Element abstractText, Map<String, ArrayList<String>> annotationElements){
		@SuppressWarnings("rawtypes")
		List content = abstractText.getContent(); 
		@SuppressWarnings("rawtypes")
		Iterator i = content.iterator(); 

		while (i.hasNext()){
			Object o = i.next(); 
			if(o instanceof Element){
				Element component = (Element)o;
				String componentType = component.getName(); 
				if(annotationElements.containsKey(componentType)){
					Map<String, String> annotationAttributes = new HashMap<String, String>(); 
					ArrayList<String> schemeAttributes = annotationElements.get(componentType);
					List<Attribute> attributes = component.getAttributes();
					for(Attribute attr : attributes ){
						if(schemeAttributes.contains(attr.getName())){
							annotationAttributes.put(attr.getName(), attr.getValue());
						}
					}
					StringBuilder componentText = new StringBuilder(""); 
					getFullSectionText(component, componentText);
					
					//Get annotation Color
					Color annotationColor;  
					if(annotationTypesColors.containsKey(componentType)){
						annotationColor = annotationTypesColors.get(componentType);
					}else{
						Random rand = new Random();
						annotationColor = new Color(rand.nextInt(254),rand.nextInt(254),rand.nextInt(254));
						annotationTypesColors.put(componentType, annotationColor);
					}
					
					//TODO: Try not to pass plainAbstract every time
					Annotation annotation = new Annotation(componentType,componentText.toString(), annotationAttributes, annotationColor, plainAbstract);
					if(annotations.containsKey(componentType)){
						annotations.get(componentType).add(annotation);
					}else{
						LinkedHashSet<Annotation> componentSet = new LinkedHashSet<Annotation>();
						componentSet.add(annotation);
						annotations.put(componentType, componentSet);
					}
					
					//Update the start location, within the abstract, of the next search for an annotation text
					Annotation.setStartSearchIndex(annotation.getEnd());					
				} 
			}

		}
	}

	public Map<String, LinkedHashSet<Annotation>> getAnnotations(){
		return annotations; 
	}

	public void setPlainAbstract(){
		plainAbstract = "";
		extractAbstract();
	}

	public String getPlainAbstract(){
		return plainAbstract;
	}

	private  void extractAbstract(){
		List<Element> abstractTexts = this.xmlAbstract.getChildren();
		this.sections = new LinkedHashMap<String, String>();
		for (int i = 0; i < abstractTexts.size(); i++){ 
			Element abstractSection = (Element) abstractTexts.get(i); 
			if(abstractSection.getName().equals("AbstractText")){
				String abstractSectionName = abstractSection.getAttributeValue("Label");
				StringBuilder abstractSectionContent = new StringBuilder(""); 
				getFullSectionText(abstractSection, abstractSectionContent);
				sections.put(abstractSectionName, abstractSectionContent.toString());
				this.plainAbstract += abstractSectionContent.toString(); 
			}      		
		}	  

	}

	/**
	 * Takes in a child of the abstract element and gets all the text within 
	 * @param el : Abstract's child element
	 *
	 */
	private void getFullSectionText(Element el, StringBuilder res){
		@SuppressWarnings("rawtypes")
		List content = el.getContent(); 
		@SuppressWarnings("rawtypes")
		Iterator i = content.iterator(); 
		while (i.hasNext()){
			Object o = i.next(); 
			if(o instanceof Element){
				Element component = (Element)o;
				getFullSectionText(component, res);  
			}else{
				res.append(((Text)o).getText()); 
			}
		}
	}


	public Map<String, Color> getAnnotationTypesColors() {
		return annotationTypesColors;
	}

	public void setAnnotationTypesColors(Map<String, Color> annotationTypesColors) {
		this.annotationTypesColors = annotationTypesColors;
	}







}
