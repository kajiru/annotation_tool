/*  _                      _        _   _               _____           _ 
   / \   _ __  _ __   ___ | |_ __ _| |_(_) ___  _ __   |_   _|__   ___ | |
  / _ \ | '_ \| '_ \ / _ \| __/ _` | __| |/ _ \| '_ \    | |/ _ \ / _ \| |
 / ___ \| | | | | | | (_) | || (_| | |_| | (_) | | | |   | | (_) | (_) | |
/_/   \_\_| |_|_| |_|\___/ \__\__,_|\__|_|\___/|_| |_|   |_|\___/ \___/|_|
 
*/

/**
 * Author: Jasper Kajiru
 * ---------------------
 * This is the main class in the program. It initializes 
 * the GUI and sets everything up
 */

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
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

	private AnnotationScheme annotationScheme = null; 
	private Abstract currentAbstract; 
	private XmlFile currentXMLFile; 
	private Highlighter abstractTextAreahighlighter; 
	private boolean getHighlight = true; //true = annotations are highlighted 
	private ArrayList<Highlight> abstractHighlightTags;
	private String mainAnnotationFrameTitle;
	private boolean isEdited = false; //is a document has been edited or not
	private boolean isDevMode = false; //switch between production & development

	private Set<String> highlightedAnnotationTypes; 

	//Main GUI components
	private AnnotationFrameComponents mainAnnotationFrameComponents; 
	private JFrame mainAnnotationFrame;
	private JPanel mainContentPanel; 

	//Five main sections within the BoarderLayout
	private JPanel titlePane;
	private AnnotationTypesGroup annotationTypesGroup = null; //Annotations Types List
	private JTextArea abstractTextArea;
	private JPanel annotationDetailsPane;
	private JPanel footerPane;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {	
		setSystemGUIProperties(); 
		new Annotator(); 
	}

	/**
	 * Does system specific settings
	 */
	private static void setSystemGUIProperties(){
		boolean IS_MAC = System.getProperty("os.name").toLowerCase().startsWith("mac os x");
		if(IS_MAC){
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			//System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Annotation Tool");
		}
		//Set Look & Feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e ) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initialize  the application by adding GUI components and listeners.
	 */
	public Annotator() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					addMainAnnotationFrame(); 	 
					addMainAnnotationFrameComponents();
					addMouseListeners();
					if(isDevMode)loadAbstract(); 
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});


	}

	/**
	 * Sets up the main Frame 
	 */
	private void addMainAnnotationFrame(){
		mainAnnotationFrame = new JFrame();
		mainContentPanel = new JPanel(); 
		mainContentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		mainAnnotationFrame.setContentPane(mainContentPanel);
		mainAnnotationFrame.setLayout(mainLayout);
		mainLayout.setHgap(MAIN_LAYOUT_HORIZONTAL_GAP);
		mainAnnotationFrame.setTitle("Annotation Tool");
		mainAnnotationFrame.setPreferredSize(MAIN_FRAME_PREFERRED_SIZE); 
		mainAnnotationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainAnnotationFrame.setJMenuBar(new MainMenuBar(this)); 
		mainAnnotationFrame.addComponentListener(new MainAnnotationFrameListener(this));
		mainAnnotationFrame.pack();
		mainAnnotationFrame.setVisible(true);
	}

	/**
	 * Creates and adds GUI components to the main Frame.
	 */
	private void addMainAnnotationFrameComponents(){
		mainAnnotationFrameComponents = new AnnotationFrameComponents(this);

		titlePane = mainAnnotationFrameComponents.createTitlePane(); 
		annotationTypesGroup = mainAnnotationFrameComponents.createAnnotationTypesListPane(this);
		abstractTextArea = mainAnnotationFrameComponents.createMainAbstractTextArea();
		annotationDetailsPane = mainAnnotationFrameComponents.createAnnotationDetailsPane(); 
		footerPane = mainAnnotationFrameComponents.createFooterPane(); 

		JScrollPane titleScrollPane = new JScrollPane(titlePane,
								JScrollPane.VERTICAL_SCROLLBAR_NEVER,
	            				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
	            				); 
					titleScrollPane.setBorder(null);
		mainAnnotationFrame.add(titleScrollPane, BorderLayout.PAGE_START);
		mainAnnotationFrame.add(annotationTypesGroup, BorderLayout.LINE_START);
		JScrollPane mainTextAreaScrollPane = new JScrollPane(abstractTextArea);
		mainTextAreaScrollPane.setBorder(null);
		mainAnnotationFrame.add(mainTextAreaScrollPane, BorderLayout.CENTER);
		mainAnnotationFrame.add(annotationDetailsPane, BorderLayout.LINE_END);
		mainAnnotationFrame.add(footerPane, BorderLayout.PAGE_END);
	}

	/**
	 * Adds mouse event listeners, mainly in the textArea section where
	 * a lot of work fakes place 
	 */
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

			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if(abstractTextArea.getSelectedText() != null){
					addNewAnnotation();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				abstractTextArea.setCursor(new Cursor(Cursor.TEXT_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {

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

	/**
	 * Displays details of a selected annotation on the right pane
	 * @param highlight the selected, and highlighted, annotation. 
	 * This is passed as an Highlight object because it's easier 
	 * to work with  
	 */
	private void displayAnnotationDetails(Highlight highlight){
		cleanUpPane(annotationDetailsPane); 
		Annotation selectedAnnotation = currentAbstract.getAnnotation(
													  highlight.getStartOffset(), 
													  highlight.getEndOffset());
		JPanel annotationDetails = new JPanel(); 
		annotationDetails.setLayout(new BoxLayout(annotationDetails, BoxLayout.Y_AXIS));
		annotationDetails.setBackground(selectedAnnotation.getColor());
		annotationDetails.add(new JLabel("Type: " + selectedAnnotation.getType()));

		Map<String, String> attributes = selectedAnnotation.getAttributes(); 
		if(attributes != null){
			for(String attrName: attributes.keySet()){
				annotationDetails.add(new JLabel(attrName + ":" + attributes.get(attrName)));
			}
		}else{
			annotationDetails.add(new JLabel("-No Attributes-"));
		}

		annotationDetails.setAlignmentX(Component.CENTER_ALIGNMENT);
		annotationDetailsPane.add(annotationDetails);

		mainAnnotationFrameComponents.addAnnotationDetailsPaneButtons(this,
																	selectedAnnotation,
																	highlight); 
		mainAnnotationFrame.validate();
	}

	/**
	 * Updates the title to notify the user that the file is edited
	 * @param title
	 */
	public void markAsEdited(String title){	
		if(!isEdited)mainAnnotationFrame.setTitle(title);
		isEdited = true; 
	}

	/**
	 * Removes a selected annotation
	 * @param annotation the selected annotation
	 * @param highlight a tag to identify the selected annotation 
	 */
	public void deleteAnnotation(Annotation annotation, Highlight highlight){
		int n = 0; 

		n = JOptionPane.showConfirmDialog(mainAnnotationFrame, 
				"Are you sure?", 
				"Delete Annotation", 
				JOptionPane.YES_NO_OPTION);
		if(n == 0){
			if(currentAbstract.deleteAnnotation(annotation)){
				abstractTextAreahighlighter.removeHighlight(highlight);
			}	
		}
		markAsEdited(mainAnnotationFrame.getTitle() + "*");
		annotationDetailsPane.removeAll();
		annotationDetailsPane.revalidate();
		annotationDetailsPane.repaint();
	}

	/**
	 * Adds a new annotation
	 */
	private void addNewAnnotation(){	
		cleanUpPane(annotationDetailsPane);
		mainAnnotationFrameComponents.createAnnotationAdditionPane();		
		mainAnnotationFrame.validate();
	}
	
	/**
	 * Read and display an annotation scheme file 
	 * @param schemeSrcFile a file read from the client's disk 
	 */
	public void loadSchemeFile(File schemeSrcFile){
		annotationScheme = new AnnotationScheme(schemeSrcFile); 
	}

	/**
	 * Read and display and abstract based on a given scheme file
	 * If the scheme file is not set, a default Scheme file
	 * (named defaultSchemeFile.xml) is used
	 */
	public void loadAbstract(){
		if(annotationScheme == null)
			loadSchemeFile(new File("resources/defaultScheme.xml"));

		File abstractSrcFile = XmlFile.selectSrcFile(); 
		currentXMLFile = new XmlFile(abstractSrcFile);
		Element targetAbstract = currentXMLFile.getAbstractElement(); 
		String title = currentXMLFile.getInDocumentTitle();

		titlePane.removeAll();
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
		updateGUI(); 
	}

	/**
	 * Prepares a list of all possible annotation types
	 */
	private void setHighlightedAnnotationTypes(){
		highlightedAnnotationTypes = new HashSet<String>(); 
		highlightedAnnotationTypes = currentAbstract.getAnnotationTypesSet();
	}

	/**
	 * Set the title at the top of the window based on the loaded file
	 * @param loadedFile the file containing content to be annotated
	 */
	private void setMainTitle(File loadedFile){
		mainAnnotationFrameTitle = "Annotation Tool - "; 
		mainAnnotationFrameTitle += loadedFile.getName();  
		mainAnnotationFrame.setTitle(mainAnnotationFrameTitle);
	}

	/**
	 * Use a variety of colors, based on the annotation scheme, to highlight 
	 * annotations on screen
	 * @param highlightedAnnotationTypes all possible annotation types that can be annotated
	 */
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

	/**
	 * Provides a visual updates when a user adds or removes an annotation
	 * @param highlightedAnnotationTypes
	 */
	public void updateHighlightedAnnotations(Set<String> highlightedAnnotationTypes) {
		abstractTextAreahighlighter.removeAllHighlights();
		highlightAnnotations(highlightedAnnotationTypes); 
	}

	/**
	 * Displays or removes all highlights from the screen
	 */
	public void toogleAllAnnotationTypes(){
		if(!getHighlight){
			highlightAnnotations(highlightedAnnotationTypes);
			getHighlight = true;
		}else{
			abstractTextAreahighlighter.removeAllHighlights();
			getHighlight = false;
		}
	}

	/**
	 * Saves all changes to the target file
	 */
	public void saveAllAnnotations(){
		currentAbstract.saveXMLAbstract(currentXMLFile);
		mainAnnotationFrame.setTitle(mainAnnotationFrameTitle);
		isEdited = false;
	}

	/**
	 * Updates the left pane with all the possible types of annotations
	 */
	private void addAnnotationTypesList(){	
		annotationTypesGroup.cleanContentPane();
		Map<String, Color> annotationTypes = annotationScheme.getAnnotationTypes(); 
		for(String type : annotationTypes.keySet()){
			annotationTypesGroup.addType(type, annotationTypes.get(type));
		}
		annotationTypesGroup.showAllAnnotationTypes();
	}

	/**
	 * Removes all contents from a given JPanel
	 * @param pane the JPanel to clear
	 */
	private void cleanUpPane(JPanel pane){
		pane.removeAll();
		pane.revalidate();
		pane.repaint();
	}

	/**
	 * Displays components to the main Frame when components are added
	 * or removed
	 */
	public void updateGUI(){
		mainAnnotationFrame.revalidate(); 
		mainAnnotationFrame.repaint(); 
	}
	/***********************************************
	 *  GETTERS                        
	 * @return private_var Returns private variables 
	 * for use in other classes 
	 ***********************************************/
	
	public Abstract getCurrentAbstract(){
		return this.currentAbstract; 
	}
	
	public JPanel getAnnotationDetailsPane(){
		return this.annotationDetailsPane;
	}
	
	public JFrame getMainAnnotationFrame(){
		return this.mainAnnotationFrame; 
	}

	public JTextArea getAbstractTextArea(){
		return this.abstractTextArea; 
	}
	
	public Highlighter getAbstractTextAreahighlighter(){
		return this.abstractTextAreahighlighter; 
	}
	
	public ArrayList<Highlight> getAbstractHighlightTags(){
		return this.abstractHighlightTags;
	}
}
