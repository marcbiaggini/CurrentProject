package com.rockspoon.rockandui.TestDataModel;

import android.graphics.Bitmap;

import com.rockspoon.rockandui.Objects.SerialBitmap;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * CreditCard.java
 * Created by Kirill Chalenko <kirill.chalenko@itechart-group.com> on 1/21/16.
 */
@NoArgsConstructor
@Getter
@Setter
public class CreditCard implements Serializable {
    private String cardNumber;
    private SerialBitmap signatureImage;

    public boolean isSigned() {
        return signatureImage != null;
    }

	public Bitmap getSignatureImage() {
		return signatureImage != null ? signatureImage.getBitmap() : null;
	}

	public void setSignatureImage(Bitmap signatureBitmap) {
		signatureImage = new SerialBitmap(signatureBitmap, 90);
	}

	public String getLastFourDigits() {
		return (cardNumber != null && cardNumber.length() >= 4) ?
				cardNumber.substring(cardNumber.length() - 4) : "";
	}
}
