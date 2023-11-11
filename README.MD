# Java Slang Dictionary

This program is a project assignment from the course **CSC13102 - Java Application Development** at VNU-HCM, University of Science.

The program implements Java's TreeMap as the primary data structure and utilizes the Swing library for GUI design. Key features include viewing and searching for slangs by entry or by a definition keyword, adding new slangs to the dictionary, as well as editing and deleting slangs. Other features include a mode to view a random slang, two guessing-game modes, and storing search history.

## Installation

Using a terminal, navigate to the directory containing the file `SlangDict.jar` and enter the line below:

```
java -jar SlangDict.jar
```

Should the method above not work, follow the steps below, entering the specified lines into the terminal:

1. Compile the `.java` files:
   ```
   javac *.java
   ```
2. Should you wish to remake the `.jar` file, skip this step.

   Run the main class directly without making a `.jar` file:

   ```
   java -cp . DictionaryDisplay
   ```

3. Create/modify the `.jar` file:
   ```
   jar cfm SlangDict.jar MANIFEST.MF *.class data
   ```
4. Run the `.jar` file:
   ```
   java -jar SlangDict.jar
   ```

## License

[MIT License](https://choosealicense.com/licenses/mit/)
