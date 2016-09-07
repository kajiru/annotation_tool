
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.Highlight;
import javax.swing.text.Highlighter.HighlightPainter;

import org.jdom2.Element;


public class Annotator implements AnnotatorConstants  {

	BorderLayout mainLayout = new BorderLayout();
	JMenuBar menuBar; 
	JMenu menu, submenu; 
	JMenuItem menuItem; 
	JRadioButtonMenuItem rbMenuItem;
	JCheckBoxMenuItem cbMenuItem;

	private AnnotationScheme annotationScheme = null; 
	private Abstract currentAbstract; 
	private XmlFile currentXMLFile; 
	private Highlighter abstractTextAreahighlighter; 
	private boolean getHighlight = true; //true = annotations are highlighted in the abstract textArea
	private ArrayList<Highlight> abstractHighlightTags;
	private String mainAnnotationFrameTitle;
	private boolean isEdited = false; //Determines whether a document has been edited or not

	private Set<String> highlightedAnnotationTypes; 
	
	//Main GUI components
	private AnnotationFrameComponents mainAnnotationFrameComponents; 
	private JFrame mainAnnotationFrame;

	//Five main sections within the BoarderLayout
	private JPanel titlePane;
	private AnnotationTypesGroup annotationTypesGroup = null; //Annotations Types List
	private JTextArea abstractTextArea;
	private JPanel rightPane;
	private JPanel footerPane;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {	
		new Annotator(); 
	}

