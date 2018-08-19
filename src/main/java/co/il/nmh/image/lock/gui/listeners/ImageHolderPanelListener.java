package co.il.nmh.image.lock.gui.listeners;

import java.util.List;

import co.il.nmh.image.lock.data.ImageLock;

/**
 * @author Maor Hamami
 */

public interface ImageHolderPanelListener
{
	void locksUpdated(List<ImageLock> imageLocks);
}
