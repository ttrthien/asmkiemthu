package poly.com.asm.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class ParamService {
	@Autowired
	HttpServletRequest request;

	public String getString(String name, String defaultValue) {
		String value = request.getParameter(name);
		return (value != null) ? value : defaultValue;
	}

	public int getInt(String name, int defaultValue) {
		String value = request.getParameter(name);
		try {
			return Integer.parseInt(value);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public double getDouble(String name, double defaultValue) {
		String value = request.getParameter(name);
		try {
			return Double.parseDouble(value);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public boolean getBoolean(String name, boolean defaultValue) {
		String value = request.getParameter(name);
		return (value != null) ? Boolean.parseBoolean(value) : defaultValue;
	}

	public Date getDate(String name, String pattern) {
		String value = request.getParameter(name);
		try {
			return new SimpleDateFormat(pattern).parse(value);
		} catch (Exception e) {
			throw new RuntimeException("Sai định dạng ngày tháng!");
		}
	}

	public File save(MultipartFile file, String path) {
		if (!file.isEmpty()) {
			try {
				String userDir = System.getProperty("user.dir");
				Path srcPath = Paths.get(userDir, "src", "main", "resources", "static", path);
				File srcDir = srcPath.toFile();
				if (!srcDir.exists())
					srcDir.mkdirs();

				File srcFile = new File(srcDir, file.getOriginalFilename());
				file.transferTo(srcFile);

				String targetPath = request.getServletContext().getRealPath(path);
				File targetDir = new File(targetPath);
				if (!targetDir.exists())
					targetDir.mkdirs();

				File targetFile = new File(targetDir, file.getOriginalFilename());
				Files.copy(srcFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

				System.out.println(">>> ĐÃ LƯU  VÀO SOURCE: " + srcFile.getAbsolutePath());

				return srcFile;
			} catch (Exception e) {
				throw new RuntimeException("Lỗi lưu file: " + e.getMessage());
			}
		}
		return null;
	}
}