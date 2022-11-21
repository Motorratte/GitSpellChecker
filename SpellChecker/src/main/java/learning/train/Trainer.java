package learning.train;

import learning.generate.SpellingErrorGeneratorGerman;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class Trainer
{
    private final int BATCH_SIZE;
    private final float[][] inputBatch;
    private final float[][] labelBatch;
    private int batchIndex = 0;
    private final String filePath;

    public Trainer(final String filePath, final int BATCH_SIZE)
    {
        this.filePath = filePath;
        this.BATCH_SIZE = BATCH_SIZE;
        this.inputBatch = new float[BATCH_SIZE][];
        this.labelBatch = new float[BATCH_SIZE][];
        for (int i = 0; i < BATCH_SIZE; ++i)
        {
            this.inputBatch[i] = new float[ModelManager.NUMBER_OF_INPUTS_CLASS_A];
            this.labelBatch[i] = new float[ModelManager.NUMBER_OF_OUTPUTS];
        }
    }

    public void train(int epochs,int printFrequency,String saveModelPath) throws IOException
    {
        final MultiLayerNetwork model = ModelManager.createSpellingErrorCorrectionModelA();
        //ModelManager.saveModel(model, "src/main/java/learning/train/saves/testModel2.net");
        //read textfile line by line

        final SpellingErrorGeneratorGerman generator = new SpellingErrorGeneratorGerman();
        final Random random = new Random();
        String line;
        for(int epoch = 0,trainingIteration = 0; epoch < epochs;++epoch)
        {
            final BufferedReader reader = new BufferedReader(new FileReader(filePath));
            while ((line = reader.readLine()) != null)
            {
                final float[] inputToFill = inputBatch[batchIndex];
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
                        ModelManager.train(model, inputBatch, labelBatch);
                        if(trainingIteration % printFrequency == 0)
                        {
                            String result = ModelManager.convertFloatArrayToString(ModelManager.predict(model, new float[][]{inputBatch[0]}));
                            System.out.println("Label: " + ModelManager.convertFloatArrayToString(labelBatch[0]));
                            System.out.println("Result: " + result);
                        }
                        ++trainingIteration;
                        batchIndex = 0;
                    }
                }
            }
        }
        ModelManager.saveModel(model, saveModelPath);
    }
}
