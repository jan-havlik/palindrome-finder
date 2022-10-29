package cz.mendelu.genetika.genoms.format

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream

/**
 * Created by Honza on 10. 10. 2015.
 */
class PlainFormatConvertorTest extends GroovyTestCase {

    private PlainFormatConvertor convertor = new PlainFormatConvertor();

    void testConvert_Correct() {
        OutputStream out = new ByteOutputStream();
        InputStream data = new ByteArrayInputStream("ATCG".bytes);
        assert convertor.convert(data, out) == true;
        assert out.toString() == "ATCG";
    }

    void testConvert_WhiteChars() {
        OutputStream out = new ByteOutputStream();
        InputStream data = new ByteArrayInputStream("A T\tC\nG".bytes);
        assert convertor.convert(data, out) == true;
        assert out.toString() == "ATCG";
    }

    void testConvert_Mistakeres() {
        OutputStream out = new ByteOutputStream();
        InputStream data = new ByteArrayInputStream("ATXCG".bytes);
        assert convertor.convert(data, out) == false;
    }

}
