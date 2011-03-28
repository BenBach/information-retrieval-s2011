import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPOutputStream;

public class BOW {
    public enum Method {
        BOOLEAN,
        TF,
        TF_IDF
    }

    @Argument(required = true, index = 0, usage = "Corpus directory.")
    private File mainDirectory;

    @Option(name = "-s", aliases = {"--stem"}, usage = "Enables stemming.")
    private boolean doStemming = false;

    @Option(name = "-l", aliases = {"--lower-bound"}, usage = "Defines lower frequency threshold.")
    private float lowerThreshold = -1.0f;

    @Option(name = "-u", aliases = {"--upper-bound"}, usage = "Defines upper frequency threshold.")
    private float upperThreshold = -1.0f;

    @Option(name = "-o", required = true, aliases = {"--output"}, usage = "name of the generated ARFF file.")
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
                String fileName = file.getName();
                Result.incrementNrDocsInClass(className);
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

            FileOutputStream fileOutputStream = new FileOutputStream(output, false);
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(fileOutputStream);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(gzipOutputStream);
            PrintStream out = new PrintStream(bufferedOutputStream);
            

            List<String> tokens = new ArrayList<String>(results.size());

            Enumeration<String> tokensEnumeration = results.keys();
            while (tokensEnumeration.hasMoreElements()) {
                String token = tokensEnumeration.nextElement();

                if (token.length() == 0) continue;

                tokens.add(token);
            }

            Collections.sort(tokens);

            out.println("% stemming: " + doStemming);
            out.println("% method: " + method);
            out.println("% lower bound: " + lowerThreshold);
            out.println("% upper bound: " + upperThreshold);
            out.print("@RELATION ");
            out.println(output.getName());
            out.println("@ATTRIBUTE _document string");
            out.println("@ATTRIBUTE _class string");

            for (String token : tokens) {
                out.print("@ATTRIBUTE ");
                out.print(token);
                out.println(" numeric");
            }
            out.println("@DATA");
            //DecimalFormat floatFormat = new DecimalFormat("0.####################E0");

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
                        result.calculateWeightsTFIDF();
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

                out.print("{ 0 ");
                out.print(row.getKey().name);
                out.print(", 1 ");
                out.print(row.getKey().clazz);

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

                    out.print(",");
                    out.print(position);
                    out.print(" ");
                    out.print(entry.weight);
                }

                out.println("}");
            }

            out.close();
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
