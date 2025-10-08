package com.curso.camel.filter;

import org.apache.camel.Predicate;
import org.apache.camel.Exchange;

public interface SoloDNIsValidos extends Predicate {
    boolean matches(Exchange exchange) ;
}
