package Entities;
import java.util.Date;

public class Appointment {

    public final int appointmentId;
    public final int fee;
    public final Date startTime;
    public final Date endTime;
    public final int nurseId; 

    public Appointment(int appointmentId, int fee, Date startTime, Date endTime, int nurseId) {
        this.appointmentId = appointmentId;
        this.fee = fee;
        this.startTime = startTime;
        this.endTime = endTime;
        this.nurseId = nurseId;
    }

    // Getter methods for the properties
    public int getAppointmentId() {
        return appointmentId;
    }

    public int getFee() {
        return fee;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }
}
