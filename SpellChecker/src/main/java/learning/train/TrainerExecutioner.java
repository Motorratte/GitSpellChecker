package learning.train;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

import java.io.IOException;
import java.util.ArrayList;

public class TrainerExecutioner
{
    public static void main(String[] args) throws IOException
    {
        System.out.println("Starting Trainer");
        //print current path
        System.out.println("Current path: " + System.getProperty("user.dir"));
        final Trainer trainer = new Trainer("src/main/java/learning/train/data/europarl-v7.de-en.datade", 256);
        ArrayList<MultiLayerNetwork> previousModelList = new ArrayList<>();
        previousModelList.add(ModelManager.loadModel("src/main/java/learning/train/saves/testModelHeavyNew.net"));
        previousModelList.add(ModelManager.loadModel("src/main/java/learning/train/saves/testModelMiddlenew.net"));
        previousModelList.add(ModelManager.loadModel("src/main/java/learning/train/saves/testModelMiddle2New.net"));

        trainer.train(15,50,previousModelList,"src/main/java/learning/train/saves/testModelClassBHeavy.net");
    }
}
