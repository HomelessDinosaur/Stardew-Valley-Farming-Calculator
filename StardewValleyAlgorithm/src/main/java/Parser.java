import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.ArrayList;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Ryan Cartwright
 */
/*
The parser class is used to parse in a text file at some specified path. It then does all the logic on that text file to split it into individual elements, each with a name and an attribute
The file can be of any arbitrary length, which allows for lots of plants to be recognised, and the user to input their own custom plants if they would like to edit the file.
The constructor of the class just needs a file and then itt does all the logic to split it by <plant></plant> tags
*/
public class Parser {
    private List<Element[]> Plants = new ArrayList<>(); //Stores a collection of elements, broken up by plant tags
    public Parser(String path)
    {   
        Boolean incorrectFormat = false;
        String[] errors = new String[2];
        String[] text = null;
        try {
            //Starts by reading the file and then counting each individual character
            List<String> lines = new ArrayList<>();
            Scanner fileReader = new Scanner(new File(path)); //Creates a new fileReader from the file specified at the path
            while (fileReader.hasNextLine()) { //Continues until the fileReader can't find a next line
                lines.add(fileReader.nextLine());
            }
            int processedTextCount = 0;
            int sizeOfLines = 0;
            for (int i = 0; i < lines.size(); i++) //Counts the size of the lines array in individual characters
            {
                sizeOfLines += lines.get(i).length();
            }
            //It then goes through each line and splits it by individual characters, adding this to the processedText array
            String[] processedText = new String[sizeOfLines]; //array to store the processed Text
            for (int i = 0; i < lines.size(); i++)
            {
                String[] linesSplit = lines.get(i).split("|"); //Splits the lines by individual characters
                for (int k = 0; k < linesSplit.length; k++)                
                {
                    processedText[processedTextCount + k] = linesSplit[k].toLowerCase(); //Adds the new split line to the processed text array at the point it left off so as to not overwrite data
                }
                processedTextCount += linesSplit.length; //adds to the length of the processed text
            }
            fileReader.close(); //Closes the scanner 
            text = processedText; //Stores processed text into the array outside the try statement
            errors[0] = "";
        } catch (FileNotFoundException e) {
            incorrectFormat = true;
            errors[0] = "File not found. Double check the path provided.\n";
        }
        //Goes through all the text elements to make sure its formatted correctly. Mainly, that theres a tag for each opening tag and vice-versa.
        for (int i = 0; i < text.length; i++)
        {
            int openingTags = 0;
            int closingTags = 0;
            if ("<".equals(text[i])) //Opening Tag
            {
                openingTags += 1;
            }
            else if ("/".equals(text[i]) && "<".equals(text[i-1]) && i != 0) //Closing Tag
            {
                closingTags += 1;
            }
            if (openingTags != closingTags) //There should be as many opening tags as there are closing tags
            {
                incorrectFormat = true;
                errors[1] = "Incorrect formatting of text file provided. Double check that all tags are closed.\n";
                break;
            }
            else
            {
                errors[1] = "";
            }
        }
        //Reads all the individual characters and splits them by if its an opening tag or closing tag. When it reaches a closing tag it combines all the characters into one string to name the element
        List<String> elements = new ArrayList<>();
        Boolean tagOpened = false;
        List<String> stringConstructor = new ArrayList<>();
        stringConstructor = new ArrayList<>();
        for (int i = 0; i < text.length; i++)
        {
            //Loops for all the elements in text
            //Will trigger the (tagOpened = false) if its a closing tag so that it doesn't write that element into the array
            if ("<".equals(text[i]) && "/".equals(text[i+1]))
            {
                tagOpened = false;
            }
            //Will trigger the (tagOpened = false) so that it can start writing the elements to the array until it encounters are closing tag
            else if ("<".equals(text[i]))
            {
                tagOpened = true;
            }
            //Once it reaches the end of the tag, will put the constructed string into the elements, clear that string, and then continue
            if (">".equals(text[i]))
            {
                StringBuffer sb = new StringBuffer();
                for (String s : stringConstructor) //Loops through all the characters in the stringConstructor appending it onto the end of a new string
                {
                    sb.append(s);
                }
                String str = sb.toString(); //Converts all the characters into one string
                if (!"".equals(str.trim())) //Makes sure the entry isn't empty, thus will ignore white space
                {
                    elements.add(str.trim()); //Adds the new string, trimming the white space from the beginning and end to an element array
                }
                stringConstructor.clear();
            }
            //adds the text to the string constructor (assuming tagOpened = true), ignoring < / > characters
            if (tagOpened && !"/".equals(text[i]) && !"<".equals(text[i]) && !">".equals(text[i]))
            {
                stringConstructor.add(text[i]);
            }
        }
        elements.add("plant"); //Adds a plant on the end as a sort of pseudo close tag
        //Creates a list of elements based on what the name is, and the attribute attached to that name.
        List<Element> plants = new ArrayList<>();
        //loops through all the elements to construct them as element 
        for (int i = 0; i < elements.size(); i++)
        {
            if (!"plant".equals(elements.get(i))) //if its not a plant tag
            {
                Element element = new Element(elements.get(i), elements.get(i+1));
                i++; //to account for the text of the element already being used
                plants.add(element);
            }
            else if ("plant".equals(elements.get(i)) && i != 0) //If it is a plant tag and not the first iteration. Also adds on the last iteration
            {
                Plants.add(plants.toArray(new Element[plants.size()])); //Adds the plant as an array to the plants total
                plants.clear();
            }
        }
    }
    //Will find a plant from the plants array with a particular name, and return that element
    public Element[] GetPlant(String PlantName)
    {
        for (int i = 0; i < Plants.size(); i++)
        {
            for (int k = 0; k < Plants.get(i).length; k++)
            {
                if ("name".equals(Plants.get(i)[k].name) && PlantName.equals(Plants.get(i)[k].attribute)) //Checks the parameter plant to see if that plant name is in the master array "Plants" then returns that plant
                {
                    return Plants.get(i);
                }
            }
        }
        return null;
    }
    //Returns an element from a list of elements, with a specified name
    public Element GetElement(Element[] elements, String name)
    {
        for (int i = 0; i < elements.length; i++)
        {
            if (elements[i].name.equals(name))
            {
                return elements[i];
            }
        }
        //creates a new element that has the attribute of 0, so that if the element doesn't exist then it won't return an error.
        //This is for in the case where the user doesn't add a regrowthRate because it doesn't have one, so instead it will return 0, and the program will work as normal
        return new Element(name, "0");
    }
    //Returns the master Plants array
    public List<Element[]> GetAllPlants()
    {
        return Plants;
    }
    //Gets the equivalent string of the parsed integer
    public String toString(int parsedInteger)
    {
        return Integer.toString(parsedInteger);
    }
}
//The element is a tag that contains a name and some text
//e.g <title>I am the title</title>
// name = title, attribute = I am the title
class Element {
    String name;
    String attribute;
    public Element(String Name, String Attribute)
    {
        name = Name;
        attribute = Attribute;
    }
    public String GetAttribute()
    {
        return attribute;
    }
    public String GetName()
    {
        return name;
    }
}
