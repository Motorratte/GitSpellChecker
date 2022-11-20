package learning.train;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

import java.io.FileNotFoundException;
import java.io.FileReader;

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

    public void train(int epochs) throws FileNotFoundException
    {
        final MultiLayerNetwork model = ModelManager.createSpellingErrorCorrectionModelA();
        final FileReader reader = new FileReader(filePath);
        
    }

}
