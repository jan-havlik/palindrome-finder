package cz.mendelu.genetika.genoms.format;

import cz.mendelu.genetika.genoms.Sequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * Created by xkoloma1 on 08.10.2015.
 */
public class PlainFormatConvertor implements FormatConvertor {

    private static final Logger LOG = LoggerFactory.getLogger(PlainFormatConvertor.class);

    private static final int END_Of_STREAM = -1;

    @Override
    public boolean convert(InputStream input, OutputStream output) throws IOException {
        int b;
        while((b = input.read()) != END_Of_STREAM) {
            b = toUpperCase(b);

            if (isSequenceChar(b)) {
                output.write(b);
                continue;
            }

            if (isSkipEnabledChar(b)) {
                continue;
            }

            return false;
        }
        return true;
    }

    protected int toUpperCase(int b) {
        return (97 <= b && b <= 122) ? b - 32 : b;
    }

    protected boolean isSequenceChar(int b) {
        return b == 'A'
            || b == 'T'
            || b == 'C'
            || b == 'G';
    }

    protected boolean isSkipEnabledChar(int  b) {
        return  b == ' ' ||
                b == '\t' ||
                b == '\n' ||
                b == 13 ||
                b == 10;
    }
}
