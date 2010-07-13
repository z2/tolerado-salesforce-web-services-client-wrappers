/*
Copyright (c) 2010 tgerm.com
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.
3. The name of the author may not be used to endorse or promote products
   derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE AUTHOR "AS IS" AND ANY EXPRESS OR
IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, 
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.tgerm.tolerado.axis14.core.util;

import java.math.BigInteger;

import javax.xml.soap.SOAPException;

import org.apache.axis.Constants;
import org.apache.axis.message.MessageElement;
import org.w3c.dom.Element;

import com.sforce.soap.partner.sobject.SObject;
import com.tgerm.tolerado.common.ToleradoException;

/**
 * Utility Class for {@link SObject} parsing
 * 
 * @author abhinav
 * 
 */
public class SObjectUtil {

	private static MessageElement TEMPLATE_MESSAGE_ELEMENT = new MessageElement(
			"", "temp");
	private static Element TEMPLATE_XML_ELEMENT;

	static {
		try {
			TEMPLATE_XML_ELEMENT = TEMPLATE_MESSAGE_ELEMENT.getAsDOM();
		} catch (Exception e) {
			throw new ToleradoException(e);
		}
		TEMPLATE_XML_ELEMENT.removeAttribute("xsi:type");
		TEMPLATE_XML_ELEMENT.removeAttribute("xmlns:ns1");
		TEMPLATE_XML_ELEMENT.removeAttribute("xmlns:xsd");
		TEMPLATE_XML_ELEMENT.removeAttribute("xmlns:xsi");
	}

	/**
	 * Highly performance MessageElement creation method. Why check details on
	 * this link
	 * http://www.tgerm.com/2010/06/messageelement-out-memory-salesforce.html
	 * 
	 * @param name
	 *            Message Element's name
	 * @param value
	 * @return Message element created from Template
	 */
	public static MessageElement fromTemplateElement(String name, Object value) {
		MessageElement me = new MessageElement(TEMPLATE_XML_ELEMENT);
		try {
			me.setObjectValue(value);
			me.setName(name);
		} catch (SOAPException e) {
			throw new ToleradoException(
					"Failed to created message element instance for Name:"
							+ name + ", value:" + value, e);
		}
		return me;
	}

	/**
	 * Parses boolean value out of the {@link MessageElement}
	 * 
	 * @param element
	 *            {@link MessageElement} to be parsed
	 * @return parsed Boolean value
	 */
	public static Boolean parseBool(MessageElement element) {
		try {
			return element != null ? (Boolean) element
					.getValueAsType(Constants.XSD_BOOLEAN) : null;
		} catch (Exception e) {
			throw new ToleradoException(
					"Failed to fetch boolean value from element "
							+ element.getName(), e);
		}
	}

	/**
	 * Parses boolean value out of the {@link MessageElement} array for the
	 * matching attribute name.
	 * 
	 * @param elements
	 *            {@link MessageElement}s from a {@link SObject}
	 * @param name
	 *            Attribute name whose value should be parsed
	 * @param defaultValue
	 *            Value that should be returned in case parsed value in
	 *            null/empty
	 * @return parsed {@link Boolean} value
	 */
	public static boolean parseBool(MessageElement[] elements, String name,
			boolean defaultValue) {
		for (MessageElement e : elements) {
			if (name.equals(e.getName())) {
				return parseBool(e);
			}
		}
		return defaultValue;
	}

	/**
	 * Parses nested {@link SObject} out of the {@link MessageElement}
	 * 
	 * For ex. if Contact has lookup to Account and relationship name is
	 * "Account", then this call should return the Sobject carrying Account
	 * stuff
	 * 
	 * @param element
	 *            {@link MessageElement} to be parsed
	 * @return parsed Nested {@link SObject} instance
	 */
	public static SObject parseNestedSObj(MessageElement element) {
		return (SObject) (element != null ? element.getObjectValue() : null);
	}

