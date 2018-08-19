package co.il.nmh.image.lock.data;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Maor Hamami
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageLock
{
	private Rectangle area;
	private BufferedImage realImage;
}
