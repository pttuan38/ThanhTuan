package GiuaKi;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.Period;
import java.util.concurrent.*;

class Student {
    String id;
    String name;
    String address;
    LocalDate dateOfBirth;

    public Student(String id, String name, String address, LocalDate dateOfBirth) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
}

class AgeCalculationThread implements Runnable {
    private BlockingQueue<Student> queue;
    private BlockingQueue<Student> resultQueue;

    public AgeCalculationThread(BlockingQueue<Student> queue, BlockingQueue<Student> resultQueue) {
        this.queue = queue;
        this.resultQueue = resultQueue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Student student = queue.take();
                LocalDate dob = student.getDateOfBirth();
                LocalDate currentDate = LocalDate.now();
                Period age = Period.between(dob, currentDate);
                student.dateOfBirth = dob.plus(age); 
                resultQueue.put(student);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

class DigitSumThread implements Runnable {
    private BlockingQueue<Student> queue;
    private BlockingQueue<Student> resultQueue;

    public DigitSumThread(BlockingQueue<Student> queue, BlockingQueue<Student> resultQueue) {
        this.queue = queue;
        this.resultQueue = resultQueue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Student student = queue.take();
                LocalDate dob = student.getDateOfBirth();
                int digitSum = calculateDigitSum(dob);
                student.address = Integer.toString(digitSum); 
                resultQueue.put(student);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private int calculateDigitSum(LocalDate date) {
        int sum = 0;
        String dateString = date.toString().replaceAll("-", "");
        for (char c : dateString.toCharArray()) {
            sum += Character.getNumericValue(c);
        }
        return sum;
    }
}

class PrimeCheckerThread implements Runnable {
    private BlockingQueue<Student> queue;

    public PrimeCheckerThread(BlockingQueue<Student> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Student student = queue.take();
                int digitSum = Integer.parseInt(student.address);
                boolean isPrime = isPrime(digitSum);
                student.address = isPrime ? "true" : "false";
                saveResult(student);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private boolean isPrime(int number) {
        if (number <= 1) {
            return false;
        }
        for (int i = 2; i <= Math.sqrt(number); i++) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }

    private void saveResult(Student student) {
        try {
            File file = new File("result.xml");
            FileWriter writer = new FileWriter(file, true);
            writer.write("<Student>\n");
            writer.write("\t<age>" + student.dateOfBirth + "</age>\n");
            writer.write("\t<sum>" + student.address + "</sum>\n");
            writer.write("\t<isDigit>" + student.name + "</isDigit>\n");
            writer.write("</Student>\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

public class GiuaKiII {
    public static void main(String[] args) {
        BlockingQueue<Student> readQueue = new LinkedBlockingQueue<>();
        BlockingQueue<Student> ageQueue = new LinkedBlockingQueue<>();
        BlockingQueue<Student> resultQueue = new LinkedBlockingQueue<>();

        ExecutorService executor = Executors.newFixedThreadPool(3);

        executor.execute(new AgeCalculationThread(readQueue, ageQueue));
        executor.execute(new DigitSumThread(ageQueue, resultQueue));
        executor.execute(new PrimeCheckerThread(resultQueue));

        try (Scanner scanner = new Scanner(new File("C:\\Users\\ACER\\eclipse-workspace\\TuanJava1\\src\\GiuaKi\\student.xml"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                String id = parts[0];
                String name = parts[1];
                String address = parts[2];
                LocalDate dob = LocalDate.parse(parts[3]);
                readQueue.put(new Student(id, name, address, dob));
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        executor.shutdown();
    }
}
