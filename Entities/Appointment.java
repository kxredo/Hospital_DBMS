package Entities;
import java.util.Date;

public class Appointment {

    public final int appointmentId;
    public final int fee;
    public final Date startTime;
    public final Date endTime;

    public Appointment(int appointmentId, int fee, Date startTime, Date endTime) {
        this.appointmentId = appointmentId;
        this.fee = fee;
        this.startTime = startTime;
        this.endTime = endTime;
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
