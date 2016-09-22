import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.Highlight;
import javax.swing.text.Highlighter.HighlightPainter;


/**
 * All major components in the main Annotation frame
 * */

public class AnnotationFrameComponents implements AnnotatorConstants{

	private Annotator annotator; 
	private JFrame mainFrame; 
	private JTextArea abstractTextArea; 
	private AnnotationTypesGroup annotationTypesGroup; 
	private AnnotationAdditionGroup annotationAdditionGroup; 
	private JPanel titlePane; 
	private JPanel annotationDetailsPane;
	private JPanel footerPane; 


	public AnnotationFrameComponents(Annotator annotator){
		this.annotator = annotator; 
		this.mainFrame = this.annotator.getMainAnnotationFrame();
	}

	//Main TitlePane for the loaded file
	public JPanel createTitlePane(){
		titlePane = new JPanel();
		return titlePane; 
	}

	//Main TextArea for the abstract
	public JTextArea createMainAbstractTextArea(){
		abstractTextArea = new JTextArea();
		abstractTextArea.setEditable(false);
		abstractTextArea.setWrapStyleWord(true);
		abstractTextArea.setLineWrap(true);
		Insets textAreaInsets = new Insets(2,2,2,2);
		abstractTextArea.setMargin(textAreaInsets);
		return abstractTextArea; 
	}


	public AnnotationTypesGroup createAnnotationTypesListPane(Annotator annotator){
		annotationTypesGroup = new AnnotationTypesGroup(annotator); 
		return annotationTypesGroup; 
	}

	public JPanel createAnnotationDetailsPane(){
		annotationDetailsPane = new JPanel();
		annotationDetailsPane.setLayout(new BoxLayout(annotationDetailsPane, BoxLayout.Y_AXIS));
		annotationDetailsPane.setBorder(new EmptyBorder(10, 10, 10, 10)); //Arbitrary Padding
		annotationDetailsPane.setAlignmentX(Component.CENTER_ALIGNMENT);
		annotationDetailsPane.setPreferredSize(new Dimension(200,200));
		return annotationDetailsPane; 
	}

	public void addAnnotationDetailsPaneButtons(Annotator annotator, Annotation selectedAnnotation, Highlight highlight){
		JPanel buttonsPane = new JPanel(); 
		buttonsPane.setLayout(new BoxLayout(buttonsPane, BoxLayout.X_AXIS));
		JButton deleteBtn = new JButton("Delete");
		buttonsPane.add(deleteBtn);

		deleteBtn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				annotator.deleteAnnotation(selectedAnnotation, highlight);
			}
		});
		buttonsPane.setBackground(null);
		buttonsPane.setAlignmentX(Component.CENTER_ALIGNMENT); 
		annotationDetailsPane.add(buttonsPane);
	}

	public  void createAnnotationAdditionPane(){
		annotationAdditionGroup = new AnnotationAdditionGroup(annotator); 	
	}

	public JPanel createFooterPane(){
		footerPane = new JPanel();
		footerPane.add(new JLabel("---*---"));
		return footerPane; 
	}


}
