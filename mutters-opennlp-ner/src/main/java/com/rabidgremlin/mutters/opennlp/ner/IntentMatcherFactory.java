package com.rabidgremlin.mutters.opennlp.ner;

import com.rabidgremlin.mutters.core.Intent;
import com.rabidgremlin.mutters.core.IntentMatcher;
import com.rabidgremlin.mutters.core.Slot;
import com.rabidgremlin.mutters.core.Tokenizer;
import com.rabidgremlin.mutters.core.ml.AbstractMachineLearningIntentMatcher;
import com.rabidgremlin.mutters.fasttext.intent.FastTextIntentMatcher;
import com.rabidgremlin.mutters.opennlp.intent.OpenNLPIntentMatcher;
import com.rabidgremlin.mutters.opennlp.intent.OpenNLPTokenizer;
import com.rabidgremlin.mutters.slots.SlotModel;
import com.rabidgremlin.mutters.templated.SimpleTokenizer;
import com.rabidgremlin.mutters.templated.TemplatedIntentMatcher;
import opennlp.tools.tokenize.WhitespaceTokenizer;

import java.util.List;
import java.util.Map;

public class IntentMatcherFactory {

    public static IntentMatcher generateIntentMatcher(TokenizerType tokenizerType,
                                                      IntentMatcherType intentMatcherType,
                                                      SlotModel slotModel,
                                                      String intentModel,
                                                      Map<String, List<Slot<?>>> intents) {

            Tokenizer tokenizer;
            switch (tokenizerType) {
                case WHITESPACE:
                    tokenizer = new OpenNLPTokenizer(WhitespaceTokenizer.INSTANCE);
                    break;
                case SIMPLE:
                    tokenizer = new SimpleTokenizer();
                    break;
                default:
                    tokenizer = null;
            }

            OpenNLPSlotMatcher slotMatcher = new OpenNLPSlotMatcher(tokenizer);

            if(slotModel != null) {
                slotMatcher.addSlotModel(slotModel.getSlotName(), slotModel.getNerModel());
            }

            IntentMatcher intentMatcher;
            switch (intentMatcherType) {
                case FAST_TEXT:
                    intentMatcher = new FastTextIntentMatcher(intentModel, tokenizer, slotMatcher);
                    break;
                case OPEN_NLP:
                    intentMatcher = new OpenNLPIntentMatcher(intentModel, tokenizer, slotMatcher);
                    break;
                case TEMPLATED:
                    intentMatcher = new TemplatedIntentMatcher(tokenizer);
                    break;
                default:
                    intentMatcher = null;
            }

            if(intentMatcher instanceof AbstractMachineLearningIntentMatcher) {
                intents.keySet().forEach((intentName -> {
                    Intent intent = new Intent(intentName);
                    List<Slot<?>> slots = intents.get(intentName);
                    if(slots != null) {
                            slots.forEach(intent::addSlot);
                    }
                    ((AbstractMachineLearningIntentMatcher) intentMatcher).addIntent(intent);
                }));
            }

        return intentMatcher;
    }
}
