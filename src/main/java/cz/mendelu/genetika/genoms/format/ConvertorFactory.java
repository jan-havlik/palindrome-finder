package cz.mendelu.genetika.genoms.format;

/**
 * Created by xkoloma1 on 08.10.2015.
 */
public class ConvertorFactory {

    public static FormatConverter convertor(String format) {
        if (format == null) {
            throw new IllegalArgumentException("No data format in parametr.");
        }

        switch (format) {
            case FormatConverter.PLAIN:
                return new PlainFormatConverter();
            case FormatConverter.FASTA:
                return new FastaFormatConverter();
        }
        throw new IllegalArgumentException(String.format("Unknown data format %s.", format));
    }
}
