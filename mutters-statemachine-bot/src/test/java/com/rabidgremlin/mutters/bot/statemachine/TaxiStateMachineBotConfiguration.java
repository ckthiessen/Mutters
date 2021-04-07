/* Licensed under Apache-2.0 */
package com.rabidgremlin.mutters.bot.statemachine;

import com.rabidgremlin.mutters.core.Slot;
import com.rabidgremlin.mutters.opennlp.ner.IntentMatcherFactory;
import com.rabidgremlin.mutters.opennlp.ner.IntentMatcherType;
import com.rabidgremlin.mutters.opennlp.ner.TokenizerType;
import com.rabidgremlin.mutters.slots.SlotModel;

import com.rabidgremlin.mutters.core.IntentMatcher;
import com.rabidgremlin.mutters.slots.LiteralSlot;
import com.rabidgremlin.mutters.state.Guard;
import com.rabidgremlin.mutters.state.State;
import com.rabidgremlin.mutters.state.StateMachine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaxiStateMachineBotConfiguration implements StateMachineBotConfiguration
{

  @Override
  public IntentMatcher getIntentMatcher()
  {
    Map<String, List<Slot<?>>> intents = new HashMap<>();
    List<Slot<?>> slots = new ArrayList<>();
    slots.add(new LiteralSlot("Address"));
    intents.put("OrderTaxi", slots);
    intents.put("CancelTaxi", null);
    intents.put("WhereTaxi", null);
    intents.put("GaveAddress", slots);

    return IntentMatcherFactory.generateIntentMatcher(
            TokenizerType.WHITESPACE,
            IntentMatcherType.OPEN_NLP,
            new SlotModel("Address", "models/en-ner-address.bin"),
            "models/en-cat-taxi-intents.bin",
            intents
    );
  }

  @Override
  public StateMachine getStateMachine()
  {
    StateMachine stateMachine = new StateMachine();

    State startState = new StartState();
    stateMachine.setStartState(startState);

    State orderTaxi = new OrderTaxiState();
    State cancelTaxi = new CancelTaxiState();
    State taxiStatus = new TaxiStatusState();
    State getAddress = new GetAddressState();

    Guard haveAddress = new HaveAddressGuard();

    stateMachine.addTransition("OrderTaxi", startState, orderTaxi, haveAddress);
    stateMachine.addTransition("OrderTaxi", startState, getAddress);

    stateMachine.addTransition("GaveAddress", getAddress, orderTaxi, haveAddress);

    stateMachine.addTransition("CancelTaxi", startState, cancelTaxi);
    stateMachine.addTransition("WhereTaxi", startState, taxiStatus);

    return stateMachine;
  }

  @Override
  public String getDefaultResponse()
  {

    return null;
  }
}
