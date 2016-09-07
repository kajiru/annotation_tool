import java.awt.Color;
import java.util.*;
import org.jdom2.*;

public class Abstract {

	private String title;  
	private Element XMLAbstract; //Contains XML tags
	private String plainAbstract;  
	private Map<String, String> sections;
	private AnnotationScheme annotationScheme;
	private Map<String, Color> annotationTypes; 
	private Map<String, LinkedHashSet<Annotation>> annotations;
	private PriorityQueue<Annotation> sortedAnnotations; 
	private Map<String, Map<String, ArrayList<String>>> schemeAnnotationElements; 

	public Abstract(Element abst, String title, AnnotationScheme annotationScheme){
		setTitle(title);
		setAnnotationScheme(annotationScheme); 
		setAnnotationTypes(annotationScheme.getAnnotationTypes());
		setXMLAbstract(abst);
		setPlainAbstract();
		Annotation.setStartSearchIndex(0);
		setAnnotations(); 
	}

	/**
	 * @return the XMLAbstract
	 */
	public Element getXMLAbstract() {
		return XMLAbstract;
	}

	/**
	 * @param XMLAbstract the Abstract, containing XML tags, to set
	 */
	public void setXMLAbstract(Element XMLAbstract) {
		this.XMLAbstract = XMLAbstract;
	}

	public void setAnnotationScheme( AnnotationScheme annotationScheme){
		this.annotationScheme =  annotationScheme;
		setSchemeAnnotationElements();
	}

	private void setAnnotationTypes(Map<String, Color> annotationTypes) {
		this.annotationTypes = annotationTypes;
	}
	
	public Map<String, Color> getAnnotationTypes(){
		return annotationTypes;
	}
	
	public Set<String> getAnnotationTypesSet() {
		Set<String> types = new HashSet<String>(); 
		for(String type: annotationTypes.keySet()){
			types.add(type);
		}
		return types;
	}

	public Map<String, Map<String, ArrayList<String>>> getSchemeAnnotationElements() {
		return schemeAnnotationElements;
	}

