import java.util.ArrayList;
import java.util.List;

// ============================================================
//  PATTERN #1 — FACTORY
//  Sedan/Sports/Smart машин тус бүр хаалга, дугуйны тоо, моторын
//  чадлаараа ялгаатай. Энэ ялгааг Car классын дотор if-else-ээр
//  бус, тусдаа Factory + дэд классуудад даалгасан тул Car өөрөө
//  "хэн намайг яаж угсрахыг" мэдэхгүй.
// ============================================================
enum CarType { SEDAN, SPORTS, SMART }

class CarFactory {
    public static Car createCar(CarType type, String brand, String model, Color color) {
        switch (type) {
            case SEDAN:  return new SedanCar(brand, model, color);
            case SPORTS: return new SportsCar(brand, model, color);
            case SMART:  return new SmartCar(brand, model, color);
            default: throw new IllegalArgumentException("Тодорхойгүй машины төрөл: " + type);
        }
    }
}

// ============================================================
//  PATTERN #2 — STRATEGY
//  Жолоодлогын горим бүр тусдаа класс тул шинэ горим нэмэхэд
//  Car болон дэд классуудын код огт өөрчлөгдөхгүй.
// ============================================================
interface DrivingMode {
    void adjustPerformance(int currentSpeed);
    String getModeName();
}

class EcoMode implements DrivingMode {
    private final int maxRpm = 3000;
    public void adjustPerformance(int currentSpeed) {
        System.out.println("  [Eco] max RPM=" + maxRpm + ", хурд=" + currentSpeed);
    }
    public String getModeName() { return "Eco"; }
}

class NormalMode implements DrivingMode {
    private final int maxRpm = 5000;
    public void adjustPerformance(int currentSpeed) {
        System.out.println("  [Normal] max RPM=" + maxRpm + ", хурд=" + currentSpeed);
    }
    public String getModeName() { return "Normal"; }
}

class SportMode implements DrivingMode {
    private final int maxRpm = 8000;
    public void adjustPerformance(int currentSpeed) {
        System.out.println("  [Sport] max RPM=" + maxRpm + ", хурд=" + currentSpeed);
    }
    public String getModeName() { return "Sport"; }
}

// ============================================================
//  PATTERN #3 — OBSERVER
//  Car хурдаа өөрчлөх бүрд dashboard, сэрэмжлүүлэгч гэрэл зэрэг
//  сонирхогч талд автоматаар мэдэгдэнэ. Car өөрөө тэднийг мэдэхгүй,
//  зөвхөн CarObserver интерфэйсээр л харилцана.
// ============================================================
interface CarObserver {
    void onSpeedChanged(String carLabel, int newSpeed);
}

class Dashboard implements CarObserver {
    public void onSpeedChanged(String carLabel, int newSpeed) {
        System.out.println("  [Dashboard] " + carLabel + " хурд: " + newSpeed + " км/ц");
    }
}

class SpeedWarningLight implements CarObserver {
    private final int redlineSpeed;
    public SpeedWarningLight(int redlineSpeed) { this.redlineSpeed = redlineSpeed; }
    public void onSpeedChanged(String carLabel, int newSpeed) {
        if (newSpeed >= redlineSpeed) {
            System.out.println("  [!! Warning] " + carLabel + " хэт хурдтай! (" + newSpeed + " км/ц)");
        }
    }
}

// ============================================================
//  Дэд эд ангиуд (Engine = composition, Wheel/Door = aggregation)
// ============================================================
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
    public void stop() { running = false; }
}

class Wheel {
    private final int sizeInInches;
    public Wheel(int sizeInInches) { this.sizeInInches = sizeInInches; }
}

class Door {
    private final String position;
    public Door(String position) { this.position = position; }
    public void open()  { System.out.println(position + " нээгдлээ"); }
    public void close() { System.out.println(position + " хаагдлаа"); }
}

enum Color { RED, BLACK, WHITE, BLUE }

// ============================================================
//  Car — abstract base. Шууд үүсэхгүй, зөвхөн дэд классаараа
//  дамжина. Хаалга/дугуйны тоог дэд класс тус бүр
//  getDoorCount()/getWheelCount()-оор өөрөө мэдүүлнэ.
// ============================================================
abstract class Car {
    protected final String brand;
    protected final String model;
    protected final Color color;
    protected int currentSpeed = 0;
    protected DrivingMode mode;
    protected final Engine engine;
    protected final List<Wheel> wheels = new ArrayList<>();
    protected final List<Door> doors = new ArrayList<>();
    private final List<CarObserver> observers = new ArrayList<>();

