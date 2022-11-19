package training.training;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.activations.Activation;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class ModelManager
{
    public static final int[] DENSE_LAYER_SIZES = new int[]{2880, 3072, 2560, 2048, 1536, 1024, 768, 512};
    public static final int NUMBER_OF_FILTERS = 256;
    public static final double LEARNING_RATE = 0.0001;
    public static final int BATCH_SIZE = 8;
    public static final int EPOCHS = 1;
    public static final int NUMBER_OF_OUTPUTS = 512;
    public static final int NUMBER_OF_INPUTS = 2688; //896Korregiertvorher 384unkorregiertvorher 512Korrektur 896restfenster = 2688
    public static MultiLayerNetwork createSpellingErrorCorrectionModelA()
    {
        NeuralNetConfiguration.ListBuilder builder = new NeuralNetConfiguration.Builder()
                .seed(123)
                .updater(new Adam(LEARNING_RATE))
                .l2(0)
                .list();

        for (int i = 0; i < DENSE_LAYER_SIZES.length; ++i)
        {
            builder.layer(i, new DenseLayer.Builder()
                    .nIn(i == 0 ? NUMBER_OF_INPUTS : DENSE_LAYER_SIZES[i - 1])
                    .nOut(DENSE_LAYER_SIZES[i])
                    .activation(Activation.RELU)
                    .weightInit(WeightInit.XAVIER)
                    .build());
        }
        builder.layer(DENSE_LAYER_SIZES.length, new OutputLayer.Builder(LossFunctions.LossFunction.MSE)
                .nIn(DENSE_LAYER_SIZES[DENSE_LAYER_SIZES.length - 1])
                .nOut(NUMBER_OF_OUTPUTS)
                .activation(Activation.SIGMOID)
                .weightInit(WeightInit.XAVIER)
                .build());
        builder.setInputType(InputType.feedForward(NUMBER_OF_INPUTS));
        final MultiLayerConfiguration conf = builder.build();
        final MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();
        model.setListeners(new ScoreIterationListener(1));
        return model;
    }

    public static float[] predict(MultiLayerNetwork model, float[][] input) //[batch, 3072]
    {
        final INDArray outputArray = model.output(Nd4j.create(input));
        return outputArray.toFloatVector();
    }

    public static void train(MultiLayerNetwork model, float[][] input, float[][] labels) //[batch, 3072]
    {
        model.fit(Nd4j.create(input), Nd4j.create(labels));
    }

    public static float[][] convertTextTo
}
