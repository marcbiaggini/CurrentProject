package com.rockspoon.rockandui.TestDataModel;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Created by mac-248 on 1/29/16.
 */
@Getter
@Setter
@RequiredArgsConstructor
public class OrderSubPaymentData implements Serializable {
    @NonNull
    private BigDecimal amount;
    @NonNull
    private BigDecimal tips;

    public BigDecimal getPaymentTotal(){
        return amount.add(tips);
    }

}
