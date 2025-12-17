import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Labels {
        private final List<String> unshuffledLabels;

        public Labels() {
            this.unshuffledLabels = new ArrayList<>(26 * 26 * 26);
            for (char a1 = 'A'; a1 <= 'Z'; ++a1) {
                for (char a2 = 'A'; a2 <= 'Z'; ++a2) {
                    for (char a3 = 'A'; a3 <= 'Z'; ++a3) {
                        this.unshuffledLabels.add("" + a1 + a2 + a3);
                    }
                }
            }
            List<String> naughtyWords = Arrays.asList("SEX", "ASS", "FUK", "DIC", "COC", "CUM", "FAG", "NIG", "END");
            unshuffledLabels.removeAll(naughtyWords);
        }

        public List<String> getUnshuffledLabels() {
            return this.unshuffledLabels;
        }
}
