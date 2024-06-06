package org.example.back.compass;

import com.example.algorithm.AlgorithmAPI;
import com.example.algorithm.implementation.rule.RuleService;
import lombok.AllArgsConstructor;
import org.example.AlternativeComparsionEntity;
import org.example.AlternativePair;
import org.example.DataContext;
import org.example.RuleEntity;
import org.example.RuleSet;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Controller
@RequestMapping("/compass")
@AllArgsConstructor
@CrossOrigin
public class CompassController {
    private final AlgorithmAPI compassAPI;
    private final RuleService ruleService;

    @RequestMapping(value = "/calculateCompareAlternatives", method = RequestMethod.POST)
    public ResponseEntity<AlternativePair> calculateCompareAlternatives(
        @RequestParam int k,
        @RequestBody DataContext dataContext
    ) throws Exception {
        return ResponseEntity.ok().body(compassAPI.calculateCompareAlternatives(dataContext, k).get(0));
    }

    @RequestMapping(value = "/findComparsionAlternatives", method = RequestMethod.POST)
    public ResponseEntity<List<AlternativeComparsionEntity>> findComparsionAlternatives(
        @RequestBody DataContext dataContext) {
        var prepareAlts = dataContext.getNonPriorAlts();
        var result = new java.util.ArrayList<AlternativeComparsionEntity>();
        for (var index1 = 0; index1 < prepareAlts.size(); ++index1) {
            for (var index2 = index1 + 1; index2 < prepareAlts.size(); ++index2) {
                var alt1 = prepareAlts.get(index1);
                var alt2 = prepareAlts.get(index2);

                var prepare1 = new RuleEntity(new AlternativePair(alt1, alt2), RuleSet.PREPARE);
                var ruleChain = ruleService.generateLogicalChainOrNull(prepare1, dataContext);
                if (ruleChain != null) {
                    result.add(new AlternativeComparsionEntity(prepare1, ruleChain));
                } else {
                    var prepare2 = new RuleEntity(new AlternativePair(alt2, alt1), RuleSet.PREPARE);
                    ruleChain = ruleService.generateLogicalChainOrNull(prepare2, dataContext);
                    if (ruleChain != null) {
                        result.add(new AlternativeComparsionEntity(prepare2, ruleChain));
                    } else {
                        var equal = new RuleEntity(new AlternativePair(alt1, alt2), RuleSet.EQUAL);
                        ruleChain = ruleService.generateLogicalChainOrNull(equal, dataContext);
                        if (ruleChain != null) {
                            result.add(new AlternativeComparsionEntity(equal, ruleChain));
                        }
                    }
                }
            }
        }
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/findConflicts", method = RequestMethod.POST)
    public ResponseEntity<List<RuleEntity>> findConflicts(@RequestBody DataContext dataContext) {
        return ResponseEntity.ok(compassAPI.findConflictChainOrNull(dataContext));
    }
}
