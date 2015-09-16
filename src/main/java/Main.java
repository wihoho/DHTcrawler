import edu.stanford.nlp.ie.regexp.NumberSequenceClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.sequences.DocumentReaderAndWriter;
import edu.stanford.nlp.sequences.PlainTextDocumentReaderAndWriter;
import edu.stanford.nlp.util.StringUtils;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author Gong Li <gong_l@worksap.co.jp> on 16/9/2015.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        Properties props = StringUtils.argsToProperties(args);
        NumberSequenceClassifier nsc = new NumberSequenceClassifier(props, true, props);
        String trainFile = nsc.flags.trainFile;
        String testFile = nsc.flags.testFile;
        String textFile = nsc.flags.textFile;
        String loadPath = nsc.flags.loadClassifier;
        String serializeTo = nsc.flags.serializeTo;

        if (loadPath != null) {
            nsc.loadClassifierNoExceptions(loadPath);
            nsc.flags.setProperties(props);
        } else if (trainFile != null) {
            nsc.train(trainFile);
        }

        if (serializeTo != null) {
            nsc.serializeClassifier(serializeTo);
        }

        if (testFile != null) {
            nsc.classifyAndWriteAnswers(testFile, nsc.makeReaderAndWriter(), true);
        }

        if (textFile != null) {
            DocumentReaderAndWriter<CoreLabel> readerAndWriter =
                    new PlainTextDocumentReaderAndWriter<CoreLabel>();
            nsc.classifyAndWriteAnswers(textFile, readerAndWriter, false);
        }


        CoreLabel coreLabel = new CoreLabel();
        coreLabel.setWord("2015?10?");

        List<CoreLabel> result =  nsc.classify(Arrays.<CoreLabel>asList(coreLabel));
        System.out.println();
    }
}
