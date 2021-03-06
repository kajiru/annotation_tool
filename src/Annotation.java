import java.awt.Color;
import java.util.*;

public class Annotation{

	private String type; 
	private String text;
	private int start; 
	private int end; 
	private int id; 
	private String plainAbstract; 
	private Map<String, String> attributes;
	private Color color; 

	private static int startSearchIndex = 0; //Keeps track of the position, in the abstract, to start searching for an annotation text.

	public Annotation(String type, String text, Map<String, String> attributes, Color color, String plainAbstract){
		this.setType(type); 
		this.setText(text);
		this.setAttributes(attributes);
		this.setPlainAbstract(plainAbstract);
		this.setStart(); 
		this.setEnd(); 
		this.setColor(color); 
	}

	//Adding Annotation from GUI
	public Annotation(String type, String text, int start, int end, Map<String, String> attributes, Color color){
		this.setType(type); 
		this.setText(text);
		this.setAttributes(attributes);
		this.start = start; 
		this.end = end;
		this.setColor(color); 
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
		start = plainAbstract.indexOf(text, startSearchIndex);
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

	public void setColor(Color color){
		this.color = color; 
	}

	public Color getColor(){
		return color;
	}

	public static int getStartSearchIndex(){
		return startSearchIndex;
	}

	public static void setStartSearchIndex(int i){
		startSearchIndex = i;
	}

	public String toString(){
		String res = "";
		res += "Type & Text: ";
		res += getType();
		res += " : "; 
		res += getText();
		if(attributes != null){
			res += "\nAttributes: {";
			for(String key : attributes.keySet()){
				res += key; 
				res += "=";
				res += attributes.get(key);
				res += " | ";
			}

			res += "}";
		}
		res += "\nIndex: "; 
		res += getStart(); 
		res += "->";
		res += getEnd();
		res += "\nColor: ";
		res += getColor();
		res += "\n";

		return res; 
	}



}