	public void setSchemeAnnotationElements() {
		this.schemeAnnotationElements = annotationScheme.getSchemeElements();
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
	
	public Annotation getAnnotation(int start, int end){
		for(String category: this.annotations.keySet()){
			LinkedHashSet<Annotation> annotationGroup = this.annotations.get(category);
			Iterator<Annotation> itr = annotationGroup.iterator();
	        while(itr.hasNext()){
	            Annotation curAnnotation = itr.next();
	            if(curAnnotation.getStart() == start && curAnnotation.getEnd() == end){
	            	return curAnnotation; 
	            }
	        }
		}
		return null; 
	}

	public void setAnnotations() {
		this.annotations = new HashMap<String, LinkedHashSet<Annotation>>(); 
		List<Element> abstractSections = this.XMLAbstract.getChildren();	 
		for (int i = 0; i < abstractSections.size(); i++){ 
			Element abstractSection = (Element) abstractSections.get(i); 
			if(abstractSection.getName().equals("AbstractText")){
				extractAnnotations(abstractSection); 
			}      		
		}	  


	} 

	private void extractAnnotations(Element abstractSection){
		@SuppressWarnings("rawtypes")
		List content = abstractSection.getContent(); 
		@SuppressWarnings("rawtypes")
		Iterator i = content.iterator(); 
		while (i.hasNext()){
			Object o = i.next(); 
			if(o instanceof Element){
				Element annotationElement = (Element)o;
				if(schemeAnnotationElements.containsKey(annotationElement.getName())){	
					assembleAnnotation(annotationElement, false);					
				} 
			}
		}
	}

	/**
	 * @param inline The type of annotation Element passed in. If True, means
	 *  the annotation is within another annotation
	 *  Note: An in-line annotations cannot have another annotation in it.  
	 * */
	private void assembleAnnotation(Element annotationElement, boolean inline){
		String typeName = annotationElement.getName(); 	
		Map<String, String> annotationAttributes = getAnnotationAttributes(annotationElement);	
		StringBuilder annotationText = new StringBuilder(""); 
		getFullAnnotationText(annotationElement, annotationText); 
		//TODO: Try not to pass plainAbstract every time
		Annotation annotation = new Annotation(typeName,annotationText.toString(), annotationAttributes, annotationTypes.get(typeName), plainAbstract);
		saveAnnotation(annotation, typeName);
		//Update the start location, within the abstract, of the next search for an annotation text
		if(!inline) Annotation.setStartSearchIndex(annotation.getEnd());	
	}
	
	/**
	 * Takes in an Annotation and gets all the text within the section 
	 * @param el : Annotation within an Abstract
	 *
	 */
	private void getFullAnnotationText(Element el, StringBuilder res){
		@SuppressWarnings("rawtypes")
		List content = el.getContent(); 
		@SuppressWarnings("rawtypes")
		Iterator i = content.iterator(); 
		while (i.hasNext()){
			Object o = i.next(); 
			if(o instanceof Element){
				Element inlineAnnotation = (Element)o;
				assembleAnnotation(inlineAnnotation, true); // Add In-line Annotation
				getFullElementText(inlineAnnotation, res);  
			}else{
				res.append(((Text)o).getText()); 
			}
		}
	}

	/**
	 *  Gets the attributes for a given annotation. The annotation is found in the XML abstract
	 *   as an element of an abstract section
	 * */
	private Map<String, String> getAnnotationAttributes(Element annotationElement){
		String annotationElementName = annotationElement.getName();
		Map<String, String> annotationAttributes = new HashMap<String, String>(); 
		Map<String, ArrayList<String>> schemeAttributes = schemeAnnotationElements.get(annotationElementName);
		if(schemeAttributes == null) return null; 
		List<Attribute> xmlAttributes = annotationElement.getAttributes();
		for(Attribute attr : xmlAttributes){
			if(schemeAttributes.containsKey(attr.getName())){
				annotationAttributes.put(attr.getName(), attr.getValue());
			}
		}
		return annotationAttributes; 
	}

	/**
	 * Adds an annotation to a HashMap containing all the Abstract's annotations
	 * */
	private void saveAnnotation(Annotation annotation, String annotationType){
		if(annotations.containsKey(annotationType)){
			annotations.get(annotationType).add(annotation);
		}else{
			LinkedHashSet<Annotation> componentSet = new LinkedHashSet<Annotation>();
			componentSet.add(annotation);
			annotations.put(annotationType, componentSet);
		}
	}

	public Map<String, LinkedHashSet<Annotation>> getAnnotations(){
		return annotations; 
	}

	public PriorityQueue<Annotation> getSortedAnnotations() {
		return sortedAnnotations;
	}

	private void sortAnnotations() {
		Comparator<Annotation> comparator = new AnnotationComparator();
		sortedAnnotations = new PriorityQueue<Annotation>(comparator);
		for(String key : annotations.keySet()){
			LinkedHashSet<Annotation> annotationGroup = annotations.get(key);
			for(Annotation annot: annotationGroup){
				sortedAnnotations.add(annot);
			}
		}

	}

	public void saveXMLAbstract(XmlFile curXMLFile){
		sortAnnotations();
		updateXMLAbstract();
		curXMLFile.saveAbstractElement(XMLAbstract);
	}

	/**
	 * Rebuilds XMLAbstract using the plain Abstract and sorted Annotations
	 * */
	private void updateXMLAbstract(){
			
		XMLAbstract = new Element("Abstract");	
		Element abstractText = new Element("AbstractText");

		int startPos = 0; 
		int prevStartPos = 0; 
		Element prevElement = null; 

		while(!sortedAnnotations.isEmpty()){	
			Annotation curAnnotation = sortedAnnotations.poll();	
			Element annotationElement = new Element(curAnnotation.getType());
			List<Attribute> annotationAttributes = getAnnotationAttributes(curAnnotation);			
			
			//TODO: Revisit in-line annotation logic...
			if(curAnnotation.getStart() < startPos){ //in-line Annotation
				//Remove Previously added in-line Element
				abstractText.removeContent(abstractText.getContentSize()-1);
				//Update positions
				prevStartPos = startPos - prevElement.getText().length(); 
				startPos = curAnnotation.getStart(); 
				//Set Attributes
				if(annotationAttributes != null) annotationElement.setAttributes(annotationAttributes);
				//Add three parts of the new element
				String preInlineElement = plainAbstract.substring(startPos, prevStartPos); 
				annotationElement.addContent(new Text(preInlineElement));
				startPos =  prevStartPos + prevElement.getText().length();
				annotationElement.addContent(prevElement);
				annotationElement.addContent(new Text(plainAbstract.substring(startPos, curAnnotation.getEnd())));
			}else{ 
				if(curAnnotation.getStart() != 0){
					abstractText.addContent(new Text(plainAbstract.substring(startPos, curAnnotation.getStart())));
				}
				if(annotationAttributes != null)annotationElement.setAttributes(annotationAttributes);	
				annotationElement.setText(curAnnotation.getText());
			}
			
			prevStartPos = startPos; 
			startPos = curAnnotation.getEnd();
			
			abstractText.addContent(annotationElement);
			prevElement = annotationElement; 
		}
		//Add any remaining text after last annotation
		abstractText.addContent(new Text(plainAbstract.substring(startPos)));

		XMLAbstract.addContent(abstractText);	
	}

	private List<Attribute> getAnnotationAttributes(Annotation annotation){
		Map<String, String> rawAttributes = annotation.getAttributes();
		if(rawAttributes == null) return null;
		List<Attribute> annotationAttributes = new ArrayList<Attribute>();
		for(String attrName : rawAttributes.keySet()){
			Attribute attr = new Attribute(attrName, rawAttributes.get(attrName));
			annotationAttributes.add(attr);
		}
		return annotationAttributes;
	}

	public void setPlainAbstract(){
		plainAbstract = "";
		extractAbstract();
	}

	public String getPlainAbstract(){
		return plainAbstract;
	}

	private  void extractAbstract(){
		List<Element> abstractTexts = this.XMLAbstract.getChildren();
		this.sections = new LinkedHashMap<String, String>();
		for (int i = 0; i < abstractTexts.size(); i++){ 
			Element abstractSection = (Element) abstractTexts.get(i); 
			if(abstractSection.getName().equals("AbstractText")){
				String abstractSectionName = abstractSection.getAttributeValue("Label");
				StringBuilder abstractSectionContent = new StringBuilder(""); 
				getFullElementText(abstractSection, abstractSectionContent);
				sections.put(abstractSectionName, abstractSectionContent.toString());
				this.plainAbstract += abstractSectionContent.toString(); 
			}      		
		}	  
	}

	/**
	 * Takes in an abstract section and gets all the text within the section 
	 * @param el : Abstract's child element
	 *
	 */
	public static void getFullElementText(Element el, StringBuilder res){
		@SuppressWarnings("rawtypes")
		List content = el.getContent(); 
		@SuppressWarnings("rawtypes")
		Iterator i = content.iterator(); 
		while (i.hasNext()){
			Object o = i.next(); 
			if(o instanceof Element){
				Element component = (Element)o;
				getFullElementText(component, res);  
			}else{
				res.append(((Text)o).getText()); 
			}
		}
	}


	public void addAnnotation(String typeName, String text, Map<String, String> attributes, int start, int end){
		Annotation annotation = new Annotation(typeName,text,start,end, attributes,annotationTypes.get(typeName));
		saveAnnotation(annotation, typeName);
	}
	
	public boolean deleteAnnotation(Annotation annotation){
		LinkedHashSet<Annotation> annotationGroup = annotations.get(annotation.getType());
		Iterator<Annotation> itr = annotationGroup.iterator();
		while(itr.hasNext()){
			Annotation target = itr.next(); 
			if(annotation.equals(target)){
				itr.remove();
				return true; 
			}
		}
		return false; 
	}
	
	public void editAnnotation(Annotation annotation){
		
	}








}
