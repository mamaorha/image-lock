package co.il.nmh.image.lock.gui.panels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import co.il.nmh.easy.swing.components.EasyImageHolder;
import co.il.nmh.easy.swing.utils.MouseRectangleSelectorUtil;
import co.il.nmh.easy.utils.ImageUtils;
import co.il.nmh.image.lock.data.ImageLock;
import co.il.nmh.image.lock.gui.enums.ToolEnum;
import co.il.nmh.image.lock.gui.listeners.ImageHolderPanelListener;
import co.il.nmh.image.lock.gui.listeners.ToolsPanelListener;

/**
 * @author Maor Hamami
 */

public class ImageHolderPanel extends EasyImageHolder implements ToolsPanelListener
{
	private static final long serialVersionUID = -1078848077286237573L;

	protected BufferedImage unLockedbufferedImage;
	protected ToolEnum activeTool;
	protected Rectangle selectedRect;
	protected Set<ImageLock> deletedLocks;
	protected List<ImageLock> imageLocks;

	protected Set<ImageHolderPanelListener> imageHolderPanelListeners;

	@Override
	protected void init(Object[] params)
	{
		imageLocks = new ArrayList<>();
		imageHolderPanelListeners = new HashSet<>();
	}

	@Override
	protected void addEvents()
	{
		new MouseRectangleSelectorUtil(this)
		{
			@Override
			protected Rectangle selectionChanged(Rectangle areaRect)
			{
				if (null != imageRect && areaRect.intersects(imageRect))
				{
					areaRect = areaRect.intersection(imageRect);
				}

				selectedRect = areaRect;
				repaint();

				return areaRect;
			}

			@Override
			protected void cancelSelection()
			{
				deletedLocks = null;
				selectedRect = null;

				repaint();
			}

			@Override
			protected void approveSelection(Rectangle areaRect)
			{
				switch (activeTool)
				{
					case LOCK:
						addLock(areaRect);
						break;
					case UNLOCK:
						removeLock(areaRect);
						break;
				}

				selectedRect = null;
				repaint();
			}
		};
	}

	@Override
	public void setActiveTool(ToolEnum activeTool)
	{
		this.activeTool = activeTool;
	}

	@Override
	protected void additionalPainting(Graphics g)
	{
		// draw the locks
		g.setColor(Color.BLACK);

		if (null != selectedRect)
		{
			if (activeTool == ToolEnum.LOCK)
			{
				g.fillRect(selectedRect.x, selectedRect.y, selectedRect.width, selectedRect.height);
			}

			else if (activeTool == ToolEnum.UNLOCK)
			{
				g.setColor(Color.WHITE);
				g.drawRect(selectedRect.x, selectedRect.y, selectedRect.width, selectedRect.height);

				for (ImageLock imageLock : imageLocks)
				{
					Rectangle realRect = createRealRect(selectedRect);

					if (imageLock.getArea().intersects(realRect))
					{
						if (null == deletedLocks)
						{
							deletedLocks = new LinkedHashSet<>();
						}

						deletedLocks.add(imageLock);
					}
				}
			}
		}
	}

	public void addImageHolderPanelListener(ImageHolderPanelListener imageHolderPanelListener)
	{
		imageHolderPanelListeners.add(imageHolderPanelListener);
		imageHolderPanelListener.locksUpdated(imageLocks);
	}

	public void setBufferedImage(BufferedImage bufferedImage, List<ImageLock> imageLocks)
	{
		this.bufferedImage = bufferedImage;
		this.unLockedbufferedImage = ImageUtils.copyImage(bufferedImage);

		if (null != imageLocks)
		{
			this.imageLocks = imageLocks;
		}

		else
		{
			this.imageLocks.clear();
		}

		repaint();
	}

	private void addLock(Rectangle areaRect)
	{
		Rectangle realRect = createRealRect(areaRect);
		BufferedImage subimage = ImageUtils.copyImage(unLockedbufferedImage.getSubimage(realRect.x, realRect.y, realRect.width, realRect.height));

		Graphics graphics = bufferedImage.getGraphics();
		graphics.setColor(Color.BLACK);
		graphics.fillRect(realRect.x, realRect.y, realRect.width, realRect.height);

		imageLocks.add(new ImageLock(realRect, subimage));

		updateLocksStatus();
	}

	private Rectangle createRealRect(Rectangle areaRect)
	{
		int x = (int) ((areaRect.x - shiftX) * maxRatio);
		int y = (int) ((areaRect.y - shiftY) * maxRatio);

		int width = (int) (areaRect.width * maxRatio);
		int height = (int) (areaRect.height * maxRatio);

		return new Rectangle(x, y, width, height);
	}

	private void removeLock(Rectangle areaRect)
	{
		if (null != deletedLocks)
		{
			for (ImageLock imageLock : deletedLocks)
			{
				bufferedImage.getGraphics().drawImage(imageLock.getRealImage(), (int) imageLock.getArea().getX(), (int) imageLock.getArea().getY(), null);
				imageLocks.remove(imageLock);
			}

			deletedLocks = null;
		}

		updateLocksStatus();
	}

	private void updateLocksStatus()
	{
		for (ImageHolderPanelListener imageHolderPanelListener : imageHolderPanelListeners)
		{
			imageHolderPanelListener.locksUpdated(imageLocks);
		}
	}

	public List<ImageLock> getImageLocks()
	{
		return imageLocks;
	}
}
