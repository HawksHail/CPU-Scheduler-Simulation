
/**
 *
 * @author Zion Mantey
 */
public class Job implements Comparable<Job> {

    public enum MODE {
        FCFS, RR, SPN, SRT, HRRN, FB
    }

    private final char name;
    private final int startTime;
    private final int duration;
    private int progress;
    static MODE mode;
    static int time; //only used by HRRN

    public Job(char name, int startTime, int duration) {
        this.name = name;
        this.startTime = startTime;
        this.duration = duration;
        progress = 0;
    }

    public char getName() {
        return name;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getDuration() {
        return duration;
    }

    public void makeProgress(int p) {
        progress += p;
    }

    public void resetProgress() {
        progress = 0;
    }

    public int getProgress() {
        return progress;
    }

    @Override
    public String toString() {
        return "{" + name + ", " + startTime + ", " + duration + ", " + progress + "}";
    }

    @Override
    public int compareTo(Job o) {
        if (null != mode) {
            switch (mode) {
                case SPN:
                    if (this.duration != o.duration) {
                        return this.duration - o.duration;
                    }
                    break;
                case SRT:
                    if ((this.duration - this.progress) != (o.duration - o.progress)) {
                        return (this.duration - this.progress) - (o.duration - o.progress);
                    }
                    break;
                case HRRN:
                    return ((time - this.startTime) + this.duration) / this.duration - ((time - o.startTime) + o.duration) / o.duration;
                default:
            }
        }
        return this.startTime - o.startTime;
    }

}
