import java.util.Comparator;

public class AnnotationComparator implements Comparator<Annotation> {

	@Override
	public int compare(Annotation o1, Annotation o2) {	
		return o1.getEnd() - o2.getEnd();
	}

}
