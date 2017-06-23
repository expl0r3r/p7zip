package com.free.util;

import java.io.File;
import java.util.Comparator;

public class SortFileSizeIncrease implements Comparator<File> {
	@Override
	public int compare(File p1, File p2) {
		if (p1.isFile() && p2.isFile()) {
			long length1 = p1.length();
			long length2 = p2.length();
			if (length1 < length2) {
				return -1;
			} else if (length1 > length2) {
				return 1;
			} else {
				return 0;
			}
		} else if (p1.isDirectory() && p2.isDirectory()) {
			String[] list1 = p1.list();
			int length1 = ((list1 == null) ? 0 : list1.length);
			String[] list2 = p2.list();
			int length2 = ((list2 == null) ? 0 : list2.length);
			return (length1 - length2);
		} else if (p1.isFile() && p2.isDirectory()) {
			return 1;
		} else {
			return -1;
		}
	}
}

