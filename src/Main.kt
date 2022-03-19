fun main(args: Array<String>) {
    val specs = Simulation.Specification();

    prompt("Starting money?") { money -> specs.money(money) };
    prompt("Starting plots?") { plots -> specs.plots(plots) };
    prompt("Current day?") { day -> specs.day(day) };
    prompt("Current season?") { season -> specs.season(season) }

    specs.run()
}

fun prompt(message: String, setFunc: (String?) -> Simulation.Specification) {
    println(message);
    setFunc(readLine())
}