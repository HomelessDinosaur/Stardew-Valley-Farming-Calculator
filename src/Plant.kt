import kotlin.math.floor

open class Plant(
    _name: String,
    _cost: Int,
    _sell: Int,
    _growthRate: Int,
    vararg _seasons: SeasonType,
) {
    private val MONTH_LENGTH = 28
    val name = _name
    val cost = _cost
    val sell = _sell
    val growthRate = _growthRate
    private val seasons = _seasons.map { type -> Season(type) }

    open fun maxHarvests(currentDate: Date): Int {
        return floor(maxDays(currentDate).toDouble() / growthRate).toInt()
    }

    fun maxDays(currentDate: Date): Int {
        val maxGrowthDays = countSeasons() * MONTH_LENGTH
        return maxGrowthDays - currentDate.day
    }

    open fun profit(plotsUsed: Int, currentDate: Date): Int {
        val maxHarvest = maxHarvests(currentDate)
        return (plotsUsed * (sell * maxHarvest)) - (plotsUsed * (cost * maxHarvest));
    }

    private fun countSeasons(): Int {
        val seasonIterator = seasons[0].getIterator()
        var count = 0;
        while (seasonIterator.hasNext()) {
            if (canGrow(seasonIterator.currentSeason)) {
                count++;
            }
            seasonIterator.next()
        }
        return count;
    }

    private fun canGrow(season: SeasonType): Boolean {
        return seasons.any { it.type == season }
    }

    override fun toString(): String {
        return name
    }
}

class RegrowablePlant(
    _name: String,
    _cost: Int,
    _sell: Int,
    _growthRate: Int,
    _regrowthRate: Int,
    vararg seasons: SeasonType
) : Plant(_name, _cost, _sell, _growthRate, *seasons) {

    val regrowthRate = _regrowthRate;

    override fun maxHarvests(currentDate: Date): Int {
        return 1 + floor(((maxDays(currentDate).toDouble() - growthRate) / regrowthRate)).toInt()
    }

    override fun profit(plotsUsed: Int, currentDate: Date): Int {
        val maxHarvest = maxHarvests(currentDate)
        return plotsUsed * (sell * maxHarvest) - (plotsUsed * cost)
    }
}