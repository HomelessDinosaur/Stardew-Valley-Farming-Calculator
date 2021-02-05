/**
 *
 * @author Ryan Cartwright
 */
import java.util.Scanner; //Import the Scanner class to read text files and user input
import java.io.IOException; //Java class that will handle the exceptions when writing and creating files
import java.io.FileWriter; //Java class that will handle the writing the outputs to file
import java.io.File; //Java class that will handle the creation, and manipulation of files
import java.util.List; //Imports the list class to handle some array logic
import java.util.ArrayList;
import java.text.NumberFormat;
import java.text.ParseException;

public class Program {
    //CONSTANTS
    private static final int MONTH_LENGTH = 28;
    static final int DATE_LENGTH = 4;
    static final int TENS_DAY_POSITION = 0;
    static final int ONES_DAY_POSITION = 1;
    static final int SLASH_POSITION = 2;
    static final int SEASON_POSITION = 3;
    static final int SEASON_MAX = 5;
    static final int SEASON_MIN = 0;
    static final int TENS_MAX = 3; //The day can't go greater then 30
    static final int DATE_SPLIT_SIZE = 4;
    static final int TENS_MULTIPLIER = 10;
    static final int SUMMER = 2;
    static final int SPRING = 1;
    static final int AUTUMN = 3;
    
    //Inputs from user
    private static int money = 0;
    private static int plots = 0;
    
    private static boolean check = false;
    //Path of the plants.txt on the persons machine
    private static String path = String.format("%s\\%s",System.getProperty("user.dir"),"plants.txt"); //Gets the directory of the solution, so that the program can access plants.txt on any machine
    
