import java.util.*

class Date(_day: Int, _season: String) {
    var season: Season = Season(_season)
        private set;

    var day: Int = _day
        private set;

    init {
        if (day > 28 || day < 1) {
            throw IllegalArgumentException("Day provided was invalid")
        }
    }

    override fun toString(): String {
        return "Day $day of ${season.toString().lowercase().replaceFirstChar { char -> char.uppercase() }}"
    }
}