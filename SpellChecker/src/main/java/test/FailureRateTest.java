package test;

import learning.generate.SpellingErrorGeneratorGerman;
import learning.train.ModelManager;
import learning.train.Trainer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FailureRateTest
{
    public static void main(String[] args) throws IOException
    {
        System.out.println("Starting FailureRateTest");
        ArrayList<MultiLayerNetwork> modelList = new ArrayList<>();
        modelList.add(ModelManager.loadModel("src/main/java/learning/train/saves/testModelHeavyNew.net"));
        modelList.add(ModelManager.loadModel("src/main/java/learning/train/saves/testModelMiddleNew.net"));
        modelList.add(ModelManager.loadModel("src/main/java/learning/train/saves/testModelMiddle2New.net"));
        //modelList.add(ModelManager.loadModel("src/main/java/learning/train/saves/testModel.net"));
        //modelList.add(ModelManager.loadModel("src/main/java/learning/train/saves/testModelHeavy.net"));
        ArrayList<MultiLayerNetwork> modelList2 = new ArrayList<>();
        modelList2.add(ModelManager.loadModel("src/main/java/learning/train/saves/modelC4914192429343944.net"));
        modelList2.add(ModelManager.loadModel("src/main/java/learning/train/saves/modelA4914192429343944495459.net"));
        modelList2.add(ModelManager.loadModel("src/main/java/learning/train/saves/modelB4914192429343944495459.net"));

        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/java/learning/train/data/europarl-v7.de-en.datade")))
        {
            ModelManager.testModel(reader,null,modelList2,new SpellingErrorGeneratorGerman(),new Random(),30000);
        }

    }
}
