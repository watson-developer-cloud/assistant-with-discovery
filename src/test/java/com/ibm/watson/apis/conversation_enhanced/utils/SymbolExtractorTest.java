package com.ibm.watson.apis.conversation_enhanced.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
public class SymbolExtractorTest {

	@Test
	public void testGetSymbol() {
		assertNull(new SymbolExtractor().getSymbol("[{ \"type1\": \"type\", \"symbol\": \"ibm\" }]"));
		assertEquals("ibm", new SymbolExtractor().getSymbol("[{ \"type\": \"type\", \"symbol\": \"ibm\" }]"));

	}

}
