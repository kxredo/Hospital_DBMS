package Entities;
import java.util.Date;
import java.util.List;

public class MedicalHistory {
    public final List<String> surgeries;
    public final List<String> medications;
    public final List<String> conditions;
    public final Date date;

    public MedicalHistory(List<String> surgeries, List<String> medications, List<String> conditions, Date date) {
        this.surgeries = surgeries;
        this.medications = medications;
        this.conditions = conditions;
        this.date = date;
    }

    // Getter methods for the properties
    public List<String> getSurgeries() {
        return surgeries;
    }

    public List<String> getMedications() {
        return medications;
    }

    public List<String> getConditions() {
        return conditions;
    }

    public Date getDate() {
        return date;
    }
}
