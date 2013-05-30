/**
 *   Copyright 2012-2013 Nekorp
 *
 *Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */
package org.nekorp.workflow.backend.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;

/**
 * inmutable, redondea .005 a .01
 */
public class MonedaHalfUpRound {
    
    private String value;
    
    public MonedaHalfUpRound() {
        this.value = "0.00";
    }
    
    private MonedaHalfUpRound(String val) {
        this.value = val;
    }
    
    public MonedaHalfUpRound suma(MonedaHalfUpRound op) {
       BigDecimal a = this.toBigDecimal(this);
       BigDecimal b = this.toBigDecimal(op);
       return this.fromBigDecimal(a.add(b));
    }
    
    public MonedaHalfUpRound resta(MonedaHalfUpRound op) {
        BigDecimal a = this.toBigDecimal(this);
        BigDecimal b = this.toBigDecimal(op);
        return this.fromBigDecimal(a.subtract(b));
    }
    
    public MonedaHalfUpRound multiplica(Integer escala) {
        BigDecimal a = this.toBigDecimal(this);
        BigDecimal b = new BigDecimal(escala);
        return this.fromBigDecimal(a.multiply(b));
    }
    
    public MonedaHalfUpRound multiplica(MonedaHalfUpRound op) {
        BigDecimal a = this.toBigDecimal(this);
        BigDecimal b = this.toBigDecimal(op);
        return this.fromBigDecimal(a.multiply(b));
    }
    
    private BigDecimal toBigDecimal(MonedaHalfUpRound m) {
        try {
            DecimalFormat nf = new DecimalFormat("#0.00");
            double loss = nf.parse(m.value).doubleValue();
            BigDecimal num = new BigDecimal(loss);
            return num.setScale(2, RoundingMode.HALF_UP);
        } catch (ParseException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
    
    private MonedaHalfUpRound fromBigDecimal(BigDecimal num) {
        BigDecimal rounded = num.setScale(2, RoundingMode.HALF_UP);
        DecimalFormat nf = new DecimalFormat("#0.00");
        return new MonedaHalfUpRound(nf.format(rounded.doubleValue()));
    }
    
    public double doubleValue() {
        try {
            DecimalFormat nf = new DecimalFormat("#0.00");
            return nf.parse(value).doubleValue();
        } catch (ParseException ex) {
            throw new IllegalArgumentException(ex);
        }
    }
    
    @Override
    public String toString() {
        return this.value;
    }
    public static MonedaHalfUpRound valueOf(String s) {
        if (s == null) {
            return new MonedaHalfUpRound();
        }
        if (s.length() == 0) {
            return new MonedaHalfUpRound();
        }
        if (s.matches("(\\-*\\d*)|(\\-\\d+.\\d{1,2})|(\\d*.\\d{1,2})")) {
            try {
                DecimalFormat nf = new DecimalFormat("#0.00");
                BigDecimal num = new BigDecimal(nf.parse(s).doubleValue());
                return new MonedaHalfUpRound(nf.format(num.doubleValue()));
            } catch (ParseException ex) {
                throw new IllegalArgumentException("no es una cantidad", ex);
            }
        } else {
            throw new IllegalArgumentException("no es una cantidad");
        }
    }
}
