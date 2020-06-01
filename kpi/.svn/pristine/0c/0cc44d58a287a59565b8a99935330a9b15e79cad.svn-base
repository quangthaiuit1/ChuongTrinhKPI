package trong.lixco.com.classInfor;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CheckFolder {
	/**
	 * Kiem tra mot duong dan neu chua ton tai thi tao folder
	 * @param path
	 * duong dan den folder
	 * 
	 */
	public static void checkCreateFolder(String path) {
		// Tạo một đối tượng File đại diện cho một đường dẫn.
		File fileDir = new File(path);
		// Kiểm tra sự tồn tại.
		if (!fileDir.exists()) {
			Path dir = Paths.get(path);
			try {
				Files.createDirectories(dir);
			} catch (Exception e) {
			}
		}
	}
}