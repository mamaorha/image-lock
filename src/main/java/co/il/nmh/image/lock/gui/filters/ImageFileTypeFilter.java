package co.il.nmh.image.lock.gui.filters;

import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * @author Maor Hamami
 */

public class ImageFileTypeFilter extends FileFilter
{
	private String extension;
	private String description;
	private FileFilter imageFilter;

	public ImageFileTypeFilter(String extension, String description)
	{
		this.extension = extension;
		this.description = description;
		this.imageFilter = new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes());
	}

	@Override
	public boolean accept(File file)
	{
		return imageFilter.accept(file) || file.getName().endsWith(extension);
	}

	@Override
	public String getDescription()
	{
		return description;
	}
}
