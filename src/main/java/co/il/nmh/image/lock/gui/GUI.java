package co.il.nmh.image.lock.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.json.JSONArray;
import org.json.JSONObject;

import co.il.nmh.easy.swing.components.alert.AlertInput;
import co.il.nmh.easy.swing.components.gui.EasyFrame;
import co.il.nmh.easy.utils.EncryptionUtils;
import co.il.nmh.easy.utils.ImageUtils;
import co.il.nmh.easy.utils.exceptions.EncryptionException;
import co.il.nmh.image.lock.data.ImageLock;
import co.il.nmh.image.lock.gui.listeners.FileMenuListener;
import co.il.nmh.image.lock.gui.menu.FileMenu;
import co.il.nmh.image.lock.gui.menu.UpperMenu;
import co.il.nmh.image.lock.gui.panels.ImageHolderPanel;
import co.il.nmh.image.lock.gui.panels.ToolsPanel;

/**
 * @author Maor Hamami
 */

public class GUI extends EasyFrame implements FileMenuListener
{
	public static final String APP_NAME = "Image-Lock";
	private static final long serialVersionUID = 3030679618483537644L;

	protected UpperMenu upperMenu;
	protected ToolsPanel toolsPanel;
	protected ImageHolderPanel imageHolderPanel;

	protected String sessionKey;

	public GUI()
	{
		super(APP_NAME, 2, 2);
	}

	@Override
	protected void buildPanel()
	{
		setLayout(new GridBagLayout());

		upperMenu = new UpperMenu(this);
		imageHolderPanel = new ImageHolderPanel();
		toolsPanel = new ToolsPanel();

		setJMenuBar(upperMenu);

		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.gridx++;
		gridBagConstraints.gridy++;
		add(toolsPanel, gridBagConstraints);

		gridBagConstraints.weightx = 1;
		gridBagConstraints.weighty = 1;
		gridBagConstraints.gridy++;
		add(imageHolderPanel, gridBagConstraints);
	}

