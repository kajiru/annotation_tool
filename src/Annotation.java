import java.util.*;

public class Annotation{

	private String type; 
	private String text;
	private int start; 
	private int end; 
	private int id; 
	private String plainAbstract; 
	private Map<String, String> attributes;
	private String color; //Change to color Object

	private static int indexSearchKey = 0; //Keeps track of the position right after the last added annotation.
				
	public Annotation(String type, String text, Map<String, String> attributes, String plainAbstract){
		this.setType(type); 
		this.setText(text);
		this.setAttributes(attributes);
		this.setPlainAbstract(plainAbstract);
		this.setStart(); 
		this.setEnd(); 
		this.setColor(""); 
		this.setIndexSearchKey();
	}
	
	public void setText(String text){
		this.text = text;
	}
	
	public String getText(){
		return text;
	}
	
	public void setAttributes(Map<String, String> attributes){
		this.attributes = attributes;
	}
	
	public Map<String, String> getAttributes(){
		return attributes;
	}
	
	/**
	 * @return the plainAbstract
	 */
	public String getPlainAbstract() {
		return plainAbstract;
	}

	/**
	 * @param plainAbstract the plainAbstract to set
	 */
	public void setPlainAbstract(String plainAbstract) {
		this.plainAbstract = plainAbstract;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the start
	 */
	public int getStart() {
		return start;
	}

	/**
	 * @param start the start to set
	 */
	public void setStart() {
		start = plainAbstract.indexOf(text, indexSearchKey);
	}

	/**
	 * @return the end
	 */
	public int getEnd() {
		return end;
	}

	/**
	 * @param end the end to set
	 */
	public void setEnd() {
		end = start + text.length();
	}
	
	public void setColor(String color){
		this.color = color; 
	}
	
	public static int getIndexSearchKey(){
		return indexSearchKey;
	}
	
	public void setIndexSearchKey(){
		indexSearchKey = end;
	}
	
	
		
}
