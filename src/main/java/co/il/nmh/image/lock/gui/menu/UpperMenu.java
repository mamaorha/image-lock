package co.il.nmh.image.lock.gui.menu;

import java.io.File;

import javax.swing.JMenuBar;

import co.il.nmh.image.lock.gui.GUI;
import co.il.nmh.image.lock.gui.listeners.FileMenuListener;

/**
 * @author Maor Hamami
 */

public class UpperMenu extends JMenuBar
{
	private static final long serialVersionUID = -1926967165199925041L;

	protected FileMenu fileMenu;

	public UpperMenu(GUI gui)
	{
		fileMenu = new FileMenu(gui);

		add(fileMenu);
	}

	public void addFileMenuListener(FileMenuListener fileMenuListener)
	{
		fileMenu.addFileMenuListener(fileMenuListener);
	}

	public void setActiveFile(File activeFile)
	{
		fileMenu.setActiveFile(activeFile);
	}

	public void enableSave(boolean enable)
	{
		fileMenu.enableSave(enable);
	}

	public void enableSaveAs(boolean enable)
	{
		fileMenu.enableSaveAs(enable);
	}
}
