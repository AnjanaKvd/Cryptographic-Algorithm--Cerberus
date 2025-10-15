import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SBox {
    private final Map<Character, Character> sBoxMap;
    private final Map<Character, Character> inverseSBoxMap;

    public SBox(List<Character> characterSet) {
        // Use a fixed seed to ensure the S-Box is the same every time
        Random seededRandom = new Random(42L);
        List<Character> shuffledSet = new java.util.ArrayList<>(characterSet);
        Collections.shuffle(shuffledSet, seededRandom);

        this.sBoxMap = new HashMap<>();
        this.inverseSBoxMap = new HashMap<>();

        for (int i = 0; i < characterSet.size(); i++) {
            char originalChar = characterSet.get(i);
            char shuffledChar = shuffledSet.get(i);
            this.sBoxMap.put(originalChar, shuffledChar);
            this.inverseSBoxMap.put(shuffledChar, originalChar);
        }
    }

    public String substitute(String block) {
        StringBuilder result = new StringBuilder();
        for (char c : block.toCharArray()) {
            result.append(sBoxMap.getOrDefault(c, c));
        }
        return result.toString();
    }

    public String inverseSubstitute(String block) {
        StringBuilder result = new StringBuilder();
        for (char c : block.toCharArray()) {
            result.append(inverseSBoxMap.getOrDefault(c, c));
        }
        return result.toString();
    }
}