package execution;

import learning.train.ModelManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CorrectorExecutioner
{
    public static void main(String[] args)
    {
        System.out.println("Starting Corrector");
        //print current path
        System.out.println("Current path: " + System.getProperty("user.dir"));
        final Corrector corrector = new Corrector(ModelManager.loadModel("src/main/java/learning/train/saves/modelB4914192429343944495459.net"));
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
