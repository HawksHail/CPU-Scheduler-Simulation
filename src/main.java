
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simulates several OS job schedulers.
 *
 * @author Zion Mantey
 */
public class main {

    public static final int QUANTUM = 1;

    /**
     * Loads jobs from specified file
     *
     * @param filename jobs file containing job letter, start time, and duration
     * @return array containing all jobs in the file.
     */
    private static Job[] loadJobs(String filename) {
        File f = new File(filename);
        if (f.exists()) {
            ArrayList<Job> jobs = new ArrayList<>();
            try {
                Scanner in = new Scanner(f);
                while (in.hasNextLine()) {
                    String[] line = in.nextLine().trim().split("\t");
                    if (line.length >= 3) {
                        try {
                            jobs.add(new Job(line[0].charAt(0), Integer.parseInt(line[1]), Integer.parseInt(line[2])));
                        } catch (NumberFormatException ex) {
                            System.err.println("Invalid line in file: integer expected");
                        }
                    }
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
            }
            return jobs.toArray(new Job[jobs.size()]);
        }
        return null;
    }

    /**
     * First come first serve
     *
     * @param jobs array of jobs to process
     */
    private static void FCFS(Job[] jobs) {
        resetJobs(jobs);
        ArrayList<Character> timeList = new ArrayList<>();
        Queue<Job> queue = new LinkedList<>();
        int index = 0;
        int time = 0;
        Job current;
        do {
            while (index < jobs.length && jobs[index].getStartTime() == time) {
                queue.add(jobs[index]);
                index++;
            }
            if (!queue.isEmpty()) {
                current = queue.remove();
                while (current.getProgress() < current.getDuration()) {
                    while (index < jobs.length && jobs[index].getStartTime() == time) {
                        queue.add(jobs[index]);
                        index++;
                    }
                    current.makeProgress(1);
                    timeList.add(current.getName());
                    time++;
                }
            } else {
                timeList.add(' ');
                time++;
            }
        } while (!queue.isEmpty() || index < jobs.length);
        System.out.println("FCFS");
        printTime(jobs, timeList);
    }

    /**
     * round robin
     *
     * @param jobs array of jobs to process
     */
    private static void RR(Job[] jobs) {
        resetJobs(jobs);
        ArrayList<Character> timeList = new ArrayList<>();
        Queue<Job> queue = new LinkedList<>();
        int time = 0;
        int index = 0;
        Job current;
        do {
            while (index < jobs.length && jobs[index].getStartTime() == time) {
                queue.add(jobs[index]);
                index++;
            }
            if (!queue.isEmpty()) {
                current = queue.remove();
                for (int i = 0; current.getProgress() < current.getDuration() && i < QUANTUM; i++) {
                    current.makeProgress(1);
                    timeList.add(current.getName());
                    time++;
                    while (index < jobs.length && jobs[index].getStartTime() == time) {
                        queue.add(jobs[index]);
                        index++;
                    }
                }
                if (current.getProgress() < current.getDuration()) {
                    while (index < jobs.length && jobs[index].getStartTime() == time) {
                        queue.add(jobs[index]);
                        index++;
                    }
                    queue.add(current);
                }
            } else {
                timeList.add(' ');
                time++;
            }
        } while (!queue.isEmpty() || index < jobs.length);
        System.out.println("RR");
        printTime(jobs, timeList);
    }

    /**
     * shortest process next
     *
     * @param jobs array of jobs to process
     */
    public static void SPN(Job[] jobs) {
        resetJobs(jobs);
        Job.mode = Job.MODE.SPN;
        ArrayList<Character> timeList = new ArrayList<>();
        Queue<Job> queue = new PriorityQueue<>();
        int index = 0;
        int time = 0;
        Job current;
        do {
            while (index < jobs.length && jobs[index].getStartTime() == time) {
                queue.add(jobs[index]);
                index++;
            }
            if (!queue.isEmpty()) {
                current = queue.remove();
                while (current.getProgress() < current.getDuration()) {
                    while (index < jobs.length && jobs[index].getStartTime() == time) {
                        queue.add(jobs[index]);
                        index++;
                    }
                    current.makeProgress(1);
                    timeList.add(current.getName());
                    time++;
                }
            } else {
                timeList.add(' ');
                time++;
            }
        } while (!queue.isEmpty() || index < jobs.length);
        System.out.println("SPN");
        printTime(jobs, timeList);
    }

    /**
     * shortest remaining time
     *
     * @param jobs array of jobs to process
     */
    public static void SRT(Job[] jobs) {
        resetJobs(jobs);
        Job.mode = Job.MODE.SRT;
        ArrayList<Character> timeList = new ArrayList<>();
        Queue<Job> queue = new PriorityQueue<>();
        int index = 0;
        int time = 0;
        Job current;
        do {
            while (index < jobs.length && jobs[index].getStartTime() == time) {
                queue.add(jobs[index]);
                index++;
            }
            if (!queue.isEmpty()) {
                current = queue.peek();
                current.makeProgress(1);
                timeList.add(current.getName());
                if (current.getProgress() == current.getDuration()) {
                    queue.remove();
                }
            } else {
                timeList.add(' ');
            }
            time++;
        } while (!queue.isEmpty() || index < jobs.length);
        System.out.println("SRT");
        printTime(jobs, timeList);
    }

    /**
     * highest response ration next
     *
     * @param jobs array of jobs to process
     */
    public static void HRRN(Job[] jobs) {
        resetJobs(jobs);
        Job.mode = Job.MODE.HRRN;
        ArrayList<Character> timeList = new ArrayList<>();
        Queue<Job> queue = new PriorityQueue<>();
        LinkedList<Job> a = new LinkedList<>();
        int index = 0;
        Job.time = 0;
        Job current;
        do {
            while (index < jobs.length && jobs[index].getStartTime() == Job.time) {
                queue.add(jobs[index]);
                index++;
            }
            if (queue.size() > 1) {
                while (!queue.isEmpty()) {  //move all jobs to list a
                    a.add(queue.remove());
                }
                while (!a.isEmpty()) {      //requeue all elements so priority is recalculated with new time. dumb method tbh
                    queue.add(a.remove());
                }
            }
            if (!queue.isEmpty()) {
                current = queue.remove();
                while (current.getProgress() < current.getDuration()) {
                    while (index < jobs.length && jobs[index].getStartTime() == Job.time) {
                        queue.add(jobs[index]);
                        index++;
                    }
                    current.makeProgress(1);
                    timeList.add(current.getName());
                    Job.time++;
                }
            } else {
                timeList.add(' ');
                Job.time++;
            }
        } while (!queue.isEmpty() || index < jobs.length);
        System.out.println("HRRN");
        printTime(jobs, timeList);
    }

    /**
     * feedback
     *
     * @param jobs array of jobs to process
     */
    public static void FB(Job[] jobs) {
        resetJobs(jobs);
        Job.mode = Job.MODE.FB;
        ArrayList<Character> timeList = new ArrayList<>();
        Queue<Job> queue1 = new LinkedList<>();
        Queue<Job> queue2 = new LinkedList<>();
        Queue<Job> queue3 = new LinkedList<>();
        int index = 0;
        int time = 0;
        Job current;
        int from;
        do {
            while (index < jobs.length && jobs[index].getStartTime() == time) {
                queue1.add(jobs[index]);
                index++;
            }
            if (!queue1.isEmpty()) {
                current = queue1.remove();
                from = 1;
            } else if (!queue2.isEmpty()) {
                current = queue2.remove();
                from = 2;
            } else if (!queue3.isEmpty()) {
                current = queue3.remove();
                from = 3;
            } else {
                time++;
                timeList.add(' ');
                continue;
            }
            do {
                for (int i = 0; current.getProgress() < current.getDuration() && i < QUANTUM; i++) {
                    current.makeProgress(1);
                    timeList.add(current.getName());
                    time++;
                    while (index < jobs.length && jobs[index].getStartTime() == time) {
                        queue1.add(jobs[index]);
                        index++;
                    }
                }
            } while (current.getProgress() < current.getDuration() && queue1.isEmpty() && queue2.isEmpty() && queue3.isEmpty());
            if (current.getProgress() < current.getDuration()) {
                switch (from) {
                    case 1:
                        queue2.add(current);
                        break;
                    case 2:
                        queue3.add(current);
                        break;
                    case 3:
                        queue3.add(current);
                        break;
                }
            }

        } while (!queue1.isEmpty() || !queue2.isEmpty() || !queue3.isEmpty() || index < jobs.length);
        System.out.println("FB");
        printTime(jobs, timeList);
    }

    /**
     * clears the progress of the jobs
     *
     * @param jobs
     */
    public static void resetJobs(Job[] jobs) {
        for (Job job : jobs) {
            job.resetProgress();
        }
    }

    /**
     * prints a formated graph of execution
     *
     * @param jobs
     * @param time
     */
    public static void printTime(Job[] jobs, ArrayList<Character> time) {
        StringBuilder sb = new StringBuilder((jobs.length + 1) * time.size());
        for (Job job : jobs) {
            sb.append(job.getName());
            sb.append("  ");
            for (int i = 0; i < time.size(); i++) {
                if (job.getName() == time.get(i)) {
                    sb.append('X');
                } else {
                    sb.append(" ");
                }
            }
            sb.append('\n');
        }
        System.out.println(sb.toString());
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Please enter a scheduler parameter (FCFS, RR, SPN, SRT, HRRN, FB, or ALL)");
            return;
        }
        Job[] jobs = loadJobs("jobs.txt");
        if (jobs == null) {
            System.err.println("jobs.txt not found");
            return;
        }
        switch (args[0].toUpperCase()) {
            case "ALL":
                FCFS(jobs);
                RR(jobs);
                SPN(jobs);
                SRT(jobs);
                HRRN(jobs);
                FB(jobs);
                break;
            case "FCFS":
                FCFS(jobs);
                break;
            case "RR":
                RR(jobs);
                break;
            case "SPN":
                SPN(jobs);
                break;
            case "SRT":
                SRT(jobs);
                break;
            case "HRRN":
                HRRN(jobs);
                break;
            case "FB":
                FB(jobs);
                break;
            default:
                System.err.println("Unknown option");
        }

    }

}