	/**
	 * Parses nested {@link SObject} out of the {@link MessageElement} array for
	 * the matching parent name. <br/>
	 * 
	 * For ex. if Contact has lookup to Account and relationship name is
	 * "Account", then this call should be done as follows <br/>
	 * 
	 * 
	 * <code>
	 * Sobject accountSobj = parseNestedSObj(contactSobj.get_any(), "Account");
	 * </code>
	 * 
	 * @param elements
	 *            {@link MessageElement}s from a {@link SObject}
	 * @param parentName
	 *            Attribute name whose {@link SObject} should be parsed
	 * @return parsed nested {@link SObject} instance
	 */
	public static SObject parseNestedSObj(MessageElement[] elements,
			String parentName) {
		SObject so = null;
		for (MessageElement e : elements) {
			if (parentName.equals(e.getName())) {
				so = (SObject) e.getObjectValue();
			}
		}
		return so;
	}

	/**
	 * This method expects that the given {@link MessageElement} is holding a
	 * relationship {@link SObject}. So it returns the value of required
	 * attribute from that relationship.
	 * 
	 * @param element
	 *            relationship {@link MessageElement} only
	 * @param childName
	 *            attribute from relationship {@link MessageElement} whose value
	 *            is required
	 * @return Text value from relationship sobject's attribute
	 */
	public static String parseNestedText(MessageElement element,
			String childName) {
		if (element == null)
			return null;
		SObject so = (SObject) element.getObjectValue();
		if (so != null) {
			return parseText(so.get_any(), childName);
		}
		return null;
	}

	/**
	 * Looks up the relationship {@link MessageElement} using "parentName" out
	 * of the array "elements". Returns the matching child attribute from that
	 * parent relationship
	 * 
	 * @param elements
	 * @param parentName
	 * @param childName
	 * @return
	 */
	public static String parseNestedText(MessageElement[] elements,
			String parentName, String childName) {
		for (MessageElement e : elements) {
			if (parentName.equals(e.getName())) {
				SObject so = (SObject) e.getObjectValue();
				if (so != null) {
					return parseText(so.get_any(), childName);
				}
			}
		}
		return null;
	}

	/**
	 * Parses {@link String} value out of the {@link MessageElement} array for
	 * the matching attribute name.
	 * 
	 * @param elements
	 *            {@link MessageElement}s from a {@link SObject}
	 * @param name
	 *            Attribute name whose value should be parsed
	 * @return parsed {@link String} value
	 */
	public static String parseText(MessageElement[] elements, String name) {
		for (MessageElement e : elements) {
			if (name.equals(e.getName())) {
				return e.getValue();
			}
		}
		return null;
	}

	/**
	 * Parses {@link String} value out of the {@link MessageElement}
	 * 
	 * @param element
	 *            {@link MessageElement} to be parsed
	 * @return parsed {@link String} value
	 */
	public static String parseText(MessageElement element) {
		return element != null ? element.getValue() : null;
	}

	/**
	 * Parses {@link BigInteger} value out of the {@link MessageElement} array
	 * for the matching attribute name.
	 * 
	 * @param elements
	 *            {@link MessageElement}s from a {@link SObject}
	 * @param name
	 *            Attribute name whose value should be parsed
	 * @return parsed {@link BigInteger} value
	 */
	public static BigInteger parseInt(MessageElement[] elements, String name) {
		for (MessageElement e : elements) {
			if (name.equals(e.getName())) {
				return parseInt(e);
			}
		}
		return null;
	}

	/**
	 * Parses {@link BigInteger} value out of the {@link MessageElement}
	 * 
	 * @param element
	 *            {@link MessageElement} to be parsed
	 * @return parsed {@link BigInteger} value
	 */
	public static BigInteger parseInt(MessageElement element) {
		BigInteger val = null;
		try {
			val = element != null ? (BigInteger) element
					.getValueAsType(Constants.XSD_INTEGER) : null;
		} catch (Exception e) {
			throw new ToleradoException(
					"Failed to fetch int value from element "
							+ element.getName(), e);

		}
		return val;
	}

	/**
	 * Parses {@link Double} value out of the {@link MessageElement}
	 * 
	 * @param element
	 *            {@link MessageElement} to be parsed
	 * @return parsed {@link Double} value
	 */
	public static Double parseDouble(MessageElement element) {
		Double val = null;
		try {
			val = element != null ? (Double) element
					.getValueAsType(Constants.XSD_DOUBLE) : null;
		} catch (Exception e) {
			throw new ToleradoException(
					"Failed to fetch double value from element "
							+ element.getName(), e);

		}
		return val;
	}
}
