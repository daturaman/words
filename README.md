# Synalogik Programming Test

## How to use the API
---

- Build the source to a jar file using `mvn package`
- Run the app with `java -jar words-1.0.jar FILE1.txt FILE2.txt` specifying the absolute or relative path of text files, separated by a space.

## Word assumptions
Words are assumed to be separated by a space. There are certain non-alphanumeric characters that are permitted as the first character of a word (e.g. currency symbols).
There are also other permitted characters that are typically used for separating components of a date or time "word".


