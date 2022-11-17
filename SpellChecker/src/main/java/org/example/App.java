package org.example;

import training.generator.SpellingErrorGeneratorGerman;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        final String testText = "Ich möchte Herrn Grosch dazu beglückwünschen, dass er dieser Versuchung widerstanden hat.\n" +
                "Absolut entscheidend ist aber, dass ein europäisches Netz von Datenbanken aufgebaut wird.\n" +
                "Solange kein solches Netz existiert, lassen sich Fälschungen nicht reduzieren, und damit kann auch die Verkehrssicherheit nicht erhöht werden.";
        SpellingErrorGeneratorGerman generator = new SpellingErrorGeneratorGerman();
        generator.updateFactors();
        generator.setOriginalText(testText);
        System.out.println(testText);
        System.out.println("------------------------------------------------------------");
        System.out.println(generator.generateErrorText());
    }
}
