import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

public class BOW {
    public enum Method {
        BOOLEAN,
        TF,
        TF_IDF
    }

    @Argument(required = true, index = 0, multiValued = false, usage = "Corpus directory.")
    private File mainDirectory;

    @Option(name = "-s", required = false, aliases = {"--stem"}, usage = "Enables stemming.")
    private boolean doStemming = false;

    @Option(name = "-l", required = false, aliases = {"--lower-bound"}, usage = "Defines lower frequency threshold.")
    private float lowerThreshold = -1.0f;

    @Option(name = "-u", required = false, aliases = {"--upper-bound"}, usage = "Defines upper frequency threshold.")
    private float upperThreshold = -1.0f;

    @Option(name = "-o", required = false, aliases = {"--output"}, usage = "name of the generated ARFF file.")
    private File output = null;

    @Option(name = "-m", aliases = {"--method"}, usage = "Method used for weight calculation")
    private Method method = Method.TF;

    public void run() throws IOException {
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

        if (output != null) {
            FileWriter writer = new FileWriter(output, false);

            List<String> tokens = new ArrayList<String>(results.size());

            Enumeration<String> tokensEnumeration = results.keys();
            while (tokensEnumeration.hasMoreElements()) {
                String token = tokensEnumeration.nextElement();

                if (token.length() == 0) continue;

                tokens.add(token);
            }

            Collections.sort(tokens);

            writer.write("@RELATION ");
            writer.write(output.getName());
            writer.write("\n@ATTRIBUTE document string\n");
            writer.write("\n@ATTRIBUTE class string\n");

            for (String token : tokens)
                writer.write("@ATTRIBUTE tok_" + token + " numeric\n");
            writer.write("@DATA\n");
            DecimalFormat floatFormat = new DecimalFormat("0.####################E0");

            Map<Id, List<Column>> rows = new HashMap<Id, List<Column>>();

            while (e.hasMoreElements()) {
                Result result = e.nextElement();

                switch (method) {
                    case BOOLEAN:
                        result.calculateWeightsBoolean();
                        break;
                    case TF:
                        result.calculateWeightsNormalizedTF();
                        break;
                    case TF_IDF:
                        // TODO
                        //result.calculateWeightsNormalizedTF();
                        break;
                }

                for (Entry entry : result.getDocs()) {
                    Id id = new Id(entry.getClassName(), "" + entry.getDocId());

                    List<Column> entries = rows.get(id);

                    if (entries == null) {
                        entries = new ArrayList<Column>(tokens.size());
                        rows.put(id, entries);
                    }

                    Column c = new Column();
                    c.token = result.getFeature();
                    c.weight = entry.getWeight();

                    entries.add(c);
                }
            }

            for (Map.Entry<Id, List<Column>> row : rows.entrySet()) {
                Collections.sort(row.getValue());

                writer.write("{ 0 ");
                writer.write(row.getKey().name);
                writer.write(", 1 ");
                writer.write(row.getKey().clazz);

                for (Column entry : row.getValue()) {
                    boolean isInsideLowerBound = lowerThreshold < 0 || entry.weight > lowerThreshold;
                    boolean isInsideUpperBound = upperThreshold < 0 || entry.weight < upperThreshold;

                    if (!isInsideLowerBound || !isInsideUpperBound) continue;

                    //int pos = tokens.indexOf(entry.)
                    int position = tokens.indexOf(entry.token) + 2;

                    if (position == 0) {
                        System.err.println("position not found for " + entry.token);
                        continue;
                    }

                    writer.write("," + position + " " + floatFormat.format(entry.weight));
                }

                writer.write("}\n");
            }

            writer.close();
        }

        // Print Result List
        //System.out.println(results.toString());

        //File file = new File("9150");
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws IOException {
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
