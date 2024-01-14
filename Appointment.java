
import java.util.Date;

public class Appointment {

    private final int appointmentId;
    //private final int fee;
    private final String startTime;
    private final String endTime;
    // private final int nurseId; 
    private final String concerns;
    private final String symptoms;
    private final String status;
    private final String patientName;

    public Appointment(int appointmentId, /*int fee, */String startTime, String endTime, /*int nurseId,*/ String concerns, String symptoms, String status, String patientName) {
        this.appointmentId = appointmentId;
       // this.fee = fee;
        this.startTime = startTime;
        this.endTime = endTime;
        //this.nurseId = nurseId;
        this.concerns = concerns;
        this.symptoms = symptoms;
        this.status = status;
        this.patientName = patientName;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    // public int getFee() {
    //     return fee;
    // }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    // public int getNurseId() {
    //     return nurseId;
    // }

    public String getConcerns() {
        return concerns;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public String getStatus() {
        return status;
    }

    public String getPatientName() {
        return patientName;
    }
}
