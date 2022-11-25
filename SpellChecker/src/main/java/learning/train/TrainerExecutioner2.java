package learning.train;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

import java.io.IOException;
import java.util.ArrayList;

public class TrainerExecutioner2
{
    public static void main(String[] args) throws IOException
    {
        System.out.println("Starting Trainer");
        //print current path
        System.out.println("Current path: " + System.getProperty("user.dir"));
        final Trainer trainer = new Trainer("src/main/java/learning/train/data/europarl-v7.de-en.datade", 128);
        ArrayList<MultiLayerNetwork> previousModelList = new ArrayList<>();
        previousModelList.add(ModelManager.loadModel("src/main/java/learning/train/saves/testModelHeavyNew.net"));
        previousModelList.add(ModelManager.loadModel("src/main/java/learning/train/saves/testModelMiddle2New.net"));
        previousModelList.add(ModelManager.loadModel("src/main/java/learning/train/saves/testModelMiddle2New.net"));

        trainer.train(60,10,null,"src/main/java/learning/train/saves/modelB4914192429343944495459.net" ,"+");
    }
}
