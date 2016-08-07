import java.util.*; 
import org.jdom2.*; 

public class AnnotationTool {

	public static void main(String[] args) {
		XmlFile xmlFile = new XmlFile("SampleInput.xml");
		
		Element targetAbstract = xmlFile.getAbstractElement(); 
		
		String title = ""; //TODO : Get exact Title
		Abstract abst = new Abstract(targetAbstract,title);
	    abst.setAnnotations();
		System.out.println(abst.getPlainAbstract());
		
		
		//AnnotationScheme items = new AnnotationScheme("defaultScheme.xml");
		//System.out.println(items.getSchemeElements());

	}

}
