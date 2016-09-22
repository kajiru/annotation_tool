import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.Highlight;
import javax.swing.text.Highlighter.HighlightPainter;


public class AnnotationAdditionGroup extends JPanel{

	private static final long serialVersionUID = 3920629589063028658L;
	private JPanel selectedTextPanel; 
	private JPanel annotationTypesPanel; 
	private JPanel attributesPanel; 
	private JPanel confirmationBtnsPanel; 	
	private JComboBox<String> annotationTypesList; 
	private JButton okBtn, cancelBtn; 
	private JTextArea abstractTextArea; 
	private JPanel parentPanel;
	
	JFrame mainFrame; 
	Annotator annotator; 
	Abstract currentAbstract; 
	String selectedTxt; 
	String selectedType; 

	private Map<String, Map<String, ArrayList<String>>>  schemeAnnotationElements; 

	public AnnotationAdditionGroup(Annotator annotator){
		this.annotator = annotator; 
		this.mainFrame = annotator.getMainAnnotationFrame(); 
		abstractTextArea = annotator.getAbstractTextArea();
		currentAbstract = annotator.getCurrentAbstract();
		parentPanel = annotator.getAnnotationDetailsPane(); 
		selectedTxt = abstractTextArea.getSelectedText();

		schemeAnnotationElements = currentAbstract.getSchemeAnnotationElements();
		Vector<String> annotationTypes = new Vector<String>() ; 	
		for(String type : schemeAnnotationElements.keySet())
			annotationTypes.add(type);
		//-------------------

		createSelectedTextPanel(selectedTxt); 
		createAnnotationTypesPanel(annotationTypes); 
		createAttributesPanel(); 
		createConfirmationBtnsPanel(); 

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.PAGE_START;
		c.gridy = 0; 
		add(selectedTextPanel,c);
		c.gridy = 1; 
		add(annotationTypesPanel,c);
		c.gridy = 2; 
		add(attributesPanel,c); 
		c.gridy = 3; 
		add(confirmationBtnsPanel,c); 
		parentPanel.add(this); 
	}

	private void createSelectedTextPanel(String txt){
		selectedTextPanel = new JPanel();
		selectedTextPanel.setLayout(new GridBagLayout());
		selectedTextPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;

		JTextArea txtArea = new JTextArea(txt);
		txtArea.setLineWrap(true);
		txtArea.setEditable(false);
		txtArea.setWrapStyleWord(true);

		txtArea.setMargin(new Insets(2,2,2,2));
		JScrollPane  selectedTxtScrollPane = new JScrollPane(txtArea); 
		selectedTxtScrollPane.setPreferredSize(new Dimension(180,50));
		selectedTxtScrollPane.setBorder(null);

		c.gridx = 0;
		//c.weighty = 1.0; 
		selectedTextPanel.add(selectedTxtScrollPane, c);

	}

	private void createAnnotationTypesPanel(Vector<String> annotationTypes){
		annotationTypesPanel = new JPanel();
		annotationTypesPanel.setLayout(new GridBagLayout());
		annotationTypesPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

		annotationTypesList = new JComboBox<String>(annotationTypes);
		annotationTypesList.setAlignmentX(Component.LEFT_ALIGNMENT);
		annotationTypesList.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				selectedType = (String) ((JComboBox<?>)e.getSource()).getSelectedItem(); 
				populateAttributesPanel(selectedType); 
			}

		});
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 2; 
		annotationTypesPanel.add(annotationTypesList, c);
	}

	private void createAttributesPanel(){
		attributesPanel  = new JPanel(new GridBagLayout()); 
	}

	private void createConfirmationBtnsPanel(){
		confirmationBtnsPanel = new JPanel(); 
		confirmationBtnsPanel.setLayout(new GridBagLayout());

		JButton addBtn = new JButton("Add"); 
		JButton cancelBtn = new JButton("Cancel");
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 0;
		confirmationBtnsPanel.add(addBtn,c); 
		c.gridx =2; 
		confirmationBtnsPanel.add(cancelBtn,c);
		confirmationBtnsPanel.setBackground(null);

		addBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				addNewAnnotation();
			}
		});

		cancelBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{	
				parentPanel.removeAll();
				parentPanel.add(new JLabel("Select text/Click Annotation"));
				annotator.updateGUI();
			}
		});

	}

	private void populateAttributesPanel(String selection){
		attributesPanel.removeAll();
		Map<String, ArrayList<String>> attributes = schemeAnnotationElements.get(selection); 
		if(attributes != null) {
			GridBagConstraints c = new GridBagConstraints();
			int n = 0; 
			for(String attrName : attributes.keySet()){
				c.gridy = n; 
				c.gridx = 0; 
				attributesPanel.add(new JLabel(attrName), c);
				c.gridx = 1; 
				ArrayList<String> attributeOptions = attributes.get(attrName);
				if(attributeOptions != null){ //
					String[] options = attributeOptions.toArray(new String[attributeOptions.size()]);
					attributesPanel.add(new JComboBox<String>(options), c);
				}else{
					JTextField txtFld = new JTextField(); 
					txtFld.setPreferredSize(new Dimension(60,30));
					attributesPanel.add(txtFld, c);
				}
				n++; 
			}
		}
		mainFrame.revalidate();
		mainFrame.repaint();
	}

	private void addNewAnnotation(){
		int selectionStartIndex = abstractTextArea.getSelectionStart(); 
		int selectionEndIndex = abstractTextArea.getSelectionEnd(); 
		Map<String, String> attributes = getAnnotationAttributes();

		currentAbstract.addAnnotation(selectedType, selectedTxt, attributes,selectionStartIndex,selectionEndIndex);

		HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(currentAbstract.getAnnotationTypes().get(selectedType));
		try {
			Highlighter abstractTextAreahighlighter = annotator.getAbstractTextAreahighlighter(); 
			Highlight tag = (Highlight) abstractTextAreahighlighter.addHighlight(selectionStartIndex, selectionEndIndex, painter);
			annotator.getAbstractHighlightTags().add(tag);
		} catch (BadLocationException ex) {
			ex.printStackTrace();
		}
		annotator.markAsEdited("*" + mainFrame.getTitle() ); 
		parentPanel.removeAll(); 
		parentPanel.add(new JLabel("Saved!"));
		annotator.updateGUI();
	}

	private Map<String, String> getAnnotationAttributes(){
		Map<String, String> attrbs = new HashMap<String, String>();
		String key = "";
		for(Component comp : attributesPanel.getComponents()){
	    	if(comp instanceof JLabel){
	    		key = ((JLabel)comp).getText();
	    	}else{
	    		String value = "";
	    		if(comp instanceof JTextField){
	    			value  = ((JTextField)comp).getText(); 
	    		}else{
	    			value = (String) ((JComboBox<?>)comp).getSelectedItem(); 
	    		}
	    		attrbs.put(key, value); 
	    	}
	    	
	    }
		return attrbs; 
	}

}