    protected Car(String brand, String model, Color color, int horsepower, String fuelType) {
        this.brand = brand;
        this.model = model;
        this.color = color;
        this.engine = new Engine(horsepower, fuelType);
        // Санамж: энд abstract методыг constructor дотор дуудаж байгаа нь
        // ерөнхийдөө Java-д болгоомжтой хэрэглэх ёстой зүйл (subclass-ийн
        // талбарууд хараахан бэлэн болоогүй байдаг). Гэхдээ getDoorCount()/
        // getWheelCount() нь зөвхөн тогтмол тоо буцаадаг тул энд аюулгүй.
        for (int i = 0; i < getWheelCount(); i++) wheels.add(new Wheel(17));
        for (int i = 0; i < getDoorCount(); i++) doors.add(new Door("Хаалга-" + (i + 1)));
        this.mode = new NormalMode();
    }

    protected abstract int getDoorCount();
    protected abstract int getWheelCount();

    public void addObserver(CarObserver o) { observers.add(o); }
    private void notifyObservers() {
        for (CarObserver o : observers) o.onSpeedChanged(brand + " " + model, currentSpeed);
    }

    public void setMode(DrivingMode mode) {
        this.mode = mode;
        System.out.println(">> Горим солигдлоо: " + mode.getModeName());
    }

    public void startEngine() { engine.start(); }

    public void accelerate(int amount) {
        currentSpeed += amount;
        mode.adjustPerformance(currentSpeed);
        notifyObservers();
    }

    public void brake(int amount) {
        currentSpeed = Math.max(0, currentSpeed - amount);
        notifyObservers();
    }

    public void openAllDoors()  { for (Door d : doors) d.open(); }
    public void closeAllDoors() { for (Door d : doors) d.close(); }

    public void printInfo() {
        System.out.println(brand + " " + model + " (" + color + ") — "
                + doors.size() + " хаалга, " + wheels.size() + " дугуй, хурд=" + currentSpeed);
    }
}

// ------------------- Гурван бодит машины төрөл -------------------
class SedanCar extends Car {
    public SedanCar(String brand, String model, Color color) {
        super(brand, model, color, 150, "Petrol");
    }
    protected int getDoorCount()  { return 4; }
    protected int getWheelCount() { return 4; }
}

class SportsCar extends Car {
    public SportsCar(String brand, String model, Color color) {
        super(brand, model, color, 450, "Petrol");
    }
    protected int getDoorCount()  { return 2; }
    protected int getWheelCount() { return 4; }

    // Спорт машин Eco горимд шилжихгүй — Strategy pattern дээр
    // нэмсэн бодит бизнесийн дүрмийн жишээ.
    @Override
    public void setMode(DrivingMode mode) {
        if (mode instanceof EcoMode) {
            System.out.println("!! Спорт машин Eco горимд шилжихгүй, хэвээрээ үлдлээ.");
            return;
        }
        super.setMode(mode);
    }
}

class SmartCar extends Car {
    public SmartCar(String brand, String model, Color color) {
        super(brand, model, color, 60, "Electric");
        setMode(new EcoMode()); // жижиг автомат машин анхандаа Eco горимтой эхэлнэ
    }
    protected int getDoorCount()  { return 1; }
    protected int getWheelCount() { return 4; }
}

// ============================================================
//  Демо: Factory-гаар үүсгэж → Strategy-гаар удирдаж → Observer-оор ажиглана
// ============================================================
public class CarDemo {
    public static void main(String[] args) {
        System.out.println("=== 1) SEDAN (4 хаалгатай) ===");
        Car sedan = CarFactory.createCar(CarType.SEDAN, "Toyota", "Camry", Color.WHITE);
        sedan.addObserver(new Dashboard());
        sedan.addObserver(new SpeedWarningLight(120));
        sedan.printInfo();
        sedan.startEngine();
        sedan.accelerate(60);
        sedan.accelerate(70);

        System.out.println("\n=== 2) SPORTS (2 хаалгатай) ===");
        Car sports = CarFactory.createCar(CarType.SPORTS, "Ferrari", "F8", Color.RED);
        sports.addObserver(new Dashboard());
        sports.addObserver(new SpeedWarningLight(200));
        sports.printInfo();
        sports.startEngine();
        sports.setMode(new EcoMode());   // татгалзана
        sports.setMode(new SportMode()); // зөвшөөрнө
        sports.accelerate(220);

        System.out.println("\n=== 3) SMART (1 хаалгатай, автомат жижиг машин) ===");
        Car smart = CarFactory.createCar(CarType.SMART, "EasyMile", "EZ10", Color.BLUE);
        smart.addObserver(new Dashboard());
        smart.printInfo();
        smart.startEngine();
        smart.openAllDoors();
        smart.accelerate(15);
    }
}