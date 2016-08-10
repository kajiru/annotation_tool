
import java.awt.*; 
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.Highlighter.HighlightPainter;

import org.jdom2.Element;


public class Annotator {

	private Abstract currentAbstract; 

	//Main GUI components
	private JFrame annotationTool;
	private JTextField filenameTextField;
	private JButton loadFileBtn; 
	private JTextArea abstractTextArea; 
	private JLabel filenameLabel; 
	private JList<JButton> annotationsList;
	private JButton highlightAnnotationsBtn; 

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {	
		launchGUI();
	}


	/**
	 * Launch the GUI
	 */
	public static void launchGUI(){
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Annotator window = new Annotator();
					window.annotationTool.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	/**
	 * Create the application.
	 */
	public Annotator() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(){
		annotationTool = new JFrame();
		annotationTool.setTitle("Annotation Tool");
		annotationTool.setBounds(100, 100, 1000, 800);
		annotationTool.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		annotationTool.getContentPane().setLayout(null);
		addComponents();
		addActionListeners(); 
	}



	/**
	 * Add GUI components to the main window Frame.
	 */
	private void addComponents(){

		//Filename TextField
		filenameTextField = new JTextField();
		filenameTextField.setBounds(6, 36, 134, 28);
		annotationTool.getContentPane().add(filenameTextField);
		filenameTextField.setColumns(10);

		filenameTextField.setText("sampleInput.xml"); //TODO: Remove after Tests


		//Load Filename Button
		loadFileBtn = new JButton("Load ");
		loadFileBtn.setBounds(135, 37, 61, 29);
		annotationTool.getContentPane().add(loadFileBtn);

		//Label for the filename TextField
		filenameLabel = new JLabel("Filename");
		filenameLabel.setBounds(6, 20, 61, 16);
		annotationTool.getContentPane().add(filenameLabel);

		//Main TextArea for the abstract
		abstractTextArea = new JTextArea();
		abstractTextArea.setEditable(false);
		abstractTextArea.setWrapStyleWord(true);
		abstractTextArea.setLineWrap(true);
		abstractTextArea.setBounds(196, 42, 580, 600);
		Insets textAreaInsets = new Insets(2,2,2,2);
		abstractTextArea.setMargin(textAreaInsets);
		annotationTool.getContentPane().add(abstractTextArea);



		//List of all annotations in the document
		annotationsList = new JList<JButton>(); //TODO : Change the content from JButton to ...
		annotationsList.setBounds(795, 42, 179, 399);
		annotationTool.getContentPane().add(annotationsList);

		//Button to update the abstract that it includes annotations
		highlightAnnotationsBtn = new JButton("Show Annotations");
		highlightAnnotationsBtn.setBounds(196, 664, 179, 29);
		annotationTool.getContentPane().add(highlightAnnotationsBtn);


	}

	/**
	 * Add ActionListeners for various GUI components found in the main window Frame.
	 */
	private void addActionListeners(){
		//Loading the abstract source file. 
		loadFileBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadAbstract(filenameTextField.getText());
			}
		});	

		//Highlighting Annotations in Abstract
		highlightAnnotationsBtn.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				highlightAnnotations();
				displayAnnotationTypes(); 
			}
		});

		addMouseListeners(); 


	}

	private void addMouseListeners(){
		abstractTextArea.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if(abstractTextArea.getSelectedText() != null){
					String selectedText = abstractTextArea.getSelectedText();
					System.out.println("Selected Text: " + selectedText);
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


	private void loadAbstract(String srcFilename){
		XmlFile xmlFile = new XmlFile(srcFilename);
		Element targetAbstract = xmlFile.getAbstractElement(); 

		String title = ""; //TODO : Get exact Title
		currentAbstract = new Abstract(targetAbstract,title);
		abstractTextArea.setText(currentAbstract.getPlainAbstract());

	}

	private void highlightAnnotations(){
		try{
			//TODO: Highlighting within a highlighted region
			Highlighter highlighter = abstractTextArea.getHighlighter();
			Map<String, LinkedHashSet<Annotation>> annotations = currentAbstract.getAnnotations();
			for(String key : annotations.keySet()){
				for(Annotation annotation : annotations.get(key)){
					HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(annotation.getColor());
					highlighter.addHighlight(annotation.getStart(), annotation.getEnd(), painter);
				}					
			}
		}catch(BadLocationException ex){
			ex.printStackTrace();
		}
	}

	private void displayAnnotationTypes(){
		Map<String, Color> annotationTypes = currentAbstract.getAnnotationTypesColors();
		//TODO: Put these as components on the right panel
		for(String key : annotationTypes.keySet()){
			System.out.print(key + " : ");
			System.out.println(annotationTypes.get(key));
		}	
	}


}
