package execution;

import com.sun.org.apache.xpath.internal.operations.Mod;
import learning.train.ModelManager;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

import java.util.ArrayList;

public class Corrector
{
    private final ArrayList<MultiLayerNetwork> models;
    private final float[] data;

    public Corrector(ArrayList<MultiLayerNetwork> model)
    {
        this.models = model;
        this.data = new float[ModelManager.NUMBER_OF_INPUTS_CLASS_A];
    }

    public String correct(String input)
    {
        StringBuilder output = new StringBuilder();
        float[][] results = new float[models.size()][];
        final float[][] dataContainer = new float[][]{data};
        for (int i = 0; i < input.length(); i++)
        {
            ModelManager.getTextWindowFromIndexAsFloat(i, input, output, data);
            //predict
            for (int k = 0; k < models.size(); ++k)
            {
                results[k] = ModelManager.predict(models.get(k), dataContainer);
            }
            //write average to 0
            final float[] average = new float[ModelManager.NUMBER_OF_OUTPUTS];
            for (int j = 0; j < results.length; ++j)
            {
                for (int k = 0; k < ModelManager.NUMBER_OF_OUTPUTS; ++k)
                {
                    average[k] += results[j][k];
                }
            }
            for (int k = 0; k < ModelManager.NUMBER_OF_OUTPUTS; ++k)
            {
                average[k] /= results.length;
            }
            output.append(ModelManager.convertFloatArrayToString(average));
        }
        return output.toString();
    }
}
