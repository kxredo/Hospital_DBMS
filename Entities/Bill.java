package Entities;
public class Bill {
    public final String status;
    public final double fees;
    public final int billNo;

    public Bill(String status, double fees, int billNo) {
        this.status = status;
        this.fees = fees;
        this.billNo = billNo;
    }

    // Getter methods for the properties
    public String getStatus() {
        return status;
    }

    public double getFees() {
        return fees;
    }

    public int getBillNo() {
        return billNo;
    }
}
