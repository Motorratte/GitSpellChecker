package training.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class SpellingErrorGeneratorGerman
{
    private final String[] letters = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "ä", "ö", "ü", "ß", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "Ä", "Ö", "Ü", "Ae", "Oe", "Ue", "Ae", "Oe", "Ue", "ae", "oe", "ue", "AE", "OE", "UE"};
    private final HashMap<String, String[]> similarLetters = new HashMap<>();
    private final HashMap<Character, char[]> mistypeLetters = new HashMap<>();

    private final HashMap<String, String[]> derDieDasMap = new HashMap<>();

    private final HashMap<String, String[]> dasDaßDassMap = new HashMap<>();

    private final List<String> wrongWordBeginnings = new ArrayList<>();
    private final List<String> wrongWordEndings = new ArrayList<>();
    private final HashMap<String, String[]> wrongFillerWords = new HashMap<>(); // nach => [zu, in];
    private String originalText = null;
    private String errorText = null;

    private final int DELETION = 2;
    private final int INSERTION = 1;
    private final int SUBSTITUTION = 0;
    private final ArrayList<Integer> editOperationAtIndex = new ArrayList<>();

    private Random random = new Random();
    private final float ERROR_CHANCES_VARIATION_FACTOR_MIN = 0.0f;
    private final float ERROR_CHANCES_VARIATION_FACTOR_MAX = 1.0f;
    private float errorChancesVariationFactor;
    private final float LETTER_CHANCE_FOR_RANDOM_SYMBOL = 0.95f;
    private final float RANDOM_SYMBOL_INSERTION_CHANCE_MIN = 0.0f;
    private final float RANDOM_SYMBOL_INSERTION_CHANCE_MAX = 0.2f;
    private float randomSymbolInsertionChance;
    private final float RANDOM_SYMBOL_DELETION_CHANCE_MIN = 0.0f;
    private final float RANDOM_SYMBOL_DELETION_CHANCE_MAX = 0.2f;
    private float randomSymbolDeletionChance;
    private final float RANDOM_SYMBOL_SUBSTITUTION_CHANCE_MIN = 0.0f;
    private final float RANDOM_SYMBOL_SUBSTITUTION_CHANCE_MAX = 0.2f;
    private float randomSymbolSubstitutionChance;
    private final float RANDOM_COMMA_INSERTION_CHANCE_MIN = 0.0f;
    private final float RANDOM_COMMA_INSERTION_CHANCE_MAX = 0.2f;
    private float randomCommaInsertionChance;
    private final float RANDOM_COMMA_DELETION_CHANCE_MIN = 0.0f;
    private final float RANDOM_COMMA_DELETION_CHANCE_MAX = 2.0f;
    private float randomCommaDeletionChance;
    private final float RANDOM_SYMBOL_TRANSPOSITION_CHANCE_MIN = 0.0f;
    private final float RANDOM_SYMBOL_TRANSPOSITION_CHANCE_MAX = 0.2f;
    private float randomSymbolTranspositionChance;
    private final float UPPER_LOWER_CASE_FAILURE_CHANCE_MIN = 0.0f;
    private final float UPPER_LOWER_CASE_FAILURE_CHANCE_MAX = 1.0f;
    private float upperLowerCaseFailureChance;
    private final float UPPER_LOWER_CASE_FAILURE_CHANCE_FOR_FIRST_LETTER_MIN = 0.0f;
    private final float UPPER_LOWER_CASE_FAILURE_CHANCE_FOR_FIRST_LETTER_MAX = 0.2f;
    private float upperLowerCaseFailureChanceForFirstLetter;
    private final float EMPTY_SPACE_INSERTION_CHANCE_MIN = 0.0f;
    private final float EMPTY_SPACE_INSERTION_CHANCE_MAX = 0.6f;
    private float emptySpaceInsertionChance;
    private final float EMPTY_SPACE_DELETION_CHANCE_MIN = 0.0f;
    private final float EMPTY_SPACE_DELETION_CHANCE_MAX = 0.6f;
    private float emptySpaceDeletionChance;
    private final float DOUBLE_LETTER_FAILURE_CHANCE_MIN = 0.0f;
    private final float DOUBLE_LETTER_FAILURE_CHANCE_MAX = 0.2f;
    private float doubleLetterFailureChance;
    private final float SIMILAR_LETTER_FAILURE_CHANCE_MIN = 0.0f;
    private final float SIMILAR_LETTER_FAILURE_CHANCE_MAX = 0.2f;
    private float similarLetterFailureChance;
    private final float MISTYPE_FAILURE_CHANCE_MIN = 0.0f;
    private final float MISTYPE_FAILURE_CHANCE_MAX = 1.0f;
    private float mistypeFailureChance;
    private final float WORD_TRANSITION_FAILURE_CHANCE_MIN = 0.0f;
    private final float WORD_TRANSITION_FAILURE_CHANCE_MAX = 0.2f;
    private float wordTransitionFailureChance;
    private float WRONG_WORD_BEGINNING_CHANCE_MIN = 0.0f;
    private float WRONG_WORD_BEGINNING_CHANCE_MAX = 0.2f;
    private float wrongWordBeginningChance;
    private float WRONG_WORD_ENDING_CHANCE_MIN = 0.0f;
    private float WRONG_WORD_ENDING_CHANCE_MAX = 0.2f;
    private float wrongWordEndingChance;
    private final float WORD_BLENDING_CHANCE_MIN = 0.0f;
    private final float WORD_BLENDING_CHANCE_MAX = 0.2f;
    private float wordBlendingChance;
    private final float FORM_OF_ADDRESS_FAILURE_CHANCE_MIN = 0.0f;
    private final float FORM_OF_ADDRESS_FAILURE_CHANCE_MAX = 0.2f;
    private float formOfAddressFailureChance;
    private final float DAS_DAß_DASS_FAILURE_CHANCE_MIN = 0.0f;
    private final float DAS_DAß_DASS_FAILURE_CHANCE_MAX = 0.2f;
    private float dasDaßDassFailureChance;
    private final float DER_DIE_DAS_FAILURE_CHANCE_MIN = 0.0f;
    private final float DER_DIE_DAS_FAILURE_CHANCE_MAX = 0.2f;
    private float derDieDasFailureChance;
    private final float WRONG_FILLER_WORD_FAILURE_CHANCE_MIN = 0.0f;
    private final float WRONG_FILLER_WORD_FAILURE_CHANCE_MAX = 0.2f;
    private float wrongFillerWordFailureChance;

    public SpellingErrorGeneratorGerman()
    {
        initSimilarLetterList();
        initMistypeLetters();
        initWrongFillerWords();
        updateFactors();
        initDasDaßDassMap();
        initDerDieDasMap();
        initWrongWordBeginnings();
        initWrongWordEndings();
    }

    private float getRandomValueBetween(float min, float max)
    {
        return min + random.nextFloat() * (max - min);
    }

    public void updateFactors()
    {
        errorChancesVariationFactor = getRandomValueBetween(ERROR_CHANCES_VARIATION_FACTOR_MIN, ERROR_CHANCES_VARIATION_FACTOR_MAX);
        randomSymbolInsertionChance = getRandomValueBetween(RANDOM_SYMBOL_INSERTION_CHANCE_MIN, RANDOM_SYMBOL_INSERTION_CHANCE_MAX) * errorChancesVariationFactor;
        randomSymbolDeletionChance = getRandomValueBetween(RANDOM_SYMBOL_DELETION_CHANCE_MIN, RANDOM_SYMBOL_DELETION_CHANCE_MAX) * errorChancesVariationFactor;
        randomSymbolSubstitutionChance = getRandomValueBetween(RANDOM_SYMBOL_SUBSTITUTION_CHANCE_MIN, RANDOM_SYMBOL_SUBSTITUTION_CHANCE_MAX) * errorChancesVariationFactor;
        randomCommaInsertionChance = getRandomValueBetween(RANDOM_COMMA_INSERTION_CHANCE_MIN, RANDOM_COMMA_INSERTION_CHANCE_MAX) * errorChancesVariationFactor;
        randomCommaDeletionChance = getRandomValueBetween(RANDOM_COMMA_DELETION_CHANCE_MIN, RANDOM_COMMA_DELETION_CHANCE_MAX) * errorChancesVariationFactor;
        randomSymbolTranspositionChance = getRandomValueBetween(RANDOM_SYMBOL_TRANSPOSITION_CHANCE_MIN, RANDOM_SYMBOL_TRANSPOSITION_CHANCE_MAX) * errorChancesVariationFactor;
        upperLowerCaseFailureChance = getRandomValueBetween(UPPER_LOWER_CASE_FAILURE_CHANCE_MIN, UPPER_LOWER_CASE_FAILURE_CHANCE_MAX) * errorChancesVariationFactor;
        upperLowerCaseFailureChanceForFirstLetter = getRandomValueBetween(UPPER_LOWER_CASE_FAILURE_CHANCE_FOR_FIRST_LETTER_MIN, UPPER_LOWER_CASE_FAILURE_CHANCE_FOR_FIRST_LETTER_MAX) * errorChancesVariationFactor;
        emptySpaceInsertionChance = getRandomValueBetween(EMPTY_SPACE_INSERTION_CHANCE_MIN, EMPTY_SPACE_INSERTION_CHANCE_MAX) * errorChancesVariationFactor;
        emptySpaceDeletionChance = getRandomValueBetween(EMPTY_SPACE_DELETION_CHANCE_MIN, EMPTY_SPACE_DELETION_CHANCE_MAX)* errorChancesVariationFactor;
        doubleLetterFailureChance = getRandomValueBetween(DOUBLE_LETTER_FAILURE_CHANCE_MIN, DOUBLE_LETTER_FAILURE_CHANCE_MAX) * errorChancesVariationFactor;
        similarLetterFailureChance = getRandomValueBetween(SIMILAR_LETTER_FAILURE_CHANCE_MIN, SIMILAR_LETTER_FAILURE_CHANCE_MAX) * errorChancesVariationFactor;
        mistypeFailureChance = getRandomValueBetween(MISTYPE_FAILURE_CHANCE_MIN, MISTYPE_FAILURE_CHANCE_MAX) * errorChancesVariationFactor;
        wordTransitionFailureChance = getRandomValueBetween(WORD_TRANSITION_FAILURE_CHANCE_MIN, WORD_TRANSITION_FAILURE_CHANCE_MAX) * errorChancesVariationFactor;
        wordBlendingChance = getRandomValueBetween(WORD_BLENDING_CHANCE_MIN, WORD_BLENDING_CHANCE_MAX) * errorChancesVariationFactor;
        formOfAddressFailureChance = getRandomValueBetween(FORM_OF_ADDRESS_FAILURE_CHANCE_MIN, FORM_OF_ADDRESS_FAILURE_CHANCE_MAX) * errorChancesVariationFactor;
        dasDaßDassFailureChance = getRandomValueBetween(DAS_DAß_DASS_FAILURE_CHANCE_MIN, DAS_DAß_DASS_FAILURE_CHANCE_MAX) * errorChancesVariationFactor;
        derDieDasFailureChance = getRandomValueBetween(DER_DIE_DAS_FAILURE_CHANCE_MIN, DER_DIE_DAS_FAILURE_CHANCE_MAX) * errorChancesVariationFactor;
        wrongFillerWordFailureChance = getRandomValueBetween(WRONG_FILLER_WORD_FAILURE_CHANCE_MIN, WRONG_FILLER_WORD_FAILURE_CHANCE_MAX) * errorChancesVariationFactor;
        wrongWordBeginningChance = getRandomValueBetween(WRONG_WORD_BEGINNING_CHANCE_MIN, WRONG_WORD_BEGINNING_CHANCE_MAX) * errorChancesVariationFactor;
        wrongWordEndingChance = getRandomValueBetween(WRONG_WORD_ENDING_CHANCE_MIN, WRONG_WORD_ENDING_CHANCE_MAX) * errorChancesVariationFactor;
    }


    private void initSimilarLetterList()
    {
        final ArrayList<String[]> similarLetterList = new ArrayList<>();
        similarLetterList.add(new String[]{"a", "e", "ä"});
        similarLetterList.add(new String[]{"o", "u", "ö", "ü"});
        similarLetterList.add(new String[]{"p", "b", "d", "t", "g"});
        similarLetterList.add(new String[]{"P", "B"});
        similarLetterList.add(new String[]{"c", "k", "q"});
        similarLetterList.add(new String[]{"C", "K", "Q"});
        similarLetterList.add(new String[]{"sch", "sh", "ch"});
        similarLetterList.add(new String[]{"SCH", "SH", "CH"});
        similarLetterList.add(new String[]{"Sch", "Sh", "Ch"});
        similarLetterList.add(new String[]{"i", "j", "y", "1", "l"});
        similarLetterList.add(new String[]{"m", "n"});
        similarLetterList.add(new String[]{"f", "v", "w"});

        generateHashMapFromListOfStringArrays(similarLetters, similarLetterList);
    }

    private void initWrongFillerWords() //experimental
    {
        final ArrayList<String[]> wrongFillerWordsList = new ArrayList<>();
        wrongFillerWordsList.add(new String[]{"zu", "um", "nach", "vor", "über", "unter", "gegen", "mit", "ohne", "durch", "wegen", "für", "von", "in", "an", "bei", "aus", "ab", "auf"});
        wrongFillerWordsList.add(new String[]{"nehmen", "reißen", "holen", "nehme", "reiße", "hole","genommen", "gerissen", "geholt"});
        wrongFillerWordsList.add(new String[]{"wollen", "möchten", "sollen", "dürfen", "können", "müssen", "müssten", "sollten","dürften", "könnten","wollten","müßten","wöllten"});
        wrongFillerWordsList.add(new String[]{"gehen", "geht", "gehe", "ging", "gingen", "gegangen"});
        wrongFillerWordsList.add(new String[]{"essen", "isst", "esse", "aß", "aßen", "gegessen"});
        wrongFillerWordsList.add(new String[]{"haben", "hat", "habe", "hatte", "hatten", "gehabt"});
        wrongFillerWordsList.add(new String[]{"sein", "ist", "war", "warst", "waren", "gewesen"});
        wrongFillerWordsList.add(new String[]{"machen", "macht", "machte", "machten", "gemacht"});
        wrongFillerWordsList.add(new String[]{"kommen", "kommt", "kam", "kamen", "gekommen"});
        wrongFillerWordsList.add(new String[]{"sehen", "sieht", "sah", "sahen", "gesehen"});

        for (String[] wrongFillerWord : wrongFillerWordsList)
        {
            for (int i = 0; i < wrongFillerWord.length; i++)
            {
                //first letter to upper case
                wrongFillerWord[i] = (wrongFillerWord[i].charAt(0) - 32) + wrongFillerWord[i].substring(1);
            }
        }

        generateHashMapFromListOfStringArrays(wrongFillerWords, wrongFillerWordsList);
    }

    private void initDerDieDasMap()
    {
        derDieDasMap.put("der", new String[]{"die", "das"});
        derDieDasMap.put("Der", new String[]{"Die", "Das"});
        derDieDasMap.put("die", new String[]{"der", "das"});
        derDieDasMap.put("Die", new String[]{"Der", "Das"});
        derDieDasMap.put("das", new String[]{"die", "der"});
        derDieDasMap.put("Das", new String[]{"Die", "Der"});
    }

    private void initDasDaßDassMap()
    {
        dasDaßDassMap.put("das", new String[]{"daß", "dass"});
        dasDaßDassMap.put("Das", new String[]{"Daß", "Dass"});
        dasDaßDassMap.put("daß", new String[]{"das", "dass"});
        dasDaßDassMap.put("Daß", new String[]{"Das", "Dass"});
        dasDaßDassMap.put("dass", new String[]{"das", "daß"});
        dasDaßDassMap.put("Dass", new String[]{"Das", "Daß"});
    }

    private void addAnyWordToListInUpperCase(ArrayList<String> list)
    {
        final int listSize = list.size();
        for (int i = 0; i < listSize; i++)
        {
            list.set(i, (list.get(i).charAt(0) - 32) + list.get(i).substring(1));
        }
    }
    private void initWrongWordBeginnings()
    {
        wrongWordBeginnings.add("ge");
        wrongWordBeginnings.add("ver");
        wrongWordBeginnings.add("ent");
        wrongWordBeginnings.add("be");
        wrongWordBeginnings.add("un");
    }

    private void initWrongWordEndings()
    {
        wrongWordEndings.add("en");
        wrongWordEndings.add("st");
        wrongWordEndings.add("t");
        wrongWordEndings.add("te");
        wrongWordEndings.add("ten");
        wrongWordEndings.add("est");
        wrongWordEndings.add("s");
        wrongWordEndings.add("es");
        wrongWordEndings.add("chen");
        wrongWordEndings.add("lein");
        wrongWordEndings.add("er");
    }

    private void generateHashMapFromListOfStringArrays(final HashMap<String, String[]> toFill, final ArrayList<String[]> listOfStringArrays)
    {
        //each element becomes key and the rest of the elements become the value
        for (final String[] stringArray : listOfStringArrays)
        {
            for (int i = 1; i < stringArray.length; i++)
            {
                toFill.put(stringArray[i], stringArray);
            }
        }
    }

    private void initMistypeLetters()
    {
        mistypeLetters.put('a', new char[]{'q', 'w', 's', 'x', 'y'});
        mistypeLetters.put('A', new char[]{'Q', 'W', 'S', 'X', 'Y'});
        mistypeLetters.put('b', new char[]{'v', 'g', 'h', 'n', ' '});
        mistypeLetters.put('B', new char[]{'V', 'G', 'H', 'N', ' '});
        mistypeLetters.put('c', new char[]{'x', 'd', 'f', 'v', ' '});
        mistypeLetters.put('C', new char[]{'X', 'D', 'F', 'V', ' '});
        mistypeLetters.put('d', new char[]{'s', 'e', 'r', 'f', 'c', 'x'});
        mistypeLetters.put('D', new char[]{'S', 'E', 'R', 'F', 'C', 'X'});
        mistypeLetters.put('e', new char[]{'w', 's', 'd', 'r', 'f', '3', '4'});
        mistypeLetters.put('E', new char[]{'W', 'S', 'D', 'R', 'F', '§', '$'});
        mistypeLetters.put('f', new char[]{'d', 'r', 't', 'g', 'v', 'c'});
        mistypeLetters.put('F', new char[]{'D', 'R', 'T', 'G', 'V', 'C'});
        mistypeLetters.put('g', new char[]{'f', 't', 'z', 'h', 'b', 'v'});
        mistypeLetters.put('G', new char[]{'F', 'T', 'Z', 'H', 'B', 'V'});
        mistypeLetters.put('h', new char[]{'g', 'z', 'u', 'j', 'n', 'b'});
        mistypeLetters.put('H', new char[]{'G', 'Z', 'U', 'J', 'N', 'B'});
        mistypeLetters.put('i', new char[]{'u', 'j', 'k', 'l', 'o', '8', '9'});
        mistypeLetters.put('I', new char[]{'U', 'J', 'K', 'L', 'O', '(', ')'});
        mistypeLetters.put('j', new char[]{'h', 'u', 'i', 'k', 'm', 'n'});
        mistypeLetters.put('J', new char[]{'H', 'U', 'I', 'K', 'M', 'N'});
        mistypeLetters.put('k', new char[]{'j', 'i', 'o', 'l', 'm', ',', '.'});
        mistypeLetters.put('K', new char[]{'J', 'I', 'O', 'L', 'M', ';', ':'});
        mistypeLetters.put('l', new char[]{'k', 'o', 'p', 'ö', ',', '.'});
        mistypeLetters.put('L', new char[]{'K', 'O', 'P', 'Ö', ';', ':'});
        mistypeLetters.put('m', new char[]{'n', 'j', 'k', ',', ' '});
        mistypeLetters.put('M', new char[]{'N', 'J', 'K', ';', ' '});
        mistypeLetters.put('n', new char[]{'b', 'h', 'j', 'm', ' '});
        mistypeLetters.put('N', new char[]{'B', 'H', 'J', 'M', ' '});
        mistypeLetters.put('o', new char[]{'i', 'k', 'l', 'p', '0', '9'});
        mistypeLetters.put('O', new char[]{'I', 'K', 'L', 'P', '=', ')'});
        mistypeLetters.put('p', new char[]{'o', 'l', 'ö', 'ü', 'ß', '0'});
        mistypeLetters.put('P', new char[]{'O', 'L', 'Ö', 'Ü', '?', '='});
        mistypeLetters.put('q', new char[]{'w', 'a', '1', '2'});
        mistypeLetters.put('Q', new char[]{'W', 'A', '!', '"'});
        mistypeLetters.put('r', new char[]{'e', 'd', 'f', 't', '4', '5'});
        mistypeLetters.put('R', new char[]{'E', 'D', 'F', 'T', '$', '%'});
        mistypeLetters.put('s', new char[]{'a', 'w', 'e', 'd', 'x', 'y'});
        mistypeLetters.put('S', new char[]{'A', 'W', 'E', 'D', 'X', 'Y'});
        mistypeLetters.put('t', new char[]{'r', 'f', 'g', 'z', '5', '6'});
        mistypeLetters.put('T', new char[]{'R', 'F', 'G', 'Z', '%', '&'});
        mistypeLetters.put('u', new char[]{'z', 'h', 'j', 'i', '7', '8'});
        mistypeLetters.put('U', new char[]{'Z', 'H', 'J', 'I', '/', '('});
        mistypeLetters.put('v', new char[]{'c', 'f', 'g', 'b', ' '});
        mistypeLetters.put('V', new char[]{'C', 'F', 'G', 'B', ' '});
        mistypeLetters.put('w', new char[]{'q', 'a', 's', 'e', '2', '3'});
        mistypeLetters.put('W', new char[]{'Q', 'A', 'S', 'E', '"', '§'});
        mistypeLetters.put('x', new char[]{'y', 's', 'd', 'c', ' '});
        mistypeLetters.put('X', new char[]{'Y', 'S', 'D', 'C', ' '});
        mistypeLetters.put('y', new char[]{'x', 's', 'a', '<'});
        mistypeLetters.put('Y', new char[]{'X', 'S', 'A', '>'});
        mistypeLetters.put('z', new char[]{'t', 'g', 'h', 'u', '6', '7'});
        mistypeLetters.put('Z', new char[]{'T', 'G', 'H', 'U', '&', '/'});
        mistypeLetters.put(' ', new char[]{'v', 'b', 'n', 'm', 'c', 'x'});
        mistypeLetters.put(',', new char[]{'m', 'k', 'l', '.'});
        mistypeLetters.put('.', new char[]{',', 'ö', 'l', '-'});
        mistypeLetters.put('-', new char[]{'.', 'ä', 'ö', '#'});
        mistypeLetters.put('ä', new char[]{'ö', 'ü', '#', '-', '+'});
        mistypeLetters.put('ö', new char[]{'ä', 'ü', 'p', 'l', '.', '-'});
        mistypeLetters.put('ü', new char[]{'ö', 'p', 'ä', '+','´','ß'});
        mistypeLetters.put('Ä', new char[]{'Ö', 'Ü', '\'', '_', '*'});
        mistypeLetters.put('Ö', new char[]{'Ä', 'Ü', 'P', 'L', ':', '_'});
        mistypeLetters.put('Ü', new char[]{'Ö', 'P', 'Ä', '*', '`', '?'});
        mistypeLetters.put('ß', new char[]{'ü', 'p', '´', '?'});
    }


    public String generateErrorText()
    {
        return null;
    }

    //process word replacements
    private void processWordReplacements()
    {
        //itera through error text

    }


}