	/**
	 * Initialize  the application by adding GUI components.
	 */
	public Annotator() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					addMainAnnotationFrame(); 	 
					addMainAnnotationFrameComponents();
					addMouseListeners();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});


	}

	private void addMainAnnotationFrame(){
		mainAnnotationFrame = new JFrame();
		mainAnnotationFrame.setLayout(mainLayout);
		mainLayout.setHgap(MAIN_LAYOUT_HORIZONTAL_GAP);
		mainAnnotationFrame.setTitle("Annotation Tool");
		mainAnnotationFrame.setPreferredSize(MAIN_FRAME_PREFERRED_SIZE);
		//mainAnnotationFrame.setLocationRelativeTo(null);     
		mainAnnotationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainAnnotationFrame.setJMenuBar(new MainMenuBar(this)); 
		mainAnnotationFrame.addComponentListener(new MainAnnotationFrameListener(this));
		mainAnnotationFrame.pack();
		mainAnnotationFrame.setVisible(true);

	}

	/**
	 * Add GUI components to the main Annotation Frame.
	 */
	private void addMainAnnotationFrameComponents(){
		titlePane = new JPanel();
		mainAnnotationFrame.add(titlePane, BorderLayout.PAGE_START);

		mainAnnotationFrameComponents = new AnnotationFrameComponents(mainAnnotationFrame);

		annotationTypesGroup = mainAnnotationFrameComponents.createAnnotationTypesListPane(this);
		mainAnnotationFrame.add(annotationTypesGroup, BorderLayout.LINE_START);

		abstractTextArea = mainAnnotationFrameComponents.createMainAbstractTextArea();
		mainAnnotationFrame.add(abstractTextArea, BorderLayout.CENTER);

		rightPane = new JPanel();
		mainAnnotationFrame.add(rightPane, BorderLayout.LINE_END);

		footerPane = new JPanel();
		footerPane.add(new JLabel("Footer"));
		mainAnnotationFrame.add(footerPane, BorderLayout.PAGE_END);

	}

	private void addMouseListeners(){
		abstractTextArea.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent e) {
				Highlight highlight  = getHighlight(); 
				if((e.getClickCount() == 2 && highlight != null) || highlight != null){
					displayAnnotationDetails(highlight);
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if(abstractTextArea.getSelectedText() != null){
					addAnnotationDetails(abstractTextArea.getSelectedText(), abstractTextArea.getSelectionStart(),  abstractTextArea.getSelectionEnd());
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				abstractTextArea.setCursor(new Cursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
			}

		});	

	}


	/**
	 * Returns the Highlight in which a selected text belongs, null otherwise
	 * */
	private Highlight getHighlight(){
		int selectionStart = abstractTextArea.getSelectionStart();
		int selectionEnd = abstractTextArea.getSelectionEnd();		
		for(int i = 0; i < abstractHighlightTags.size(); i++){
			Highlight cur = abstractHighlightTags.get(i);
			if((selectionStart > cur.getStartOffset()) && (selectionEnd  < cur.getEndOffset())){
				return cur; 
			}
		}
		return null; 
	}

	//TODO: Displaying details of an in-line Annotation when the outlining one is deleted --Seems buggy
	private void displayAnnotationDetails(Highlight highlight){
		highlight.getStartOffset();
		Annotation selectedAnnotation = currentAbstract.getAnnotation(highlight.getStartOffset(), highlight.getEndOffset());
		String[] options = {"Cancel","Edit", "Delete"}; 
		String msg = "Type: " + selectedAnnotation.getType() + "\n"; 
		Map<String, String> attributes = selectedAnnotation.getAttributes(); 
		if(attributes != null){
			for(String attrName: attributes.keySet()){
				msg += attrName + ": " + attributes.get(attrName) + "\n";
			}
		}else{
			msg += "--No Attributes--"; 
		}

		int n = JOptionPane.showOptionDialog(mainAnnotationFrame,
				msg, 
				"Annotation Details", 
				JOptionPane.YES_NO_CANCEL_OPTION, 
				JOptionPane.QUESTION_MESSAGE, 
				null, 
				options, 
				options[0]);

		if(n == 1){ //Edit
			editAnnotation(selectedAnnotation, highlight);
		}else if(n == 2){ //Delete
			deleteAnnotation(selectedAnnotation, highlight, false); 
		}
	}

	private void markAsEdited(String title){	
		if(!isEdited)mainAnnotationFrame.setTitle(title);
		isEdited = true; 
	}

	private void editAnnotation(Annotation annotation, Highlight highlight){
		//TODO: Write independent, more optimized code for editing 
		deleteAnnotation(annotation, highlight, true);
		addAnnotationDetails(annotation.getText(), annotation.getStart(), annotation.getEnd());
		markAsEdited("*" + mainAnnotationFrame.getTitle()); 	
	}

	private void deleteAnnotation(Annotation annotation, Highlight highlight, boolean isEdit){
		int n = 0; 
		if(!isEdit){
			n = JOptionPane.showConfirmDialog(mainAnnotationFrame, 
					"Are you sure?", 
					"Delete Annotation", 
					JOptionPane.YES_NO_OPTION);
		}

		if(n == 0){
			if(currentAbstract.deleteAnnotation(annotation)){
				abstractTextAreahighlighter.removeHighlight(highlight);
			}	
		}
		markAsEdited(mainAnnotationFrame.getTitle() + "*");
	}

	private void addAnnotationDetails(String selectedText, int selectedTextStartIndex, int selectedTextEndIndex){
		Map<String, Map<String, ArrayList<String>>> schemeAnnotationElements = currentAbstract.getSchemeAnnotationElements();
		Vector<String> annotationTypes = new Vector<String>() ; 	
		for(String type : schemeAnnotationElements.keySet()){
			annotationTypes.add(type);
		}
		JPanel annotationDetailsPanel = new JPanel();
		annotationDetailsPanel.setLayout(new BoxLayout(annotationDetailsPanel, BoxLayout.Y_AXIS));
		annotationDetailsPanel.add(new JLabel("Selected Text: "+selectedText + "\n"));

		JComboBox<String> annotationTypeOptions = new JComboBox<String>(annotationTypes);
		annotationDetailsPanel.add(annotationTypeOptions);

		int option =  JOptionPane.showConfirmDialog(mainAnnotationFrame,
				annotationDetailsPanel, 
				"Add Annotation", 
				JOptionPane.OK_CANCEL_OPTION, 
				JOptionPane.PLAIN_MESSAGE, 
				null);
		String typeName ="";
		if(option == JOptionPane.OK_OPTION){
			typeName  = (String)annotationTypeOptions.getSelectedItem();
			Map<String, String> attributes = getAnnotationAttributes(schemeAnnotationElements.get(typeName));
			currentAbstract.addAnnotation(typeName, selectedText, attributes,selectedTextStartIndex,selectedTextEndIndex);
		}

		HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(currentAbstract.getAnnotationTypes().get(typeName));
		try {
			Highlight tag = (Highlight) abstractTextAreahighlighter.addHighlight( selectedTextStartIndex, selectedTextEndIndex, painter);
			abstractHighlightTags.add(tag);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		markAsEdited("*" + mainAnnotationFrame.getTitle() ); 
	}


	private Map<String, String> getAnnotationAttributes(Map<String, ArrayList<String>> schemeAnnotationAttributes){
		if(schemeAnnotationAttributes == null)return null; 
		Map<String, String> attributes = new HashMap<String, String>(); 
		for(String attrName : schemeAnnotationAttributes.keySet()){
			String attrValue = ""; 
			ArrayList<String> schemeAnnotationAttributeOptions = schemeAnnotationAttributes.get(attrName);
			if(schemeAnnotationAttributeOptions != null){
				String[] options = schemeAnnotationAttributeOptions.toArray(new String[schemeAnnotationAttributeOptions.size()]);
				attrValue = (String)JOptionPane.showInputDialog(
						mainAnnotationFrame,
						attrName + ":\n",
						"Customized Dialog",
						JOptionPane.PLAIN_MESSAGE,
						null,
						options,
						schemeAnnotationAttributeOptions.get(0)
						);
			}else{
				attrValue = JOptionPane.showInputDialog(attrName + ":");
			}
			attributes.put(attrName, attrValue);
		}	
		return attributes; 
	}


	public void loadSchemeFile(File schemeSrcFile){
		annotationScheme = new AnnotationScheme(schemeSrcFile); 
	}

	public void loadAbstract(){
		if(annotationScheme == null)
			loadSchemeFile(new File("resources/defaultScheme.xml"));

		File abstractSrcFile = XmlFile.selectSrcFile(); 
		currentXMLFile = new XmlFile(abstractSrcFile);
		Element targetAbstract = currentXMLFile.getAbstractElement(); 
		String title = currentXMLFile.getInDocumentTitle(); 
		titlePane.add(new JLabel(title));
		currentAbstract = new Abstract(targetAbstract,title, annotationScheme);
		Map<String, String> curAbstractSections = currentAbstract.getSections();
		abstractTextArea.setText("");
		for(String sectionName : curAbstractSections.keySet()){
			abstractTextArea.append(curAbstractSections.get(sectionName));
			//abstractTextArea.append("\n");
		}
		setMainTitle(abstractSrcFile); 
		setHighlightedAnnotationTypes();
		highlightAnnotations(highlightedAnnotationTypes);
		addAnnotationTypesList(); 
	}
	
	private void setHighlightedAnnotationTypes(){
		highlightedAnnotationTypes = new HashSet<String>(); 
		highlightedAnnotationTypes = currentAbstract.getAnnotationTypesSet();
	}

	private void setMainTitle(File loadedFile){
		mainAnnotationFrameTitle = "Annotation Tool - "; 
		mainAnnotationFrameTitle += loadedFile.getName();  
		mainAnnotationFrame.setTitle(mainAnnotationFrameTitle);
	}


	private void highlightAnnotations(Set<String> highlightedAnnotationTypes){
		abstractHighlightTags = new ArrayList<Highlight>();
		try{
			abstractTextAreahighlighter = abstractTextArea.getHighlighter();
			Map<String, LinkedHashSet<Annotation>> annotations = currentAbstract.getAnnotations();
			for(String key : annotations.keySet()){ 
				if(highlightedAnnotationTypes.contains(key)){
					for(Annotation annotation : annotations.get(key)){
						HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(annotation.getColor());
						Highlight tag = (Highlight) abstractTextAreahighlighter.addHighlight(annotation.getStart(), annotation.getEnd(), painter);
						abstractHighlightTags.add(tag);
					}	
				}				
			}
		}catch(BadLocationException ex){
			ex.printStackTrace();
		}
	}

	public void updateHighlightedAnnotations(Set<String> highlightedAnnotationTypes) {
		abstractTextAreahighlighter.removeAllHighlights();
		highlightAnnotations(highlightedAnnotationTypes); 
	}


	public ArrayList<Highlight> getAbstractHighlightTags(){
		return this.abstractHighlightTags;
	}


	public void toogleAllAnnotationTypes(){
		if(!getHighlight){
			highlightAnnotations(highlightedAnnotationTypes);
			getHighlight = true;
		}else{
			abstractTextAreahighlighter.removeAllHighlights();
			getHighlight = false;
		}
	}

	public void toogleSingleAnnotationType(){

	}


	public void saveAllAnnotations(){
		currentAbstract.saveXMLAbstract(currentXMLFile);
		mainAnnotationFrame.setTitle(mainAnnotationFrameTitle);
		isEdited = false;
	}

	private void addAnnotationTypesList(){	
		annotationTypesGroup.cleanContentPane();
		Map<String, Color> annotationTypes = annotationScheme.getAnnotationTypes(); 
		for(String type : annotationTypes.keySet()){
			annotationTypesGroup.addType(type, annotationTypes.get(type));
		}
		annotationTypesGroup.showAllAnnotationTypes();
	}






}
