import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Configuration {
    public static final int BLOCK_SIZE = 8;
    public static final int NUM_ROUNDS = 4;
    public static final char PAD_CHAR = '~';

    // **FIX**: The padding character '~' has been added back to the end of the set.
    public static final String CHARACTER_SET_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            + "abcdefghijklmnopqrstuvwxyz"
            + "0123456789"
            + " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";

    // Convert the string to a list of characters
    public static final List<Character> CHARACTER_SET = CHARACTER_SET_STRING.chars()
            .mapToObj(c -> (char) c)
            .collect(Collectors.toList());

    public static final int CHAR_SET_SIZE = CHARACTER_SET.size();

    // These maps are crucial for the key mixing logic
    public static final Map<Character, Integer> CHAR_TO_INDEX = IntStream.range(0, CHAR_SET_SIZE)
            .boxed()
            .collect(Collectors.toMap(CHARACTER_SET::get, i -> i));

    public static final Map<Integer, Character> INDEX_TO_CHAR = IntStream.range(0, CHAR_SET_SIZE)
            .boxed()
            .collect(Collectors.toMap(i -> i, CHARACTER_SET::get));
}