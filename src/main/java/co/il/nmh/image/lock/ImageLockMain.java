package co.il.nmh.image.lock;

import java.io.File;

import co.il.nmh.image.lock.gui.GUI;

/**
 * @author Maor Hamami
 */

public class ImageLockMain
{
	public static void main(String[] args)
	{
		GUI gui = new GUI();

		if (args.length > 0 && !args[0].isEmpty())
		{
			gui.open(new File(args[0]));
		}
	}
}
