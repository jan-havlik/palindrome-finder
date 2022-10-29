package cz.mendelu.genetika.genoms.resources;


import java.io.InputStream;

/**
 * Created by Honza on 23.01.2016.
 */
public class NCBI extends AccessLimitedResource {

    private final String url;

    private final String db;

    private final String retmode;

    private final String rettype;

    private final String urlFormat = "%s?db=%s&id=%s&retmode=%s&rettype=%s";

    public NCBI(long restrictionTiming, String url, String db, String retmode, String rettype) {
        super(restrictionTiming);
        this.url = url;
        this.db = db;
        this.retmode = retmode;
        this.rettype = rettype;
    }

    public InputStream getResourceByID(String id) {
        String urlAddress = String.format(urlFormat, url, db, id, retmode, rettype);
        return getResource(urlAddress);
    }
}
