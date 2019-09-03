package tools.data.input;

import java.io.IOException;

public class GenericSeekableLittleEndianAccessor extends GenericLittleEndianAccessor implements SeekableLittleEndianAccessor
{
    private final SeekableInputStreamBytestream bs;
    
    public GenericSeekableLittleEndianAccessor(final SeekableInputStreamBytestream bs) {
        super(bs);
        this.bs = bs;
    }
    
    @Override
    public final void seek(final long offset) {
        try {
            this.bs.seek(offset);
        }
        catch (IOException e) {
            System.err.println("Seek failed" + e);
        }
    }
    
    @Override
    public final long getPosition() {
        try {
            return this.bs.getPosition();
        }
        catch (IOException e) {
            System.err.println("getPosition failed" + e);
            return -1L;
        }
    }
    
    @Override
    public final void skip(final int num) {
        this.seek(this.getPosition() + num);
    }
}
