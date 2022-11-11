package training.generator;

import java.util.HashMap;

public class SpellingErrorGeneratorGerman
{
    private final String[] letters = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "ä", "ö", "ü", "ß", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "Ä", "Ö", "Ü","Ae","Oe","Ue","Ae","Oe","Ue","ae","oe","ue","AE","OE","UE"};
    private final HashMap<String,String[]> similarLetters = new HashMap<>();
    private final HashMap<String, String[]> wrongFillerWords = new HashMap<>(); // nach => [zu, in];
    private String originalText = null;
    private String errorText = null;
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

    }


}
