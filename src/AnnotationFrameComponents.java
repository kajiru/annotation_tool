import java.awt.BorderLayout;
import java.awt.Color;
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
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.Scrollable;
import javax.swing.UIManager;




/**
 * All major components in the main Annotation frame
 * */

public class AnnotationFrameComponents implements AnnotatorConstants {

	private JFrame mainFrame; 
	private JTextArea abstractTextArea; 
	private AnnotationTypesGroup annotationTypesGroup; 

	public AnnotationFrameComponents(JFrame mainFrame){
		this.mainFrame = mainFrame;
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
	 
}
