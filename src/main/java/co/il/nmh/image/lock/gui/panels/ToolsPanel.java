package co.il.nmh.image.lock.gui.panels;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.SwingConstants;

import co.il.nmh.easy.swing.components.group.EasyGroup;
import co.il.nmh.easy.swing.components.group.EasyGroupButton;
import co.il.nmh.easy.swing.components.gui.EasyPanel;
import co.il.nmh.easy.utils.ResourceUtils;
import co.il.nmh.image.lock.data.ImageLock;
import co.il.nmh.image.lock.gui.enums.ToolEnum;
import co.il.nmh.image.lock.gui.listeners.ImageHolderPanelListener;
import co.il.nmh.image.lock.gui.listeners.ToolsPanelListener;

/**
 * @author Maor Hamami
 */
public class ToolsPanel extends EasyPanel implements ImageHolderPanelListener
{
	private static final long serialVersionUID = -265386756673455684L;

	protected EasyGroupButton lockBtn;
	protected EasyGroupButton unlockBtn;

	protected Set<ToolsPanelListener> toolsPanelListeners;

	@Override
	protected void init(Object[] params)
	{
		toolsPanelListeners = new HashSet<>();
	}

	@Override
	protected void buildPanel()
	{
		EasyGroup easyGroup = new EasyGroup();

		lockBtn = new EasyGroupButton(easyGroup, ResourceUtils.getIcon("lock-icon.png"));
		lockBtn.setFont(new Font("Arial", Font.BOLD, 10));
		lockBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
		lockBtn.setHorizontalTextPosition(SwingConstants.CENTER);
		lockBtn.setText(ToolEnum.LOCK.getValue());
		lockBtn.setEnabled(false);
		add(lockBtn);

		unlockBtn = new EasyGroupButton(easyGroup, ResourceUtils.getIcon("lock-unlock-icon.png"));
		unlockBtn.setFont(new Font("Arial", Font.BOLD, 10));
		unlockBtn.setVerticalTextPosition(SwingConstants.BOTTOM);
		unlockBtn.setHorizontalTextPosition(SwingConstants.CENTER);
		unlockBtn.setText(ToolEnum.UNLOCK.getValue());
		unlockBtn.setEnabled(false);
		add(unlockBtn);
	}

	@Override
	protected void addEvents()
	{
		lockBtn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				updateActiveTool(ToolEnum.LOCK);
			}
		});

		unlockBtn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				updateActiveTool(ToolEnum.UNLOCK);
			}
		});
	}

	private void updateActiveTool(ToolEnum tool)
	{
		for (ToolsPanelListener toolsPanelListener : toolsPanelListeners)
		{
			toolsPanelListener.setActiveTool(tool);
		}
	}

	public void addToolsPanelListener(ToolsPanelListener toolsPanelListener)
	{
		toolsPanelListeners.add(toolsPanelListener);
	}

	public void reset()
	{
		lockBtn.setEnabled(true);
		lockBtn.setActive(true);

		updateActiveTool(ToolEnum.LOCK);
	}

	@Override
	public void locksUpdated(List<ImageLock> imageLocks)
	{
		unlockBtn.setEnabled(!imageLocks.isEmpty());

		if (!unlockBtn.isEnabled())
		{
			lockBtn.setActive(true);
			updateActiveTool(ToolEnum.LOCK);
		}
	}
}
