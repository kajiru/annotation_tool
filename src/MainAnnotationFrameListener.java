import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JFrame;

public class MainAnnotationFrameListener implements ComponentListener {

	Annotator annotator; //
	JFrame mainFrame; 

	public MainAnnotationFrameListener(Annotator annotator){
		this.annotator = annotator; 
	}

	@Override
	public void componentResized(ComponentEvent e) {
		mainFrame = (JFrame) e.getComponent();
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub

	}

}
