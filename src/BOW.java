import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.util.*;

public class BOW {
    @Argument(required = true, index = 0, usage = "Corpus directory.")
    private File mainDirectory;

    @Option(name = "-s", aliases = {"--stem"}, usage = "Enables stemming.")
    private boolean doStemming;

    @Option(name = "-l", aliases = {"--lower-bound"}, usage = "Defines lower frequency threshold.")
    private int lowerThreshold = -1;

    @Option(name = "-u", aliases = {"--upper-bound"}, usage = "Defines upper frequency threshold.")
    private int upperThreshold = -1;

    public void run() {
        Dictionary<String, Result> results = new Hashtable<String, Result>();
//
//        // Read File and tokenize
//        File mainDirectory = new File("20news-18828");

        // Iterate directories
        File directories[];

        if (mainDirectory.isDirectory())
            directories = mainDirectory.listFiles();
        else
            directories = new File[]{mainDirectory};

        for (File directory : directories) {
            // Get class name
            String className = directory.getName();

            // Iterate files
            File files[] = directory.listFiles();
            for (File file : files) {
                Long fileName = Long.parseLong(file.getName());

                ArrayList<String> tokens = Utility.Tokenize(file);

                if (doStemming)
                    tokens = Utility.Stemm(tokens);

                // Add Tokens to Result List
                for (String token : tokens) {
                    Result result = results.get(token);

                    // Already in Result List?
                    if (result == null) {
                        // No add to dictionary
                        List<Entry> newList = new LinkedList<Entry>();
                        Entry entry = new Entry(className, fileName);
                        // entry.setDocId(fileName);
                        entry.setFreq(1);

                        newList.add(entry);

                        results.put(token, new Result(token, 1, newList));

                        Result.incrementDocumentLength(className, fileName);
                    } else {
                        // Add document if it's not already in list

                        // Add Document Reference to dictionary
                        result.addToDocList(className, fileName);
                    }
                }
            }


        }

        Enumeration<Result> e = results.elements();

        //iterate through Hashtable values Enumeration
        while (e.hasMoreElements()) {
            Result result = e.nextElement();
            result.calculateWeightsNormalizedTF();

            if (result.performFeatureSelection(lowerThreshold, upperThreshold)) {
                results.remove(result.getFeature());
            } else {
                System.out.println(result);
            }


        }

        // Print Result List
        //System.out.println(results.toString());

        //File file = new File("9150");
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        BOW bow = new BOW();
        CmdLineParser parser = new CmdLineParser(bow);
        parser.setUsageWidth(80); // width of the error display area

        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println("java DotsMain [options...] arguments...");
            // print the list of available options
            parser.printUsage(System.err);
            System.err.println();
            System.exit(1);
        }

        bow.run();
    }


}
