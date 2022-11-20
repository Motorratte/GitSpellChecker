package learning.train;

import learning.generate.SpellingErrorGeneratorGerman;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class ModelManager
{
    public static final int[] DENSE_LAYER_SIZES = new int[]{2880, 3072, 2560, 2048, 1536, 1024, 768, 512};
    public static final int NUMBER_OF_FILTERS = 256;
    public static final double LEARNING_RATE = 0.0001;
    public static final int EPOCHS = 1;
    public static final int NUMBER_OF_OUTPUT_SYMBOLS = 64;
    public static final int OUTPUT_SYMBOL_SIZE = 8;
    public static final int NUMBER_OF_OUTPUTS = NUMBER_OF_OUTPUT_SYMBOLS * OUTPUT_SYMBOL_SIZE;
    public static final int PREVIOUS_WINDOW_SIZE = 112; //number of symbols
    public static final int PREVIOUS_ERROR_WINDOW_SIZE = 48;
    public static final int MAIN_WINDOW_SIZE = 176;
    public static final int MIN_TEXT_SIZE_FOR_SPELLCHECK = 3;
    public static final int NUMBER_OF_INPUTS_CLASS_A = (PREVIOUS_WINDOW_SIZE + PREVIOUS_ERROR_WINDOW_SIZE + MAIN_WINDOW_SIZE) * OUTPUT_SYMBOL_SIZE; //896Korregiertvorher 384unkorregiertvorher 512Korrektur 896restfenster = 2688
    public static final int MAX_CHAR_VALUE = (1 << OUTPUT_SYMBOL_SIZE) - 1;

    public static MultiLayerNetwork createSpellingErrorCorrectionModelA()
    {
        NeuralNetConfiguration.ListBuilder builder = new NeuralNetConfiguration.Builder()
                .seed(123)
                .updater(new Adam(LEARNING_RATE))
                .l2(0)
                .list();

        for (int i = 0; i < DENSE_LAYER_SIZES.length; ++i)
        {
            builder.layer(i, new DenseLayer.Builder()
                    .nIn(i == 0 ? NUMBER_OF_INPUTS_CLASS_A : DENSE_LAYER_SIZES[i - 1])
                    .nOut(DENSE_LAYER_SIZES[i])
                    .activation(Activation.RELU)
                    .weightInit(WeightInit.XAVIER)
                    .build());
        }
        builder.layer(DENSE_LAYER_SIZES.length, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                .nIn(DENSE_LAYER_SIZES[DENSE_LAYER_SIZES.length - 1])
                .nOut(NUMBER_OF_OUTPUTS)
                .activation(Activation.SIGMOID)
                .weightInit(WeightInit.XAVIER)
                .build());
        builder.setInputType(InputType.feedForward(NUMBER_OF_INPUTS_CLASS_A));
        final MultiLayerConfiguration conf = builder.build();
        final MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();
        model.setListeners(new ScoreIterationListener(1));
        return model;
    }

    public static float[] predict(MultiLayerNetwork model, float[][] input) //[batch, input]
    {
        final INDArray outputArray = model.output(Nd4j.create(input));
        return outputArray.toFloatVector();
    }

    public static void train(MultiLayerNetwork model, float[][] input, float[][] labels) //[batch, input]
    {
        model.fit(Nd4j.create(input), Nd4j.create(labels));
    }

    public static boolean saveModel(MultiLayerNetwork model, String path)
    {
        try
        {
            model.save(new File(path), true);
            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public static MultiLayerNetwork loadModel(String path)
    {
        try
        {
            return MultiLayerNetwork.load(new File(path), true);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private static int fillFloatFromText(final float[] toFill, final String text, int toFillIndex, final int endIndex) //returns new Index
    {
        //converts each character to an 8 bit number which gets represented in 8 float values
        for (int i = 0; i < text.length(); ++i)
        {
            final int charValue = Math.min(MAX_CHAR_VALUE, text.charAt(i));
            for (int j = OUTPUT_SYMBOL_SIZE - 1; j >= 0; --j)
            {
                toFill[toFillIndex++] = (charValue >> j) & 1;
            }
        }
        while(toFillIndex < endIndex)
            toFill[toFillIndex++] = 0.0f;
        return toFillIndex;
    }

    public static boolean getSampleFromTextAndConvertItToFailureText(final String textOriginal, final SpellingErrorGeneratorGerman errorGenerator, final Random random,final float[] dataToFill, final float[] labelsToFill) //returns two float arrays with data and labels
    {
        if(textOriginal.length() < MIN_TEXT_SIZE_FOR_SPELLCHECK)
            return false;
        errorGenerator.updateFactors();
        final int mainWindowStartIndex = random.nextInt(textOriginal.length() - MIN_TEXT_SIZE_FOR_SPELLCHECK);
        final String mainWindowOriginal = textOriginal.substring(mainWindowStartIndex, Math.min(mainWindowStartIndex + MAIN_WINDOW_SIZE, textOriginal.length()));
        final String labelText = mainWindowOriginal.substring(0, NUMBER_OF_OUTPUT_SYMBOLS);
        errorGenerator.setOriginalText(mainWindowOriginal);
        final String mainWindow = errorGenerator.generateErrorText();
        final String previousWindow = textOriginal.substring(Math.max(0, mainWindowStartIndex - PREVIOUS_WINDOW_SIZE), mainWindowStartIndex);
        errorGenerator.setOriginalText(previousWindow);
        final String previousErrorWindow = errorGenerator.generateErrorText();

        int dataIndex = 0; //previousWindow + previousErrorWindow + mainWindow
        int labelIndex = 0; //labelText
        dataIndex = fillFloatFromText(dataToFill, previousWindow, dataIndex, dataIndex + PREVIOUS_WINDOW_SIZE * OUTPUT_SYMBOL_SIZE);
        dataIndex = fillFloatFromText(dataToFill, previousErrorWindow, dataIndex, dataIndex + PREVIOUS_ERROR_WINDOW_SIZE * OUTPUT_SYMBOL_SIZE);
        dataIndex = fillFloatFromText(dataToFill, mainWindow, dataIndex, dataIndex + MAIN_WINDOW_SIZE * OUTPUT_SYMBOL_SIZE);
        if(dataIndex != NUMBER_OF_INPUTS_CLASS_A)
            throw new RuntimeException("dataIndex != NUMBER_OF_INPUTS_CLASS_A");
        labelIndex = fillFloatFromText(labelsToFill, labelText, labelIndex, labelIndex + NUMBER_OF_OUTPUT_SYMBOLS * OUTPUT_SYMBOL_SIZE);
        if(labelIndex != NUMBER_OF_OUTPUTS)
            throw new RuntimeException("labelIndex != NUMBER_OF_OUTPUTS");
        return true;
    }
}
