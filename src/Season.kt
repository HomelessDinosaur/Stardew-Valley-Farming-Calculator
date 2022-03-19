enum class SeasonType(val value: Int) {
    SPRING(0), SUMMER(1), AUTUMN(2), WINTER(3);

    companion object {
        fun fromInt(value: Int) = values().first { it.value == value }
    }
}

class Season {
    var type: SeasonType = SeasonType.SPRING
        private set;

    constructor(_season: String) {
        this.type = SeasonType.valueOf(_season.uppercase())
    }

    constructor(_season: SeasonType) {
        this.type = _season;
    }

    fun getIterator(): SeasonIterator {
        return SeasonIterator(type)
    }

    override fun toString(): String {
        return type.toString()
    }
}

class SeasonIterator(_currentSeason: SeasonType) : Iterator<SeasonType> {
    var currentSeason = _currentSeason
        private set;

    private var iterations = 0

    override fun next(): SeasonType {
        iterations++
        currentSeason = SeasonType.fromInt((currentSeason.ordinal + 1) % 4);
        return currentSeason
    }

    override fun hasNext(): Boolean {
        return iterations < 4
    }
}
