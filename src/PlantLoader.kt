class PlantLoader : Iterator<Plant> {
    private var plants: Array<Plant> = arrayOf(
        Plant("Parsnip", 20, 25, 4, SeasonType.SPRING),
        RegrowablePlant("Coffee Beans", 15, 60, 10, 2, SeasonType.SPRING, SeasonType.SUMMER),
        Plant("Garlic", 40, 60, 5, SeasonType.SPRING),
        RegrowablePlant("Green Bean", 60, 40, 10, 3, SeasonType.SPRING),
        RegrowablePlant("Kale", 70, 110, 10, 3, SeasonType.SPRING),
        Plant("Potato", 40, 80, 6, SeasonType.SPRING),
        Plant("Rhubarb", 100, 220, 13, SeasonType.SPRING),
        RegrowablePlant("Strawberry", 100, 150, 8, 4, SeasonType.SPRING),
        Plant("Cauliflower", 80, 120, 12, SeasonType.SPRING),
        Plant("Tulip", 20, 30, 6, SeasonType.SPRING),
        Plant("Rice", 40, 30, 6, SeasonType.SPRING),
        RegrowablePlant("Corn", 150, 50, 14, 4, SeasonType.SUMMER, SeasonType.AUTUMN),
        RegrowablePlant("Hops", 50, 25, 11, 1, SeasonType.SUMMER),
        RegrowablePlant("Hot Pepper", 40, 40, 5, 3, SeasonType.SUMMER),
        Plant("Melon", 80, 250, 12, SeasonType.SUMMER),
        Plant("Poppy", 100, 130, 7, SeasonType.SUMMER),
        Plant("Radish", 40, 90, 6, SeasonType.SUMMER),
        Plant("Red Cabbage", 100, 260, 9, SeasonType.SUMMER),
        Plant("Starfruit", 400, 750, 13, SeasonType.SUMMER),
        Plant("Summer Spangle", 50, 90, 8, SeasonType.SUMMER, SeasonType.AUTUMN),
        Plant("Sunflower", 200, 80, 8, SeasonType.SUMMER, SeasonType.AUTUMN),
        RegrowablePlant("Tomato", 50, 60, 11, 4, SeasonType.SUMMER),
        Plant("Wheat", 10, 25, 4, SeasonType.SUMMER, SeasonType.AUTUMN),
        Plant("Amaranth", 70, 150, 7, SeasonType.AUTUMN),
        Plant("Artichoke", 30, 160, 7, SeasonType.AUTUMN),
        Plant("Beetroot", 20, 100, 6, SeasonType.AUTUMN),
        Plant("Bok Choy", 50, 80, 4, SeasonType.AUTUMN),
        RegrowablePlant("Cranberries", 240, 150, 7, 5, SeasonType.AUTUMN),
        RegrowablePlant("Eggplant", 20, 60, 5, 5, SeasonType.AUTUMN),
        Plant("Fairy Rose", 200, 290, 12, SeasonType.AUTUMN),
        RegrowablePlant("Grape", 60, 80, 10, 3, SeasonType.AUTUMN),
        Plant("Pumpkin", 100, 320, 13, SeasonType.AUTUMN),
        Plant("Yam", 60, 160, 10, SeasonType.AUTUMN)
    )
    private var currentInt: Int = 0;
    var current: Plant = plants[currentInt];

    override fun next(): Plant {
        currentInt++;
        current = plants[currentInt];
        return current;
    }

    override fun hasNext(): Boolean {
        return currentInt < plants.size - 1;
    }
}