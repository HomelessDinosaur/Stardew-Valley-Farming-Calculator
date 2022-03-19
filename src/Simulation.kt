import java.text.ParseException
import kotlin.system.exitProcess

class Simulation private constructor(_money: Int, _plots: Int, _date: Date) {
    private var money = _money;
    private var plots = _plots;
    private var date = _date;

    init {
        println("Running new simulation with parameters:\n Money: $money\n Plots: $plots\n Date: $date\n")
        val plantIterator = PlantLoader()
        while (plantIterator.hasNext()) {
            processSpecs(plantIterator.current, money, plots, date)
            plantIterator.next()
        }
    }

    private fun processSpecs(plant: Plant, money: Int, plots: Int, date: Date) {
        var tempMoney = money;
        var plotsUsed = 0;
        while (tempMoney >= plant.cost && plotsUsed < plots) {
            tempMoney -= plant.cost;
            plotsUsed++;
        }
        Results(
            plant,
            plotsUsed,
            tempMoney,
            money - tempMoney,
            plant.profit(plotsUsed, date),
            date
        ).print()
    }

    class Results(
        private val plant: Plant,
        private val plotsUsed: Int,
        private val moneyRetained: Int,
        private val moneyUsed: Int,
        private val profit: Int,
        private val date: Date,
    ) {
        fun print() {
            val goldPerDay = profit / plant.maxDays(date)

            var message = "Planted every ${plant.growthRate} days for ${plant.maxDays(date)} days"
            if (plant is RegrowablePlant) {
                message = "Makes profit for ${plant.growthRate + (plant.regrowthRate * plant.maxHarvests(date))} days"
            }

            println(
                "Plant: ${plant.name}\n " +
                        "$message\n " +
                        "Plots Used: $plotsUsed\n " +
                        "Money Spent $moneyUsed\n " +
                        "Money Retained: $moneyRetained\n " +
                        "Gold Per Day: $goldPerDay\n"
            )
        }
    }

    class Specification {
        private var _money: Int = 0;
        private var _plots: Int = 0;
        private var _day: Int = 0
        private var _season: String = "Spring"

        fun money(money: String?) = apply { _money = validateInput(money) }
        fun plots(plots: String?) = apply { _plots = validateInput(plots) }
        fun day(day: String?) = apply { _day = validateInput(day) }
        fun season(season: String?) = apply { _season = season ?: "" }
        fun run() = Simulation(_money, _plots, Date(_day, _season))

        private fun validateInput(input: String?): Int {
            try {
                return Integer.parseInt(input)
            } catch (_: ParseException) {
                println("Please provide a valid number")
                exitProcess(1);
            }
        }
    }
}
