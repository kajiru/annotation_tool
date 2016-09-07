import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class AnnotationTypesGroup extends JPanel implements ItemListener{
	private static final long serialVersionUID = 1L;
	private JCheckBox all;
	private List<JCheckBox> checkBoxes;
	private JPanel header; 
	private JPanel content; 
	public Annotator annotator; 
	
	private List<String> annotationTypesTags; 

	public AnnotationTypesGroup(Annotator annotator) {
		this.annotator = annotator; 
		setAnnotationTypesTags(); 
		checkBoxes = new ArrayList<>();
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(10, 10, 10, 10));
		header = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 1));

		all = new JCheckBox("Show All");
		all.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (JCheckBox cb : checkBoxes) {
					cb.setSelected(all.isSelected());
				}
				annotator.toogleAllAnnotationTypes();
			}
		});
		header.add(all);
		add(header, BorderLayout.NORTH);

		content = new JPanel(new GridBagLayout());
		add(content);
	}
	
	public void setAnnotationTypesTags(){
		this.annotationTypesTags = new ArrayList<String>(); 
	}
	
	public List<String> getAnnotationTypesTags(){
		return annotationTypesTags; 
	}

	public void addType(String type, Color color){
		annotationTypesTags.add(type); 
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.weightx = 1;

		JCheckBox cb = new JCheckBox(type);
		cb.setOpaque(true);
		cb.setBackground(color);
		cb.addItemListener(this);
		checkBoxes.add(cb);
		content.add(cb, gbc);
	}
	
	public void cleanContentPane(){
		content.removeAll(); 
	}
	
	public void showAllAnnotationTypes(){
		all.setSelected(true);
		for (JCheckBox cb : checkBoxes) {
			cb.setSelected(true);
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		Set<String> highlightedAnnotationTypes = new HashSet<String>(); 
		int index = 0; 
		for (JCheckBox cb : checkBoxes) {
			if(cb.isSelected())
				highlightedAnnotationTypes.add(annotationTypesTags.get(index));
			index++; 	
		}
		System.out.println(highlightedAnnotationTypes);
		annotator.updateHighlightedAnnotations(highlightedAnnotationTypes); 
	}

}