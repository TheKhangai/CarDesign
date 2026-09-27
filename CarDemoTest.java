import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Санамж: энэ файлыг Maven-ийн стандарт бүтэцтэй төсөлд ашиглах бол
// src/test/java/ дор, JUnit 5 dependency-тэй pom.xml-тэй хамт байрлана.
// ICSI304-ийн sad-template-д яг ийм бүтэц аль хэдийн байгаа.
class CarDemoTest {

    @Test
    void sportsCarShouldRejectEcoMode() {
        Car sports = CarFactory.createCar(CarType.SPORTS, "Ferrari", "F8", Color.RED);
        sports.setMode(new EcoMode());
        assertEquals("Normal", sports.getCurrentModeName(),
                "SportsCar Eco горимд шилжих ёсгүй, Normal хэвээрээ байх ёстой");
    }

    @Test
    void sportsCarShouldAcceptSportMode() {
        Car sports = CarFactory.createCar(CarType.SPORTS, "Ferrari", "F8", Color.RED);
        sports.setMode(new SportMode());
        assertEquals("Sport", sports.getCurrentModeName());
    }

    @Test
    void doorCountsShouldMatchCarType() {
        Car sedan  = CarFactory.createCar(CarType.SEDAN,  "Toyota",   "Camry", Color.WHITE);
        Car sports = CarFactory.createCar(CarType.SPORTS, "Ferrari",  "F8",    Color.RED);
        Car smart  = CarFactory.createCar(CarType.SMART,  "EasyMile", "EZ10",  Color.BLUE);

        assertEquals(4, sedan.getDoorCount());
        assertEquals(2, sports.getDoorCount());
        assertEquals(1, smart.getDoorCount());
    }

    @Test
    void accelerateWithNegativeAmountShouldThrow() {
        Car sedan = CarFactory.createCar(CarType.SEDAN, "Kia", "Rio", Color.BLACK);
        assertThrows(IllegalArgumentException.class, () -> sedan.accelerate(-10));
    }

    @Test
    void brakeWithNegativeAmountShouldThrow() {
        Car sedan = CarFactory.createCar(CarType.SEDAN, "Kia", "Rio", Color.BLACK);
        assertThrows(IllegalArgumentException.class, () -> sedan.brake(-5));
    }

    @Test
    void registryShouldBeSameInstanceEverywhere() {
        assertSame(CarRegistry.getInstance(), CarRegistry.getInstance(),
                "Singleton тул хоёр дуудалт ЯГ ижил объект буцаах ёстой");
    }

    @Test
    void registryShouldGrowWhenFactoryCreatesCar() {
        int before = CarRegistry.getInstance().size();
        CarFactory.createCar(CarType.SEDAN, "Hyundai", "Sonata", Color.BLACK);
        assertEquals(before + 1, CarRegistry.getInstance().size(),
                "Factory-гаар машин үүсэх бүрд Registry-ийн хэмжээ 1-ээр нэмэгдэх ёстой");
    }
}