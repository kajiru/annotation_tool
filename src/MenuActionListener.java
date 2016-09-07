import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;


class MenuActionListener implements ActionListener {

	private Annotator mainAnnotator; 

	MenuActionListener(Annotator annotator){
		this.mainAnnotator = annotator; 
	}

	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand(); 
		switch(cmd){
		case "Load File": 
			mainAnnotator.loadAbstract();  
			break; 
		case "Save File": 
			mainAnnotator.saveAllAnnotations(); 
			break; 
		case "All Annotations":
			mainAnnotator.toogleAllAnnotationTypes();
			break; 
		case "Load Scheme":
			File schemeSrcFile = selectFile(); 
			if(schemeSrcFile != null)
				mainAnnotator.loadSchemeFile(schemeSrcFile);
			break; 
		case "General Help": 
			System.out.println(cmd);
			break; 
		case "Scheme Files Help": 
			System.out.println(cmd);
			break; 
		default:
			System.out.println(cmd + " NOT YET SET");

		}
	}

	private File selectFile(){
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
		int result = fileChooser.showOpenDialog(fileChooser);
		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			return selectedFile; 
		}else{
			return null;	
		}

	}

}