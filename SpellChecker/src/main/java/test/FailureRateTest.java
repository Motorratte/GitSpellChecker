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
        modelList.add(ModelManager.loadModel("src/main/java/learning/train/saves/modelNextA120.net"));
        modelList.add(ModelManager.loadModel("src/main/java/learning/train/saves/modelNextB120.net"));
        //modelList.add(ModelManager.loadModel("src/main/java/learning/train/saves/testModel.net"));
        //modelList.add(ModelManager.loadModel("src/main/java/learning/train/saves/testModelHeavy.net"));
        ArrayList<MultiLayerNetwork> modelList2 = new ArrayList<>();
        modelList2.add(ModelManager.loadModel("src/main/java/learning/train/saves/ModelOptimicerHeavy175.net"));
        //modelList2.add(ModelManager.loadModel("src/main/java/learning/train/saves/ModelOptimicerLight175.net"));

        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/java/learning/train/data/europarl-v7.de-en.datade")))
        {
            ModelManager.testModel(reader,modelList,modelList2,new SpellingErrorGeneratorGerman(),new Random(65443),30000);
        }

    }
}
