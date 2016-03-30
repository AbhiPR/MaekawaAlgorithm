package project2;

import java.util.Comparator;

public class comp implements  Comparator<String>{

	
	public int compare(String x, String y) { // request priority

		if (Integer.parseInt(x.split("\\s+")[1]) < Integer.parseInt(y.split("\\s+")[1])) {
			return -1;
		} else if (Integer.parseInt(x.split("\\s+")[1]) > Integer.parseInt(y.split("\\s+")[1])) {
			return 1;
		} else {
			if (Integer.parseInt(x.split("\\s+")[0]) < Integer.parseInt(y.split("\\s+")[0])) {
				return -1;
			} else if (Integer.parseInt(x.split("\\s+")[0]) > Integer.parseInt(y.split("\\s+")[0])) {
				return 1;
			}
		}
		return 0;
	}
}
