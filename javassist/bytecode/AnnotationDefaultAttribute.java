package javassist.bytecode;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;
import javassist.bytecode.annotation.AnnotationsWriter;
import javassist.bytecode.annotation.MemberValue;

public class AnnotationDefaultAttribute extends AttributeInfo {

    public static final String tag = "AnnotationDefault";

    public AnnotationDefaultAttribute(ConstPool cp, byte[] info) {
        super(cp, "AnnotationDefault", info);
    }

    public AnnotationDefaultAttribute(ConstPool cp) {
        this(cp, new byte[] { (byte) 0, (byte) 0});
    }

    AnnotationDefaultAttribute(ConstPool cp, int n, DataInputStream in) throws IOException {
        super(cp, n, in);
    }

    public AttributeInfo copy(ConstPool newCp, Map classnames) {
        AnnotationsAttribute.Copier copier = new AnnotationsAttribute.Copier(this.info, this.constPool, newCp, classnames);

        try {
            copier.memberValue(0);
            return new AnnotationDefaultAttribute(newCp, copier.close());
        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
    }

    public MemberValue getDefaultValue() {
        try {
            return (new AnnotationsAttribute.Parser(this.info, this.constPool)).parseMemberValue();
        } catch (Exception exception) {
            throw new RuntimeException(exception.toString());
        }
    }

    public void setDefaultValue(MemberValue value) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        AnnotationsWriter writer = new AnnotationsWriter(output, this.constPool);

        try {
            value.write(writer);
            writer.close();
        } catch (IOException ioexception) {
            throw new RuntimeException(ioexception);
        }

        this.set(output.toByteArray());
    }

    public String toString() {
        return this.getDefaultValue().toString();
    }
}
