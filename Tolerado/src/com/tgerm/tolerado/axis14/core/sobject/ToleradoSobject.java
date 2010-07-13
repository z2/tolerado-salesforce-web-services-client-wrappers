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

package com.tgerm.tolerado.axis14.core.sobject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.axis.message.MessageElement;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.sforce.soap.partner.sobject.SObject;
import com.tgerm.tolerado.axis14.core.util.SObjectUtil;

/**
 * An intelligent wrapper over {@link SObject} that makes easy to perform create
 * and updates. It also boosts performance by caching the {@link MessageElement}
 * instances intelligently, so fetching values is faster.
 * 
 * @author abhinav
 * 
 */
public class ToleradoSobject {
	protected SObject orignalSObj;
	/**
	 * Created once for fast parsing of Sobject later on.
	 */
	protected Map<String, MessageElement> msgElementCache = new HashMap<String, MessageElement>();
	/**
	 * Tracks the changes made to this Sobject instance
	 */
	protected Map<String, MessageElement> modifiedMsgElements = new HashMap<String, MessageElement>();

	/**
	 * Constructs using the given {@link SObject} instance
	 * 
	 * @param sobj
	 */
	public ToleradoSobject(SObject sobj) {
		this.orignalSObj = sobj;
		MessageElement[] any = this.orignalSObj.get_any();
		for (MessageElement me : any) {
			msgElementCache.put(me.getName(), me);
		}
	}

	/**
	 * Constructs an in memory {@link SObject} for the give Sobject type
	 * 
	 * @param sobjectType
	 *            like Contact, Custom_Object__c
	 */
	public ToleradoSobject(String sobjectType) {
		this.orignalSObj = new SObject();
		orignalSObj.setType(sobjectType);
	}

	/**
	 * 
	 * @return Returns reference to the Original {@link SObject} wrapped by this
	 *         instance
	 */
	public SObject getOriginalSObject() {
		return orignalSObj;
	}

	/**
	 * @return Returns the updated sobject. "Updated" {@link SObject} means any
	 *         changes done to this instance via
	 *         {@link ToleradoSobject#setAttribute(String, Object)}. One can use
	 *         that Sobject directly in Partner update or create calls
	 */
	public SObject getUpdatedSObject() {
		SObject modified = new SObject();
		Collection<MessageElement> updatedElements = modifiedMsgElements
				.values();
		modified.set_any(updatedElements
				.toArray(new MessageElement[modifiedMsgElements.size()]));
		modified.setFieldsToNull(new String[] {});
		modified.setType(orignalSObj.getType());
		modified.setId(getId());

		return modified;
	}

	public String getId() {
		return orignalSObj.getId();
	}

	public void setId(String id) {
		orignalSObj.setId(id);
	}

	/**
	 * An all in one setter for Sobject. Just mention the attribute to update
	 * and the new value, it will handle the rest.
	 * 
	 * @param attribName
	 *            Attribute Name to update
	 * @param newVal
	 *            new value of attribute
	 */
	public void setAttribute(String attribName, Object newVal) {
		MessageElement updatedMsgElem = SObjectUtil.fromTemplateElement(
				attribName, newVal);
		// Log this as a change
		modifiedMsgElements.put(attribName, updatedMsgElem);
		// Update the original mapping to, so that getters are working correctly
		msgElementCache.put(attribName, updatedMsgElem);
	}

	public SObject getNestedSObject(String name) {
		MessageElement messageElement = msgElementCache.get(name);
		return SObjectUtil.parseNestedSObj(messageElement);
	}

	public String getNestedValue(String parent, String childName) {
		SObject parentObj = getNestedSObject(parent);
		return parentObj != null ? SObjectUtil.parseText(parentObj.get_any(),
				childName) : null;
	}

	public List<SObject> getChildrens(String childRelationshipName) {
		MessageElement relationElement = msgElementCache
				.get(childRelationshipName);
		List<MessageElement> children = relationElement.getChildren();
		List<SObject> sObjects = new ArrayList<SObject>();
		for (MessageElement m : children) {
			SObject nestedSObject = SObjectUtil.parseNestedSObj(m);
			if (nestedSObject != null)
				sObjects.add(nestedSObject);
		}
		return sObjects;
	}

	/**
	 * Returns the String/TEXT value for the given attribute name
	 * 
	 * @param attributeName
	 *            Name of attribute or Sobject field whose value should be
	 *            fetched
	 * @return Text value of the attribute
	 */
	public String getTextValue(String attributeName) {
		MessageElement messageElement = msgElementCache.get(attributeName);
		return SObjectUtil.parseText(messageElement);
	}

	/**
	 * Returns the {@link Boolean} value for the given attribute name
	 * 
	 * @param attributeName
	 *            Name of attribute or Sobject field whose value should be
	 *            fetched
	 * @return {@link Boolean} value of the attribute
	 */
	public Boolean getBoolValue(String attributeName) {
		MessageElement messageElement = msgElementCache.get(attributeName);
		return SObjectUtil.parseBool(messageElement);
	}

	/**
	 * Returns the {@link BigInteger} value for the given attribute name
	 * 
	 * @param attributeName
	 *            Name of attribute or Sobject field whose value should be
	 *            fetched
	 * @return Int value of the attribute
	 */
	public BigInteger getIntValue(String attributeName) {
		MessageElement messageElement = msgElementCache.get(attributeName);
		return SObjectUtil.parseInt(messageElement);
	}

	/**
	 * Returns the Double value for the given attribute name
	 * 
	 * @param attributeName
	 *            Name of attribute or Sobject field whose value should be
	 *            fetched
	 * @return Double value of the attribute
	 */
	public Double getDoubleValue(String attributeName) {
		MessageElement messageElement = msgElementCache.get(attributeName);
		return SObjectUtil.parseDouble(messageElement);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.MULTI_LINE_STYLE);
	}

}
