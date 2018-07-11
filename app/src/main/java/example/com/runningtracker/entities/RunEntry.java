package example.com.runningtracker.entities;
import java.util.Date;
import java.util.concurrent.TimeUnit;


//Main structure of each run entry
public class RunEntry
{
    public int ID;
    public long Duration;
    public double Distance;
    public Date Date;


    public RunEntry(long Duration, double Distance, Date Date)
    {
        this.Duration = Duration;
        this.Distance = Distance;
        this.Date = Date;
    }

    public RunEntry(int ID, long Duration, double Distance, Date Date)
    {
        this.ID = ID;
        this.Duration = Duration;
        this.Distance = Distance;
        this.Date = Date;
    }

    public void setDuration(long duration) {
        Duration = duration;
    }

    public void setDistance(double distance) {
        Distance = distance;
    }

    public void setDate(Date date) {
        Date = date;
    }

    public long getDuration() {
        return Duration;
    }

    public double getDistance() {
        return Distance;
    }

    public Date getDate() {
        return Date;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getID() {
        return ID;
    }


    //Reformats time into specific format
    private String toTimeFormat()
    {
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(Duration),
                TimeUnit.MILLISECONDS.toMinutes(Duration) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(Duration)),
                TimeUnit.MILLISECONDS.toSeconds(Duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(Duration)));
    }


    @Override
    //Returns distance with duration
    public String toString()
    {
        return "Distance: " + String.format("%.2f", Distance) + " km" + " Duration: " + toTimeFormat() + "\nOn " + Date;
    }
}
