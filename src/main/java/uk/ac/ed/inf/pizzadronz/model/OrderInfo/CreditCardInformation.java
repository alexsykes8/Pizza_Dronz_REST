package uk.ac.ed.inf.pizzadronz.model.OrderInfo;

import java.util.Objects;

/**
 * Represents credit card info.
 */
public class CreditCardInformation {
    private String creditCardNumber;
    private String creditCardExpiry;
    private String cvv;

    public CreditCardInformation() {
    }

    public CreditCardInformation(String creditCardNumber, String creditCardExpiry, String cvv) {
        this.creditCardNumber = creditCardNumber;
        this.creditCardExpiry = creditCardExpiry;
        this.cvv = cvv;
    }

    public String getCvv() {
        return cvv;
    }

    public String getCreditCardExpiry() {
        return creditCardExpiry;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreditCardInformation that = (CreditCardInformation) o;
        return Objects.equals(creditCardNumber, that.creditCardNumber) &&
                Objects.equals(creditCardExpiry, that.creditCardExpiry) &&
                Objects.equals(cvv, that.cvv);
    }


    /**
     * allows pretty printing
     */
    @Override
    public String toString() {
        return "CreditCardInformation{" +
                "creditCardNumber='" + creditCardNumber + '\'' +
                ", creditCardExpiry='" + creditCardExpiry + '\'' +
                ", cvv='" + cvv + '\'' +
                '}';
    }

}