    //Main program that does all the user interfacing.
    //It then does all the calculations at the end based on what the user puts in as the variables.
    //Afterwards, spits out these calculations and writes them to the provided text file.
    public static void main(String[] args){
        NumberFormat nf = NumberFormat.getInstance();
        Scanner scan = new Scanner(System.in);
        //Runs until the user provides an integer for the amount of starting money
        while(true)
        {
            System.out.println("Starting Money? (Input Number)");
            String moneyString = scan.nextLine();
            try {
               money = nf.parse(moneyString).intValue();
               //Stops the loop if the money is correctly parsed
               break;
            }
            catch (ParseException e)
            {
               money = 0;
               System.out.println("The money you inputted isn't a number. Try again");
            }
        }    
        //Runs until the user provides an integer for the amount of farm plots to calculate
        while(true)
        {
            System.out.println("Amount of farm plots? (Input Number)");
            String plotsString = scan.nextLine();
            try {
               plots = nf.parse(plotsString).intValue();
               //Stops the loop if the money is correctly parsed
               break;
            }
            catch (ParseException e)
            {
               plots = 0;
               System.out.println("The money you inputted isn't a number. Try again");
            }
        }
        //Runs until the user provides an integer for the starting date
        int[] dateSplit = new int[DATE_SPLIT_SIZE];
        while(true)
        {
            //Resets dateSplit so that it doesn't get null pointer exceptions when it gets returned as null
            dateSplit = new int[] {0,0,0,0};
            try {
                dateSplit = CheckDateInput(dateSplit);
                //Breaks the loop if the date is the correct input
                break;
            } catch (DateInputException e)
            {
                System.out.println(e.getMessage());
            }
        }
        Date date = new Date((dateSplit[0]*10) + dateSplit[1], dateSplit[3]);
        //Gets the file name that the calculations will be outputted to
        System.out.println("File name to output to?");
        String fileName = scan.nextLine();
        //Loads all the plants into ArrayList plants
        Parser parser = new Parser(path);
        List<Element[]> plants = parser.GetAllPlants();
        Plant[] allPlants = new Plant[plants.size()];
        //Loops, creating plant objects with the LoadPlant() method, which does all the logic
        for (int i = 0; i < plants.size(); i++)
        {
            allPlants[i] = LoadPlant(parser, plants.get(i), date.day, date.season);
        }
        List<String> outputLines = new ArrayList<>();
        //Loops for every plant, only printing to the console if there is going to be profit, either positive or negative
        for(int p = 0; p < plants.size(); p++)
        {
            String s = Simulate(allPlants[p], money, plots);
            System.out.print(s);
            outputLines.add(s);
        }
        WriteToFile(fileName, outputLines.toArray(new String[outputLines.size()]));
    }
    private static String Simulate(Plant plant, int money, int plots)
    {
        int tempMoney = money;
        int plotsUsed = 0;
        //Loops until there isn't enough money or there isn't enough farm plots
        while(tempMoney >= plant.cost && plotsUsed < plots)
        {
            tempMoney -= plant.cost;
            plotsUsed++;
        }
        int profit = 0;
        if  (plant.regrowth == 0)
        {
            //Calculates profit, but keeps in mind that there is multiple costs because the plant doesn't regrow its fruit
            profit = (plotsUsed * (plant.sell * plant.maxHarvest)) - (plotsUsed * (plant.cost * plant.maxHarvest)); 
        }
        else if  (plant.maxHarvest != 0)
        {
            //Calculates the profit, based on the cost and the selling price. As well as the amount of plots that are used
            profit = plotsUsed * (plant.sell * plant.maxHarvest) - (plotsUsed * plant.cost);
        }
        if (profit != 0 && plant.maxDays != 0)
        {
            if (plant.regrowth == 0)
            {
                //Returns a formatted string, which is unique to a plant that does not regrow. And such, has a different way of making money
                return String.format("Plant: %s\n Planted every %d days for %d days\n Plots Used: %d\n Money Spent: %d\n Money Retained: %d\n Profit: %d\n Gold Per Day: %dg/d\n\n", plant.name, plant.growth, plant.maxHarvest * plant.growth, plotsUsed, money - tempMoney, tempMoney, profit, profit/plant.maxDays);
            }
            else
            {
                //Returns a formatted string, which reports the earnings and such for that plant
                return String.format("Plant: %s" + "\n Makes profit for %d days\n Plots Used: %d\n Money Spent: %d\n Money Retained: %d\n Profit: %d\n Gold Per Day: %dg/d\n\n", plant.name, plant.growth + (plant.regrowth * (plant.maxHarvest - 1)), plotsUsed, money - tempMoney, tempMoney, profit, profit/plant.maxDays);
            }
        }
        else
        {
            return "";
        }
    }
    //Loads the plants properly by calculating the max days they can be alive 
    private static Plant LoadPlant(Parser parser, Element[] plant, int currentDay, int currentSeason)
    {
        String name = parser.GetElement(plant, "name").attribute;
        int cost = Integer.parseInt(parser.GetElement(plant, "cost").attribute);
        int sell = Integer.parseInt(parser.GetElement(plant, "sell").attribute);
        int growthRate = Integer.parseInt(parser.GetElement(plant, "growth").attribute);
        int regrowthRate = Integer.parseInt(parser.GetElement(plant, "regrowth").attribute); //For plants with recurring production e.g strawberries
        int primarySeason = 0;
        int secondarySeason = 0;
        //Goes through every season, and assigns a number to it, to assist with the logic in the simulation component
        if ((parser.GetElement(plant, "primaryseason").attribute).equals("spring"))
        {
            primarySeason = SPRING;
        }
        else if ((parser.GetElement(plant, "primaryseason").attribute).equals("summer"))
        {
            primarySeason = SUMMER;
        }
        else if ((parser.GetElement(plant, "primaryseason").attribute).equals("autumn"))
        {
            primarySeason = AUTUMN;
        }
        if ((parser.GetElement(plant, "secondaryseason").attribute).equals("spring"))
        {
            secondarySeason = SPRING;
        }
        else if ((parser.GetElement(plant, "secondaryseason").attribute).equals("summer"))
        {
            secondarySeason = SUMMER;
        }
        else if ((parser.GetElement(plant, "secondaryseason").attribute).equals("autumn"))
        {
            secondarySeason = AUTUMN;
        }
        int maxHarvest = 1;
        int maxDays = 0;
        //Checks to make sure that the day + one cycle of the growth rate isn't more then a season or 2 season if the secondary season is next. It also checks that the plant can grow in the current season
        if ((currentDay + growthRate < (MONTH_LENGTH + (MONTH_LENGTH * Math.round((secondarySeason/(secondarySeason + 1)))))) && (currentSeason == primarySeason || currentSeason == secondarySeason))
        {
            //Checks to see if this season and the next season are both seasons the plant grows in
            if (currentSeason == primarySeason && currentSeason + 1 == secondarySeason)
            {
                maxDays = MONTH_LENGTH * 2; //2 full seasons = 56 days 
            }
            //The current season is the secondary season
            else if (currentSeason == secondarySeason)
            {
                maxDays = MONTH_LENGTH;
            }
            //This season is the primary season but the next season isn't the secondary season
            else if (currentSeason == primarySeason && currentSeason + 1 != secondarySeason)
            {
                maxDays = MONTH_LENGTH;
            }
            //If the regrowth rate isn't 0 does a separate calculation
            if (regrowthRate != 0)
            {
                //Calculates the amount of harvests by how many times in maxDays it can regrow, after removing the initial growthRate and currentDay. Its +1 because the initial growth adds to the total harvests
               maxHarvest = 1 + (int)Math.floor(((maxDays - growthRate - currentDay) / regrowthRate));
            }
            //If there isn't a regrowth rate
            else
            {
                //Calculates the amount of times the plant will grow during the growth time (maxDays)
                maxHarvest = (int)Math.floor((maxDays-currentDay)/growthRate);
            }
        }
        else if (currentSeason != primarySeason && currentSeason != secondarySeason)
        {
            maxHarvest = 0;
        }
        return new Plant(name, cost, sell, growthRate, regrowthRate, maxHarvest, new int[] { primarySeason, secondarySeason}, maxDays);
    }
    //Takes the file name and the words and then writes the words to the file name
    private static void WriteToFile(String fileName, String[] words)
    {
        try
        {
            fileName = String.format("%s%s", fileName, ".txt"); //Adds a .txt to the end of the fileName
            File myFile = new File(fileName); //Makes a new file object
            if (!myFile.createNewFile()) //Creates that new file in the project folder, based on the file object
            {
                System.out.println("File already existed, overwrote data"); //If the file already existed, then will overwrite the data
            }
            FileWriter myWriter = new FileWriter(fileName); //Makes a new file writer object to write the words to the file
            for (int i = 0; i < words.length; i++) //Loops for all the words that need to be written, starting at a new line for each entry
            {
                myWriter.write(words[i]);
            }
            myWriter.close(); //Closes the file writer
        } catch (IOException e) { //Catches any IO exceptions, like if the file cannot be found, created, or written to.
            System.out.println("An error occured writing to file."); //Will print out an error message if so.
        }
    }
    private static int[] CheckDateInput(int[] dateSplit) throws DateInputException
    {
        System.out.println("Date: DD/S\nWhere S is the season, Spring (1) - Winter (4)?");
        Scanner scan = new Scanner(System.in);
        NumberFormat nf = NumberFormat.getInstance();
        String dateString = scan.nextLine();
        DateInputException wrongDate = new DateInputException("The date that you inputted is in the wrong format. Try again.");
        //Double checks the formatting of the date input.
        if (dateString.length() == DATE_LENGTH)
        {
            for(int c = 0; c < dateString.length(); c++)
            {
                if (c == SLASH_POSITION)
                {
                    if (dateString.charAt(c) != '/')
                    {
                        //The provided input is in the wrong format.
                        throw wrongDate;
                    }
                }
                else
                {
                    int currentInt = 0; //Stores the current char position as an integer to be dealt with
                    //Makes sure that chars 0,1 and 3 of dateString are all in the correct format
                    try {
                        currentInt = nf.parse(String.valueOf(dateString.charAt(c))).intValue();
                    }
                    catch(ParseException e)
                    {
                        //The provided input is in the wrong format. This means it will definitely repeat.
                        throw wrongDate;
                    }
                    /*Checks that all the numbers are sensible.
                      Firstly, makes sure that the date is less than 3, meaning the date has to be less then 30.
                      Secondly, makes sure that the first number + the second number is less than 29. As a season in the game is  28 days.
                    */
                    if((c == TENS_DAY_POSITION && currentInt < TENS_MAX) || (c == ONES_DAY_POSITION && ((dateSplit[0] * TENS_MULTIPLIER) + currentInt) <= MONTH_LENGTH && ((dateSplit[0] * TENS_MULTIPLIER) + currentInt) > 0) || ((c == SEASON_POSITION && currentInt > SEASON_MIN) && (c == SEASON_POSITION && currentInt < SEASON_MAX)))
                    {
                        dateSplit[c] = currentInt;
                    }
                    else
                    {
                        //The provided input is in the wrong format.10
                        throw wrongDate;
                    }
                }
            }
            return dateSplit;
        }
        //If the try condition throws an exception it will return null in catch, otherwise it will just return DateSplit;
        return null;
    }
}
//Is the plant object that combines all the different components of the plants into a single object
class Plant {
    public String name;
    public int cost;
    public int sell;
    public int growth;
    public int regrowth;
    public int maxHarvest;
    public int maxDays;
    //First int is the season it grows in; Second is the secondary season it grows in, otherwise it is 0
    public int[] seasons;
    public Plant(String Name, int Cost, int Sell,int Growth, int Regrowth, int MaxHarvest, int[] Seasons, int MaxDays)
    {
            name = Name;
            cost = Cost;
            sell = Sell;
            growth = Growth;
            regrowth = Regrowth;
            maxHarvest = MaxHarvest;
            maxDays = MaxDays;
            seasons = Seasons;
    }
}
//Custom date object that has a day and a season
class Date {
    public int day;
    public int season;
    public Date(int Day, int Season)
    {
        day = Day;
        season = Season;
    }
}
class DateInputException extends Exception
{
    DateInputException(String message)
    {
        super(message);
    }
}
   