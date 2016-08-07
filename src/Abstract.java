import java.util.*;
import org.jdom2.*;

public class Abstract {

	private String title;  
	private Set<Annotation> annotations;
	private Element xmlAbstract; //Contains XML tags
	private String plainAbstract;  //Does not contain any additional info
	private Map<String, String> sections;
	
	private AnnotationScheme annotationScheme = new AnnotationScheme("defaultScheme.xml");
  	
	public Abstract(Element abst, String title){
		setxmlAbstract(abst);
		setTitle(title);
		setPlainAbstract(); 
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
		 extractAbstract();
		 return sections;
	}

	public void setAnnotations() {	
		AnnotationScheme annotationScheme = new AnnotationScheme("defaultScheme.xml");
		Map<String, ArrayList<String>> annotationElements = annotationScheme.getSchemeElements(); 
		
		List<Element> abstractTexts = this.xmlAbstract.getChildren();	
		if (abstractTexts.size() == 0){ 
			//TODO : Account for non-divided abstracts! 
		}else{ 
			for (int i = 0; i < abstractTexts.size(); i++){ 
				Element abstractSection = (Element) abstractTexts.get(i); 
				if(abstractSection.getName().equals("AbstractText")){
					extractAnnotation(abstractSection,annotationElements); 
				}      		
			}	  
		}
		
	} 
	
	
	private void extractAnnotation(Element abstractText, Map<String, ArrayList<String>> annotationElements){
		List content = abstractText.getContent(); 
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
					
					Annotation annotation = new Annotation(componentType,componentText.toString(), annotationAttributes, plainAbstract);
					System.out.println(annotation.getType() + " : " + annotation.getText());
					System.out.println(annotation.getAttributes());
					System.out.println(annotation.getStart() + " -> " + annotation.getEnd()); //TODO
					System.out.println("----");	
				} 
			}
			
		}
		
		
		//this.annotations = annotations; 
	}
	
	public Set<Annotation> getAnnotations(){
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
		if (abstractTexts.size() == 0){ //TODO : Revisit how An Abstract can be added without sections is added
			sections.put("Abstract", this.xmlAbstract.getText());
			this.plainAbstract = this.xmlAbstract.getText(); 
		}else{ 
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
	}
	
	/**
	 * Takes in a child of the abstract element and gets all the text within the child 
	 * @param el : Abstract's child element
	 *
	 */
	private void getFullSectionText(Element el, StringBuilder res){
		List content = el.getContent(); 
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
	
	
	
	
	
	
	
}
