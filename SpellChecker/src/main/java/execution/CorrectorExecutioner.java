package execution;

import learning.train.ModelManager;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CorrectorExecutioner
{
    public static void main(String[] args)
    {
        System.out.println("Starting Corrector");
        ArrayList<MultiLayerNetwork> modelList = new ArrayList<>();
        modelList.add(ModelManager.loadModel("src/main/java/learning/train/saves/modelNextB75.net"));
        modelList.add(ModelManager.loadModel("src/main/java/learning/train/saves/modelNextB30.net"));
        modelList.add(ModelManager.loadModel("src/main/java/learning/train/saves/modelNextA75.net"));
        modelList.add(ModelManager.loadModel("src/main/java/learning/train/saves/modelNextA30.net"));
        final Corrector corrector = new Corrector(modelList);
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        while(true)
        {
            try
            {
                System.out.println("Enter sentence to correct:");
                String input = br.readLine();
                System.out.println(corrector.correct(input));
                System.out.println();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