	@Override
	protected void addEvents()
	{
		upperMenu.addFileMenuListener(this);
		imageHolderPanel.addImageHolderPanelListener(toolsPanel);
		toolsPanel.addToolsPanelListener(imageHolderPanel);

		setDropTarget(new DropTarget()
		{
			private static final long serialVersionUID = -1262296456604644445L;

			@Override
			@SuppressWarnings("unchecked")
			public synchronized void drop(DropTargetDropEvent evt)
			{
				try
				{
					evt.acceptDrop(DnDConstants.ACTION_COPY);
					List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);

					for (File file : droppedFiles)
					{
						if (FileMenu.pnglFilter.accept(file))
						{
							open(file);
							break;
						}
					}
				}
				catch (Exception ex)
				{
				}
			}
		});
	}

	@Override
	public void open(File file)
	{
		try
		{
			BufferedImage bufferedImage = ImageIO.read(file);
			List<ImageLock> imageLocks = null;

			try
			{
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

				String line;
				String areaLines = null;
				String hashKey = null;

				while ((line = br.readLine()) != null)
				{
					if (line.startsWith("image-lock"))
					{
						areaLines = "";
						hashKey = line.substring(line.indexOf(",") + 1);
					}

					else if (null != areaLines)
					{
						areaLines += line;
					}
				}
				br.close();

				if (null != areaLines)
				{
					String key = null;

					if (null != sessionKey)
					{
						String decrypt = EncryptionUtils.decrypt(hashKey, sessionKey);

						if ("validateClientSecretKey".equals(decrypt))
						{
							key = sessionKey;
						}
					}

					if (null == key)
					{
						key = AlertInput.getPassword("Enter Password", APP_NAME);

						if (null == key)
						{
							return;
						}

						String decrypt = EncryptionUtils.decrypt(hashKey, key);

						if (!"validateClientSecretKey".equals(decrypt))
						{
							throw new RuntimeException("invalid password");
						}
					}

					sessionKey = key;

					areaLines = EncryptionUtils.decrypt(areaLines, key);

					imageLocks = new ArrayList<>();
					JSONArray jsonArray = new JSONArray(areaLines);

					for (int i = 0; i < jsonArray.length(); i++)
					{
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						String[] area = jsonObject.getString("area").split(",");
						JSONArray data = jsonObject.getJSONArray("data");

						byte[] dataBytes = new byte[data.length()];

						for (int j = 0; j < data.length(); j++)
						{
							dataBytes[j] = Byte.valueOf(String.valueOf(data.get(j)));
						}

						imageLocks.add(new ImageLock(new Rectangle(Integer.valueOf(area[0]), Integer.valueOf(area[1]), Integer.valueOf(area[2]), Integer.valueOf(area[3])), ImageUtils.byteArrayToBufferImage(dataBytes)));
					}
				}
			}
			catch (IOException e)
			{
				JOptionPane.showMessageDialog(null, e.getMessage(), APP_NAME, JOptionPane.ERROR_MESSAGE);
			}

			loadImage(file, bufferedImage, imageLocks);
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(null, e.getMessage(), APP_NAME, JOptionPane.ERROR_MESSAGE);
		}
	}

	private void loadImage(File file, BufferedImage bufferedImage, List<ImageLock> imageLocks)
	{
		upperMenu.setActiveFile(file);
		upperMenu.enableSave(file.getName().endsWith(".pngl"));
		upperMenu.enableSaveAs(true);

		imageHolderPanel.setBufferedImage(bufferedImage, imageLocks);
		toolsPanel.reset();

		setTitle(APP_NAME + " - " + file.getAbsolutePath());
	}

	@Override
	public void saveAsFile(File filePath)
	{
		try
		{
			BufferedImage bufferImage = imageHolderPanel.getBufferImage();

			if (filePath.getName().endsWith(".jpg"))
			{
				ImageIO.write(bufferImage, "jpg", filePath);
			}

			else if (filePath.getName().endsWith(".bmp"))
			{
				ImageIO.write(bufferImage, "bmp", filePath);
			}

			else if (filePath.getName().endsWith(".png"))
			{
				ImageIO.write(bufferImage, "png", filePath);
			}

			else
			{
				JSONArray jsonArray = new JSONArray();

				List<ImageLock> imageLocks = imageHolderPanel.getImageLocks();

				for (ImageLock imageLock : imageLocks)
				{
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ImageIO.write(imageLock.getRealImage(), "png", baos);
					baos.close();

					JSONObject json = new JSONObject();
					json.put("area", String.format("%s,%s,%s,%s", imageLock.getArea().x, imageLock.getArea().y, imageLock.getArea().width, imageLock.getArea().height));
					json.put("data", baos.toByteArray());

					jsonArray.put(json);

				}

				String key;

				do
				{
					key = AlertInput.getPassword("Enter Password", GUI.APP_NAME);

					if (null == key)
					{
						return;
					}
				} while (key.isEmpty());

				String hashKey = EncryptionUtils.encrypt("validateClientSecretKey", key);
				String data = EncryptionUtils.encrypt(jsonArray.toString(), key);

				ImageIO.write(bufferImage, "png", filePath);

				try
				{
					Files.write(Paths.get(filePath.getAbsolutePath()), ("\nimage-lock," + hashKey + "\n" + data).getBytes(), StandardOpenOption.APPEND);
					upperMenu.setActiveFile(filePath);
					upperMenu.enableSave(true);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		catch (IOException | EncryptionException e)
		{
			JOptionPane.showMessageDialog(null, "error occured - " + e.getMessage());
		}
	}

	@Override
	public void close()
	{
		Object[] options = { "Close", "Cancel" };

		int result = JOptionPane.showOptionDialog(null, "What would you like to do?", APP_NAME, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);

		if (result == 0)
		{
			super.close();

			setVisible(false);
			dispose();
		}
	}
}
