# Car Design — Зохиомжийн жишээ (ICSI304)

Энэ бол бүрэн систем биш, зөвхөн **машины класс/зохиомж (Class & Object Design)**-ийн жишээ. Зорилго нь Factory, Singleton, Strategy, Observer гэсэн дөрвөн зохиомжийн загварыг (design pattern) нэг жижиг, ажиллагаатай кодон дээр зөв учир шалтгаантайгаар харуулах явдал юм.

## Файлын бүтэц

| Файл | Агуулга |
|---|---|
| `CarDemo.java` | Бүх класс + `main()` демо |
| `CarDemoTest.java` | JUnit 5 тестүүд (7 ширхэг) |
| `car_design_v3.puml` | Класс диаграмын PlantUML код |

## Ашигласан зохиомжийн загварууд

- **Factory** — `CarFactory.createCar(type, ...)` нь `CarType`-аас хамааран `SedanCar`, `SportsCar`, `SmartCar`-ын аль нэгийг үүсгэнэ. Хаалга/дугуйны тоо, моторын төрлийг сонгох логик client код дотор биш, энд төвлөрсөн.
- **Singleton** — `CarRegistry.getInstance()` нь программ даяар яг нэг л объект байхыг баталгаажуулна (private constructor). `CarFactory`-гаар үүссэн машин бүр автоматаар энд бүртгэгдэнэ.
- **Strategy** — `DrivingMode` интерфэйс (`EcoMode`, `NormalMode`, `SportMode`). Жолоодлогын горимын логикийг `Car`-аас тусгаарласан тул шинэ горим нэмэхэд `Car`-ын код хөндөгдөхгүй.
- **Observer** — `CarObserver` интерфэйс (`Dashboard`, `SpeedWarningLight`). `Car` хурдаа өөрчлөх бүрд бүртгэлтэй ажиглагч бүрт автоматаар мэдэгдэнэ (`addObserver`/`removeObserver`).
- **Engine polymorphism** (`PetrolEngine`, `ElectricEngine`) — энэ бол нэртэй GoF pattern биш, зүгээр л interface-based polymorphism. `SmartCar` цахилгаан, бусад нь бензин мотортой байхыг ингэж загварчилсан.

## Хамаарлын төрлүүд

- **Composition** (`Car *-- Engine`) — Car-гүйгээр Engine утгагүй.
- **Aggregation** (`Car o-- Wheel`, `Car o-- Door`) — дэд эд ангиуд тусад нь ч оршиж болно.
- **Inheritance** (`Car <|-- SedanCar/SportsCar/SmartCar`) — хаалга/дугуйны тоог дэд класс тус бүр өөрөө мэдүүлнэ.

## Класс диаграм

`car_design_v3.puml` файлыг https://www.plantuml.com/plantuml дээр буулгаад рендерлэнэ, эсвэл VS Code-д "PlantUML" өргөтгөл суулгаад шууд харж болно.

## Ажиллуулах

```bash
javac CarDemo.java
java CarDemo
```

## Тест ажиллуулах

JUnit 5 dependency Maven/IDE-гүй орчинд шаардлагатай тул console-standalone jar ашиглана:

```bash
# 1. JUnit 5 jar татах (нэг л удаа)
curl -L -o junit-platform-console-standalone-1.10.2.jar \
  https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.10.2/junit-platform-console-standalone-1.10.2.jar

# 2. Compile
javac -cp junit-platform-console-standalone-1.10.2.jar CarDemo.java CarDemoTest.java

# 3. Ажиллуулах
java -cp junit-platform-console-standalone-1.10.2.jar:. org.junit.platform.console.ConsoleLauncher execute --scan-classpath
```

## Шаардлага (Requirements)

**Функциональ:**
- Машин асна, хаалга нээгдэж/хаагдана
- Жолоодлогын горим (Eco/Normal/Sport) солигдоно, машины төрлөөс хамааран зарим горим хориглогдоно (жишээ нь Sports → Eco)
- Хурд өөрчлөгдөх бүрд dashboard болон сэрэмжлүүлэгч мэдэгдэнэ
- Үүссэн машин бүр төвлөрсөн бүртгэлд орно

**Функциональ бус:**
- Шинэ горим/машины төрөл нэмэхэд одоо байгаа код өөрчлөгдөхгүй байх (Open-Closed)
- Бизнесийн логикийг өгөгдлийн сан, UI-гүйгээр unit тестлэх боломжтой байх
- Буруу оролт (сөрөг хурд) шидэгдэхгүй, тодорхой алдаа шиднэ

## Дүгнэлт

Даалгаврын гол зорилго нь бүрэн систем биш, зөвхөн класс/зохиомжийн түвшинд design pattern-уудыг зөв мотивтойгоор ашиглаж 
сурах явдал байсан тул сүлжээ, өгөгдлийн сан, UI зэрэг системийн бусад давхарга санаатайгаар оруулаагүй болно.
