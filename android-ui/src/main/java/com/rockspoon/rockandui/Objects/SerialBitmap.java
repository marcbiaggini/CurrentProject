package com.rockspoon.rockandui.Objects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * SerialBitmap.java
 * Created by Kirill Chalenko <kirill.chalenko@itechart-group.com> on 1/26/16.
 *
 * Bitmap wrapper that implements serializable.
 */
public class SerialBitmap implements Serializable {
	private static final long serialVersionUID = 1L;
	private Bitmap bitmap;
	private int quality;

	public Bitmap getBitmap() {
		return bitmap;
	}

	public SerialBitmap(Bitmap bitmap, int quality) {
		this.bitmap = bitmap;
		this.quality = quality;
	}

	public SerialBitmap(Bitmap bitmap) {
		this(bitmap, 100);
	}

	// Converts the Bitmap into a byte array for serialization
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteStream);
		byte bitmapBytes[] = byteStream.toByteArray();
		out.write(bitmapBytes, 0, bitmapBytes.length);
	}

	// Deserializes a byte array representing the Bitmap and decodes it
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

		int b;
		while ((b = in.read()) != -1) {
			byteStream.write(b);
		}

		byte bitmapBytes[] = byteStream.toByteArray();
		bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
	}
}
