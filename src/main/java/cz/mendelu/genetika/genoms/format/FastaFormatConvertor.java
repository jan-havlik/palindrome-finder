package cz.mendelu.genetika.genoms.format;

import cz.mendelu.genetika.genoms.Sequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Created by xkoloma1 on 08.10.2015.
 */
public class FastaFormatConvertor extends PlainFormatConvertor {

    private static final Logger LOG = LoggerFactory.getLogger(FastaFormatConvertor.class);

    /**
     Nucleic Acid Code	Meaning	Mnemonic
     A	A	Adenine
     C	C	Cytosine
     G	G	Guanine
     T	T	Thymine
     U	U	Uracil
     R	A or G	puRine
     Y	C, T or U	pYrimidines
     K	G, T or U	bases which are Ketones
     M	A or C	bases with aMino groups
     S	C or G	Strong interaction
     W	A, T or U	Weak interaction
     B	not A (i.e. C, G, T or U)	B comes after A
     D	not C (i.e. A, G, T or U)	D comes after C
     H	not G (i.e., A, C, T or U)	H comes after G
     V	neither T nor U (i.e. A, C or G)	V comes after U
     N	A C G T U	Nucleic acid
     -	gap of indeterminate length
     *
     * @param input
     * @param output
     * @return
     * @throws IOException
     */
    @Override
    public boolean convert(InputStream input, OutputStream output) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith(">") || line.startsWith(";")) {
                continue;
            }

            byte[] bytes = line.getBytes();
            int b;
            for (int i = 0; i < bytes.length; i++) {
                b = toUpperCase(bytes[i]);

                if (isSkipEnabledChar(b)) {
                    continue;
                }

                if (isSequenceChar(b)) {
                    output.write(b);
                    continue;
                }

                return false;
            }

        }
        return true;
    }

    @Override
    protected boolean isSequenceChar(int b) {
        if (super.isSequenceChar(b)) {
            return true;
        }

        return b == 'U'
            || b == 'R'
            || b == 'Y'
            || b == 'K'
            || b == 'M'
            || b == 'S'
            || b == 'W'
            || b == 'B'
            || b == 'D'
            || b == 'H'
            || b == 'V'
            || b == 'N';
    }

}
