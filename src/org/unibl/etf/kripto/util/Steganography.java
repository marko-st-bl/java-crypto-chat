package org.unibl.etf.kripto.util;

import java.io.File;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.image.DataBufferByte;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class Steganography {

	public Steganography() {
		super();
	}

	public boolean encode(String path, String stegan, byte[] message) {
		BufferedImage imageOriginal = getImage(path);

		BufferedImage image = userSpace(imageOriginal);
		image = addText(image, message);

		return (setImage(image, new File(stegan), "png"));
	}

	public byte[] decode(String path) {
		byte[] decode = null;
		try {
			BufferedImage image = userSpace(getImage(path));
			decode = decodeText(getByteData(image));
			return decode;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "There is no hidden message in this image!", "Error",
					JOptionPane.ERROR_MESSAGE);
			return decode;
		}
	}

	private BufferedImage getImage(String f) {
		BufferedImage image = null;
		File file = new File(f);

		try {
			image = ImageIO.read(file);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "Image could not be read!", "Error", JOptionPane.ERROR_MESSAGE);
		}
		return image;
	}

	private boolean setImage(BufferedImage image, File file, String ext) {
		try {
			file.delete(); // delete resources used by the File
			ImageIO.write(image, ext, file);
			return true;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "File could not be saved!", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}

	private BufferedImage addText(BufferedImage image, byte[] msg) {
		// convert all items to byte arrays: image, message, message length
		byte img[] = getByteData(image);
		byte len[] = bitConversion(msg.length);
		try {
			encodeText(img, len, 0); // 0 first positiong
			encodeText(img, msg, 32); // 4 bytes of space for length: 4bytes*8bit = 32 bits
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Target File cannot hold message!", "Error", JOptionPane.ERROR_MESSAGE);
		}
		return image;
	}

	private BufferedImage userSpace(BufferedImage image) {
		BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D graphics = newImage.createGraphics();
		graphics.drawRenderedImage(image, null);
		graphics.dispose(); // release all allocated memory for this image
		return newImage;
	}

	private byte[] getByteData(BufferedImage image) {
		WritableRaster raster = image.getRaster();
		DataBufferByte buffer = (DataBufferByte) raster.getDataBuffer();
		return buffer.getData();
	}

	private byte[] bitConversion(int i) {
		byte byte3 = (byte) ((i & 0xFF000000) >>> 24); // 0
		byte byte2 = (byte) ((i & 0x00FF0000) >>> 16); // 0
		byte byte1 = (byte) ((i & 0x0000FF00) >>> 8); // 0
		byte byte0 = (byte) ((i & 0x000000FF));
		return (new byte[] { byte3, byte2, byte1, byte0 });
	}

	private byte[] encodeText(byte[] image, byte[] addition, int offset) {
		// check that the data + offset will fit in the image
		if (addition.length + offset > image.length) {
			throw new IllegalArgumentException("File not long enough!");
		}
		// loop through each addition byte
		for (int i = 0; i < addition.length; ++i) {
			// loop through the 8 bits of each byte
			int add = addition[i];
			for (int bit = 7; bit >= 0; --bit, ++offset)
			{
				int b = (add >>> bit) & 1;
				image[offset] = (byte) ((image[offset] & 0xFE) | b);
			}
		}
		return image;
	}

	private byte[] decodeText(byte[] image) {
		int length = 0;
		int offset = 32;
		// loop through 32 bytes of data to determine text length
		for (int i = 0; i < 32; ++i){
			length = (length << 1) | (image[i] & 1);
		}

		byte[] result = new byte[length];

		// loop through each byte of text
		for (int b = 0; b < result.length; ++b) {
			// loop through each bit within a byte of text
			for (int i = 0; i < 8; ++i, ++offset) {
				result[b] = (byte) ((result[b] << 1) | (image[offset] & 1));
			}
		}
		return result;
	}

}
