/**
 * TLS-Attacker - A Modular Penetration Testing Framework for TLS
 *
 * Copyright 2014-2016 Ruhr University Bochum / Hackmanit GmbH
 *
 * Licensed under Apache License 2.0 http://www.apache.org/licenses/LICENSE-2.0
 */
package tlsattacker.fuzzer.analyzer;

import tlsattacker.fuzzer.config.EvolutionaryFuzzerConfig;
import tlsattacker.fuzzer.result.Result;
import java.util.LinkedList;
import java.util.List;

/**
 * An analyzer implementation which uses a set of Rules to find interesting TestVectors.
 * @author Robert Merget - robert.merget@rub.de
 */
public class RuleAnalyzer extends Analyzer {
    private List<Rule> ruleList;
    private EvolutionaryFuzzerConfig config;

    public static final String optionName = "rule";
    
    public RuleAnalyzer(EvolutionaryFuzzerConfig config) {
	this.config = config;
	ruleList = new LinkedList<Rule>();
	//THE IS GOOD RULE SHOULD ALWAYS BE EXECUTED ON THE START
	ruleList.add(new IsGoodRule(config));
	ruleList.add(new FindAlertsRule(config));
	ruleList.add(new IsCrashRule(config));
	ruleList.add(new IsTimeoutRule(config));
	ruleList.add(new AnalyzeTimeRule(config));
	ruleList.add(new UniqueFlowsRule(config));
	ruleList.add(new AnalyzeModificationRule(config));
	ruleList.add(new AnalyzeGoodModificationRule(config));
	ruleList.add(new ProtocolVersionRule(config));
	ruleList.add(new EarlyHeartbeatRule(config));
    }

    public Rule getRule(Class tempClass) {
	for (Rule r : ruleList) {
	    if (r.getClass().equals(tempClass)) {
		return r;
	    }
	}
	return null;
    }

    public void analyze(Result result) {
	for (Rule r : ruleList) {
	    if (r.applies(result)) {
		r.onApply(result);
	    } else {
		r.onDecline(result);
	    }
	}
    }

    public String getReport() {
	StringBuilder builder = new StringBuilder();
	for (Rule r : ruleList) {
	    String temp = r.report();
	    if (temp != null) {
		builder.append(r.report());
	    }
	}
	return builder.toString();
    }

}
