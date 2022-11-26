package execution;

import com.sun.org.apache.xpath.internal.operations.Mod;
import learning.train.ModelManager;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

public class Corrector
{
    private final MultiLayerNetwork model;
    private final float[] data;

    public Corrector(MultiLayerNetwork model)
    {
        this.model = model;
        this.data = new float[ModelManager.NUMBER_OF_INPUTS_CLASS_A];
    }

    public String correct(String input)
    {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < input.length(); i++)
        {
            ModelManager.getTextWindowFromIndexAsFloat(i,input,output,data);
            final float[] result = ModelManager.predict(model,new float[][]{data});
            output.append(ModelManager.convertFloatArrayToString(result));
        }
        return output.toString();
    }
}
