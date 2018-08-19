package co.il.nmh.image.lock.gui.menu;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import co.il.nmh.easy.utils.ResourceUtils;
import co.il.nmh.image.lock.gui.GUI;
import co.il.nmh.image.lock.gui.filters.ImageFileTypeFilter;
import co.il.nmh.image.lock.gui.listeners.FileMenuListener;

/**
 * @author Maor Hamami
 */

public class FileMenu extends JMenu
{
	public static final ImageFileTypeFilter pnglFilter = new ImageFileTypeFilter(".pngl", "All Picture Files");
	private static final long serialVersionUID = -7946354638924763932L;

	protected JMenuItem saveBtn;
	protected JMenuItem saveAsBtn;

	protected File activeFile;
	protected Set<FileMenuListener> fileMenuListeners;

	public FileMenu(GUI gui)
	{
		super("File");

		fileMenuListeners = new HashSet<>();

		buildFileMenu();
	}

	private void buildFileMenu()
	{
		buildOpen();
		buildSave();
		buildSaveAs();
		buildClose();

		enableSave(false);
		enableSaveAs(false);
	}

	private void buildOpen()
	{
		JMenuItem openBtn = new JMenuItem("Open", ResourceUtils.getIcon("open-file-icon.png"));
		openBtn.setPreferredSize(new Dimension(200, openBtn.getPreferredSize().height));
		openBtn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		openBtn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setAcceptAllFileFilterUsed(false);
				fileChooser.addChoosableFileFilter(pnglFilter);

				if (null != activeFile)
				{
					fileChooser.setCurrentDirectory(activeFile.getParentFile());
				}

				if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
				{
					File file = fileChooser.getSelectedFile();

					for (FileMenuListener fileMenuListener : fileMenuListeners)
					{
						fileMenuListener.open(file);
					}
				}
			}
		});

		add(openBtn);
	}

	private void buildSave()
	{
		saveBtn = new JMenuItem("Save", ResourceUtils.getIcon("save-icon.png"));
		saveBtn.setPreferredSize(new Dimension(200, saveBtn.getPreferredSize().height));
		saveBtn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		saveBtn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				saveAsFile(activeFile);
			}
		});

		add(saveBtn);
	}

	private void buildSaveAs()
	{
		saveAsBtn = new JMenuItem("Save As", ResourceUtils.getIcon("save-as-icon.png"));
		saveAsBtn.setPreferredSize(new Dimension(200, saveAsBtn.getPreferredSize().height));
		saveAsBtn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
		saveAsBtn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				FileNameExtensionFilter jpgFilter = new FileNameExtensionFilter("JPEG", "jpg");
				FileNameExtensionFilter bmpFilter = new FileNameExtensionFilter("24-bit Bitmap", "bmp");
				FileNameExtensionFilter pngFilter = new FileNameExtensionFilter("PNG", "png");
				FileNameExtensionFilter imageLockFilter = new FileNameExtensionFilter("Image-Lock", "pngl");

				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileFilter(jpgFilter);
				fileChooser.setFileFilter(bmpFilter);
				fileChooser.setFileFilter(pngFilter);
				fileChooser.setFileFilter(imageLockFilter);
				fileChooser.setAcceptAllFileFilterUsed(false);

				if (null != activeFile)
				{
					String fileName = activeFile.getName();

					int start = fileName.lastIndexOf(".");

					if (start > -1)
					{
						fileName = fileName.substring(0, start);
					}

					fileChooser.setCurrentDirectory(activeFile.getParentFile());
					fileChooser.setSelectedFile(new File(activeFile.getParentFile() + File.separator + fileName));
				}

				if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
				{
					File file = fileChooser.getSelectedFile();
					FileFilter fileFilter = fileChooser.getFileFilter();

					String extension;

					if (fileFilter == jpgFilter)
					{
						extension = ".jpg";
					}

					else if (fileFilter == bmpFilter)
					{
						extension = ".bmp";
					}

					else if (fileFilter == pngFilter)
					{
						extension = ".png";
					}

					else
					{
						extension = ".pngl";
					}

					if (!file.getName().endsWith(extension))
					{
						file = new File(file.toString() + extension);
					}

					saveAsFile(file);
				}
			}
		});

		add(saveAsBtn);
	}

	private void saveAsFile(File filePath)
	{
		for (FileMenuListener fileMenuListener : fileMenuListeners)
		{
			fileMenuListener.saveAsFile(filePath);
		}
	}

	private void buildClose()
	{
		JMenuItem closeBtn = new JMenuItem("Close");
		closeBtn.setPreferredSize(new Dimension(200, saveAsBtn.getPreferredSize().height));
		closeBtn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				for (FileMenuListener fileMenuListener : fileMenuListeners)
				{
					fileMenuListener.close();
				}
			}
		});

		add(closeBtn);
	}

	public void addFileMenuListener(FileMenuListener fileMenuListener)
	{
		fileMenuListeners.add(fileMenuListener);
	}

	public void setActiveFile(File activeFile)
	{
		this.activeFile = activeFile;
	}

	public void enableSave(boolean enable)
	{
		saveBtn.setEnabled(enable);
	}

	public void enableSaveAs(boolean enable)
	{
		saveAsBtn.setEnabled(enable);
	}

}
