# Java Slang Dictionary

This program is a project assignment from the course **CSC13102 - Java Application Development (21KTPM2)** at VNU-HCM, University of Science.

The program implements Java's TreeMap as the primary data structure and utilizes the Swing library for GUI design. Key features include viewing and searching for slangs by entry or by a definition keyword, adding new slangs to the dictionary, as well as editing and deleting slangs. Other features include a mode to view a random slang, two guessing-game modes, and storing search history.

The demo for the program can be viewed on [Youtube](https://www.youtube.com/watch?v=hSIEP39lbK8) (in Vietnamese).

## How to use

Using a terminal, navigate to the directory containing the file `SlangDict.jar` and enter the line below:

```
java -jar SlangDict.jar
```

The `.jar` file was built using JDK 19. Should the method above not work due to _UnsupportedClassVersionError_, follow the steps below, entering the specified lines into the terminal:

1. Compile the `.java` files:
   ```
   javac src/*.java
   ```
2. Should you wish to rebuild the `.jar` file, skip this step.

   Run the main class directly without making a `.jar` file:

   ```
   java -cp . src.DictionaryDisplay
   ```

3. Create/modify the `.jar` file:
   ```
   jar cfm SlangDict.jar MANIFEST.MF src/*.class
   ```
4. Run the `.jar` file:

   ```
   java -jar SlangDict.jar
   ```

Please ensure the `.jar` file is in the same directory as the `data` folder when executing.

## License

[MIT License](https://choosealicense.com/licenses/mit/)
