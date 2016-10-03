package rd.ml.nlp.data;

import java.io.File;
import java.io.FileFilter;
import java.util.HashSet;
import java.util.Set;

/**
 * Parses directory structure recursively starting from root node. Requires a
 * file filter. Contains implementation of a text file filter or user can
 * provide their own.
 * 
 * @author azahar
 *
 */
public class DirectoryParser {

	public static Set<File> getFiles(File rootDir, final FileFilter fileFilter) {
		Set<File> files = new HashSet<>();

		File[] fileList = rootDir.listFiles(fileFilter);
		if (fileList != null && fileList.length > 0) {
			for (File file : fileList) {
				if (file.isDirectory()) {
					files.addAll(getFiles(file, fileFilter));
				} else {
					files.add(file);
				}
			}
		}
		return files;
	}

	/**
	 * Default .txt file filter.
	 */
	public static final FileFilter TEXT_FILE_FILTER = new FileFilter() {

		@Override
		public boolean accept(File arg0) {
			if (arg0 == null) {
				return false;
			}
			if (arg0.isDirectory() || (arg0.isFile() && arg0.getName().endsWith(".txt"))) {
				return true;
			}
			return false;
		}
	};

}
