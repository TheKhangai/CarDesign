import java.util.ArrayList;
import java.util.List;

// ===================== Strategy pattern: жолоодлогын горим =====================
// DrivingMode нь бүх горимын "гэрээ". Car класс энэ интерфэйсээр л ажилладаг тул
// шинэ горим нэмэхэд Car-ыг өөрчлөх шаардлагагүй (Open-Closed зарчим).
interface DrivingMode {
    void adjustPerformance(int currentSpeed); // хурдны логик
    String getModeName();
}

class EcoMode implements DrivingMode {
    private final int maxRpm = 3000;
    public void adjustPerformance(int currentSpeed) {
        System.out.println("[Eco] Түлш хэмнэх горим, max RPM=" + maxRpm + ", хурд=" + currentSpeed);
    }
    public String getModeName() { return "Eco"; }
}

class NormalMode implements DrivingMode {
    private final int maxRpm = 5000;
    public void adjustPerformance(int currentSpeed) {
        System.out.println("[Normal] Энгийн горим, max RPM=" + maxRpm + ", хурд=" + currentSpeed);
    }
    public String getModeName() { return "Normal"; }
}

class SportMode implements DrivingMode {
    private final int maxRpm = 7000;
    public void adjustPerformance(int currentSpeed) {
        System.out.println("[Sport] Спорт горим, max RPM=" + maxRpm + ", хурд=" + currentSpeed);
    }
    public String getModeName() { return "Sport"; }
}

// ===================== Дэд эд ангиуд =====================
class Engine {
    private final int horsepower;
    private final String fuelType;
    private boolean running = false;

    public Engine(int horsepower, String fuelType) {
        this.horsepower = horsepower;
        this.fuelType = fuelType;
    }
    public void start() {
        running = true;
        System.out.println("Мотор аслаа (" + horsepower + " hp, " + fuelType + ")");
    }
    public void stop() {
        running = false;
        System.out.println("Мотор унтарлаа");
    }
    public boolean isRunning() { return running; }
}

class Wheel {
    private final int sizeInInches;
    private double pressurePsi;

    public Wheel(int sizeInInches, double pressurePsi) {
        this.sizeInInches = sizeInInches;
        this.pressurePsi = pressurePsi;
    }
    public void inflate(double psi) {
        this.pressurePsi = psi;
        System.out.println(sizeInInches + "\" дугуйны даралт " + psi + " psi боллоо");
    }
}

class Door {
    private final String position; // жишээ нь "жолооч", "баруун ар"
    private boolean isOpen = false;

    public Door(String position) { this.position = position; }
    public void open() {
        isOpen = true;
        System.out.println(position + " хаалга нээгдлээ");
    }
    public void close() {
        isOpen = false;
        System.out.println(position + " хаалга хаагдлаа");
    }
}

enum Color { RED, BLACK, WHITE, BLUE }

// ===================== Гол класс: Car =====================
// Engine-тэй composition (Car байхгүй бол Engine ч оршихгүй),
// Wheel/Door-той aggregation (жагсаалт хэлбэрээр эзэмшдэг ч тусад нь солигдож болно),
// DrivingMode-той Strategy хамаарал (ашиглана, гэхдээ өөрөө үүсгэдэггүй).
class Car {
    private final String brand;
    private final String model;
    private final Color color;
    private int currentSpeed = 0;
    private DrivingMode mode;              // Strategy-г "барьж" байгаа reference
    private final Engine engine;           // composition
    private final List<Wheel> wheels = new ArrayList<>(); // aggregation
    private final List<Door> doors = new ArrayList<>();   // aggregation

    public Car(String brand, String model, Color color) {
        this.brand = brand;
        this.model = model;
        this.color = color;
        this.engine = new Engine(150, "Petrol");
        for (int i = 0; i < 4; i++) wheels.add(new Wheel(17, 32.0));
        doors.add(new Door("жолооч"));
        doors.add(new Door("зорчигч"));
        doors.add(new Door("зүүн ар"));
        doors.add(new Door("баруун ар"));
        this.mode = new NormalMode(); // анхны утга
    }

    public void setMode(DrivingMode mode) {
        this.mode = mode;
        System.out.println(">> Горим солигдлоо: " + mode.getModeName());
    }

    public void startEngine() { engine.start(); }

    public void accelerate(int amount) {
        currentSpeed += amount;
        mode.adjustPerformance(currentSpeed); // Car өөрөө логикоо мэдэхгүй, mode-д даалгасан
    }

    public void brake(int amount) {
        currentSpeed = Math.max(0, currentSpeed - amount);
        System.out.println("Хурд буурлаа: " + currentSpeed);
    }

    public void openAllDoors()  { for (Door d : doors) d.open(); }
    public void closeAllDoors() { for (Door d : doors) d.close(); }

    public void printInfo() {
        System.out.println(brand + " " + model + " (" + color + "), одоогийн хурд=" + currentSpeed);
    }
}

// ===================== Демо ажиллуулах =====================
public class CarDemo {
    public static void main(String[] args) {
        Car car = new Car("Toyota", "Prius", Color.BLUE);
        car.printInfo();
        car.startEngine();
        car.openAllDoors();
        car.closeAllDoors();

        // Анхны горим Normal
        car.accelerate(40);

        // Жолооч Eco горим руу шилжүүлэв — Car классын код хөндөгдөөгүй
        car.setMode(new EcoMode());
        car.accelerate(20);

        // Sport горим руу шилжүүлэв
        car.setMode(new SportMode());
        car.accelerate(60);

        car.brake(30);
        car.printInfo();
    }
}

