package test;

import learning.generate.SpellingErrorGeneratorGerman;
import learning.train.ModelManager;
import learning.train.Trainer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class FailureRateTest
{
    public static void main(String[] args) throws IOException
    {
        System.out.println("Starting FailureRateTest");
        String nnPath = "src/main/java/learning/train/saves/testModelMiddle.net";
        MultiLayerNetwork model = ModelManager.loadModel(nnPath);
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/java/learning/train/data/europarl-v7.de-en.datade")))
        {
            ModelManager.testModel(reader,model,new SpellingErrorGeneratorGerman(),new Random(),10000);
        }

    }
}
