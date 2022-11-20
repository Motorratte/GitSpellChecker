package learning.train;

import java.io.IOException;

public class TrainerExecutioner
{
    public static void main(String[] args) throws IOException
    {
        System.out.println("Starting Trainer");
        //print current path
        System.out.println("Current path: " + System.getProperty("user.dir"));
        final Trainer trainer = new Trainer("src/main/java/learning/train/data/europarl-v7.de-en.datade", 8);
        trainer.train(1);
    }
}
