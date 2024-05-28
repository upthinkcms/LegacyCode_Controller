// 
// Decompiled by Procyon v0.6.0
// 

package com.upthinkexperts.common.parsing;

import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Range;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.apache.poi.hwpf.HWPFDocument;
import java.util.List;
import java.util.Map;
import org.apache.poi.hwpf.usermodel.Picture;
import java.util.Set;
import org.apache.poi.hwpf.model.PicturesTable;

public class PicturesSource
{
    private PicturesTable picturesTable;
    private Set<Picture> output;
    private Map<Integer, Picture> lookup;
    private List<Picture> nonU1based;
    private List<Picture> all;
    private int pn;
    
    public PicturesSource(final HWPFDocument doc) {
        this.output = new HashSet<Picture>();
        this.pn = 0;
        this.picturesTable = doc.getPicturesTable();
        this.all = this.picturesTable.getAllPictures();
        this.lookup = new HashMap<Integer, Picture>();
        for (final Picture p : this.all) {
            this.lookup.put(p.getStartOffset(), p);
        }
        (this.nonU1based = new ArrayList<Picture>()).addAll(this.all);
        final Range r = doc.getRange();
        for (int i = 0; i < r.numCharacterRuns(); ++i) {
            final CharacterRun cr = r.getCharacterRun(i);
            if (this.picturesTable.hasPicture(cr)) {
                final Picture p2 = this.getFor(cr);
                final int at = this.nonU1based.indexOf(p2);
                this.nonU1based.set(at, null);
            }
        }
    }
    
    private boolean hasPicture(final CharacterRun cr) {
        return this.picturesTable.hasPicture(cr);
    }
    
    private void recordOutput(final Picture picture) {
        this.output.add(picture);
    }
    
    private boolean hasOutput(final Picture picture) {
        return this.output.contains(picture);
    }
    
    private int pictureNumber(final Picture picture) {
        return this.all.indexOf(picture) + 1;
    }
    
    public Picture getFor(final CharacterRun cr) {
        return this.lookup.get(cr.getPicOffset());
    }
    
    private Picture nextUnclaimed() {
        Picture p = null;
        while (this.pn < this.nonU1based.size()) {
            p = this.nonU1based.get(this.pn);
            ++this.pn;
            if (p != null) {
                return p;
            }
        }
        return null;
    }
}
