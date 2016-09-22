
import java.util.List;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;

import org.jdom2.*; 
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class XmlFile {

	private File srcFile; 
	private Element abstractElement;
	private String targetElementName; 
	private int targetElementIndex; 
	private Document document; 
	private String inDocumentTitle = " - "; 

	public XmlFile(String fileName){
		setSrcFile(new File(fileName)); 
		loadFile(this.srcFile); 
	}
	
	public XmlFile(File inputFile){ 
		setSrcFile(inputFile); 
		loadFile(inputFile); 
	}

	private void loadFile(File inputFile){
		setTargetElementName("Abstract");
		try{
			SAXBuilder saxBuilder = new SAXBuilder(); 
			document = saxBuilder.build(inputFile);		
			setAbstractElement(getAbstractElement(document));	
		}catch(JDOMException e){
			e.printStackTrace();
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
	}
	
	public String getTargetElementName() {
		return targetElementName;
	}

	public void setTargetElementName(String targetElementName) {
		this.targetElementName = targetElementName;
	}

	public String getInDocumentTitle(){
		return inDocumentTitle; 
	}
	
	//File chooser to help select a source file //TODO: Revisit
	public static File selectSrcFile(){
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
		int result = fileChooser.showOpenDialog(fileChooser);
		if (result == JFileChooser.APPROVE_OPTION) {
		    File selectedFile = fileChooser.getSelectedFile();
		    		//new File("resources/sampleInput.xml");	
		    return selectedFile; 
		}
		return null; 
	}
	
	public void setSrcFile(File srcFile){
		this.srcFile = srcFile; 
	}
	
	public File getsrcFile(){
		return this.srcFile; 
	}
	

	/**
	 * @return the abstractElement
	 */
	public Element getAbstractElement() {
		return abstractElement;
	}

	/**
	 * @param abstractElement the abstractElement to set
	 */
	public void setAbstractElement(Element abstractElement) {
		this.abstractElement = abstractElement;
	}

	private  Element getAbstractElement(Document doc){
		Element rootElement = doc.getRootElement();
		Element pubmedArticle = rootElement.getChild("PubmedArticle");
		Element medlineCitation = pubmedArticle.getChild("MedlineCitation");
		Element article = medlineCitation.getChild("Article");
		setTargetElementIndex(article);
		setInDocumentTitle(article);
		return article.getChild(targetElementName);  
	}
	
	public void setInDocumentTitle(Element parentElem){
		StringBuilder res = new StringBuilder(); 
		Abstract.getFullElementText(parentElem.getChild("ArticleTitle"), res);
		inDocumentTitle = res.toString(); 
	}

	private void setTargetElementIndex(Element parentElement){
		int i = 0; 
		List<Element> children = parentElement.getChildren();
		for(Element child : children){
			if(child.getName().equals(targetElementName))
				targetElementIndex = i; 
			i++; 
		}
	}
	
	public void saveAbstractElement(Element abstractElement){
		//Set the abstract Content
		updateAbstractElement(abstractElement);
		//Write back to XML file
		try{
			XMLOutputter xmlOutput = new XMLOutputter();
			xmlOutput.setFormat(Format.getRawFormat());
			xmlOutput.output(document, new FileWriter(srcFile));
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	private void updateAbstractElement(Element abstElement){
		Element rootElement = document.getRootElement();
		Element pubmedArticle = rootElement.getChild("PubmedArticle");
		Element medlineCitation = pubmedArticle.getChild("MedlineCitation");
		Element article = medlineCitation.getChild("Article");
		article.removeChild(targetElementName);
		article.addContent(targetElementIndex, abstElement);
	}
	


}
