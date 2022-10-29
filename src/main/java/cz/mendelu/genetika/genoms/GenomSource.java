package cz.mendelu.genetika.genoms;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

/**
 * Created by Honza on 23. 7. 2015.
 */
public interface GenomSource extends Iterable<Genom> {

    public Genom getGenom(String name);

    public Collection<Genom> getGenoms();

    public Genom createGenom(String name) throws IOException;
}