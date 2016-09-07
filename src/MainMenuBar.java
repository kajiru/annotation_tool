import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;


import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class MainMenuBar extends JMenuBar implements ItemListener {

	private static final long serialVersionUID = 1L; // To suppress serialVersionUID warning(as seen in Eclipse). 
	public Annotator mainAnnotator; 
	
	//Menus
	private JMenu fileMenu; 
	private JMenu helpMenu;
	private JMenu schemeMenu; 
	private JMenu toogleAnnotationsMenu;
	
	


	public MainMenuBar(Annotator mainAnnotator){
		this.mainAnnotator = mainAnnotator; 
		addMenus(); 
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Temp");

	}

	private void addMenus(){	
		fileMenu = new JMenu("File"); 
		fileMenu.getAccessibleContext().setAccessibleDescription( "Load and save files");
		this.add(fileMenu);
		addFileMenuItems();  

		toogleAnnotationsMenu = new JMenu("Toogle Annotations"); 
		toogleAnnotationsMenu.getAccessibleContext().setAccessibleDescription( "Toogle annotations");
		this.add(toogleAnnotationsMenu);
		addToogleAnnotationsMenuItems(); 

		schemeMenu = new JMenu("Scheme"); 
		schemeMenu.getAccessibleContext().setAccessibleDescription( "Set up scheme file");
		this.add(schemeMenu);
		addschemeMenuItems(); 

		helpMenu = new JMenu("Help"); 
		helpMenu.getAccessibleContext().setAccessibleDescription( "Help on usage");
		this.add(helpMenu);
		addHelpMenuItems(); 
	}


	private void addFileMenuItems(){
		fileMenu.add(createMenuItem("Load File","Load a new file",KeyEvent.VK_N )); 
		fileMenu.add(createMenuItem("Save File","Save to a loaded file",KeyEvent.VK_S)); 
	}

	private void addToogleAnnotationsMenuItems() {
		toogleAnnotationsMenu.add(createMenuItem("All Annotations", "Toogle annotations displayed", KeyEvent.VK_T));
	}

	private void addschemeMenuItems() {
		schemeMenu.add(createMenuItem("Load Scheme", "Set a scheme file", 0));
	}

	private void addHelpMenuItems() { 
		helpMenu.add(createMenuItem("General Help", "Help on General usage", KeyEvent.VK_H));
		helpMenu.add(createMenuItem("Scheme Files Help", "Help on Scheme Files", 0));
	}

	private JMenuItem createMenuItem(String name, String description, int shortcutKey){
		JMenuItem menuItem = new JMenuItem(name);
		if(shortcutKey != 0)menuItem.setAccelerator(KeyStroke.getKeyStroke(shortcutKey, ActionEvent.ALT_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription(description);
		menuItem.addActionListener(new MenuActionListener(mainAnnotator));
		return menuItem; 
	}

}


