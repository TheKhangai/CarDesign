import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// ============================================================
//  PATTERN #1 — FACTORY
//  Sedan/Sports/Smart машин тус бүр хаалга, дугуйны тоо, моторын
//  төрлөөрөө ялгаатай. Энэ ялгааг Car классын дотор if-else-ээр
//  бус, тусдаа Factory + дэд классуудад даалгасан.
// ============================================================
enum CarType { SEDAN, SPORTS, SMART }

class CarFactory {
    public static Car createCar(CarType type, String brand, String model, Color color) {
        Car car;
        switch (type) {
            case SEDAN:  car = new SedanCar(brand, model, color); break;
            case SPORTS: car = new SportsCar(brand, model, color); break;
            case SMART:  car = new SmartCar(brand, model, color); break;
            default: throw new IllegalArgumentException("Тодорхойгүй машины төрөл: " + type);
        }
        // Factory өөрөө үүсгэсэн машин бүрээ Singleton registry-д бүртгэнэ.
        CarRegistry.getInstance().register(car);
        return car;
    }
}

// ============================================================
//  PATTERN #2 — SINGLETON
//  Программ даяар машины бүртгэл хөтлөгч НЭГ л объект байх ёстой,
//  тиймээс constructor-ыг private болгож, гадаад талаас зөвхөн
//  getInstance()-оор л хандах боломжтой болгосон.
// ============================================================
class CarRegistry {
    // Eager initialization — класс ачаалагдмагц нэг л удаа үүснэ, thread-safe.
    private static final CarRegistry INSTANCE = new CarRegistry();

    private final List<Car> cars = new ArrayList<>();

    private CarRegistry() { } // гаднаас "new CarRegistry()" хийхийг хориглоно

    public static CarRegistry getInstance() { return INSTANCE; }

    public void register(Car car) { cars.add(car); }

    public int size() { return cars.size(); }

    public List<Car> getAllCars() { return Collections.unmodifiableList(cars); }
}

// ============================================================
//  PATTERN #3 — STRATEGY
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
//  PATTERN #4 — OBSERVER
//  Car хурдаа өөрчлөх бүрд dashboard, сэрэмжлүүлэгч гэрэл зэрэг
//  сонирхогч талд автоматаар мэдэгдэнэ. addObserver/removeObserver
//  хоёулаа байгаа тул ажиглагчийг ажиллаж байх зуур нь ч болзошгүй.
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
//  Engine — ЭНЭ БОЛ НЭРТЭЙ GoF PATTERN БИШ, зүгээр л
//  interface-based polymorphism. Petrol/Electric моторын ажиллах
//  зарчим өөр тул Car нь "хэдэн hp вэ, ямар түлш вэ" гэсэн string
//  барихын оронд Engine интерфэйсийг л мэднэ.
// ============================================================
interface Engine {
    void start();
    void stop();
}

class PetrolEngine implements Engine {
    private final int horsepower;
    private boolean running = false;
    public PetrolEngine(int horsepower) { this.horsepower = horsepower; }
    public void start() {
        running = true;
        System.out.println("Бензин мотор аслаа (" + horsepower + " hp)");
    }
    public void stop() { running = false; }
}

class ElectricEngine implements Engine {
    private final int horsepower;
    private boolean running = false;
    public ElectricEngine(int horsepower) { this.horsepower = horsepower; }
    public void start() {
        running = true;
        System.out.println("Цахилгаан мотор дуугүй аслаа (" + horsepower + " hp)");
    }
    public void stop() { running = false; }
}

