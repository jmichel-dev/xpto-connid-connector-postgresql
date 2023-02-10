package com.evolveum.polygon.connector.xpto.utils;

import com.evolveum.polygon.connector.xpto.XptoConnector;
import org.identityconnectors.common.logging.Log;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeUtil;
import org.identityconnectors.framework.common.objects.filter.*;

import java.util.List;

public class FilterHandler implements FilterVisitor<String, String> {
    private static final Log LOG = Log.getLog(FilterHandler.class);
    private static final String EQUAL = "=";
    private static final String GREATER_THAN = ">";
    private static final String GREATER_THAN_OR_EQUAL = ">=";
    private static final String LESS_THAN = "<";
    private static final String LESS_THAN_OR_EQUAL = "<=";
    private static final String NOT_EQUAL = "<>";
    private static final String AND = " AND ";
    private static final String OR = " OR ";
    private static final String LIKE = " ILIKE ";
    private static final String IN = " IN ";
    private static final String ALL = " ALL ";
    private static final String ANY = " ANY ";
    private static final String BETWEEN = " BETWEEN ";
    private static final String EXISTS = " EXISTS ";
    private static final String SOME = " SOME ";
    private static final String NOT = " NOT ";
    private static final String SINGLE_QUOTE = "'";

    @Override
    public String visitAndFilter(String s, AndFilter andFilter) {
        return null;
    }

    @Override
    public String visitContainsFilter(String s, ContainsFilter containsFilter) {
        return null;
    }

    @Override
    public String visitContainsAllValuesFilter(String s, ContainsAllValuesFilter containsAllValuesFilter) {
        return null;
    }

    @Override
    public String visitEqualsFilter(String s, EqualsFilter equalsFilter) {
        s = s != null ? s : "";

        Attribute attr = equalsFilter.getAttribute();
        StringBuilder query = new StringBuilder();

        if (attr != null) {
            String singleValue;
            String name = attr.getName();
            List<Object> value = attr.getValue();

            if (value != null && !value.isEmpty()) {
                singleValue = AttributeUtil.getStringValue(attr);
            } else {
                singleValue = ALL;
            }

            query.append(name);
            query.append(isNumeric(singleValue) ? EQUAL : LIKE);
            query.append(singleValue.equals(ALL) ? ALL : parseSingleValue(singleValue));
        } else {
            return null;
        }

        return query.toString();
    }

    private static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private String parseSingleValue(String value) {
        StringBuilder stringValue = new StringBuilder();
        if (isNumeric(value)) {
            stringValue.append(value);
        } else {
            stringValue.append(SINGLE_QUOTE);
            stringValue.append(value);
            stringValue.append(SINGLE_QUOTE);
        }

        return stringValue.toString();
    }

    @Override
    public String visitExtendedFilter(String s, Filter filter) {
        return null;
    }

    @Override
    public String visitGreaterThanFilter(String s, GreaterThanFilter greaterThanFilter) {
        return null;
    }

    @Override
    public String visitGreaterThanOrEqualFilter(String s, GreaterThanOrEqualFilter greaterThanOrEqualFilter) {
        return null;
    }

    @Override
    public String visitLessThanFilter(String s, LessThanFilter lessThanFilter) {
        return null;
    }

    @Override
    public String visitLessThanOrEqualFilter(String s, LessThanOrEqualFilter lessThanOrEqualFilter) {
        return null;
    }

    @Override
    public String visitNotFilter(String s, NotFilter notFilter) {
        return null;
    }

    @Override
    public String visitOrFilter(String s, OrFilter orFilter) {
        return null;
    }

    @Override
    public String visitStartsWithFilter(String s, StartsWithFilter startsWithFilter) {
        return null;
    }

    @Override
    public String visitEndsWithFilter(String s, EndsWithFilter endsWithFilter) {
        return null;
    }

    @Override
    public String visitEqualsIgnoreCaseFilter(String s, EqualsIgnoreCaseFilter equalsIgnoreCaseFilter) {
        return null;
    }
}
