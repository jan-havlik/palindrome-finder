package cz.mendelu.genetika.genoms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * nacteni popisnych informaci o sekvenci
 *
 * https://www.ncbi.nlm.nih.gov/WebSub/html/help/feature-table.html
 *
 * Created by Jiří Lýsek on 7.12.2016.
 */
public class FeatureTable {

    private static final Logger LOG = LoggerFactory.getLogger(FeatureTable.class);

    private enum State {
        HEADER, POSITION, QUALIFIER, ERROR
    }

    private State state;
    private File file;

    public FeatureTable(File file) {
        this.file = file;
    }

    private String[] explodeLine(String line) {
        return line.split("\t");
    }

    public Stream<Feature> loadFeatureDescriptions() throws IOException {
        List<Feature> result = new ArrayList<Feature>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            state = State.HEADER;

            Feature tmp = null;
            while ((line = br.readLine()) != null) {
                switch(state) {
                    case HEADER: {
                        if(line.substring(0, 1).equals(">")) {
                            state = State.POSITION;
                        } else {
                            state = State.ERROR;
                        }
                        break;
                    }
                    case QUALIFIER: {
                        if(tmp != null) {
                            String[] parts = explodeLine(line);
                            if(parts.length == 4) {
                                tmp.addQualifier(parts[3], "");
                                break;
                            } else if(parts.length == 5) {
                                tmp.addQualifier(parts[3], parts[4]);
                                break;
                            }
                        }
                        state = State.POSITION;
                    }
                    case POSITION: {
                        state = State.QUALIFIER;

                        if(tmp != null) {
                            result.add(tmp);
                        }
                        tmp = null;

                        String[] parts = explodeLine(line);
                        if(parts.length >= 3 && !parts[0].equals("") && !parts[1].equals("")) {
                            LOG.debug("Found new feature <{}, {}>, name: {}", parts[0], parts[1], parts[2]);
                            parts[0] = parts[0].replace("<", "");
                            parts[1] = parts[1].replace(">", "");
                            parts[0] = parts[0].replace("^", "");
                            parts[1] = parts[1].replace("^", "");
                            int start = Integer.parseInt(parts[0]) - 1;
                            int end = Integer.parseInt(parts[1]) - 1;
                            //ty co maji opacne poradi, jsou na doplnkovem retezci DNA
                            if(start < end) {
                                tmp = new Feature(parts[2], start, end, Feature.FORWARD);
                            } else {
                                tmp = new Feature(parts[2], end, start, Feature.BACKWARD);
                            }
                        }
                        break;
                    }
                    case ERROR: {
                        LOG.error("Error on line [{}]", line);
                        tmp = null;
                        state = State.POSITION;
                    }
                }
            }
        }
        return result.stream();
    }

}

