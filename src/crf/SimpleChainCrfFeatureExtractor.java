package crf;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.aliasi.crf.ChainCrfFeatureExtractor;
import com.aliasi.crf.ChainCrfFeatures;

public class SimpleChainCrfFeatureExtractor
    implements ChainCrfFeatureExtractor<String>,
               Serializable {

    public ChainCrfFeatures<String> extract(List<String> tokens,
                                            List<String> tags) {
        return new SimpleChainCrfFeatures(tokens,tags);
    }

    static class SimpleChainCrfFeatures
        extends ChainCrfFeatures<String> {

        public SimpleChainCrfFeatures(List<String> tokens,
                                      List<String> tags) {
            super(tokens,tags);
        }
        public Map<String,Integer> nodeFeatures(int n) {
            return Collections
                .singletonMap("TOK_" + token(n),
                              Integer.valueOf(1));
        }
        public Map<String,Integer> edgeFeatures(int n, int k) {
            return Collections
                .singletonMap("PREV_TAG_" + tag(k),
                              Integer.valueOf(1));
        }
    }
}