// ============================================================
//  Дэд эд ангиуд (Wheel/Door = aggregation)
// ============================================================
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
//  дамжина. getDoorCount()/getWheelCount() нь public болсон тул
//  JUnit тестээс шууд шалгаж болно (тестлэх боломжийг сайжруулав).
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

    protected Car(String brand, String model, Color color, Engine engine) {
        this.brand = brand;
        this.model = model;
        this.color = color;
        this.engine = engine;
        // Санамж: abstract методыг constructor дотор дуудаж байгаа нь
        // Java-д ерөнхийдөө болгоомжтой хэрэглэдэг зүйл (subclass-ийн
        // field хараахан бэлэн болоогүй байдаг). getDoorCount()/
        // getWheelCount() зөвхөн тогтмол тоо буцаадаг тул энд аюулгүй.
        for (int i = 0; i < getWheelCount(); i++) wheels.add(new Wheel(17));
        for (int i = 0; i < getDoorCount(); i++) doors.add(new Door("Хаалга-" + (i + 1)));
        this.mode = new NormalMode();
    }

    public abstract int getDoorCount();
    public abstract int getWheelCount();

    public void addObserver(CarObserver o) { observers.add(o); }
    public void removeObserver(CarObserver o) { observers.remove(o); }
    private void notifyObservers() {
        for (CarObserver o : observers) o.onSpeedChanged(brand + " " + model, currentSpeed);
    }

    public void setMode(DrivingMode mode) {
        this.mode = mode;
        System.out.println(">> Горим солигдлоо: " + mode.getModeName());
    }

    public String getCurrentModeName() { return mode.getModeName(); }

    public void startEngine() { engine.start(); }

    public void accelerate(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Хурдасгах хэмжээ сөрөг байж болохгүй: " + amount);
        }
        currentSpeed += amount;
        mode.adjustPerformance(currentSpeed);
        notifyObservers();
    }

    public void brake(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Тормослох хэмжээ сөрөг байж болохгүй: " + amount);
        }
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
        super(brand, model, color, new PetrolEngine(150));
    }
    public int getDoorCount()  { return 4; }
    public int getWheelCount() { return 4; }
}

class SportsCar extends Car {
    public SportsCar(String brand, String model, Color color) {
        super(brand, model, color, new PetrolEngine(450));
    }
    public int getDoorCount()  { return 2; }
    public int getWheelCount() { return 4; }

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
        super(brand, model, color, new ElectricEngine(60));
        setMode(new EcoMode()); // жижиг автомат машин анхандаа Eco горимтой эхэлнэ
    }
    public int getDoorCount()  { return 1; }
    public int getWheelCount() { return 4; }
}

// ============================================================
//  Демо: Factory-гаар үүсгэж (→ Singleton registry-д бүртгэгдэнэ)
//  → Strategy-гаар удирдаж → Observer-оор ажиглаж → validation шалгана
// ============================================================
public class CarDemo {
    public static void main(String[] args) {
        System.out.println("=== 1) SEDAN (4 хаалгатай, Petrol) ===");
        Car sedan = CarFactory.createCar(CarType.SEDAN, "Toyota", "Camry", Color.WHITE);
        Dashboard sedanDash = new Dashboard();
        sedan.addObserver(sedanDash);
        sedan.addObserver(new SpeedWarningLight(120));
        sedan.printInfo();
        sedan.startEngine();
        sedan.accelerate(60);
        sedan.removeObserver(sedanDash); // dashboard-оо унтраая гэж бодъё
        sedan.accelerate(70);            // одоо зөвхөн warning light л мэдэгдэнэ

        System.out.println("\n=== 2) SPORTS (2 хаалгатай, Petrol) ===");
        Car sports = CarFactory.createCar(CarType.SPORTS, "Ferrari", "F8", Color.RED);
        sports.addObserver(new Dashboard());
        sports.addObserver(new SpeedWarningLight(200));
        sports.printInfo();
        sports.startEngine();
        sports.setMode(new EcoMode());   // татгалзана
        sports.setMode(new SportMode()); // зөвшөөрнө
        sports.accelerate(220);

        System.out.println("\n=== 3) SMART (1 хаалгатай, Electric) ===");
        Car smart = CarFactory.createCar(CarType.SMART, "EasyMile", "EZ10", Color.BLUE);
        smart.addObserver(new Dashboard());
        smart.printInfo();
        smart.startEngine();
        smart.openAllDoors();
        smart.accelerate(15);

        System.out.println("\n=== 4) Validation шалгах ===");
        try {
            smart.accelerate(-10); // санаатайгаар буруу утга өгье
        } catch (IllegalArgumentException e) {
            System.out.println("Хүлээгдэж байсан алдаа баригдлаа: " + e.getMessage());
        }

        System.out.println("\n=== 5) Singleton Registry ===");
        System.out.println("Нийт бүртгэгдсэн машин: " + CarRegistry.getInstance().size());
        // getInstance() хаанаас дуудсан ч ЯГ НЭГ л объект буцаана:
        boolean sameInstance = CarRegistry.getInstance() == CarRegistry.getInstance();
        System.out.println("Registry үргэлж адилхан объект уу? " + sameInstance);
    }
}