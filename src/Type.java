import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Моклев Вячеслав
 */
public class Type {
    public static final int SIMPLE_TYPE = 0;
    public static final int ARRAY_TYPE = 1;
    public static final int GENERIC_TYPE = 2;

    private int kind;
    private String typeName;
    private List<Type> subTypes;

    private Type(int kind, String typeName, List<Type> subTypes) {
        this.kind = kind;
        this.typeName = typeName;
        this.subTypes = subTypes;
    }

    public static Type simpleType(String typeName) {
        return new Type(SIMPLE_TYPE, typeName, null);
    }

    public static Type arrayType(Type subType) {
        return new Type(ARRAY_TYPE, null, Collections.singletonList(subType));
    }

    public static Type genericType(String typeName, List<Type> subTypes) {
        return new Type(GENERIC_TYPE, typeName, subTypes);
    }

    public int getKind() {
        return kind;
    }

    public String getTypeName() {
        return typeName;
    }

    public List<Type> getSubTypes() {
        return subTypes;
    }

    public String toString() {
        switch (kind) {
            case SIMPLE_TYPE:
                return typeName;
            case ARRAY_TYPE:
                return subTypes.get(0) + "[]";
            case GENERIC_TYPE:
                return typeName + "<" + subTypes.stream().map(t -> "" + t).collect(Collectors.joining(", ")) + ">";
            default:
                return "<unknown type>";
        }
    }
}
