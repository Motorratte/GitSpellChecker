package learning.train;

import learning.generate.SpellingErrorGeneratorGerman;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class Trainer
{
    private final int BATCH_SIZE;
    private final float[][] inputBatchClassA;

    private final float[][] inputBatchClassB;
    private final float[][] labelBatch;
    private int batchIndex = 0;
    private final String filePath;

    public Trainer(final String filePath, final int BATCH_SIZE)
    {
        this.filePath = filePath;
        this.BATCH_SIZE = BATCH_SIZE;
        this.inputBatchClassA = new float[BATCH_SIZE][];
        this.inputBatchClassB = new float[BATCH_SIZE][];
        this.labelBatch = new float[BATCH_SIZE][];
        for (int i = 0; i < BATCH_SIZE; ++i)
        {
            this.inputBatchClassA[i] = new float[ModelManager.NUMBER_OF_INPUTS_CLASS_A];
            this.inputBatchClassB[i] = new float[ModelManager.NUMBER_OF_INPUTS_CLASS_B];
            this.labelBatch[i] = new float[ModelManager.NUMBER_OF_OUTPUTS];
        }
    }
    private void fillInputBatchClassBWithPreviousModelResults(final float[][] previousModelResults)
    {
        final int numberOfPreviousModels = previousModelResults.length;
        final int previousModelResultsIndexStepSize = previousModelResults[0].length / BATCH_SIZE;
        for (int i = 0, previousModelResultIndex = 0; i < BATCH_SIZE; ++i, previousModelResultIndex += previousModelResultsIndexStepSize)
        {
            final float[] inputToFill = inputBatchClassB[i];
            final float[] toCopy = inputBatchClassA[i];
            for (int j = 0; j < numberOfPreviousModels; ++j)
            {
                System.arraycopy(previousModelResults[j], previousModelResultIndex, inputToFill, j * ModelManager.NUMBER_OF_OUTPUTS, ModelManager.NUMBER_OF_OUTPUTS);
            }
            System.arraycopy(toCopy, 0, inputToFill, numberOfPreviousModels * ModelManager.NUMBER_OF_OUTPUTS, ModelManager.NUMBER_OF_INPUTS_CLASS_A);
        }

    }
    public void train(int epochs, int printFrequency, List<MultiLayerNetwork> previousModels, String saveModelPath, String addToSaveModelPathIfModelExists) throws IOException
    {
        //check if model at path exists
        final MultiLayerNetwork model;
        if (new java.io.File(saveModelPath).exists())
        {
            if(addToSaveModelPathIfModelExists == null)
                addToSaveModelPathIfModelExists = "New";
            addToSaveModelPathIfModelExists = addToSaveModelPathIfModelExists + ".net";
            System.out.println("Loading model from path: " + saveModelPath);
            model = ModelManager.loadModel(saveModelPath);
            System.out.println("Model loaded!");
            saveModelPath = saveModelPath.substring(0,saveModelPath.lastIndexOf('.'));
            System.out.println("Changing save path to: " + saveModelPath + addToSaveModelPathIfModelExists);
            saveModelPath = saveModelPath + addToSaveModelPathIfModelExists;
        }
        else
        {
            model = (previousModels == null || previousModels.size() == 0) ? ModelManager.createSpellingErrorCorrectionModelA() : ModelManager.createSpellingErrorCorrectionModelB();
        }

        final float[][] previousModelResults = (previousModels == null) ? null : new float[previousModels.size()][BATCH_SIZE * ModelManager.NUMBER_OF_OUTPUTS];

        final SpellingErrorGeneratorGerman generator = new SpellingErrorGeneratorGerman();
        final Random random = new Random();
        String line;
        for(int epoch = 0,trainingIteration = 0; epoch < epochs;++epoch)
        {
            final BufferedReader reader = new BufferedReader(new FileReader(filePath));
            while ((line = reader.readLine()) != null)
            {
                final float[] inputToFill = inputBatchClassA[batchIndex];
                final float[] labelToFill = labelBatch[batchIndex];
                final String contextAsText = ModelManager.getSampleFromTextAndConvertItToFailureText(line, generator, random, inputToFill, labelToFill);
                //final String labelAsString = ModelManager.convertFloatArrayToString(labelToFill);
                if (contextAsText != null)
                {
                    //System.out.println(contextAsText);
                    ++batchIndex;
                    if (batchIndex == BATCH_SIZE)
                    {
                        //System.out.println("Training with batch");
                        if(previousModels == null)
                        {
                            ModelManager.train(model, inputBatchClassA, labelBatch);
                        }
                        else
                        {
                            for (int i = 0; i < previousModels.size(); ++i)
                            {
                                previousModelResults[i] = ModelManager.predict(previousModels.get(i), inputBatchClassA);
                            }
                            fillInputBatchClassBWithPreviousModelResults(previousModelResults);
                            ModelManager.train(model, inputBatchClassB, labelBatch);
                        }
                        if(trainingIteration % printFrequency == 0)
                        {
                            String result = ModelManager.convertFloatArrayToString(ModelManager.predict(model, new float[][]{(previousModels == null || previousModels.size() == 0) ? inputBatchClassA[0] : inputBatchClassB[0]}));
                            System.out.println("Label: " + ModelManager.convertFloatArrayToString(labelBatch[0]));
                            System.out.println("Result: " + result);
                        }
                        ++trainingIteration;
                        batchIndex = 0;
                    }
                }
            }
            if((epoch + 1) % 5 == 0)
            {
                String temporaryPath = saveModelPath.substring(0,saveModelPath.lastIndexOf('.'));
                temporaryPath = temporaryPath + (epoch + 1) + ".net";
                ModelManager.saveModel(model, temporaryPath);
            }
        }
        ModelManager.saveModel(model, saveModelPath);
    }
}
