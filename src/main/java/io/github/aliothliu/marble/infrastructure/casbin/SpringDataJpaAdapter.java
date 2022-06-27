package io.github.aliothliu.marble.infrastructure.casbin;

import org.casbin.jcasbin.model.Assertion;
import org.casbin.jcasbin.model.Model;
import org.casbin.jcasbin.persist.BatchAdapter;
import org.casbin.jcasbin.persist.Helper;
import org.casbin.jcasbin.persist.UpdatableAdapter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class SpringDataJpaAdapter implements BatchAdapter, UpdatableAdapter {

    private final CasbinRuleRepository repository;

    private final JdbcTemplate jdbcTemplate;

    public SpringDataJpaAdapter(CasbinRuleRepository repository, JdbcTemplate jdbcTemplate) {
        this.repository = repository;
        this.jdbcTemplate = jdbcTemplate;
    }

    private static void loadPolicyLine(CasbinRule line, Model model) {
        String lineText = line.getPtype();
        if (line.getV0() != null) {
            lineText += ", " + line.getV0();
        }
        if (line.getV1() != null) {
            lineText += ", " + line.getV1();
        }
        if (line.getV2() != null) {
            lineText += ", " + line.getV2();
        }
        if (line.getV3() != null) {
            lineText += ", " + line.getV3();
        }
        if (line.getV4() != null) {
            lineText += ", " + line.getV4();
        }
        if (line.getV5() != null) {
            lineText += ", " + line.getV5();
        }
        Helper.loadPolicyLine(lineText, model);
    }

    @Override
    public void loadPolicy(Model model) {
        this.repository.findAll().forEach(rule -> loadPolicyLine(rule, model));
    }

    @Override
    public void savePolicy(Model model) {
        for (Map.Entry<String, Assertion> entry : model.model.get("p").entrySet()) {
            String ptype = entry.getKey();
            Assertion ast = entry.getValue();
            this.repository.saveAll(ast.policy.stream().map(rule -> savePolicyLine(ptype, rule)).collect(Collectors.toList()));
        }
        for (Map.Entry<String, Assertion> entry : model.model.get("g").entrySet()) {
            String ptype = entry.getKey();
            Assertion ast = entry.getValue();
            this.repository.saveAll(ast.policy.stream().map(rule -> savePolicyLine(ptype, rule)).collect(Collectors.toList()));
        }
    }

    private CasbinRule savePolicyLine(String ptype, List<String> rule) {
        CasbinRule line = new CasbinRule();
        line.setPtype(ptype);
        if (rule.size() > 0) {
            line.setV0(rule.get(0));
        }
        if (rule.size() > 1) {
            line.setV1(rule.get(1));
        }
        if (rule.size() > 2) {
            line.setV2(rule.get(2));
        }
        if (rule.size() > 3) {
            line.setV3(rule.get(3));
        }
        if (rule.size() > 4) {
            line.setV4(rule.get(4));
        }
        if (rule.size() > 5) {
            line.setV5(rule.get(5));
        }

        return line;
    }

    @Override
    public void addPolicy(String sec, String ptype, List<String> rule) {
        this.repository.save(savePolicyLine(ptype, rule));
    }

    @Override
    public void removePolicy(String sec, String ptype, List<String> rule) {
        if (CollectionUtils.isEmpty(rule)) return;
        removeFilteredPolicy(sec, ptype, 0, rule.toArray(new String[0]));
    }

    @Override
    public void removeFilteredPolicy(String sec, String ptype, int fieldIndex, String... fieldValues) {
        List<String> rules = Optional.of(Arrays.asList(fieldValues)).orElse(new ArrayList<>());
        if (CollectionUtils.isEmpty(rules)) {
            return;
        }
        StringBuilder sql = new StringBuilder("DELETE FROM rbac_rule WHERE ptype = '" + ptype + "'");
        for (int i = 0; i < rules.size(); i++) {
            if (Objects.nonNull(rules.get(i))) {
                sql.append(" AND v").append(i).append(" = '").append(rules.get(i)).append("'");
            }
        }
        this.jdbcTemplate.execute(sql.toString());
    }

    @Override
    public void addPolicies(String sec, String ptype, List<List<String>> list) {
        List<CasbinRule> rules = list.stream().map(rule -> this.savePolicyLine(ptype, rule)).collect(Collectors.toList());

        this.repository.saveAll(rules);
    }

    @Override
    public void removePolicies(String sec, String ptype, List<List<String>> list) {
        list.forEach(rule -> this.removePolicy(sec, ptype, rule));
    }

    @Override
    public void updatePolicy(String sec, String ptype, List<String> oldRule, List<String> newRule) {

    }
}
