import java.io.File;
import java.io.IOException;
import java.util.*; 

import org.jdom2.*; 
import org.jdom2.input.SAXBuilder;

public class XmlFile {
	
	private String sourceFileName;
	private Element abstractElement; 
	
	public XmlFile(String fileName){
		this.sourceFileName = fileName; 
		loadFile(); 
	}
	
	private void loadFile(){
		try{
			//TODO : Change the path below to an absolute path
			File inputFile = new File("resources/"+ this.sourceFileName);
			SAXBuilder saxBuilder = new SAXBuilder(); 
			Document document = saxBuilder.build(inputFile);		
			Element targetAbstract = getAbstractElement(document);
			setAbstractElement(targetAbstract);	
		}catch(JDOMException e){
			e.printStackTrace();
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
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

	private static Element getAbstractElement(Document doc){
		Element rootElement = doc.getRootElement();
		Element pubmedArticle = rootElement.getChild("PubmedArticle");
		Element medlineCitation = pubmedArticle.getChild("MedlineCitation");
		Element article = medlineCitation.getChild("Article"); 
		return article.getChild("Abstract");  
	}
		

}
