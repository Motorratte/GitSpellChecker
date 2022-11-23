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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import static com.sun.org.apache.xml.internal.serialize.Method.TEXT;

public class ModelManager
{
    public static final int[] DENSE_LAYER_SIZES = new int[]{2880, 3072, 2560, 2048, 1536, 1024, 768, 512};
    //public static final int[] DENSE_LAYER_SIZES = new int[]{1536,2048,1024,256};

    //public static final int[] DENSE_LAYER_SIZES = new int[]{1024};
    public static final double LEARNING_RATE = 0.00005;
    public static final int NUMBER_OF_OUTPUT_SYMBOLS = 1;
    public static final int SYMBOL_BIT_SIZE = 8;
    public static final int NUMBER_OF_OUTPUTS = NUMBER_OF_OUTPUT_SYMBOLS * SYMBOL_BIT_SIZE;
    public static final int PREVIOUS_WINDOW_SIZE = 128; //number of symbols
    public static final int PREVIOUS_ERROR_WINDOW_SIZE = 48;
    public static final int MAIN_WINDOW_SIZE = NUMBER_OF_OUTPUT_SYMBOLS + PREVIOUS_WINDOW_SIZE;
    public static final int MIN_TEXT_SIZE_FOR_SPELLCHECK = 3;
    public static final int NUMBER_OF_INPUTS_CLASS_A = (PREVIOUS_WINDOW_SIZE + PREVIOUS_ERROR_WINDOW_SIZE + MAIN_WINDOW_SIZE) * SYMBOL_BIT_SIZE; //896Korregiertvorher 384unkorregiertvorher 8Korrektur 896restfenster = 2440
    public static final int NUMBER_OF_INPUTS_CLASS_B = (NUMBER_OF_OUTPUT_SYMBOLS * 3 + PREVIOUS_WINDOW_SIZE + PREVIOUS_ERROR_WINDOW_SIZE + MAIN_WINDOW_SIZE) * SYMBOL_BIT_SIZE; //24PreviousOutputs 1024Korregiertvorher 384unkorregiertvorher 8Korrektur 1024restfenster = 2464
    public static final int MAX_CHAR_VALUE = (1 << SYMBOL_BIT_SIZE) - 1;

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
                .nIn(DENSE_LAYER_SIZES.length == 0 ? NUMBER_OF_INPUTS_CLASS_A : DENSE_LAYER_SIZES[DENSE_LAYER_SIZES.length - 1])
                .nOut(NUMBER_OF_OUTPUTS)
                .activation(Activation.SIGMOID)
                .weightInit(WeightInit.XAVIER)
                .build());
        builder.setInputType(InputType.feedForward(NUMBER_OF_INPUTS_CLASS_A));
        final MultiLayerConfiguration conf = builder.build();
        final MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();
        model.setListeners(new ScoreIterationListener(50));
        return model;
    }

    public static MultiLayerNetwork createSpellingErrorCorrectionModelB()
    {
        NeuralNetConfiguration.ListBuilder builder = new NeuralNetConfiguration.Builder()
                .seed(123)
                .updater(new Adam(LEARNING_RATE))
                .l2(0)
                .list();

        for (int i = 0; i < DENSE_LAYER_SIZES.length; ++i)
        {
            builder.layer(i, new DenseLayer.Builder()
                    .nIn(i == 0 ? NUMBER_OF_INPUTS_CLASS_B : DENSE_LAYER_SIZES[i - 1])
                    .nOut(DENSE_LAYER_SIZES[i])
                    .activation(Activation.RELU)
                    .weightInit(WeightInit.XAVIER)
                    .build());
        }
        builder.layer(DENSE_LAYER_SIZES.length, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                .nIn(DENSE_LAYER_SIZES.length == 0 ? NUMBER_OF_INPUTS_CLASS_B : DENSE_LAYER_SIZES[DENSE_LAYER_SIZES.length - 1])
                .nOut(NUMBER_OF_OUTPUTS)
                .activation(Activation.SIGMOID)
                .weightInit(WeightInit.XAVIER)
                .build());
        builder.setInputType(InputType.feedForward(NUMBER_OF_INPUTS_CLASS_B));
        final MultiLayerConfiguration conf = builder.build();
        final MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();
        model.setListeners(new ScoreIterationListener(50));
        return model;
    }

    public static float[] predict(MultiLayerNetwork model, float[][] input) //[batch, input]
    {
        final INDArray outputArray = model.output(Nd4j.create(input));
        //get number of dimensions
        final int dimensions = outputArray.shape().length;
        if (dimensions == 1)
        {
            return outputArray.toFloatVector();
        }
        else
        {
            final float[][] output = outputArray.toFloatMatrix();
            float[] result = new float[output.length * output[0].length];
            for (int i = 0; i < output.length; ++i)
            {
                System.arraycopy(output[i], 0, result, i * NUMBER_OF_OUTPUTS, NUMBER_OF_OUTPUTS);
            }
            return result;
        }
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
            final MultiLayerNetwork model = MultiLayerNetwork.load(new File(path), true);
            model.setListeners(new ScoreIterationListener(50));
            return model;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private static int fillFloatFromText(final float[] toFill, final String text, int toFillIndex, final int endIndex, final boolean fillUpWithNullFromLeft) //returns new Index
    {
        //converts each character to an 8 bit number which gets represented in 8 float values
        if (toFillIndex + text.length() * SYMBOL_BIT_SIZE > endIndex)
            throw new IllegalArgumentException("Text is too long to fit into the array!");
        if (fillUpWithNullFromLeft)
        {
            final int newToFillIndex = endIndex - (text.length() * SYMBOL_BIT_SIZE);
            while (toFillIndex < newToFillIndex)
                toFill[toFillIndex++] = 0.0f;
        }
        for (int i = 0; i < text.length(); ++i)
        {
            final int charValue = Math.min(MAX_CHAR_VALUE, text.charAt(i));
            for (int j = SYMBOL_BIT_SIZE - 1; j >= 0; --j)
            {
                toFill[toFillIndex++] = (charValue >> j) & 1;
            }
        }
        if (!fillUpWithNullFromLeft)
        {
            while (toFillIndex < endIndex)
                toFill[toFillIndex++] = 0.0f;
        }
        return toFillIndex;
    }

    public static String convertFloatArrayToString(final float[] array)
    {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < array.length; i += SYMBOL_BIT_SIZE)
        {
            int charValue = 0;
            for (int j = 0; j < SYMBOL_BIT_SIZE; ++j)
            {
                charValue |= (array[i + j] > 0.5f ? 1 : 0) << (SYMBOL_BIT_SIZE - j - 1);
            }
            if (charValue == 0)
                continue;
            builder.append((char) charValue);
        }
        return builder.toString();
    }

    public static String getSampleFromTextAndConvertItToFailureText(final String textOriginal, final SpellingErrorGeneratorGerman errorGenerator, final Random random, final float[] dataToFill, final float[] labelsToFill) //returns two float arrays with data and labels
    {
        if (textOriginal.length() < MIN_TEXT_SIZE_FOR_SPELLCHECK)
            return null;
        errorGenerator.updateFactors();
        final int mainWindowStartIndex = random.nextInt((textOriginal.length() - MIN_TEXT_SIZE_FOR_SPELLCHECK) + 1);
        final String mainWindowOriginal = textOriginal.substring(mainWindowStartIndex, Math.min(mainWindowStartIndex + MAIN_WINDOW_SIZE, textOriginal.length()));
        final String labelText = mainWindowOriginal.substring(0, Math.min(mainWindowOriginal.length(), NUMBER_OF_OUTPUT_SYMBOLS));
        errorGenerator.setOriginalText(mainWindowOriginal);
        String mainWindow = errorGenerator.generateErrorText();
        mainWindow = mainWindow.substring(0, Math.min(mainWindow.length(), MAIN_WINDOW_SIZE));
        final String previousWindow = textOriginal.substring(Math.max(0, mainWindowStartIndex - PREVIOUS_WINDOW_SIZE), mainWindowStartIndex);
        errorGenerator.setOriginalText(previousWindow);
        String previousErrorWindow = errorGenerator.generateErrorText();
        previousErrorWindow = previousErrorWindow.substring(Math.max(0, previousErrorWindow.length() - PREVIOUS_ERROR_WINDOW_SIZE));

        int dataIndex = 0; //previousWindow + previousErrorWindow + mainWindow
        int labelIndex = 0; //labelText
        dataIndex = fillFloatFromText(dataToFill, previousWindow, dataIndex, dataIndex + PREVIOUS_WINDOW_SIZE * SYMBOL_BIT_SIZE, true);
        dataIndex = fillFloatFromText(dataToFill, previousErrorWindow, dataIndex, dataIndex + PREVIOUS_ERROR_WINDOW_SIZE * SYMBOL_BIT_SIZE, true);
        dataIndex = fillFloatFromText(dataToFill, mainWindow, dataIndex, dataIndex + MAIN_WINDOW_SIZE * SYMBOL_BIT_SIZE, false);
        if (dataIndex != NUMBER_OF_INPUTS_CLASS_A)
            throw new RuntimeException("dataIndex != NUMBER_OF_INPUTS_CLASS_A");
        labelIndex = fillFloatFromText(labelsToFill, labelText, labelIndex, labelIndex + NUMBER_OF_OUTPUT_SYMBOLS * SYMBOL_BIT_SIZE, false);
        if (labelIndex != NUMBER_OF_OUTPUTS)
            throw new RuntimeException("labelIndex != NUMBER_OF_OUTPUTS");
        return "Error Text main: " + mainWindow + "\nCorrect Text main: " + mainWindowOriginal + "\nLabelText: " + labelText;
    }

    public static void testModel(BufferedReader reader, final List<MultiLayerNetwork> models, final SpellingErrorGeneratorGerman errorGenerator, final Random random, final int numberOfTests) throws IOException
    {
        final float[] data = new float[NUMBER_OF_INPUTS_CLASS_A];
        final float[] labels = new float[NUMBER_OF_OUTPUTS];
        int correct = 0;
        int failureCorrected = 0;
        int failureProduced = 0;
        int numberOfFailures = 0;
        int total = 0;
        float[][] outputs = new float[models.size()][];
        for (int i = 0; i < numberOfTests;)
        {
            final String text = reader.readLine();
            final String sample = getSampleFromTextAndConvertItToFailureText(text, errorGenerator, random, data, labels);
            if (sample == null)
                continue;
            ++i;
            float[][] data2D = new float[][]{data};
            for (int j = 0; j < models.size(); ++j)
            {
                outputs[j] = predict(models.get(j), data2D);
            }
            final float[] outputAverage = new float[outputs[0].length];
            for(int j = 0; j < outputs.length; ++j)
            {
                final float[] currentOutput = outputs[j];
                for(int k = 0; k < currentOutput.length; ++k)
                {
                    outputAverage[k] += currentOutput[k];
                }
            }
            for(int j = 0; j < outputAverage.length; ++j)
            {
                outputAverage[j] /= outputs.length;
            }


            final String outputString = convertFloatArrayToString(outputAverage);
            final String labelString = convertFloatArrayToString(labels);
            final boolean labelEqualsToInput = labelString.equals("" + sample.charAt(17)) || labelString.charAt(0) >= MAX_CHAR_VALUE;
            if (outputString.equals(labelString))
            {
                ++correct;
                if (!labelEqualsToInput)
                {
                    ++failureCorrected;
                    ++numberOfFailures;
                }
            }
            else
            {
                if (labelEqualsToInput)
                    ++failureProduced;
                else
                    ++numberOfFailures;
            }
            ++total;
            System.out.println("Sample: " + sample);
            System.out.println("Output: " + outputString);
            System.out.println("Label: " + labelString);
            System.out.println("Correct: " + correct + "/" + total);
            System.out.println("Failure Corrected: " + failureCorrected + "/" + numberOfFailures);
            System.out.println("Failure Produced: " + failureProduced);
            System.out.println();
        }
    }

}
