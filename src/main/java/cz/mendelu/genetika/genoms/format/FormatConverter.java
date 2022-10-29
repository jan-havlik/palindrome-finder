package cz.mendelu.genetika.genoms.format;

import cz.mendelu.genetika.genoms.Sequence;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by xkoloma1 on 08.10.2015.
 */
public interface FormatConverter {

    String PLAIN = "plain";
    String FASTA = "fasta";

    /**
     * Metoda pro převedí genomu z jednoho formátu do druhého.
     * @param input vstupní zdroj data
     * @param output výstup, kam se data mají převést
     * @param info informace pripojene ke genomu.
     * @return true, pokud byl převod úspěšný.
     */
    boolean convert(InputStream input, OutputStream output, StringBuilder info) throws IOException;
}
