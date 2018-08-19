package co.il.nmh.image.lock.gui.listeners;

import java.io.File;

/**
 * @author Maor Hamami
 */

public interface FileMenuListener
{
	void open(File file);

	void saveAsFile(File filePath);

	void close();
}
